package org.audiopulse.analysis;

import java.io.IOException;

import org.audiopulse.io.ShortFile;
import org.audiopulse.ui.SpectralPlotFrame;
import org.jfree.ui.RefineryUtilities;

//Toy class to help explore and debug the DPOAE analysis 
//
public class DPOAEExplorer {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		int Fs=16000, M=512;
		String filename="/home/ikaro/APData/opt/k4.raw";
		short[] rawData = ShortFile.readFile(filename);	
		double[] audioData=new double[rawData.length];
		double f=1000;
		double gain=Math.pow(10,0/20.0);
		System.out.println(gain);
		for(int n=0;n<rawData.length;n++){
			//audioData[n]=(double) rawData[n];
			audioData[n]=Math.sin(2*Math.PI*f*n/Fs);
			rawData[n]= (short) ( gain*Math.sin(2*Math.PI*f*n/Fs)*(Short.MAX_VALUE-1));
		}
		
		PlotEpochsTEOAE mplot2= new PlotEpochsTEOAE("stim"
				,audioData,null,Fs);

		double[][]Pxx=DPOAEAnalysis.getSpectrum(rawData,Fs,M);
		double Fres=(int) 2*M/Fs;
		SpectralPlotFrame demo = new SpectralPlotFrame("spec",Pxx,Fres,"Test");
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
		
	}

}
