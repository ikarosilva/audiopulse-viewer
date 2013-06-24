package org.audiopulse.utilities;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.audiopulse.hardware.AcousticConverter;


public class SignalProcessing {

	@Deprecated // function name should indicate that it does not actually calculate linearly scaled rms. Perhaps name it dBfs? 
	public static double rms(short[] x){
		double y=0;
		double rms;
		//Calculate mean squared
		for(int i=0;i<x.length;i++){
			y= (i*y + (x[i]*x[i]))/(i+1);
		}
		//Return RMS in decibels wrt to 1 gain
		//And Round RMS value to nearest 
		rms=20*Math.log10(Math.sqrt(y)/Short.MAX_VALUE);
		return Math.round(rms*10)/10;
	}

	public static double rms(double[] x) {
		double rms = 0;
		int N = x.length;
		for (int n=0; n<N; n++) {
			rms += x[n]*x[n]/N; 		//warning: not rms yet!
		}
		rms = Math.sqrt(rms);
		return rms;
	}

	public static boolean isclipped(short[] rawData, double Fs) {
		// TODO: Crude method to detect clipping of the waveform by 
		//using a moving window and checking if all samples in that window
		//are the same value. We are essentially checking if the signal 
		//has any "flat-regions" of any sort (the playback can be clipped 
		//while the recording can still be ok).
		
		double winSize=0.01; //window size in milliseconds
		int window=(int) Math.round(Fs*winSize);
		double sum=0;
		double lastSample=0;
		double currentSample=0;
		boolean clipped=false;
		for(int i=0;i<rawData.length;i++){
			currentSample=Math.abs(rawData[i]);
			if(i> (window-1)){
				lastSample=Math.abs(rawData[i-window]);
				sum-=lastSample;
				sum+=currentSample;
				//TODO : Maybe allow for some uncertainty around 1 because
				//play back can be clipped but rec noise may mask some of it.
				if(sum/(currentSample*window) == 1){
					clipped=true;
					break;
				}
			}else {
				//Initial transient stage, filling the filter
				sum+=currentSample;
			}
		}
		return clipped;
	}		
	
	@Deprecated 	// as written, this is linear scaling in power (not rms) to dB (not dBu)
	public static double rms2dBU(double x){
		return 10*Math.log10(x);
	}

	//convert linear scaling to dB
	public static double lin2dB(double rms) {
		return 20*Math.log10(rms);
	}

	//convert dB scaling to linear
	public static double dB2lin(int a) {
		return dB2lin((double)a);
	}
	public static double dB2lin(double a) {
		return Math.pow(10, a/20);
	}

	public static double[][] getSpectrum(short [] x, double Fs, int SPEC_N){
		double[] y= new double[x.length];
		for(int i=0;i<x.length;i++){
			y[i]=(double) x[i]/(Short.MAX_VALUE+1);
		}	
		return getSpectrum(y,Fs,SPEC_N);
		
	}
	public static double[][] getSpectrum(double[] x, double Fs, int SPEC_N){
		FastFourierTransformer FFT = new 
				FastFourierTransformer(DftNormalization.STANDARD);
		//Calculate the size of averaged waveform
		//based on the maximum desired frequency for FFT analysis

		//Calculate the number of sweeps given the epoch time
		int sweeps=Math.round(x.length/SPEC_N);
		double[] winData=new double[SPEC_N];
		Complex[] tmpFFT=new Complex[SPEC_N];
		double[][] Pxx = new double[2][SPEC_N/2];
		double tmpPxx;
		double SpectrumResolution = Fs/SPEC_N;
		double scaleFactor=2.0/Pxx[0].length;
		//Break FFT averaging into SPEC_N segments for averaging
		//Calculate spectrum, variation based on
		//http://www.mathworks.com/support/tech-notes/1700/1702.html

		//Perform windowing and running average on the power spectrum
		//averaging is done by filling a buffer (windData) of size SPECN_N at offset i*SPEC_N
		//until the end of the data.
		for (int i=0; i < sweeps; i++){
			if(i*SPEC_N+SPEC_N > x.length)
				break;
			for (int k=0;k<SPEC_N;k++){
				winData[k]= ((double)x[i*SPEC_N + k])*SpectralWindows.hamming(k,SPEC_N);
			}
			tmpFFT=FFT.transform(winData,TransformType.FORWARD);
			for(int k=0;k<Pxx[0].length;k++){
				tmpPxx = tmpFFT[k].abs()*scaleFactor;
				Pxx[1][k]=( (i*Pxx[1][k]) + tmpPxx )/((double) i+1); //averaging
			}
		}
		
		//Get frequency index and convert values to db SPL
		for(int i=0;i<Pxx[0].length;i++){
			Pxx[0][i]=SpectrumResolution*i;
			Pxx[1][i]=AcousticConverter.getFrequencyInputLevel(Pxx[1][i]);
		}

		return Pxx;
	}

	/*
	public static double[] getDPOAEResults(Bundle audioBundle, RecordThreadRunnable rRun){
		short[] audioBuffer = audioBundle.getShortArray("samples");
		double[] Pxx=SignalProcessing.getSpectrum(audioBuffer);
		//TODO: set noise estimation to mid-frequency between desired stimulus and F1 for now
		//double Fnoise=rRun
		}
	 */

}
