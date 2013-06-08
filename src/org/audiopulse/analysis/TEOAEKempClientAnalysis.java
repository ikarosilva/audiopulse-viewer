package org.audiopulse.analysis;
import java.util.ArrayList;
import java.util.List;
import org.audiopulse.utilities.*;


public class TEOAEKempClientAnalysis {

	private static final String TAG="TEOAEKempClientAnalysis";

	//Frequencies at which to look for a response
	static final double[] fResp={2,3,4};
	//Tolerance, in Hz, from which to get the closest FFT bin relative to the actual desired frequency
	static final double spectralTolerance=50; 

	public static double[] getThreshold(double[] audioData, int epochSize){
		double[] peakThreshold = {0,0};
		int numberOfEpochs =20;
		double percThreshold=0; //In terms of sigma (std) from normal distribution

		int midPoint = Math.round(audioData.length/2), leftPoint, rightPoint;
		leftPoint = midPoint - (epochSize*numberOfEpochs);
		rightPoint = midPoint + ( epochSize*numberOfEpochs);

		// Get an idea of the signal max  positive and negative values to determine the threshold			
		// The threshold is the percentile of the mid range data
		double[] tmpData=Signals.copyOfRange(audioData,leftPoint,rightPoint);
		double[] mx={0,0}, pxx={0,0}, sigma={0,0}, max={0,0};
		int[] count={0,0};
		int ind;
		for(int n=0;n<tmpData.length;n++){
			if(tmpData[n]<0){
				ind=1; //Negative peaks
			}else{
				ind=0;
			}
			mx[ind]=(count[ind]*mx[ind]+tmpData[n])/(count[ind]+1);
			pxx[ind]=(count[ind]*pxx[ind]+tmpData[n]*tmpData[n])/(count[ind]+1);
			count[ind]=count[ind]+1;
			max[ind]=(Math.abs(tmpData[n]) > max[ind]) ? 
					Math.abs(tmpData[n]) :max[ind];
		}
		sigma[0]=Math.sqrt(pxx[0]-(mx[0]*mx[0]));
		sigma[1]=Math.sqrt(pxx[1]-(mx[1]*mx[0]));
		peakThreshold[0]=mx[0]+(percThreshold*sigma[0]);
		peakThreshold[1]=mx[1]-(percThreshold*sigma[1]);

		peakThreshold[0]=(peakThreshold[0] > max[0]) ? max[0]:peakThreshold[0];
		peakThreshold[1]=(peakThreshold[1] > max[1]) ? max[1]:peakThreshold[1];

		Log.v(TAG,"Using peakThreshold of= " + peakThreshold[0] + " and " + peakThreshold[1]);
		return peakThreshold;

	}

	public static int getAbsMaxInd(double[] x, int start, int end){
		double max=Double.MIN_VALUE;
		int ind=-1;
		if(x.length<end){
			end=x.length;
			Log.w(TAG,"Setting maximum index search to same size as data");
		}
		for(int i=start;i<end;i++){
			if(Math.abs(x[i])>max){
				max=Math.abs(x[i]);
				ind=i;
			}
		}
		return ind;
	}

	public static List<Integer>  getPeakIndices(double[] audioData, double[] peakThreshold,int epochSize, int Fs){
		int winSlide = (int) Math.round(epochSize/2);
		int  j;
		List<Integer> peakInd = new ArrayList<Integer>();	
		double peakCandVal; //peak value, and is its index
		int peakCandInd;
		int countNegative=0, countPositive=0;
		int onsetDelay= (int) Math.round(Fs*0.01);//Peak processing delay in order to avoid transient artifacts
		//NOTE: To help with synchronization/timing issues, the procedures looks first for the biggest negative
		//peak in the first epoch. From then on the next 3 epochs are expected to have positive peaks. After 3 positive
		//peak epochs are found it then proceeds to find the biggest negative peak again and re-start the cycle. 

		//Find initial peak index based on matched filtering
		int endIndex=epochSize*4*20;
		endIndex=(endIndex>(audioData.length-1-onsetDelay)) ? 
				(audioData.length-1-onsetDelay):endIndex;
				Log.v(TAG,"onsetDelay= "+ onsetDelay 
						+ " endIndex (for initial match filter search)= " + endIndex 
						+ " using winSlide= " +winSlide);
				int start=find4EpochOnset(Signals.copyOfRange(audioData, onsetDelay,endIndex),epochSize);
				if(start >-1){
					if( (onsetDelay+ start) > (audioData.length-(4*epochSize)) ){
						Log.w(TAG,"Cannot add offset to starting delay value, leaving delay at original value.");
					}else {
						onsetDelay=onsetDelay+ start;
						Log.v(TAG,"finding individual peaks starting from " + onsetDelay 
								+ " (added " + start + " sample offset)" );
					}
				}
				//Negative peaks are tagged with a negative index to help identify them later on (so that only a single array need to be returned)
				//all peakCandVal amplitudes are positive non-negative if found
				for (j = onsetDelay; j < (audioData.length -  epochSize - winSlide); j = j + winSlide){
					//Get maximum peak value and location within epoch
					peakCandInd=getAbsMaxInd(audioData,j,j+epochSize);
					peakCandVal=audioData[peakCandInd];
					if ( (peakCandVal > peakThreshold[0]) ){
						//Looking for positive peaks only
						countPositive++;
					}else if((peakCandVal < peakThreshold[1]) ){
						//Looking for negative peaks only
						peakCandInd=peakCandInd*-1; //Tag negative peaks with negative indices for later analysis
						countNegative++;
					}else{
						peakCandVal=0;
					}

					if(peakCandVal != 0){
						//Peak found, take the max as the new peak
						//For tag negative peaks with negative sign
						if( ( Math.abs(peakCandInd) ) > audioData.length){
							Log.w(TAG,"Incorrect peak location: " + Math.abs(peakCandInd) + "  + delay " + onsetDelay);
						}else if(( Math.abs(peakCandInd)+ epochSize ) > audioData.length) {
							Log.w(TAG,"Last peak index is too short for processing: " + Math.abs(peakCandInd) );
						}else{
							Log.w(TAG,"cP " + countPositive + " cN " + countNegative +
									" ind= "+ (peakCandInd)/(16000.0));
							peakInd.add(Math.abs(peakCandInd));
						}
						j=Math.abs(peakCandInd)+1;
					}
				}	
				Log.v(TAG,"Done finding epochs, found: " + peakInd.size());
				return peakInd;
	}

