package org.audiopulse.analysis;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.audiopulse.graphics.Plot;
import org.audiopulse.graphics.PlotAudiogram;
import org.audiopulse.graphics.SpectralPlot;
import org.audiopulse.io.PackageDataThreadRunnable;
import org.audiopulse.io.ShortFile;
import org.audiopulse.utilities.SignalProcessing;
import org.jfree.ui.RefineryUtilities;

class DPOAEAnalysisException extends Exception {
	private static final long serialVersionUID = 1L;
	public DPOAEAnalysisException() {
	}
	public DPOAEAnalysisException(String msg) {
		super(msg);
	}
}


public class DPOAEAnalysis {

	public static double[][] getSpectrum(short[] x, double Fs, int epochTime){
		return SignalProcessing.getSpectrum(x, Fs,epochTime);
	}

	public static void plotSpectrum(String title, double[][] Pxx, double Fres, String outFileName){
		SpectralPlot demo = new SpectralPlot(title,Pxx,Fres,outFileName);
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
	}

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
		double[] DPOAEData=new double[2*M];
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
			if(outFileName.contains("AP_DPOAE-2kHz")){
				F2=2000;F1=F2/1.2;Fres=(2*F1)-F2;	
				fIndex=0*2;//index are all even, data amp goes in the odd indeces
			}else if(outFileName.contains("AP_DPOAE-3kHz")){
				F2=3000;F1=F2/1.2;Fres=(2*F1)-F2;
				fIndex=1*2;
			}else if(outFileName.contains("AP_DPOAE-4kHz")){
				F2=4000;F1=F2/1.2;Fres=(2*F1)-F2;
				fIndex=2*2;
			}else{
				System.err.println("Unexpected DPOAE File Name!");
			}
			rawData = ShortFile.readFile(oaeFiles[i].getAbsolutePath());

			//Check to see if any clipping occurred
			if(SignalProcessing.isclipped(rawData,Fs)){
				//Plot waveform
				Plot mPlot= new Plot(outFileName,"time","y",rawData);
				mPlot.showPlot();
				System.err.println("Error: clipping occured in:" + outFileName);
				throw new DPOAEAnalysisException("Corrupted (Clipped) data: " + outFileName);
			}

			
			double[][] XFFT= DPOAEAnalysis.getSpectrum(rawData,Fs,epochTime);
			//Plot spectrum
			plotSpectrum("DPOAE",XFFT,Fres,outFileName);
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

			DPOAEData[fIndex]=F2;
			DPOAEData[fIndex+1]=Math.round(results[2]);

			noiseFloor[fIndex]=F2;
			noiseFloor[fIndex+1]=getNoiseLevel(XFFT,FresIndex);
			 
		}	

		String outFileName2=dataDir+File.separator+"DPAudiogram.png";		 		
		PlotAudiogram audiogram=new PlotAudiogram("DPGram",DPOAEData,noiseFloor,f1Data,f2Data,outFileName2);
		System.out.println("2kHz:\t" + "DPOAE= " + DPOAEData[1] 
				+ "\tDPOAE - Noise= " +((double)Math.round((DPOAEData[1]-noiseFloor[1])*10)/10));
		System.out.println("3kHz:\t" + "DPOAE= " + DPOAEData[3]
				+ "\tDPOAE - Noise= " +((double)Math.round((DPOAEData[3]-noiseFloor[3])*10)/10));
		System.out.println("4kHz:\t" + "DPOAE= " + DPOAEData[5]
				+ "\tDPOAE - Noise= " +((double)Math.round((DPOAEData[5]-noiseFloor[5])*10)/10 ));
		System.out.println("Analysis complete! ");
	}

	public static void main(String[] args) throws Exception {
		DPOAEAnalysis.runAnalysis(args);
		System.exit(0);
	}
}


















