
package org.audiopulse.graphics;

import java.awt.BasicStroke;
import java.awt.Paint;

import javax.swing.JPanel;

import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.Layer;
import org.jfree.ui.LengthAdjustmentType;

public class SpectralPlot extends ApplicationFrame {

	public SpectralPlot(String title,double[] XFFT, double Fs,double[] expFreq) {
		super(title);
		JPanel chartPanel = createDemoPanel(XFFT,Fs, title,expFreq);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);
	}

	private static JFreeChart createChart(XYDataset dataset, String title, double[] expectedFrq) {
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
		
		//Plot expected range
		if(expectedFrq[0] != 0){
			Marker marker_V = new IntervalMarker(expectedFrq[0], expectedFrq[1]);
			marker_V.setLabelOffsetType(LengthAdjustmentType.EXPAND);
			marker_V.setPaint(ChartColor.LIGHT_BLUE);
			plot.addDomainMarker(marker_V, Layer.BACKGROUND);
			Marker marker_V_Start = new ValueMarker(expectedFrq[0], ChartColor.LIGHT_BLUE, new BasicStroke(2.0F));
			Marker marker_V_End = new ValueMarker(expectedFrq[1], ChartColor.LIGHT_BLUE, new BasicStroke(2.0F));
			plot.addDomainMarker(marker_V_Start, Layer.BACKGROUND);
			plot.addDomainMarker(marker_V_End, Layer.BACKGROUND);
		}
		
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
	public static JPanel createDemoPanel(double[] XFFT, double Fs, String title,double[] expFreq) {
		JFreeChart chart = createChart(createDataset(XFFT,Fs), title,expFreq);
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
