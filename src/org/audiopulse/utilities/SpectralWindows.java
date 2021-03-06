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
 * SpectralWindows.java
 * -----------------
 * (C) Copyright 2012, by SanaAudioPulse
 *
 * Original Author:  Ikaro Silva
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * Check: http://code.google.com/p/audiopulse/source/list
 */ 

package org.audiopulse.utilities;

public class SpectralWindows {

	public static double hamming(int n, int N){
		// assert that the absolute value is >= 0
		assert ( n <= N ) : "Window sample: " + n + " is beyond expected window range: "+ N;
		double out=(0.54 - 0.46*Math.cos(2*Math.PI*n/((double) (N-1) )));
		return 2*out;	//2 factor so that amplitude matches for pure sine wave as well
	}
	
	public static double hanning(int n, int N){
		// assert that the absolute value is >= 0
		assert ( n <= N ) : "Window sample: " + n + " is beyond expected window range: "+ N;
		double out=0.5 - 0.5*Math.cos(2*Math.PI*n/((double) (N-1) ));
		return 2*out;	//2 factor so that amplitude matches for pure sine wave as well
	}
	
	public static double rect(int n, int N){
		// assert that the absolute value is >= 0
		assert ( n <= N ) : "Window sample: " + n + " is beyond expected window range: "+ N;
		return 1;	
	}
}
