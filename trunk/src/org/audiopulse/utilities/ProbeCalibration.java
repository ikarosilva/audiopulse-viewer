// Here we will have the three subfunctions which deal with ear fitting, 
//and more might come later...

package org.audiopulse.analysis;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.audiopulse.graphics.Plot;
import org.audiopulse.graphics.PlotAudiogram;
import org.audiopulse.graphics.SpectralPlot;
import org.audiopulse.io.PackageDataThreadRunnable;
import org.audiopulse.io.ShortFile;
import org.audiopulse.utilities.SignalProcessing;
import org.jfree.ui.RefineryUtilities;

class ProbeCalibration extends Exception 
{
	private static final long serialVersionUID = 1L;
	public ProbeCalibrationException() {
	}
	public ProbeCalibrationException(String msg) {
		super(msg);
	}
}


