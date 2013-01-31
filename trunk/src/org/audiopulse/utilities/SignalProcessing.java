package org.audiopulse.utilities;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;


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

	public static double[] getSpectrum(short[] x){
		FastFourierTransformer FFT = new FastFourierTransformer(DftNormalization.STANDARD);
		//Calculate the size of averaged waveform
		//based on the maximum desired frequency for FFT analysis
		int N=x.length;
		int SPEC_N=(int) Math.pow(2,Math.floor(Math.log((int) N)/Math.log(2)));
		double[] winData=new double[SPEC_N];
		Complex[] tmpFFT=new Complex[SPEC_N];
		double[] Pxx = new double[SPEC_N/2];
		double tmpPxx;
		double refMax=0;
		//Break FFT averaging into SPEC_N segments for averaging
		//Calculate spectrum, variation based on
		//http://www.mathworks.com/support/tech-notes/1700/1702.html

		//Perform windowing and averaging on the power spectrum
		for (int i=0; i < N; i++){
			if(i*SPEC_N+SPEC_N > N)
				break;
			for (int k=0;k<SPEC_N;k++){
				winData[k]= (double) x[i*SPEC_N + k]*SpectralWindows.hamming(k,SPEC_N);
			}
			tmpFFT=FFT.transform(winData,TransformType.FORWARD);
			for(int k=0;k<(SPEC_N/2);k++){
				tmpPxx = tmpFFT[k].abs()/(double)SPEC_N;
				tmpPxx*=tmpPxx; //Not accurate for the DC & Nyquist, but we are not using it!
				Pxx[k]=( (i*Pxx[k]) + tmpPxx )/((double) i+1); //averaging
				refMax= (Pxx[k] > refMax) ? Pxx[k]:refMax;
			}
		}

		//Convert to dB
		for(int i=0;i<Pxx.length;i++){
			Pxx[i]=10*Math.log10(Pxx[i]/refMax);
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
