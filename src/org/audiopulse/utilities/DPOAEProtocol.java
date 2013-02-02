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
 * Original Author:  Ikaro Silva
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * Check: http://code.google.com/p/audiopulse/source/list
 */ 

package org.audiopulse.utilities;

public class DPOAEProtocol{

	public double[] f;
	public double[] A; 
	public double expectedResponse; //Expected response frequency
	public String protocol;
	
	public static enum protocolBioLogic {
		//Generate a specific set of DPOAE stimuli based on the same parameters from 
		//that of the Bio-Logic Otoacoustic emissions Report (2012)
		
		//Amplitudes are in dB SPL!!
		F8k(6516,7969,64.8,54.9),
		F6k(4594,5625,64.8,56.6),
		F4k(3281,3984,64.8,55.6),
		F3k(2297,2813,64.6,55.1),
		F2k(1641,2016,64.4,53.4);

		private double[] f=new double[2];
		private double[] A=new double[2];	//Amplitudes are in dB
		private double expectedResponse; //Expected response frequency
		private String protocol="BioLogic";
		private int[] stimPresentation=new int[2]; //Right and Left order in which to present stimulus
		protocolBioLogic(double f1,double f2, double A1, double A2) {
			f[0]=f1;
			f[1]=f2;
			A[0]=(double)(Short.MAX_VALUE)*.1; 
			A[1]=(double)(Short.MAX_VALUE)*.1;
			expectedResponse=2*f[0]-f[1];
			stimPresentation[0]=0;
			stimPresentation[1]=1;
		}
	}

	public static enum protocolHOAE{
		//Generate a specific set of DPOAE stimuli based on the same parameters from 
		//"Handbook of Otocoustic Emissions" J. Hall, Singular Publishing Group Copyright 2000
		// Screening parameters in page 136.
		F8k(8000),
		F6k(6000),
		F4k(4000),
		F3k(3000),
		F2k(2000);
		private double[] f=new double[2];
		private double[] A=new double[2]; //Amplitudes are in dB
		private int[] stimPresentation=new int[2]; //Right and Left order in which to present stimulus
		private double expectedResponse; //Expected response frequency
		private String protocol="HOAE";
		protocolHOAE(double f2) {
			f[0]=f2/1.2;
			f[1]=f2;
			A[0]=(double)(Short.MAX_VALUE)*.1;//65; 
			A[1]=(double)(Short.MAX_VALUE)*.1;//65;
			expectedResponse=2*f[0]-f[1];
			stimPresentation[0]=0;
			stimPresentation[1]=1;
		}
	}
}
