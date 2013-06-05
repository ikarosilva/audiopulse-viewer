package org.audiopulse.analysis;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.stat.*;
import org.audiopulse.utilities.*;
import org.audiopulse.io.ShortFile;

class TEOAEAnalysisException extends Exception {
	private static final long serialVersionUID = 1L;
	public TEOAEAnalysisException() {
	}
	public TEOAEAnalysisException(String msg) {
		super(msg);
	}
}

public class TEOAEAnalysis {

	static final double Fs = 16000;	 					
	static final double epochTime = 0.020;// Time units for search windows (in seconds) 			 
	static final int epochSize = (int) Math.round(epochTime*Fs);
	static int onsetDelay= (int) Math.round(Fs*0.1);//Peak processing delay in order to avoid transient artifacts

	//Set the start time from which to start analzying the averaged
	//epoch response in order to avoid transients (in seconds)
	static final int epochOnsetDelay= (int) Math.round(Fs*0.014);

	//Frequencies at which to look for a response
	static final double[] fResp={2,3,4};
	//Tolerance, in Hz, from which to get the closest FFT bin relative to the actual desired frequency
	static final double spectralTolerance=50; 

	public static double getThreshold(double[] audioData){
		double peakThreshold = 0;
		int numberOfEpochs =20;
		int percThreshold=99;

		int midPoint = Math.round(audioData.length/2), leftPoint, rightPoint;
		leftPoint = midPoint - (epochSize*numberOfEpochs);
		rightPoint = midPoint + ( epochSize*numberOfEpochs);

		// Get an idea of the signal max to determine the threshold			
		double[] absData= new double[rightPoint-leftPoint];
		for (int j = 0; j < absData.length; j++) 
			absData[j] = Math.abs(audioData[leftPoint+j]);

		// The threshold is the 75th percentile of the mid range data
		peakThreshold = StatUtils.percentile(absData,percThreshold);
		return peakThreshold;

	}

	public static List<Integer>  getPeakIndices(double[] audioData, double peakThreshold){
		int winSlide = (int) Math.round(epochSize/2);
		int k, j;
		List<Integer> peakInd = new ArrayList<Integer>();	
		double peakCandVal, tmpVal; //peak value, and is its index
		int peakCandInd;
		int countNegative=0, countPositive=0;
		boolean lookForNegative=true;

		//NOTE: To help with synchronization/timing issues, the procedures looks first for the biggest negative
		//peak in the first epoch. From then on the next 3 epochs are expected to have positive peaks. After 3 positive
		//peak epochs are found it then proceeds to find the biggest negative peak again and re-start the cycle. 

		//Find initial peak indix based on matched filtering
		int endIndex=epochSize*4*20;
		endIndex=(endIndex>audioData.length-onsetDelay) ? 
				(audioData.length-onsetDelay):endIndex;
		int start=find4EpochOnset(Arrays.copyOfRange(audioData, onsetDelay,endIndex));
		for (j = onsetDelay+ start; j < (audioData.length -  epochSize); j = j + winSlide)
		{
			//Get maximum peak value and location within epoch
			peakCandVal=0;
			peakCandInd=-1;
			//System.out.println("Searching: "  + j + " - " + (j+epochSize));
			for(k=j;k<(j+epochSize);k++){
				tmpVal=Math.abs(audioData[k]);
				if (tmpVal > peakThreshold && (tmpVal > peakCandVal)){
					if(lookForNegative && (audioData[k] <0)){
						//Looking for negative peaks only
						peakCandInd = k;
						peakCandVal = tmpVal;
						countNegative++;
					}else if(!lookForNegative && (audioData[k] >0) ){
						//Looking for positive peaks only
						peakCandInd = k;
						peakCandVal = tmpVal;
						countPositive++;
					}
				}			
			}		

			if(peakCandInd >= 0){
				//Peak found, take the max as the new peak
				//For tag negative peaks with negative sign
				if(lookForNegative ==false){
					peakInd.add(peakCandInd);
				}else{
					peakInd.add(-1*peakCandInd);
				}

				//if 3 positive peaks where found, Look for a negative peak next
				if(countPositive == 3){
					countPositive=0;
					countNegative=0;
					lookForNegative=true;
				}

				//if 1 negative peak was found, Look for 3 positive peaks next
				if(countNegative >0){
					countPositive=0;
					countNegative=0;
					lookForNegative=false;
				}

				// Move j to peak location + 1, so the next search will be from peak + 20 msec
				j = peakCandInd + 1;
			}
		}	
		System.out.println("Done finding epochs, found: " + peakInd.size());
		return peakInd;
	}