	private static double[] get4AverageWaveform(double[] audioData,	List<Integer> peakInd, int epochSize) {
		double[] sum=new double[epochSize];
		int countPositive=0, countNegative=0;
		int countMismatched=0;
		boolean start=false, isNegative;
		int expectedNegativePeaks= Math.round ((audioData.length)/(4*epochSize));
		//TODO: Set tolerance for number of mismatches
		Log.v(TAG,"Attempting to average over roughly "+ expectedNegativePeaks + " epochs.");
		for(int n=0; n<peakInd.size();n++){
			//NOTE: ASSUMPTION: Negative peaks are 3x bigger than the positive peaks!!!
			//Check polarity of the peak, only start after the first negative peak due to
			//possible transients
			isNegative=(audioData[Math.abs(peakInd.get(n))]<0);
			if(isNegative){
				if(start){
					//After find the first negative peak, there should always be 3 positive peaks
					//before the next negative peak. If not, this should be considered a mismatch
					if((countPositive%3) !=0){
						countMismatched++;
					}
				}
				start=true;
				countNegative++;
			}else if(start){
				//Add counts of positive
				countPositive++;
			}
			if(start){
				//Passed the first negative peak. Keep running sum
				//Sum to the running average
				for(int k=0;k<sum.length;k++)
					sum[k]+=audioData[Math.abs(peakInd.get(n))+k];
			}

		}
		//Get final 4average (note we assume countNegative is the most accurate statistic regarding the number
		//of epochs collected
		for(int k=0;k<sum.length;k++)
			sum[k]=sum[k]/((double) countNegative);

		Log.v(TAG,"Averaged : " + countNegative + " negative peaks, " + countPositive
				+ " positive peaks. With " + countMismatched + " mismatches");
		Log.v(TAG,"Expected about : " + expectedNegativePeaks + " negative peaks.");
		return sum;
	}

	public static double[] getTemporalAverage(double[] audioData, int epochSize,int Fs){
		double[] epochAverage; 	
		double[] peakThreshold = {0,0};//Values for positive and negative peaks respectively

		//Estimate peak threshold
		peakThreshold=getThreshold(audioData,epochSize);

		//Get peak indices
		List<Integer> peakInd = getPeakIndices(audioData,peakThreshold,epochSize,Fs);

		PlotEpochsTEOAE mplot2= new PlotEpochsTEOAE("TEOAE Average"
				,audioData,peakInd,Fs);

		//Get average 4sub waveform
		epochAverage = get4AverageWaveform(audioData,peakInd,epochSize);
		return epochAverage;
	}


	public static int find4EpochOnset(double[] data, int epochSize){
		//Searches for 4 epoch onset based on matched filtering
		double[] filter={-1,1,1,1};
		int M=(int) Math.floor((double) data.length/(4*epochSize));
		int L=6*epochSize; //Cross correlation of six epoch durations
		double[] sumBuffer=new double[L]; 
		int j, m, maxInd=-1;
		double maxVal=0;
		for(j=0;j<L;j++){
			for(m=0;m<M;m++){
				sumBuffer[j]+=data[j+ (epochSize*m)]*filter[m%4];
			}
		}
		//Maximum value is the onset
		for(j=0;j<L;j++){
			if(sumBuffer[j] > maxVal){
				maxInd=j;
				maxVal=sumBuffer[j];
			}
		}

		if(maxInd>data.length)
			Log.e(TAG,"Incorrect starting index picked: " + maxInd);
		if(maxInd==-1 || maxInd==L){
			Log.w(TAG,"Could not find suitable starting point for averaging.");
			maxInd=-1;
		}
		return maxInd;
	}

