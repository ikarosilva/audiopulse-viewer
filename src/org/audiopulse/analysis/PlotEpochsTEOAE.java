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

package org.audiopulse.analysis;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class PlotEpochsTEOAE extends ApplicationFrame {

	public PlotEpochsTEOAE(String title,double[] audioData,
			List<Integer> peakInd, double Fs) {
		super(title);
		JPanel chartPanel = createDemoPanel(audioData,peakInd,Fs);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);
		this.pack();
		RefineryUtilities.centerFrameOnScreen(this);
		this.setVisible(true);
	}


	private static JFreeChart createChart(XYDataset dataset) {
		// create the chart...
		JFreeChart chart = ChartFactory.createXYLineChart(
				"TEOAE Epochs ",       // chart title
				"Time (seconds)",                      // x axis label
				"Amplitude",                      // y axis label
				dataset,                  // data
				PlotOrientation.VERTICAL, 
				true,                     // include legend
				true,                     // tooltips
				false                     // urls
				);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.getDomainAxis().setLowerMargin(0.0);
		plot.getDomainAxis().setUpperMargin(0.0);
		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setSeriesLinesVisible(1,false);
			renderer.setSeriesShapesVisible(1,true);
			renderer.setSeriesShapesFilled(1,true);
		}
		return chart;
	}

	public static XYDataset createDataset(double[] audioData,
			List<Integer> peakInd, double Fs) {
		XYSeriesCollection result = new XYSeriesCollection();
		XYSeries signal = new XYSeries(1);
		XYSeries epoch = new XYSeries(2);

		//Insert data into plotting series 
		for(int n=0;n<audioData.length;n++){
			signal.add((double) n/Fs,audioData[n]);
		}
		result.addSeries(signal);
		if(peakInd != null){
			for(int n=0;n<peakInd.size();n++) {
				epoch.add((double) peakInd.get(n)/Fs,
						audioData[peakInd.get(n)]);
			}
			result.addSeries(epoch);
		}
		return result;

	}

	/**
	 * Creates a panel for the demo (used by SuperDemo.java).
	 *
	 * @return A panel.
	 */
	public static JPanel createDemoPanel(double[] audioData,
			List<Integer> peakInd, double Fs) {
		JFreeChart chart = createChart(createDataset(audioData,peakInd, Fs));
		return new ChartPanel(chart);
	}

	/*
	public static void main(String[] args) {
		PlotEpochsTEOAE demo = new PlotEpochsTEOAE(
				"TEOAE Epochs",double[] audioData,
				List<Integer> peakInd);
	}
	 */
}
