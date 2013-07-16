package org.audiopulse.utilities;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.audiopulse.analysis.PlotEpochsTEOAE;
import org.audiopulse.hardware.AcousticConverter;


public class SignalProcessing {

	public static double rms(short[] x){
		double r = 0;
		double N = (double) x.length;
		//Calculate mean squared
		for(int i=0;i<x.length;i++){
			r += (x[i]*x[i]);
		}
		return Math.sqrt(r/N);
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

	public static double max(double[] x){
		double max=Double.MIN_VALUE;
		for(double z: x)
			max=(z>max) ? z:max;
		return max;
	}

	public static short max(short[] x){
		short max=Short.MIN_VALUE;
		for(short z: x)
			max=(z>max) ? z:max;
		return max;
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
			y[i]=(double) x[i]/((double) Short.MAX_VALUE);
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
		double[] weight=new double[sweeps];
		double weightSum=0;
		double[] winData=new double[SPEC_N];
		Complex[] tmpFFT=new Complex[SPEC_N];
		double[][] Axx = new double[2][SPEC_N/2];
		double SpectrumResolution = Fs/SPEC_N;
		double scaleFactor=1.0/((double) Axx[0].length);
		//Break FFT averaging into SPEC_N segments for averaging

		//Calcute weights on each trial based on variance
		for (int i=0; i < sweeps; i++){
			if(( i*SPEC_N+SPEC_N ) > x.length)
				break;
			for (int k=0;k<SPEC_N;k++){
				weight[i]= ((double) (x[i*SPEC_N + k]*x[i*SPEC_N + k]));
			}
		}
		for (int i=0; i < sweeps; i++){
			if(weight[i]==0){
				weight[i]=0;
			}else{
				weight[i]=SPEC_N/weight[i];
			}
			weightSum+=weight[i];
		}
		//Normalize the weights
		for(int i=0;i<sweeps;i++){
			weight[i]=1/weightSum;
		}
		//Perform windowing and running average on the Amplitude spectrum
		//averaging is done by filling a buffer (windData) of size SPECN_N at offset i*SPEC_N
		//until the end of the data.
		for (int i=0; i < sweeps; i++){
			if(( i*SPEC_N+SPEC_N ) > x.length)
				break;
			for (int k=0;k<SPEC_N;k++){
				winData[k]= weight[i]*x[i*SPEC_N + k]*SpectralWindows.hanning(k,SPEC_N);
			}

			tmpFFT=FFT.transform(winData,TransformType.FORWARD);
			for(int k=0;k<Axx[0].length;k++){
				Axx[1][k]=( (i*Axx[1][k]) + tmpFFT[k].abs()*scaleFactor )/
						((double) i+1.0); //averaging
			}

		}

		//Get frequency index and convert values to db SPL
		for(int i=0;i<Axx[0].length;i++){
			Axx[0][i]=SpectrumResolution*i;
			Axx[1][i]=AcousticConverter.getFrequencyInputLevel(Axx[1][i]);
		}

		return Axx;
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