	public static double[] getEvokedResponse(double[] average,int cutPoint,int epochSize, int Fs){

		double[] trimAverage=Signals.copyOfRange(average,
				epochSize-cutPoint-1,epochSize-1);
		double[][] responseFFT= SignalProcessing.getSpectrum(trimAverage,
				Fs,trimAverage.length);
		double[] results= new double[fResp.length];

		for(int n=0;n<fResp.length;n++)
			results[n]=getResponse(responseFFT,fResp[n],spectralTolerance);

		return results;
	}

	public static double getResponse(double[][] XFFT, double desF, double tolerance){

		//Search through the spectrum to get the closest bin 
		//to the respective frequencies
		double dminF=Short.MAX_VALUE;
		double dF; 
		double result=-1;
		int ind=0;
		for(int n=0;n<XFFT[0].length;n++){
			dF=Math.abs(XFFT[0][n]-desF);
			if( dF < dminF ){
				dminF=dF;
				result=XFFT[1][n];
				ind =n;
			}		
		}
		if(dminF > tolerance){
			double  actF=XFFT[0][ind];
			System.err.println("Results are innacurate because frequency tolerance has been exceeded. Desired F= "
					+ desF +" closest F= " + actF);
		}
		return result;
	}


	private static double getNoiseEstimate(double[] audioData, int epochSize) {
		// Estimate noise by holding a fixed point wrt to the epoch and measuring its 
		//variance across every other 4th epoch
		int nPoints=32, pointStep; //How many points in an epoch to be used for variance estimation
		//Based on previous work related to ABR (see Fsp)
		double[] mean= new double[nPoints];
		double[] pow= new double[nPoints];
		int count=0;
		double var=0, residueNoiseVar=0, tmpVar;
		if(nPoints > epochSize){
			System.err.println("Epoch size (" + epochSize + 
					") is smaller than the number of points required for noise " +
					"estimation: " + nPoints);
			//TODO: throw exception instead ??
		}
		pointStep=Math.round(epochSize/nPoints);

		for(int n=0; n<(audioData.length-epochSize);n=n+(4*epochSize) ){
			for(int k=0;k<epochSize;k=k+pointStep){
				mean[k/pointStep]=(count*mean[k/pointStep] + audioData[n+k])/(count+1);
				pow[k/pointStep]=(count*pow[k/pointStep] + audioData[n+k]*audioData[n+k])/(count+1);
			}
			count++;
		}

		//Estimate the variance and the standard error, averaged across
		//the nPoints independent points
		for(int k=0;k<nPoints;k++){
			tmpVar=pow[k]-(mean[k]*mean[k]);
			var=(k*var + tmpVar)/(k+1);
		}
		residueNoiseVar= var/count;		
		return residueNoiseVar;
	}


	public static double[] mainAnalysis(short[] rawData, int Fs, int epochSize) throws Exception 
	{
		double epochTime=(int) Math.round(epochSize*Fs);
		//Set the start time from which to start analzying the averaged
		//epoch response in order to avoid transients (in seconds)
		int epochOnsetDelay= (int) Math.round(Fs*0.01);

		Log.v(TAG,rawData.length + "  / epoch size= " 
				+ ((double) rawData.length/epochSize + " epochTime= " + epochTime)
				+ "epochOnsetDelay= " + epochOnsetDelay);

		// Convert the raw data from short to double
		Log.v(TAG,"Converting data to short");
		double[] audioData = AudioSignal.convertMonoToDouble(rawData);
		Log.v(TAG,"estimating average");
		double[] epochAverage=getTemporalAverage(audioData,epochSize,Fs);
		
		PlotEpochsTEOAE mplot2= new PlotEpochsTEOAE("epochAverage"
				,epochAverage,null,16000);
		Log.v(TAG,"estimating noise");
		double residueNoiseVar=getNoiseEstimate(audioData,epochSize);
		int fftSize=(int) Math.pow(2,
				(int) Math.floor(Math.log(epochSize-epochOnsetDelay)/Math.log(2)));
		Log.v(TAG,"estimating evoked response");
		double[] results=getEvokedResponse(epochAverage,fftSize,epochSize,Fs);

		//Normalize and convert to dB wrt Short.MAX
		//as is done in the getResponse method
		residueNoiseVar=residueNoiseVar/(Short.MAX_VALUE*Short.MAX_VALUE);
		residueNoiseVar=10*Math.log10(residueNoiseVar/(fftSize*2*Math.PI));
		Log.v(TAG,"TEOAE analysis complete!");
		double[] output={results[0],results[1],results[2],residueNoiseVar};

		return output;
	}

}

