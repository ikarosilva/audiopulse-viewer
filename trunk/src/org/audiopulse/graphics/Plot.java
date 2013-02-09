/* ===========================================================
 * WFDB Java : Interface to WFDB Applications.
 *              
 * ===========================================================
 *
 * (C) Copyright 2012, by Ikaro Silva
 *
 * Project Info:
 *    Code: http://code.google.com/p/wfdb-java/
 *    WFDB: http://www.physionet.org/physiotools/wfdb.shtml
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
 *
 *
 * Original Author:  Ikaro Silva
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * Check: http://code.google.com/p/wfdb-java/list
 */ 


/* Example on how to plot a ECG signal
 * 
 * This example requires the package jfreechar-1.0.14.jar
 * 
 * Available for download at in the download area of the wfdb-java Google project:
 * 
 * http://code.google.com/p/wfdb-java/downloads/detail?name=jfreechart-1.0.14.zip&can=2&q=
 * 
 * To install external package in Eclipse from a working "wfdb-java" project:
 * 
 * 1. Right click in the "src" folder under the wfdb-java project in Eclipse
 * 
 * 2. Select:
 * 	   Build Path -> Configure Build Path
 * 
 * 3. Go to:
 * 		"Libraries" Tab -> Add External Jars
 * 
 * select the jfreechar-1.0.14.jar and hit "Ok".
 * 
 * 4. Go to the "Order and Export" Tab, check "jfreechar-1.0.14.jar" and hit "Ok". 
 */

package org.audiopulse.graphics;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;

/**
 *
 */
public class Plot {

	private final String title;
	private final String timeLabel; 
	private final String amplitudeLabel;
	private final XYDataset dataset;
	
	/**
	 * Constructs a new plot object with title, labels, and data
	 * 
	 * @param title
	 * @param timeLabel
	 * @param amplitudeLabel
	 * @param data
	 */
	public Plot(String title, String timeLabel, 
			String amplitudeLabel, XYDataset data) {
		this.title = title;
		this.timeLabel = timeLabel;
		this.amplitudeLabel = amplitudeLabel;
		this.dataset = data;
	}

	/**
	 * Renders this object's data as a chart.
	 */
	public JFreeChart render(){
		return Plot.createChart(dataset, title, timeLabel, amplitudeLabel);
	}
	
	private static JFreeChart createChart(XYDataset dataset, String title,
			String timeLabel, String amplitudeLabel) {
		// create the chart...
		JFreeChart chart = ChartFactory.createXYLineChart(
				title,       // chart title
				timeLabel,                      // x axis label
				amplitudeLabel,                      // y axis label
				dataset,                  // data
				PlotOrientation.VERTICAL, 
				true,                     // include legend
				true,                     // tooltips
				false                     // urls
				);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.getDomainAxis().setLowerMargin(0.0);
		plot.getDomainAxis().setUpperMargin(0.0);
		return chart;
	}

	/**
	 * Returns a new Plot object with and converts any data into an XYDataset.
	 * 
	 * @param title the plot title
	 * @param timeLabel 
	 * @param amplitudeLabel
	 * @param data data that will be plotted
	 * @return
	 */
	public static Plot fromData(String title, String timeLabel,
			String amplitudeLabel, ArrayList[] data) {
		XYDataset dataset = PlotUtils.createDataset(data);
		return new Plot(amplitudeLabel, amplitudeLabel, amplitudeLabel, dataset);
	}

	/**
	 * Returns a new Plot object with and converts any raw data into an XYDataset.
	 * 
	 * @param title the plot title
	 * @param timeLabel 
	 * @param amplitudeLabel
	 * @param data data that will be plotted
	 * @return
	 */
	public static Plot fromData(String title, String timeLabel,
			String amplitudeLabel, short[] data) {
		XYDataset dataset = PlotUtils.createDataset(data);
		return new Plot(amplitudeLabel, amplitudeLabel, amplitudeLabel, dataset);
	}
}
