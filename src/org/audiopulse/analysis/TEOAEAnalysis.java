package org.audiopulse.analysis;
import java.util.ArrayList;
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
	static final int onsetDelay= (int) Math.round(Fs*0.2);//Peak processing delay in order to avoid transient artifacts
	
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
		
		for (j = onsetDelay; j < (audioData.length -  epochSize); j = j + winSlide)
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
				peakInd.add(peakCandInd);	
				
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
			isNegative=(audioData[peakInd.get(n)]<0);
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
					sum[k]+=audioData[peakInd.get(n)+k];
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

	public static double[] runAnalysis(short[] rawData){
		double[] audioData = null, epochAverage; 	
		double peakThreshold = 0;

		// Convert the raw data from short to double
		audioData = AudioSignal.convertMonoToDouble(rawData);

		//Estimate peak threshold
		peakThreshold=getThreshold(audioData);

		//Get peak indices
		System.out.println("Searching for epochs...");
		List<Integer> peakInd = getPeakIndices(audioData,peakThreshold);

		//Get average 4sub waveform
		epochAverage = get4AverageWaveform(audioData,peakInd);
		
		//Uncoment this to help debug Peak picking procedures
		//plotRawEpochs(audioData,peakInd);
		
		
		return epochAverage;
		
		// Do the FFT of the averaged epoch
		//double[][] XFFT= TEOAEAnalysis.getSpectrum(rawData,Fs,epochTime);
	}
	
	public static void plotRawEpochs(double[] audioData, List<Integer> peakInd){
		//Use this method for help in debugging the epoch selection routine
		PlotEpochsTEOAE mplot= new PlotEpochsTEOAE("TEOAE",audioData,peakInd);
	}
	
	
	public static void main(String[] args) throws Exception 
	{

		// Read the data in 
		String filename="/home/ikaro/TEOAE_Samples/AP_TEOAE-kHz-Sat-Mar-02-13-58-20-EST-2013.raw";
		short[] rawData = ShortFile.readFile(filename);

		double[] epochAverage=runAnalysis(rawData);
		
		//Plot averaged waveform
		PlotEpochsTEOAE mplot= new PlotEpochsTEOAE("TEOAE Average"
				,epochAverage,null);

	}

}

