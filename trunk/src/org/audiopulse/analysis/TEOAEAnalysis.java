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

	public static double getThreshold(double[] absData){
		double peakThreshold = 0;
		int numberOfEpochs =20;
		int midPoint = Math.round(absData.length/2), leftPoint, rightPoint;
		leftPoint = midPoint - (epochSize*numberOfEpochs);
		rightPoint = midPoint + ( epochSize*numberOfEpochs);

		// The threshold is the 75th percentile of the mid range data
		peakThreshold = StatUtils.percentile(Arrays.copyOfRange(absData, leftPoint, rightPoint)
				, 75);
		return peakThreshold;

	}

	public static List<Integer>  getPeakIndices(double[] absData, double peakThreshold){
		int winSlide = (int) Math.round(epochSize/2);
		int k, j;
		List<Integer> peakInd = new ArrayList<Integer>();	
		double peakCandVal; //peak value, and is its index
		int peakCandInd;
		System.out.println("step size: " + winSlide);
		//Search in  epochSize windows for the peak, moving each time by winSlide plus peak
		for (j = 0; j < (absData.length -  epochSize); j = j + winSlide)
		{
			//Get maximum peak value and location within epoch
			peakCandVal=0;
			peakCandInd=-1;
			System.out.println("Searching: "  + j + " - " + (j+epochSize));
			for(k=j;k<(j+epochSize);k++){
				if (absData[k] > peakThreshold){
					peakCandInd = (absData[k] > peakCandVal) ? k:peakCandInd;
					peakCandVal = (absData[k] > peakCandVal) ? 
							absData[k]:peakCandVal;
				}			
			}		
			// If a peak was found take the max peak index as the new peak
			if(peakCandInd >= 0){
				peakInd.add(peakCandInd);				
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
					sum[k]+=audioData[peakInd.get(n)];
			}

		}

		//Get final 4average (note we assume countNegative is the most accurate statistic regarding the number
		//of epochs collected
		for(int k=0;k<sum.length;k++)
			sum[k]=sum[k]/((double) countNegative);

		System.out.println("Averaged : " + countNegative + " negative peaks, " + countPositive
				+ " positive peaks. With " + countMismatched + " mismatches");
		return sum;
	}

	public static double[] runAnalysis(short[] rawData){
		double[] audioData = null, absData = null, epochAverage; 	
		double peakThreshold = 0;

		// Convert the raw data from short to double
		audioData = AudioSignal.convertMonoToDouble(rawData);

		// Get an idea of the signal max to determine the threshold			
		absData= new double[audioData.length];
		for (int j = 0; j < audioData.length; j++) 
			absData[j] = Math.abs(audioData[j]);

		//Estimate peak threshold
		peakThreshold=getThreshold(absData);

		//Get peak indices
		System.out.println("Searching for epochs...");
		List<Integer> peakInd = getPeakIndices(absData,peakThreshold);

		//Get average 4sub waveform
		epochAverage = get4AverageWaveform(audioData,peakInd);
		
		plotRawEpochs(audioData,peakInd);
		
		
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
		
		


	}

}

