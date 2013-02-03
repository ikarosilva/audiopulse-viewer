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

	public static double[][] getSpectrum(short[] x, double Fs){
		return SignalProcessing.getSpectrum(x, Fs);
	}

	public static void plotSpectrum(String title, double[][] Pxx, double Fres, String outFileName){
		SpectralPlot demo = new SpectralPlot(title,Pxx,Fres,outFileName);
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
	}

	public static double[] getResponse(double[][] XFFT, double desF, double tolerance){
	
		//Search through the spectrum to get the closest bin 
		//to the respective frequencies
		double dminF=Short.MAX_VALUE;
		double dF; 
		//Results will be stored in a vector where first row is the closest
		//bin from the FFT wrt the frequency and second row is the power in that
		//bin. 
		double[] result=new double[2];
		for(int n=0;n<XFFT[0].length;n++){
			dF=Math.abs(XFFT[0][n]-desF);
			if( dF < dminF ){
				dminF=dF;
				result[0]=n;
				result[1]=XFFT[1][n];
			}		
		}
		if(dminF > tolerance){
			double  actF=XFFT[0][(int)result[0]];
			System.err.println("Results are innacurate because frequency tolerance has been exceeded. Desired F= "
					+ desF +" closest F= " + actF);
		}
		return result;
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
		double Fs=8000, F2=0, F1=0,Fres=0;	 //Frequency of the expected response
		File[] oaeFiles=finder(data_dir);
		Arrays.sort(oaeFiles);
		double[][] results = new double[2][3]; 
		double tolerance=10; //Tolerance, in Hz, from which to get the closest FFT bin relative to the actual desired frequency
		int M=3, fIndex=0; //number of frequencies being tested
		//The data is sent for plotting in an interleaved fashion
		//where odd elements are x-axis and even elements are y-axis
		double[] DPOAEData=new double[2*M];
		double[] noiseFloor=new double[2*M];
		double[] f1Data=new double[2*M];
		double[] f2Data=new double[2*M];
		double[] tmpResult=new double[2];
		
		for(int i=0;i<oaeFiles.length;i++){
			String outFileName=oaeFiles[i].getAbsolutePath().replace(".raw","")+".png";		
			//Determine which frequency was tested
			//TODO: Right now the analysis is based on the Handbook of Otoacoustic Emissions Book by Hall
			//These parameters (F1,F2,Fres) should be loaded dynamically based on the protocol description
			//on the acompanying XML File
			if(outFileName.contains("AP_DPOAE-2kHz")){
				F2=2000;F1=F2/1.2;Fres=(2*F1)-F2;	
				fIndex=0*2;//index are all even, data amp goes in the odd indeces
			}else if(outFileName.contains("AP_DPOAE-3kHz")){
				F2=3000;F1=F2/1.2;Fres=(2*F1)-F2;
				fIndex=1*2;
			}else if(outFileName.contains("AP_DPOAE-4kHz")){
				F2=4000;F1=F2/1.2;Fres=(2*F1)-F2;
				fIndex=2*2;
			}else{
				System.err.println("Unexpected DPOAE File Name!");
			}
			
			double[][] XFFT= DPOAEAnalysis.getSpectrum
					(ShortFile.readFile(oaeFiles[i].getAbsolutePath()),Fs);
			//Plot spectrum
			plotSpectrum("DPOAE",XFFT,Fres,outFileName);
			tmpResult=getResponse(XFFT,F1,tolerance);
			results[0][0]=tmpResult[0];
			results[1][0]=tmpResult[1];
			
			tmpResult=getResponse(XFFT,F2,tolerance);
			results[0][1]=tmpResult[0];
			results[1][1]=tmpResult[1];
			
			tmpResult=getResponse(XFFT,Fres,tolerance);
			results[0][2]=tmpResult[0];
			results[1][2]=tmpResult[1];
			
			f1Data[fIndex]=F2;
			f1Data[fIndex+1]=Math.round(results[1][0]);
			
			f2Data[fIndex]=F2;
			f2Data[fIndex+1]=Math.round(results[1][1]);
			
			DPOAEData[fIndex]=F2;
			DPOAEData[fIndex+1]=Math.round(results[1][2]);
			
			noiseFloor[fIndex]=F2;
			noiseFloor[fIndex+1]=Math.round(results[1][2]-10);
		}
		
		String outFileName2=data_dir+"dpaudiogram.png";		 		
		System.out.println("f1=" + f1Data[1]+"f2=" + f2Data[1]+"res=" + DPOAEData[1]);
		System.out.println("f1=" + f1Data[3]+"f2=" + f2Data[3]+"res=" + DPOAEData[3]);
		System.out.println("f1=" + f1Data[5]+"f2=" + f2Data[5]+"res=" + DPOAEData[5]);
		PlotAudiogram audiogram=new PlotAudiogram("DPGram",DPOAEData,noiseFloor,f1Data,f2Data,outFileName2);
		System.out.println("Analysis complete! ");
		System.exit(0);
	}


}


















