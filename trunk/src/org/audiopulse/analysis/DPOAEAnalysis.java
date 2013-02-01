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
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {

		//Get directory listing 
		String oaeFile="/home/ikaro/test2.raw";
		double Fs=8000;
		double[] expFreq={950,1050};
		System.out.println("Analzing: " + oaeFile); 
		double[] XFFT= DPOAEAnalysis.getSpectrum(ShortFile.readFile(oaeFile));
		
		//Plot spectrum
		SpectralPlot demo = new SpectralPlot("DPOAE",XFFT,Fs,expFreq);
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
		
	
	}


}


















