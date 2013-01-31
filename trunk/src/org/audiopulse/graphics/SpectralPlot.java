
package org.audiopulse.graphics;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.JPanel;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class SpectralPlot extends ApplicationFrame {

	public SpectralPlot(String title,double[] XFFT, double Fs) {
		super(title);
		JPanel chartPanel = createDemoPanel(XFFT,Fs, title);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);
	}

	private static JFreeChart createChart(XYDataset dataset, String title) {
		// create the chart...
		JFreeChart chart = ChartFactory.createXYLineChart(
				title,       // chart title
				"X",                      // x axis label
				"Y",                      // y axis label
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

	public static XYDataset createDataset(double[] XFFT, double Fs) {
		XYSeriesCollection result = new XYSeriesCollection();
		XYSeries series = new XYSeries(1);
		//Calculate spectrum 
		double fres= (double) Fs/XFFT.length;
		for(int n=0;n<(XFFT.length/2);n++){
			series.add(((double) 0) + n*fres, XFFT[n]);
		}
		result.addSeries(series);
		return result;
	}
	public static JPanel createDemoPanel(double[] XFFT, double Fs, String title) {
		JFreeChart chart = createChart(createDataset(XFFT,Fs), title);
		return new ChartPanel(chart);
	}      
	
	/*
	public static void main(String[] args) {
		SpectralPlot demo = new SpectralPlot(
				"Test");
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
	}
	*/
}
