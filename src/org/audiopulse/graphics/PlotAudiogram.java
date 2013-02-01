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
 * PlotSpectralActivity.java 
 * based on DeviationRendererDemo02Activity.java
 * from afreechartdemo
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

package org.audiopulse.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;
//import org.jfree.graphics.SolidColor;
import org.jfree.ui.ApplicationFrame;


/**
 * DeviationRendererDemo02View
 */
public class PlotAudiogram extends ApplicationFrame {

	public PlotAudiogram(String title, double[] DPOAEData, 
			double[] noiseFloor, double[] f1Data, double[] f2Data, String outFileName) {
		super(title);

		JPanel chartPanel = createDemoPanel(title, DPOAEData,noiseFloor, f1Data, f2Data, outFileName);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);
	}

	/* From the android client
	public PlotAudiogram(Context context, String title, double[] DPOAEData, 
			double[] noiseFloor, double[] f1Data, double[] f2Data) {
		super(context);
		final AFreeChart chart = createChart2();
		setChart(chart);
	}
	 */

	public static JPanel createDemoPanel(String title, double[] DPOAEData, 
			double[] noiseFloor, double[] f1Data, double[] f2Data, String outFileName) {
		JFreeChart chart = createChart2(title, DPOAEData,noiseFloor, f1Data, f2Data, outFileName);
		if(outFileName != null){
			try {
				ChartUtilities.saveChartAsPNG(new File(outFileName),chart, 400, 400);
			} catch (IOException e) {
				System.err.println("Could not print image to file: " + outFileName);
				e.printStackTrace();
			}
			System.out.println("Saved image to file: " + outFileName);
		}
		return new ChartPanel(chart);
	}

	private static YIntervalSeriesCollection createDataset2() {

		//TODO: These are normative values. Maybe be best to move these values
		//into an resource folder where they can be easily modified in the future.

		//TODO: Add this dataset to the graph
		YIntervalSeries normativeRange = new YIntervalSeries("Normative Range");
		int[] NUB={-10, -5, -5, -5, -4};
		int[] NLB={-15, -10, -13, -15, -13};

		normativeRange.add(7.206, -7,NLB[0], NUB[0]);
		normativeRange.add(5.083, 13.1,NLB[1], NUB[1]);
		normativeRange.add(3.616, 17.9,NLB[2], NUB[2]);
		normativeRange.add(2.542, 11.5,NLB[3], NUB[3]);
		normativeRange.add(1.818, 17.1,NLB[4], NUB[4]);

		YIntervalSeriesCollection dataset = new YIntervalSeriesCollection();
		dataset.addSeries(normativeRange);
		return dataset;
	}


	private  static XYSeriesCollection createDataset(double[] DPOAEData, 
			double[] noiseFloor, double[] f1Data, double[] f2Data) 
	{
		XYSeries series1 = new XYSeries("DPOAE");
		XYSeries series2 = new XYSeries("Noise Floor");
		XYSeries series3 = new XYSeries("F1");
		XYSeries series4 = new XYSeries("F2");

		//NOTE: We assume data is being send in an interleaved array where
		// odd samples are X-axis and even samples go in the Y-axis
		for(int i=0;i<(DPOAEData.length/2);i++){
			series1.add(DPOAEData[i*2], DPOAEData[i*2+1]);
			series2.add(noiseFloor[i*2], noiseFloor[i*2+1]);
			series3.add(f1Data[i*2], f1Data[i*2+1]);
			series4.add(f2Data[i*2], f2Data[i*2+1]);

		}

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series1);
		dataset.addSeries(series2);
		dataset.addSeries(series3);
		dataset.addSeries(series4);
		return dataset;
	}

	public static JFreeChart createChart2(String title, double[] DPOAEData, 
			double[] noiseFloor, double[] f1Data, double[] f2Data, String outFileName) {
		XYDataset data = createDataset(DPOAEData,noiseFloor,f1Data,f2Data);
		JFreeChart chart = ChartFactory.createXYLineChart(
				title, // chart title
				"Frequency (kHz)", // x axis label
				"Level (dB SPL)", // y axis label
				data, // data
				PlotOrientation.VERTICAL,
				true, // include legend
				true, // tooltips
				false // urls
				);

		XYPlot plot = (XYPlot) chart.getPlot();

		DeviationRenderer renderer = new DeviationRenderer(true, false);
		renderer.setSeriesStroke(0, new BasicStroke(3.0F));
		renderer.setSeriesStroke(1, new BasicStroke(3.0F));
		renderer.setSeriesPaint(0, Color.blue);
		renderer.setSeriesFillPaint(0, Color.BLUE);
		renderer.setSeriesPaint(1, Color.green);
		renderer.setSeriesFillPaint(1, Color.GREEN);
		renderer.setSeriesPaint(2, Color.red);
		renderer.setSeriesFillPaint(2, Color.RED);
		renderer.setSeriesPaint(3, Color.DARK_GRAY);
		renderer.setSeriesFillPaint(3, Color.DARK_GRAY);
		plot.setRenderer(renderer);

		return chart;
	}
}


