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

	public static double[] getSpectrum(short[] x){
		return SignalProcessing.getSpectrum(x);
	}

	public static void plotSpectrum(String title, double[] Pxx,
			double Fs, double Fres, String outFileName){
		SpectralPlot demo = new SpectralPlot(title,Pxx,Fs,Fres,outFileName);
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
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
		double F1=1000;
		double F2=1500;
		double Fres=(2*F1)-F2;	
		File[] oaeFiles=finder(data_dir);
		Arrays.sort(oaeFiles);

		for(int i=0;i<oaeFiles.length;i++){

			System.out.println("Analyzing raw file: " + oaeFiles[i]); 
			double[] XFFT= DPOAEAnalysis.getSpectrum
					(ShortFile.readFile(oaeFiles[i].getAbsolutePath()));
			//Plot spectrum
			String outFileName=oaeFiles[i].getAbsolutePath().replace(".raw","")+".png";
			plotSpectrum("DPOAE",XFFT,Fs,Fres,outFileName);

		}
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
		System.exit(0);
	}


}


















