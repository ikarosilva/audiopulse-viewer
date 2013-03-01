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

package org.audiopulse.io;
import java.io.*;



public class ShortFile {

	public static void writeFile(File outFile, short[] samples) throws IOException{
		//Write Short file to disk
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		fos = new FileOutputStream(outFile);
		out = new ObjectOutputStream(fos);
		out.writeObject(samples);
		out.flush();
		out.close();
	}

	public static short[] readFile(String file) throws IOException, ClassNotFoundException{
		//Read Short file from disk	
		FileInputStream nFile = new FileInputStream(file);
		ObjectInputStream in = new ObjectInputStream(nFile);
		short[] data=null;
		data = (short[]) in.readObject();
		in.close();
		return data;
	}


}