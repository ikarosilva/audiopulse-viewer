
package org.audiopulse.graphics;

import java.awt.BasicStroke;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;

import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
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

	private static double Fresrange=50;//Range for which to plot the blue region in which 
									   //we expect a response
	
	public SpectralPlot(String title,double[] XFFT, double Fs,double Fres, String outFileName) {
		super(title);
		JPanel chartPanel = createDemoPanel(XFFT,Fs, title,Fres, outFileName);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);
	}

	private static JFreeChart createChart(XYDataset dataset, String title, double Fres) {
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
		if(Fres != 0){
			Marker marker_V = new IntervalMarker(Fres-Fresrange, Fres+Fresrange);
			marker_V.setLabelOffsetType(LengthAdjustmentType.EXPAND);
			marker_V.setPaint(ChartColor.LIGHT_BLUE);
			plot.addDomainMarker(marker_V, Layer.BACKGROUND);
			Marker marker_V_Start = new ValueMarker(Fres-Fresrange, ChartColor.LIGHT_BLUE, new BasicStroke(2.0F));
			Marker marker_V_End = new ValueMarker(Fres+Fresrange, ChartColor.LIGHT_BLUE, new BasicStroke(2.0F));
			plot.addDomainMarker(marker_V_Start, Layer.BACKGROUND);
			plot.addDomainMarker(marker_V_End, Layer.BACKGROUND);
		}
		
		return chart;
	}

	public static void setFresrange(double x){
		//Sets the range around Fres for which to plot the blue region
		//where we expect a response (in Hz)
		SpectralPlot.Fresrange=x;
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
	public static JPanel createDemoPanel(double[] XFFT, double Fs, String title,double Fres, String outFileName) {
		JFreeChart chart = createChart(createDataset(XFFT,Fs), title,Fres);
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
}
