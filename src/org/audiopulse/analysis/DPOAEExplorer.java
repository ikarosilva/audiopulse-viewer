package org.audiopulse.analysis;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.audiopulse.graphics.SpectralPlot;
import org.audiopulse.hardware.AcousticConverter;
import org.audiopulse.io.ShortFile;
import org.audiopulse.ui.SpectralPlotFrame;
import org.audiopulse.utilities.SignalProcessing;
import org.jfree.ui.RefineryUtilities;

//Toy class to help explore and debug the DPOAE analysis 
//
public class DPOAEExplorer {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static File[] finder( String dirName){
		File dir = new File(dirName);
		return dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename)
			{ return filename.endsWith(".raw"); }
		} );

	}

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		int Fs=16000, M=512;
		String data_dir="/home/ikaro/APData/sweep10db";

		File[] audioFiles=finder(data_dir);
		Arrays.sort(audioFiles);
		short[] rawData;
		ArrayList<Double> rms= new ArrayList<Double>();
		double f2=2000;
		double f1=f2/1.2;
		double desF=f2;//2*f1-f2;
		
		for(int i=0;i<audioFiles.length;i++){              

			if(audioFiles[i].getName().contains("2kHz")){
				rawData=ShortFile.readFile(audioFiles[i].getAbsolutePath());
				rawData=Arrays.copyOfRange(rawData,Fs,rawData.length);
				SpectralPlotFrame demo = new SpectralPlotFrame(audioFiles[i].getName(),rawData,Fs,M);

				System.out.println("desF= " +desF);
				double[] resp= DPOAEAnalysis.getResponse(demo.getSpectrum(),desF,50);
				rms.add(resp[1]);
				System.out.println( audioFiles[i] +" spl= " + resp[1]);
			}
		}
		double[] data=new double[rms.size()];
		for(int j=0;j<rms.size();j++)
			data[j]=(double) rms.get(j);

		PlotEpochsTEOAE mplot4= new PlotEpochsTEOAE("stimulus"
				,data,null,1);

	}


}
