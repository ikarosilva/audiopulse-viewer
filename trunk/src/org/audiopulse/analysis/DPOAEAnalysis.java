package org.audiopulse.analysis;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;

import org.audiopulse.graphics.PlotAudiogram;
import org.audiopulse.graphics.SpectralPlot;
import org.audiopulse.io.ShortFile;
import org.audiopulse.utilities.SignalProcessing;
import org.jfree.ui.RefineryUtilities;



public class DPOAEAnalysis {

	public static double[][] getSpectrum(short[] x, double Fs){
		return SignalProcessing.getSpectrum(x, Fs);
	}

	public static void plotSpectrum(String title, double[][] Pxx, double Fres, String outFileName){
		SpectralPlot demo = new SpectralPlot(title,Pxx,Fres,outFileName);
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
	}

	public static double[][] getResponse(double[][] XFFT, double F1, 
			double F2, double Fres){
	
		//Search through the spectrum to get the closest bin 
		//to the respective frequencies
		double dminF1=Short.MAX_VALUE,
				dminF2=Short.MAX_VALUE,dminFres=Short.MAX_VALUE;
		double dF1, dF2,dFres;
		//Results will be stored in a matrix where first row is the closest
		//bin from the FFT wrt the frequency and second row is the power in that
		//bin. The columns are organized as F1, F2 and Fres
		double[][] result=new double[2][3];
		for(int n=0;n<XFFT[0].length;n++){
			dF1=Math.abs(XFFT[0][n]-F1);
			dF2=Math.abs(XFFT[0][n]-F2);
			dFres=Math.abs(XFFT[0][n]-Fres);		
			if( dF1 < dminF1 ){
				dminF1=dF1;
				result[0][0]=n;
				result[1][0]=XFFT[1][n];
			}
			if( dF2 < dminF2 ){
				dminF2=dF2;
				result[0][1]=n;
				result[1][1]=XFFT[1][n];
			}
			if( dFres < dminFres ){
				dminFres=dFres;
				result[0][2]=n;
				result[1][2]=XFFT[1][n];
			}
			
		}
		return result;
	}
	
	
	public static File[] finder( String dirName){
		File dir = new File(dirName);
		return dir.listFiles(new FilenameFilter() { 
			public boolean accept(File dir, String filename)
			{ return filename.endsWith(".raw"); }
		} );

	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {

		//Get directory listing 
		String data_dir=args[0];
		double Fs=8000, F2=0, F1=0,Fres=0;	 //Frequency of the expected response
		File[] oaeFiles=finder(data_dir);
		Arrays.sort(oaeFiles);
		double[][] results; 
		int M=3, fIndex=0; //number of frequencies being tested
		//The data is sent for plotting in an interleaved fashion
		//where odd elements are x-axis and even elements are y-axis
		double[] DPOAEData=new double[2*M];
		double[] noiseFloor=new double[2*M];
		double[] f1Data=new double[2*M];
		double[] f2Data=new double[2*M];
		for(int i=0;i<oaeFiles.length;i++){
			String outFileName=oaeFiles[i].getAbsolutePath().replace(".raw","")+".png";		
			//Determine which frequency was tested
			//TODO: Right now the analysis is based on the Handbook of Otoacoustic Emissions Book by Hall
			//These parameters (F1,F2,Fres) should be loaded dynamically based on the protocol description
			//on the acompanying XML File
			if(outFileName.contains("AP_DPOAE-2kHz")){
				F2=2000;F1=F2/1.2;Fres=(2*F1)-F2;	
				fIndex=0;
			}else if(outFileName.contains("AP_DPOAE-3kHz")){
				F2=3000;F1=F2/1.2;Fres=(2*F1)-F2;
				fIndex=1*2;
			}else if(outFileName.contains("AP_DPOAE-4kHz")){
				F2=4000;F1=F2/1.2;Fres=(2*F1)-F2;
				fIndex=2*2;
			}else{
				System.err.println("Unexpected DPOAE File Name!");
			}
			
			double[][] XFFT= DPOAEAnalysis.getSpectrum
					(ShortFile.readFile(oaeFiles[i].getAbsolutePath()),Fs);
			//Plot spectrum
			plotSpectrum("DPOAE",XFFT,Fres,outFileName);
			results=getResponse(XFFT,F1,F2,Fres);
			
			f1Data[fIndex]=Math.round(results[0][0]);
			f1Data[fIndex+1]=Math.round(results[1][0]);
			f2Data[fIndex]=Math.round(results[0][1]);
			f2Data[fIndex+1]=Math.round(results[1][1]);
			DPOAEData[fIndex]=Math.round(results[0][2]);
			DPOAEData[fIndex+1]=Math.round(results[1][2]);
			noiseFloor[fIndex]=Math.round(results[0][2]-10);
			noiseFloor[fIndex+1]=Math.round(results[1][2]-10);
		}
		
		String outFileName2=data_dir+"dpaudiogram.png";		 		
		System.out.println("f1=" + f1Data[0]+"f2=" + f2Data[0]+"res=" + DPOAEData[0]);
		System.out.println("f1=" + f1Data[1]+"f2=" + f2Data[1]+"res=" + DPOAEData[1]);
		System.out.println("f1=" + f1Data[2]+"f2=" + f2Data[2]+"res=" + DPOAEData[2]);
		PlotAudiogram audiogram=new PlotAudiogram("DPGram",DPOAEData,noiseFloor,f1Data,f2Data,outFileName2);
		System.out.println("Analysis complete! ");
		System.exit(0);
	}


}


















