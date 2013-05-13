/**
 * 
 */
package org.audiopulse.graphics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *  Collection of static utility methods for plotting charts.
 */
public final class PlotUtils {

	private PlotUtils(){}
	
	/**
	 * Write a chart to a PNG image file.
	 *  
	 * @param outfile name of file to write to
	 * @param width Image width
	 * @param height Image height
	 * @throws IOException If an error occurs opening or writing to the file.
	 */
	public static File renderToFile(JFreeChart chart, String outfile, int width,
			int height) throws IOException 
	{
		File out = new File(outfile);
		ChartUtilities.saveChartAsPNG(out,chart, width, height);
		return out;
	}
	
	/**
	 * Convenience wrapper to write a 400x400 PNG image file.
	 * @param chart
	 * @param outfile
	 * @throws IOException If an error occurs opening or writing to the file.
	 */
	public static File renderToFile(JFreeChart chart, String outfile) throws IOException {
		return renderToFile(chart, outfile, 400, 400);
	}
	

	/**
	 * Writes a rendered chart to a PNG image file. 
	 * 
	 * @param renderer An object which holds the data to render and renders it.
	 * @param outfile name of file to write to
	 * @param width Image width
	 * @param height Image height
	 * @throws IOException If an error occurs opening or writing to the file.
	 */
	public static <T extends ChartRenderer> File renderToFile(T renderer, 
			String outfile, int width, int height) throws IOException 
	{
		return renderToFile(renderer.render(), outfile, width, height);
	}
	
	/**
	 * Convenience wrapper to write a rendered 400x400 PNG image file.
	 * 
	 * @param renderer An object which holds the data to render and renders it.
	 * @param height Image height
	 * @throws IOException If an error occurs opening or writing to the file.
	 */
	public static <T extends ChartRenderer> File renderToFile(T renderer, 
			String outfile) throws IOException 
	{
		return renderToFile(renderer.render(), outfile, 400, 400);
	}
	
	/**
	 * Creates and returns a chart object. The resulting chart will also be 
	 * written as a 400x400 pix image in PNG format if the file name is 
	 * specified.
	 * 
	 * @param renderer Object holding the chart data that will render the chart.
	 * @param outFileName The output file
	 * @return A new chart object 
	 */
	public static <T extends ChartRenderer> JFreeChart createDemoChart(T renderer,
			String outFileName)
	{
		return PlotUtils.createDemoChart(renderer, outFileName, 400, 400);
	}
	
	/**
	 * Creates and returns a chart object. The resulting chart will also be 
	 * written as an image in PNG format if the file name is specified.
	 * 
	 * @param renderer Object holding the chart data that will render the chart.
	 * @param outFileName The output file
	 * @param width The output image file width
	 * @param height The output image file height
	 * @return A new chart object 
	 */
	public static <T extends ChartRenderer> JFreeChart createDemoChart(T renderer, 
			String outFileName, int width, int height)
	{
		JFreeChart chart = renderer.render();		
		if(outFileName != null){
			try {
				renderToFile(chart, outFileName, width, height);
			} catch (IOException e) {
				System.err.println("Could not print image to file: " + outFileName);
				e.printStackTrace();
			}
			System.out.println("Saved image to file: " + outFileName);
		}
		return chart;
	}

	/**
	 * Transform an array of ArrayLists into an XYDataset
	 * @param data The raw data
	 * @return
	 */
	public static XYDataset createDataset(ArrayList[] data) {
		//XYDataset result = DatasetUtilities.sampleFunction2D(new X2(),
		//        -4.0, 4.0, 40, "f(x)");
		XYSeriesCollection result = new XYSeriesCollection();
		XYSeries series = new XYSeries(1);
		//Insert data into plotting series 
		for(int n=0;n<data[1].size();n++){
			series.add(Double.valueOf((String) data[0].get(n)),
					Double.valueOf((String) data[1].get(n)));
		}
		result.addSeries(series);
		return result;
	}
	
	public static XYDataset createDataset(double[] data) {
		//XYDataset result = DatasetUtilities.sampleFunction2D(new X2(),
		//        -4.0, 4.0, 40, "f(x)");
		XYSeriesCollection result = new XYSeriesCollection();
		XYSeries series = new XYSeries(1);
		//Insert data into plotting series 
		for(int n=0;n<data.length;n++){
			series.add(n,data[n]);
		}
		result.addSeries(series);
		return result;
	}
	
	/**
	 * Takes an array of short values and returns them as an XYDataset
	 * 
	 * @param data
	 * @return
	 */
	public static XYDataset createDataset(short[] data) {
		//XYDataset result = DatasetUtilities.sampleFunction2D(new X2(),
		//        -4.0, 4.0, 40, "f(x)");
		XYSeriesCollection result = new XYSeriesCollection();
		XYSeries series = new XYSeries(1);
		//Insert data into plotting series 
		for(int n=0;n<data.length;n++){
			series.add(n,data[n]);
		}
		result.addSeries(series);
		return result;
	}
}
