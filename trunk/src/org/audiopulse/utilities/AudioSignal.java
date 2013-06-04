/* ===========================================================
 * SanaAudioPulse : a free platform for teleaudiology.
 *              
 * ===========================================================
 *
 * (C) Copyright 2012, by Sana AudioPulse
 *
 * Project Info:
 *    SanaAudioPulse: http://code.google.com/p/audiopulse/
 *    Sana: http://sana.mit.edu/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * [Android is a trademark of Google Inc.]
 *
 * -----------------
 * AudioPulseCalibrationActivity.java
 * -----------------
 * (C) Copyright 2012, by SanaAudioPulse
 *
 * Original Author:  Andrew Schwartz
 * Contributor(s):   Ikaro Silva;
 *
 * Changes
 * -------
 * Check: http://code.google.com/p/audiopulse/source/list
 */
package org.audiopulse.utilities;


// Useful functions for converting audio signals between double (for math) and short (for playback)

public class AudioSignal {
	public static final String TAG="AudioSignal";
	
	//convert a mono double vector to shorts
	public static short[] convertMonoToShort(double[] signal) {
		short[] shortVector = new short [signal.length];
		for (int n=0;n<signal.length;n++) {
			shortVector[n] = convertSampleToShort(signal[n]);
		}
		return shortVector;
	}
	//convert stereo double vector to stereo-interleaved shorts
	public static short[] convertStereoToShort(double[][] signal) {
		confirmValidStereoSignal(signal);
		
		short[] left = convertMonoToShort(signal[0]);
		short[] right = convertMonoToShort(signal[1]);
		
		return interleave(left,right);
	}
	//convert mono short vector to double
	public static double[] convertMonoToDouble(short[] signal) {
		double[] doubleVector = new double[signal.length];
		for (int n=0;n<signal.length;n++) {
			doubleVector[n] = convertSampleToDouble(signal[n]);
		}
		return doubleVector;
	}
	//convert stereo-interleaved short vector to double
	public static double[][] convertStereoToDouble(short[] signal) {
		if ((signal.length & 1) == 1)
			throw new IllegalArgumentException("Input stereo-interleaved vector must have an even number of samples");
		
		double[][] doubleVector = new double[2][signal.length/2];
		for (int n=0; n<signal.length; n+=2) {
			doubleVector[0][n/2] = convertSampleToDouble(signal[n]);
			doubleVector[1][n/2] = convertSampleToDouble(signal[n+1]);
		}
		return doubleVector;
	}
	
	//Convert stereo double to mono (via channel mean)
	public static double[] convertToMono(double[][] signal) {
		confirmValidStereoSignal(signal);
		
		int N = signal[0].length;
		double[] monoSignal = new double [N];

		for (int n=0;n<N; n++) {
			monoSignal[n] += 1/2 * (signal[0][n] + signal[1][n]);
		}
		
		return monoSignal;
	}
	//Convert mono double vector to stereo (duplicate)
	public static double[][] convertToStereo(double[] signal) {
		double[][] stereoSignal = new double[2][];
		stereoSignal[0] = signal.clone();
		stereoSignal[1] = signal.clone();
		return stereoSignal;
	}
	//Create stereo signal with given mono signal in left channel, zeros in right
	public static double[][] monoToStereoLeft(double[] monoSignal) {
		double[][] stereoSignal = new double[2][monoSignal.length];
		stereoSignal[0] = monoSignal.clone();
		return stereoSignal;
	}
	//Create stereo signal with given mono signal in left channel, zeros in left
	public static double[][] monoToStereoRight(double[] monoSignal) {
		double[][] stereoSignal = new double[2][monoSignal.length];
		stereoSignal[1] = monoSignal.clone();
		return stereoSignal;
	}
	
	//scale and convert a single double sample to short.
	public static short convertSampleToShort(double sample) {
		if(Math.abs(sample)>1){	
			return (short) (Short.MAX_VALUE * (Math.signum(sample)));
		}else{
			return (short) (Short.MAX_VALUE * (sample));
		}
	}
	//scale and convert a single short sample to double
	public static double convertSampleToDouble(short sample) {
		return ((double)sample) / ((double)Short.MAX_VALUE);
	}
	
	//interleave left and right vectors into stereo interleaved for stereo playback
	public static short[] interleave(short[] left, short[] right) {
		if (left.length != right.length)
			throw new IllegalArgumentException("Cannot interleave vectors of unequal length");
		
		int N = left.length;
		short[] interleaved = new short [2*N];
		for (int n=0; n<N; n++) {
			interleaved[2*n] = left[n];
			interleaved[2*n+1] = right[n];
		}
		return interleaved;
	}
	
	//throw IllegalArgumentException if signal is not a valid stereo signal
	private static void confirmValidStereoSignal (double[][] signal) {
		if (signal.length != 2 || signal[0].length != signal[1].length)
			throw new IllegalArgumentException("Stereo data must be 2 vectors of equal length");
	}
	
}
