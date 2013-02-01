package org.audiopulse.analysis;

import java.io.IOException;
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


	public static void main(String[] args) throws IOException, ClassNotFoundException {

		//Get directory listing 
		String oaeFile="/home/ikaro/test2.raw";
		double Fs=8000;
		double F1=1000;
		double F2=1500;
		double Fres=(2*F1)-F2;
		System.out.println("Analzing: " + oaeFile); 
		double[] XFFT= DPOAEAnalysis.getSpectrum(ShortFile.readFile(oaeFile));

		//Plot spectrum
		String outFileName="/home/ikaro/dpoae.png";
		plotSpectrum("DPOAE",XFFT,Fs,Fres,outFileName);


	}


}


















