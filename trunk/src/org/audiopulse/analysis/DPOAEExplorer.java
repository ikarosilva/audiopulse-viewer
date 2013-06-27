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
		String filename="/home/ikaro/APData/sweep4k/"+
		"AP_0.0-DPOAE-LE-4kHz-Wed-Jun-26-22-43-45-EDT-2013.raw";
		String filename2="/home/ikaro/APData/sweep4k/"+
		"AP_0.0-DPOAE-Stim-LE-4kHz-Wed-Jun-26-22-43-45-EDT-2013.raw";
		String filename3="/home/ikaro/APData/sweep4k/"+
				"AP_40.0-DPOAE-Stim-LE-4kHz-Wed-Jun-26-22-43-52-EDT-2013.raw";
		
		short[] rawData = ShortFile.readFile(filename);		
		PlotEpochsTEOAE mplot2= new PlotEpochsTEOAE("raw data"
				,rawData,null,Fs);

		double[][]Pxx=DPOAEAnalysis.getSpectrum(rawData,Fs,M);
		double Fres=(int) 2*M/Fs;
		SpectralPlotFrame demo = new SpectralPlotFrame("spec",Pxx,Fres,"Test");
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
		
		short[] stimData = ShortFile.readFile(filename2);	
		short[] data=new short[stimData.length/2];
		double sum=0;
		for(int n=0;n<data.length;n++){
			data[n]=stimData[n*2];
			sum+=Math.abs(data[n]);
		}
		PlotEpochsTEOAE mplot3= new PlotEpochsTEOAE("stimulus"
				,data,null,Fs);
		
		short[] stimData2 = ShortFile.readFile(filename3);	
		short[] data2=new short[stimData2.length/2];
		double sum2=0;
		for(int n=0;n<data2.length;n++){
			data2[n]=stimData2[n*2];
			sum2+=Math.abs(data2[n]);
		}
		PlotEpochsTEOAE mplot4= new PlotEpochsTEOAE("stimulus"
				,data2,null,Fs);
		
		System.out.println("sum= " + sum + " sum2= " + sum2);
	}
	

}
