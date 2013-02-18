// Here we will have the three subfunctions which deal with ear fitting, 
//and more might come later...

package org.audiopulse.analysis;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

class ProbeCalibrationException extends Exception {
	private static final long serialVersionUID = 1L;
	public ProbeCalibrationException() {
	}
	public ProbeCalibrationException(String msg) {
		super(msg);
	}
}


public class ProbeCalibration 
{

	// Applying a broad-band, chirp signal in each of the two ER-2 earphones sequentially. 
	// The chirp is equi-ampliude sine waves having a specified phase distribution. 
	// Choose f2-f1 between fs/2 and fs/4.
	@depecated public static XYSeries generateChirp(double Fs, double f1, double f2, double tf)
	{		
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
	
	// Check the fit according to the REFERENCE: Distortion-product and click-evoked otoacoustic emissions of normally-hearing adults
	// J Smurzynski, DO Kim - Hearing research, 1992	
	@deprecated public static boolean isFit()
	{
		// Play sound
		
		// Record response
		
		// Do the FFT
		
		//Check the response
		
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
}
