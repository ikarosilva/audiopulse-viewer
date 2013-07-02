package org.audiopulse.analysis;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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
		String data_dir="/home/ikaro/APData/sweep3k";

		File[] audioFiles=finder(data_dir);
		Arrays.sort(audioFiles);
		short[] rawData;
		ArrayList<Double> rms= new ArrayList<Double>();
		double tmp=0;
		for(int i=0;i<audioFiles.length;i++){              
			if(audioFiles[i].toString().contains("Stim")){
				rawData=ShortFile.readFile(audioFiles[i].getAbsolutePath());
				rawData=Arrays.copyOfRange(rawData,Fs,rawData.length);
				
				/*
				 * 	double amp= (double) SignalProcessing.max(rawData);
				for(int k=0;k<rawData.length;k++)
					rawData[k]=(short) ((short) Math.sin(
							Math.PI*2*1000*k/((double) Fs)
							)*amp);
				 */
				if(audioFiles[i].toString().contains("Stim")){
					tmp=AcousticConverter.getInputLevel(rawData);
				}else{
					tmp=AcousticConverter.getOutputLevel(rawData);
				}
				rms.add(tmp);
				System.out.println( audioFiles[i] +" spl= " + tmp);
				PlotEpochsTEOAE mplot4= new PlotEpochsTEOAE("stimulus"
						,rawData,null,Fs);
			}
		}
		double[] data=new double[rms.size()];
		for(int i=0;i<rms.size();i++)
			data[i]=(double) rms.get(i);

		PlotEpochsTEOAE mplot4= new PlotEpochsTEOAE("stimulus"
				,data,null,1);

	}


}
