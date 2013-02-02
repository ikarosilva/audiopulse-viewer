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
		double Fs=8000;
		//double F2=2000;;
		//double F1=F2/1.2;
		double F2=2016;
		double F1=1641;
		double Fres=(2*F1)-F2;	 //Frequency of the expected response
		File[] oaeFiles=finder(data_dir);
		Arrays.sort(oaeFiles);
		double[][] results;
		for(int i=0;i<oaeFiles.length;i++){

			System.out.println("Analyzing raw file: " + oaeFiles[i]); 
			double[][] XFFT= DPOAEAnalysis.getSpectrum
					(ShortFile.readFile(oaeFiles[i].getAbsolutePath()),Fs);
			//Plot spectrum
			String outFileName=oaeFiles[i].getAbsolutePath().replace(".raw","")+".png";
			plotSpectrum("DPOAE",XFFT,Fres,outFileName);
			results=getResponse(XFFT,F1,F2,Fres);
			System.out.println(" F1amp= " + results[1][1] + " ,F2amp= " 
						+ results[1][1]	+  " ,Fres= " + results[1][2]);
		}
		/*
		//Plot Audiogram
		//TODO: Extract these results from data!
		System.out.println("Calculating DPGram Results...");
		double[] DPOAEData={7.206, -7, 5.083, 13.1,3.616, 17.9,2.542, 11.5,1.818, 17.1};
		double[] noiseFloor={7.206, -7-10,5.083, 13.1-10,3.616, 17.9-10,2.542, 11.5-10,1.818, 17.1-10};
		double[] f1Data={7.206, 64,5.083, 64,3.616, 64,2.542, 64,1.818, 64};
		double[] f2Data={7.206, 54.9,5.083, 56.6,3.616, 55.6,2.542, 55.1,1.818, 55.1};

		String outFileName2=data_dir+"dpaudiogram.png";
		PlotAudiogram audiogram=new PlotAudiogram("test",DPOAEData,noiseFloor,f1Data,f2Data,outFileName2);
		System.out.println("Analysis complete! ");
		*/
		System.exit(0);
	}


}


















