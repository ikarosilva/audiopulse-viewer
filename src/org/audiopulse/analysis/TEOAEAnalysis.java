package org.audiopulse.analysis;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.audiopulse.graphics.*;
import org.audiopulse.ui.*;
import org.audiopulse.graphics.PlotAudiogram;
import org.audiopulse.graphics.SpectralPlot;
import org.audiopulse.io.PackageDataThreadRunnable;
import org.audiopulse.io.ShortFile;
import org.audiopulse.utilities.SignalProcessing;
import org.jfree.ui.RefineryUtilities;

class TEOAEAnalysisException extends Exception {
	private static final long serialVersionUID = 1L;
	public TEOAEAnalysisException() {
	}
	public TEOAEAnalysisException(String msg) {
		super(msg);
	}
}


public class TEOAEAnalysis {

	public static void main(String[] args) throws Exception {
		short[] tmpData=null;
		tmpData = ShortFile.readFile(args[0]);
		if(tmpData != null){
			System.out.println("Reading file: " + args[0]);
			System.out.println("Data size: " + tmpData.length);
			PlotFrame plot= new PlotFrame("tmp.png","time","TEOAE",tmpData);
			plot.showPlot();
		}else{
			System.out.println("File not found: " + args[0] );
		}

	}

	/*

	public static double[][] getSpectrum(short[] x, double Fs, int epochTime){
		return SignalProcessing.getSpectrum(x, Fs,epochTime);
	}
	 */
	/*
	public static void plotSpectrum(String title, double[][] Pxx, double Fres, String outFileName){
		SpectralPlot demo = new SpectralPlot(title,Pxx,Fres,outFileName);
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
	}
	 */
	/*
	public static double[] getResponse(double[][] XFFT, double desF, double tolerance){

		//Search through the spectrum to get the closest bin 
		//to the respective frequencies
		double dminF=Short.MAX_VALUE;
		double dF; 
		//Results will be stored in a vector where first row is the closest
		//bin from the FFT wrt the frequency and second row is the power in that
		//bin. 
		double[] result=new double[2];
		for(int n=0;n<XFFT[0].length;n++){
			dF=Math.abs(XFFT[0][n]-desF);
			if( dF < dminF ){
				dminF=dF;
				result[0]=n;
				result[1]=XFFT[1][n];
			}		
		}
		if(dminF > tolerance){
			double  actF=XFFT[0][(int)result[0]];
			System.err.println("Results are innacurate because frequency tolerance has been exceeded. Desired F= "
					+ desF +" closest F= " + actF);
		}
		//System.out.println("F= "+ desF +" closest F= " + XFFT[0][(int)result[0]]);
		return result;
	}

	public static double getNoiseLevel(double[][] XFFT, int Find){

		//Estimates noise by getting the average level of 3 frequency bins above and below
		//the desired response frequency (desF)
		double noiseLevel=0;	
		//Get the average from 3 bins below and 3 bins above
		for(int i=0;i<=6;i++){
			if(i !=3){
				noiseLevel+= XFFT[1][(Find+i-3)];
			}
		}
		return (noiseLevel/6);
	}

	public static File[] finder( String dirName){
		File dir = new File(dirName);
		return dir.listFiles(new FilenameFilter() { 
			public boolean accept(File dir, String filename)
			{ return filename.endsWith(".raw"); }
		} );
	}

	public static void runAnalysis(String[] args) throws Exception {

		System.out.println("Analyzing results of compressed file...");
		//Get directory listing 
		String dataDir=PackageDataThreadRunnable.unpackData(args[0]);

		//TODO: Use cross correlation to line up the stimulus and the response
		// Note, the pulse train is 4sec high and 50 msec low, so 
		// create function array[indecies of onset starts] = ...
		//     findOnset(stimulus, recording) and generates a moving average of [1/4 1/4 1/4 1/4]
		//     Cross-correlation = Real{IFFT(FFT(X).*FFT(S))}
		// double[] averageEpoch = createResidues(recording, indices, umber of pulses per epoch)
		//    first by creating the residual for each epoch by -> adding the first three, and subtracting the last one} this is now a resideual with no onset and just the linear response, average out THESE residuls
		// 
		// THis calls the function below:
		// double[] getAverage(double[][] epochs)
		//    THen average out all the response epochs (maybe there are 20) into a single response
		//     this may eventually turn into haivng a weighted average where the weight is based on the variance in blocks of 10 across the residues
		// There will be a adelay from the rectangluar pulse to where we actually expect to start
		// After seeing a real freuency response, about 10msec perhaps? Then do analysis on the window 20 msecn 
		// after the delay and the output spectrum. 
		// CHeck the peaks at 2, 3, 4kHz... 
		// UNsure about 4, think that might be the middle ear canal.



		//Set parameters according to the procedures defined by
		//Gorga et al 1993,"Otoacoustic Emissions from Normal-hearing and hearing-impaired subject: distortion product responses
		double Fs=16000, F2=0, F1=0,Fres=0;	 //Frequency of the expected response
		int epochTime=512; //Size of each epoch from which to do the averaging, this is a trade off between
		//being close the Gorga's value (20.48 ms) and being a power of 2 for FFT analysis and given our Fs. 
		File[] oaeFiles=finder(dataDir);
		Arrays.sort(oaeFiles);
		double[] results = new double[3]; 
		double tolerance=50; //Tolerance, in Hz, from which to get the closest FFT bin relative to the actual desired frequency
		int M=3, fIndex=0; //number of frequencies being tested
		//The data is sent for plotting in an interleaved fashion
		//where odd elements are x-axis and even elements are y-axis
		double[] TEOAEData=new double[2*M];
		double[] noiseFloor=new double[2*M];
		double[] f1Data=new double[2*M];
		double[] f2Data=new double[2*M];
		double[] tmpResult=new double[2];
		int FresIndex;
		short[] rawData=null;

		for(int i=0;i<oaeFiles.length;i++){
			String outFileName=oaeFiles[i].getAbsolutePath().replace(".raw","")+".png";		
			//TODO: Right now the analysis is based on the Handbook of Otoacoustic Emissions Book by Hall
			//These parameters (F1,F2,Fres) should be loaded dynamically based on the protocol description
			//on the acompanying XML File
			if(outFileName.contains("AP_TEOAE-2kHz")){
				F2=2000;F1=F2/1.2;Fres=(2*F1)-F2;	
				fIndex=0*2;//index are all even, data amp goes in the odd indeces
			}else if(outFileName.contains("AP_TEOAE-3kHz")){
				F2=3000;F1=F2/1.2;Fres=(2*F1)-F2;
				fIndex=1*2;
			}else if(outFileName.contains("AP_TEOAE-4kHz")){
				F2=4000;F1=F2/1.2;Fres=(2*F1)-F2;
				fIndex=2*2;
			}else{
				System.err.println("Unexpected TEOAE File Name!");
			}
			rawData = ShortFile.readFile(oaeFiles[i].getAbsolutePath());

			//Check to see if any clipping occurred
			if(SignalProcessing.isclipped(rawData,Fs)){
				//Plot waveform
				Plot mPlot= new Plot(outFileName,"time","y",rawData);
				mPlot.showPlot();
				System.err.println("Error: clipping occured in:" + outFileName);
				throw new TEOAEAnalysisException("Corrupted (Clipped) data: " + outFileName);
			}


			double[][] XFFT= TEOAEAnalysis.getSpectrum(rawData,Fs,epochTime);
			//Plot spectrum
			plotSpectrum("TEOAE",XFFT,Fres,outFileName);
			tmpResult=getResponse(XFFT,F1,tolerance);
			results[0]=tmpResult[1];

			tmpResult=getResponse(XFFT,F2,tolerance);
			results[1]=tmpResult[1];

			tmpResult=getResponse(XFFT,Fres,tolerance);
			results[2]=tmpResult[1];
			FresIndex =(int) tmpResult[0]; //the closest FFT bin to the desired frequency that we want

			f1Data[fIndex]=F2;
			f1Data[fIndex+1]=Math.round(results[0]);

			f2Data[fIndex]=F2;
			f2Data[fIndex+1]=Math.round(results[1]);

			TEOAEData[fIndex]=F2;
			TEOAEData[fIndex+1]=Math.round(results[2]);

			noiseFloor[fIndex]=F2;
			noiseFloor[fIndex+1]=getNoiseLevel(XFFT,FresIndex);

		}	

		String outFileName2=dataDir+File.separator+"DPAudiogram.png";		 		
		PlotAudiogram audiogram=new PlotAudiogram("DPGram",TEOAEData,noiseFloor,f1Data,f2Data,outFileName2);
		System.out.println("2kHz:\t" + "TEOAE= " + TEOAEData[1] 
				+ "\tTEOAE - Noise= " +((double)Math.round((TEOAEData[1]-noiseFloor[1])*10)/10));
		System.out.println("3kHz:\t" + "TEOAE= " + TEOAEData[3]
				+ "\tTEOAE - Noise= " +((double)Math.round((TEOAEData[3]-noiseFloor[3])*10)/10));
		System.out.println("4kHz:\t" + "TEOAE= " + TEOAEData[5]
				+ "\tTEOAE - Noise= " +((double)Math.round((TEOAEData[5]-noiseFloor[5])*10)/10 ));
		System.out.println("Analysis complete! ");
	}

	public static void main(String[] args) throws Exception {
		TEOAEAnalysis.runAnalysis(args);
		System.exit(0);
	}
	 */
}


















