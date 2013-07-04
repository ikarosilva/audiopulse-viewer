package org.audiopulse.hardware;

import org.audiopulse.utilities.SignalProcessing;


public class AcousticConverter {
	//TODO: put this into a resource file
	private static final double ER10CGain=0.0;			//Gain setting for the ER10C in dB
	private static final double VPerDU_output = 1.0;	//V for 1 amplitude at output
	private static final double VPerDU_input = 0.020;	//V for 1 amplitude at input
	private static final double SPL1V = 72.0;			//dB SPL for 1V rms electrical signal
	private static final double SPL1uV = 0-ER10CGain;	//dB SPL for 1uV rms microphone electrical signal
	private static final double SQRT2=Math.sqrt(2.0);
	private static final String TAG="AcousticConverter";
	private static final double MAX_MIC_SPL=20*Math.log10(VPerDU_input*1e6);  //Max SPL recorded by the mic
	private static final double MAX_SPEAKER_SPL=SPL1V;  //Max SPL played by the phone	
	public AcousticConverter() {
		//TODO: determine that mic & headphone jack are connected to something

	}

	//Convert energy value(s) for output (in DU) to expected SPL
	static public double convertOutputToSPL(double rms) {
		return 20*Math.log10(rms*SQRT2) + MAX_SPEAKER_SPL;
	}
	
	@Deprecated //use getOutputLevel
	static public double[] convertOutputToSPL(double[] x) {
		double[] spl = new double[x.length];
		for (int ii=0; ii<x.length;ii++) {
			spl[ii] = convertOutputToSPL(x[ii]);
		}
		return spl;
	}

	//Convert energy value(s) from input (in DU) to SPL
	@Deprecated //use getInputLevel instead
	static public double convertInputToSPL(double x) {
		return getInputLevel(x);
	}
	static public double[] convertInputToSPL(double[] x) {
		double[] spl = new double[x.length];
		for(int ii=0;ii<x.length;ii++) {
			spl[ii] = convertInputToSPL(x[ii]);
		}
		return spl;
	}

	static public double getInputLevel(short[] x) {
		double[] audio = new double[x.length];
		for(int i=0;i<x.length;i++)
			audio[i]= x[i]/((double)(Short.MAX_VALUE)) ;

		return getInputLevel(audio); 
	}

	// get total signal energy output level in dB SPL
	static public double getOutputLevel(double[] x) {
		return getOutputLevel(x,0,x.length-1);
	}
	static public double getOutputLevel(double[] x, int fromSample, int toSample) {
		//compute sum of squares
		double rms = 0;
		int N = toSample-fromSample+1;
		for (int n=fromSample; n<=toSample; n++) {
			rms +=x[n]*x[n];
		}
		rms  = Math.sqrt(rms /((double) N) );	//convert to RMS
		if (rms ==0)							//avoid log(0), return min value instead
			return Double.NEGATIVE_INFINITY;
		
		return 20*Math.log10(rms*SQRT2) + MAX_SPEAKER_SPL;
	}

	//set output signal level in dB SPL
	static public double[] setOutputLevel(double[] x, double spl) {
		double a = getOutputLevel(x);
		double gain = spl - a;
		for (int n=0; n<x.length; n++) {
			x[n] *= Math.pow(10, gain/20.0);
		}
		return x;
	}

	//compute input signal level in dB SPL
	static public double getInputLevel(double[] x) {
		return getInputLevel(x,0,x.length-1);
	}
	static public double getInputLevel(double[] x, int fromSample, int toSample) {
		//compute sum of squares
		double r = 0;
		int N = toSample-fromSample+1;
		for (int n=fromSample; n<=toSample; n++) {
			r+=x[n]*x[n];
		}
		r = Math.sqrt(r/((double)N));		//convert to rms DU
		return getInputLevel(r);
	}
	public static double getInputLevel(double rms) {
		//Normalize wrt peak to peak sine wave rms
		double out=Double.NEGATIVE_INFINITY; //Avoid log(0)
		if(rms !=0)
			out=20*Math.log10(rms*SQRT2) + MAX_MIC_SPL;
		return 	out;//SPL = dBuV + SPL1uV
	}

	public static double getFrequencyInputLevel(double Amp) {
		//Given a frequency peak-to-peak amplitude, Amp, calculates the equivalent 
		//dB SPL level given setup configuration
		double out=Double.NEGATIVE_INFINITY;//Avoid log(0)
		if(Amp !=0)
			out=20*Math.log10(Amp) + MAX_MIC_SPL;
		return 	out;//SPL = dBuV + SPL1uV
	}

	//return dB offset: dB SPL = 10*log10(A^2) + dBOffset
	static public double getDBOffset_output() {
		return 20*Math.log10(VPerDU_output) + SPL1V;
	}
	//return dB offset: dB SPL = 10*log10(A^2) + dBOffset
	static public double getDBOffset_input() {
		return 20*Math.log10(VPerDU_input*1e6) + SPL1uV;
	}


	//convert output vector to expect input vector for flat, 1 acoustic response 
	static public double[] outputToInput(double[] output) {
		int N = output.length;
		double[] input = new double[N];
		for (int n=0; n<N; n++) {
			input[n] = output[n] * (VPerDU_output / VPerDU_input )* Math.pow(10, (SPL1V-(SPL1uV+120))/20.0);
		}
		return input;
	}

	public static double getOutputLevel(short[] rawData) {
		double[] data=new double[rawData.length];
		for(int i=0;i<rawData.length;i++)
			data[i]=(double) rawData[i]/Short.MAX_VALUE;
		return getOutputLevel(data);
	}
}
