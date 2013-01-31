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
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

//TODO: Translate all message (informMiddle informEnd) Strings into the Resource folder for easy changes to other languages
public class PackageDataThreadRunnable implements Runnable
{
	private File outFile =null;
	private AudioPulseXMLData xmlData;


	public synchronized void run()
	{
		// Write file to disk
		// Define file name here because inform finish is adding the Uri to the message bundle 
		// TODO Does the bundling need to happen post SHortfile.writeFile	
		if(xmlData == null){
		} else {
			try {
				outFile=packageData();
			} catch (AudioPulseXmlException e) {
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
	}

	private synchronized File packageData() throws AudioPulseXmlException, IOException {

		//Create XML File
		String xmlFileName="AP_MetaData_"+ new Date().toString()+".xml";
		xmlFileName= "/" +  xmlFileName.replace(" ","-").replace(":", "-");
		xmlData.writeXMLFile(xmlFileName);
		//TODO: Generate list of files to compress and send to zip
		List<String> fileList= new ArrayList<String>();
		fileList.add(xmlFileName);

		//Zip all the files
		String zipFileName="AP_Encounder_"+ new Date().toString()+".zip";
		zipFileName= "/" +zipFileName.replace(" ","-").replace(":", "-");
		OutputStream os= new FileOutputStream(zipFileName);
		ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(os));
		//create byte buffer
		byte[] buffer = new byte[1024];
		try {
			for (String fileName : fileList) {
				//File file=new File(fileName);
				ZipEntry entry = new ZipEntry(fileName);
				zos.putNextEntry(entry);
				zos.write(buffer);
				zos.closeEntry();
			}
		} finally {
			zos.close();
		}
		return new File(zipFileName);
	}

	public File getOutFile(){
		return outFile;
	}

}
