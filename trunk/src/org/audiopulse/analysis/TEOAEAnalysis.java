package org.audiopulse.analysis;
import org.audiopulse.io.ShortFile;
import org.audiopulse.utilities.Signals;

class TEOAEAnalysis {
	
	public static short[] simulate(short[] rawData, int epochSize){
		short amp=(short) Math.round(Short.MAX_VALUE/8);
		int count=0;
		for(int i=0;i<rawData.length;i++){
			if((i%epochSize) == 0){
				if(count == 0 ){
					rawData[i]=(short) (amp*(-3.0));
				}else{
					rawData[i]=amp;
					if(count==3){
						count=-1;
					}
				}
				count++;
			}
		}
		return rawData;
	}
	public static void main(String[] args) throws Exception 
	{
		int Fs=16000;
		double epochTime=0.015;//Signals.getclickKempSweepDurationSeconds();
		int epochSize=(int) Math.round(Fs*epochTime);
		// Read the data in 
		String filename="/home/ikaro/APData/TEOAE/R1.raw";
		short[] rawData = ShortFile.readFile(filename);
		//rawData=simulate(rawData,epochSize);
		System.out.println(rawData.length + "  epochTime= " 
				+ epochTime);
		
		double[] test= Signals.clickKempMethod(Fs, 1);
		
		PlotEpochsTEOAE mplot2= new PlotEpochsTEOAE("stim"
				,test,null,Fs);
		
		double[] results=TEOAEKempClientAnalysis.mainAnalysis(rawData,Fs,epochSize);
		System.out.println("response[0] level is: " + results[0] );
		System.out.println("response[1] level is: " + results[1]);
		System.out.println("response[2] level is: " + results[2]);
		System.out.println("response[2] level is: " + results[3]);


	}

}

