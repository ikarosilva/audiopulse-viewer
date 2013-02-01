package org.audiopulse.analysis;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.audiopulse.graphics.SpectralPlot;
import org.audiopulse.io.ShortFile;
import org.audiopulse.utilities.SignalProcessing;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.encoders.ImageFormat;
import org.jfree.ui.RefineryUtilities;



public class DPOAEAnalysis {

	public static double[] getSpectrum(short[] x){
		return SignalProcessing.getSpectrum(x);
	}

	public static void plotSpectrum(String title, double[] Pxx,
			double Fs, double[] expFreq, String outFileName){
		SpectralPlot demo = new SpectralPlot(title,Pxx,Fs,expFreq,outFileName);
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
	}


	public static void main(String[] args) throws IOException, ClassNotFoundException {

		//Get directory listing 
		String oaeFile="/home/ikaro/test2.raw";
		double Fs=8000;
		double[] expFreq={950,1050};
		System.out.println("Analzing: " + oaeFile); 
		double[] XFFT= DPOAEAnalysis.getSpectrum(ShortFile.readFile(oaeFile));

		//Plot spectrum
		String outFileName="/home/ikaro/dpoae.png";
		plotSpectrum("DPOAE",XFFT,Fs,expFreq,outFileName);


	}


}


















