
package org.audiopulse.graphics;

import java.awt.BasicStroke;

import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Layer;
import org.jfree.ui.LengthAdjustmentType;

public class SpectralPlot implements ChartRenderer{

	private static double Fresrange=95;//Range for which to plot the blue region in which 
									   //we expect a response
	
	private final String title;
	private final double Fres;
	private final XYDataset dataset;
	
	public SpectralPlot(String title, XYDataset data, double Fres) {
		this.title = title;
		this.dataset = data;
		this.Fres = Fres;
		
	}
	
	/**
	 * Renders this object's data as a chart.
	 */
	public JFreeChart render() {
		// TODO Auto-generated method stub
		return createChart(dataset,title,Fres);
	}  
	
	/**
	 * Returns a new SpectralPlot object with and converts any data into an 
	 * XYDataset.
	 * 
	 * @param title
	 * @param XFFT
	 * @param Fres
	 * @return
	 */
	public static SpectralPlot fromData(String title,double[][] XFFT,double Fres){
		return new SpectralPlot(title, createDataset(XFFT), Fres);
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
	
	public static XYDataset createDataset(double[][] XFFT) {
		XYSeriesCollection result = new XYSeriesCollection();
		XYSeries series = new XYSeries(1);
		//Calculate spectrum 
		for(int n=0;n<XFFT[0].length;n++){
			series.add(XFFT[0][n], XFFT[1][n]);
			//System.out.println("f= " + XFFT[0][n]);
		}
		result.addSeries(series);
		return result;
	}
  
}