	private static double[] get4AverageWaveform(double[] audioData,
			List<Integer> peakInd) {
		double[] sum=new double[epochSize];
		int countPositive=0, countNegative=0;
		int countMismatched=0;
		boolean start=false, isNegative;
		int expectedNegativePeaks= Math.round ((audioData.length-onsetDelay)/(4*epochSize));
		//TODO: Set tolerance for number of mismatches

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

		System.out.println("Averaged : " + countNegative + " negative peaks, " + countPositive
				+ " positive peaks. With " + countMismatched + " mismatches");
		System.out.println("Expected about : " + expectedNegativePeaks + " negative peaks, with epoch time of: "
				+ epochTime + " seconds");
		return sum;
	}

	public static double[] getTemporalAverage(double[] audioData){
		double[] epochAverage; 	
		double peakThreshold = 0;

		//Estimate peak threshold
		peakThreshold=getThreshold(audioData);

		//Get peak indices
		List<Integer> peakInd = getPeakIndices(audioData,peakThreshold);

		//Get average 4sub waveform
		epochAverage = get4AverageWaveform(audioData,peakInd);

		//Uncoment this to help debug Peak picking procedures
		plotRawEpochs(audioData,peakInd);

		return epochAverage;
	}


	public static int find4EpochOnset(double[] data){
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
		
		return maxInd;
	}

	public static double[] getEvokedResponse(double[] average,int cutPoint){

		double[] trimAverage=Arrays.copyOfRange(average,
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


	public static void plotRawEpochs(double[] audioData, List<Integer> peakInd){
		//Use this method for help in debugging the epoch selection routine
		PlotEpochsTEOAE mplot= new PlotEpochsTEOAE("TEOAE",audioData,peakInd,Fs);
	}

	private static double getNoiseEstimate(double[] audioData) {
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


	public static void main(String[] args) throws Exception 
	{

		// Read the data in 
		String filename="/home/ikaro/TEOAE_Samples/AP_TEOAE-kHz-Sat-Mar-02-13-58-20-EST-2013.raw";
		short[] rawData = ShortFile.readFile(filename);

		System.out.println(rawData.length + "  / epoch size= " 
				+ ((double) rawData.length/epochSize));

		// Convert the raw data from short to double
		double[] audioData = AudioSignal.convertMonoToDouble(rawData);

		double[] epochAverage=getTemporalAverage(audioData);
		double residueNoiseVar=getNoiseEstimate(audioData);
		int fftSize=(int) Math.pow(2,
				(int) Math.floor(Math.log(epochSize-epochOnsetDelay)/Math.log(2)));
		double[] results=getEvokedResponse(epochAverage,fftSize);
		
		//Normalize and convert to dB wrt Short.MAX
		//as is done in the getResponse method
		residueNoiseVar=residueNoiseVar/(Short.MAX_VALUE*Short.MAX_VALUE);
		residueNoiseVar=10*Math.log10(residueNoiseVar/(fftSize*2*Math.PI));
		
		System.out.println("Noise level is: " + residueNoiseVar);

		System.out.println("response[0] level is: " + results[0] +
				" with " + fftSize + " fft bins");
		System.out.println("response[1] level is: " + results[1]);
		System.out.println("response[2] level is: " + results[2]);
		//Plot averaged waveform
		PlotEpochsTEOAE mplot= new PlotEpochsTEOAE("TEOAE Average"
				,epochAverage,null,Fs);

	}

}

