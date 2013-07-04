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
		String data_dir="/home/ikaro/APData/sweep";

		File[] audioFiles=finder(data_dir);
		Arrays.sort(audioFiles);
		short[] rawData, right;
		short[] left;
		double[] mix;
		ArrayList<Double> rms= new ArrayList<Double>();
		double tmp=0;
		for(int i=0;i<audioFiles.length;i++){              
			if(!audioFiles[i].toString().contains("Stim")){
				rawData=ShortFile.readFile(audioFiles[i].getAbsolutePath());
				rawData=Arrays.copyOfRange(rawData,Fs,rawData.length);
				left=new short[rawData.length/2];
				right=new short[rawData.length/2];
				mix=new double[rawData.length/2];
				for(int n=0;n< rawData.length/2;n++){
					if(audioFiles[i].toString().contains("Stim")){
						left[n]=rawData[2*n];
						right[n]=rawData[2*n+1];
						mix[n]=left[n]+right[n];
					}else{
						left[n]=rawData[n];
						right[n]=rawData[n];
						mix[n]=left[n];
					}
				}

			
				double amp= (double) Short.MAX_VALUE/100;//SignalProcessing.max(left);

				
				for(int k=0;k<left.length;k++)
					left[k]= (short) (Math.cos(Math.PI*2*1000*k/((double) Fs))*amp);
				tmp=AcousticConverter.getInputLevel(left);
				
				/*
				if(!audioFiles[i].toString().contains("Stim")){
					tmp=AcousticConverter.getInputLevel(left);
				}else{
					tmp=AcousticConverter.getOutputLevel(left);
				}
				*/
				
				rms.add(tmp);
				System.out.println( audioFiles[i] +" spl= " + tmp);
				PlotEpochsTEOAE mplot4= new PlotEpochsTEOAE("stimulus"
						,left,null,Fs);

				//SpectralPlotFrame demo = new SpectralPlotFrame("rigth",right,Fs,M);
				SpectralPlotFrame demo2 = new SpectralPlotFrame("left",left,Fs,M);
				//SpectralPlotFrame demo3 = new SpectralPlotFrame("mix",mix,Fs,M);

				break;
			}
		}
		double[] data=new double[rms.size()];
		for(int i=0;i<rms.size();i++)
			data[i]=(double) rms.get(i);

		//PlotEpochsTEOAE mplot4= new PlotEpochsTEOAE("stimulus"
		//		,data,null,1);

	}


}
