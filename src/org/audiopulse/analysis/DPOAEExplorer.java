package org.audiopulse.analysis;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.audiopulse.graphics.ScatterPlot;
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
		int f=3;
		double[] SPL=new double[5];
		if(f==4){
			SPL[0]=30;SPL[1]=40;SPL[2]=50;SPL[3]=60;SPL[4]=70;
		}else{
			SPL[0]=50;SPL[1]=55;SPL[2]=60;SPL[3]=65;SPL[4]=70;
		}
		String[] sbj={"s1l","s2l","s3l","s4l","s5l","s6l","s7l"
				,"s1r","s2r","s3r","s4r","s5r","s6r","s7r","cc"};
		double[] mopt=new double[sbj.length];
		double[] bopt=new double[sbj.length];

		for(int s=0;s<sbj.length;s++){
			String data_dir="/home/ikaro/APData/csail/" + sbj[s]+ Integer.toString(f)+ "k";
			double f2=1000.0*f;
			File[] audioFiles=finder(data_dir);
			Arrays.sort(audioFiles);
			short[] rawData;
			ArrayList<Double> rms= new ArrayList<Double>();
			double f1=f2/1.2;
			double desF=2*f1-f2;

			for(int i=0;i<audioFiles.length;i++){              
				//for(int i=3;i<4;i++){ 
				rawData=ShortFile.readFile(audioFiles[i].getAbsolutePath());
				rawData=Arrays.copyOfRange(rawData,Fs,rawData.length);

				//used for debuggin
				/*
				for(int n=0;n<rawData.length;n++){
                    //audioData[n]=(double) rawData[n];
                    rawData[n]= (short) (Math.sin(2*Math.PI*desF*n/Fs)*(Short.MAX_VALUE/10.0));
                    if(n>=0 && n< 1*M )
                    	rawData[n]= (short) (rawData[n]*10.0);
				}
				 */

				SpectralPlotFrame demo = new SpectralPlotFrame(audioFiles[i].getName(),rawData,Fs,M);
				double[] resp= DPOAEAnalysis.getResponse(demo.getSpectrum(),desF,50);
				rms.add(resp[1]);
			}
			double[][] data=new double[rms.size()][2];
			for(int j=0;j<rms.size();j++){
				data[j][1]=(double) rms.get(j);
				data[j][0]=SPL[j];
			}

			//Get least square fit to data
			SimpleRegression regression = new SimpleRegression();
			regression.addData(data);

			/*
			PlotEpochsTEOAE mplot4= new PlotEpochsTEOAE("stimulus"
					,data,null,1);
			 */
			mopt[s]=regression.getIntercept();
			bopt[s]=regression.getSlope();

			System.out.println(sbj[s] + "  " + mopt[s] +", " + bopt[s]);
			//break;
		}

		//Print descriminationa values values
		ScatterPlot demo = new ScatterPlot("Stats", mopt, bopt);

	}


}
