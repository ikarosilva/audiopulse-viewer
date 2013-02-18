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
		
	@depecated public static XYSeries generateChirp(double Fs, double f1, double f2, double tf)
	{
		// Applying a broad-band, chirp signal in each of the two ER-2 earphones sequentially. 
		// The chirp is equi-ampliude sine waves having a specified phase distribution. 
		// Choose f2-f1 between fs/2 and fs/4.
		
		//double Fs, f1, f2, tf;   // sample rate and the frequency band
		double sl;			 // the duration of the chirp
		int size;			 // size of the time array
		double[] t, half_t;  // the arrays of time
		double[] f_first, f_second, f;			 // the array of freuquencies that will be used for the chirp
		XYSeries y;
		
		// Sample rate and frequency bands		
		//Fs = 44100; // In Hertz 
		//f1 = 500;
		//f2 = 800; 
		//tf = 1; // In seconds
		
		// Set time arrays		
		t = new double[size];
		f = new double[size];
		f_first = new double[size/2];
		f_second = new double[size/2];
		half_t = new double[size/2];
		
		// Set the time ranges for the entire signal, and the half signal
		t = [0:1/Fs:(tf-1/Fs)];
		half_t = [0:1/Fs:(tf/2-1/Fs)];

		// Get the frequncy range slope
		sl = f2-f1/2;

		// Calculation of frequencies for the first and second part of the chirp
		f1_1 = f1*semi_t+(sl*semi_t);
		f2_1 = f1_1(end)+f2*semi_t-(sl*semi_t); 
		f = [f1_1 f2_1];

		// Now create the sound to be played
		y = new XYSeries(1.33*cos(2*pi*f.*t));		
	}
	
	@deprecated public static boolean isFit()
	{
		// Play sound
		
		// Record response
		
		// If the fit is good, the frequency characteristics of the ER-2 earphones are
		//  nearly flat for the low frequency region (below 2 kHz).
		if(dminF > tolerance)
		{
			double  actF=XFFT[0][(int)result[0]];
			System.err.println("Results are innacurate because frequency tolerance has been exceeded. Desired F= "
					+ desF +" closest F= " + actF);
		}
			
		// The sound pressure level was decreased below 1 kHz when a probe was loosely fit.
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

	public static double[][] getSpectrum(short[] x, double Fs, int SPEC_N){
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
		double REFMAX=(double) Short.MAX_VALUE; //Normalizing value

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
				winData[k]= ((double)x[i*SPEC_N + k]/REFMAX)*SpectralWindows.hamming(k,SPEC_N);
			}
			tmpFFT=FFT.transform(winData,TransformType.FORWARD);
			for(int k=0;k<(SPEC_N/2);k++){
				tmpPxx = tmpFFT[k].abs()/(double)SPEC_N;
				tmpPxx*=tmpPxx; //Not accurate for the DC & Nyquist, but we are not using it!
				Pxx[1][k]=( (i*Pxx[1][k]) + tmpPxx )/((double) i+1); //averaging
			}
		}

		//Convert to dB
		for(int i=0;i<Pxx[0].length;i++){
			Pxx[0][i]=SpectrumResolution*i;
			Pxx[1][i]=10*Math.log10(Pxx[1][i]);
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
