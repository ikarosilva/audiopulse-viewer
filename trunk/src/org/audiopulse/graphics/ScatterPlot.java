package org.audiopulse.graphics;

import java.awt.Color;
import java.awt.Shape;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.util.ShapeUtilities;

@SuppressWarnings("serial")
public class ScatterPlot extends ApplicationFrame {

	String title;
	
	public ScatterPlot(String title, File datafile, int x, int y) {
		
		super(title);
		this.title=title;
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series1 = new XYSeries("Series 1");
		Double number = null;
		try {
			//use buffering, reading one line at a time
			//FileReader always assumes default encoding is OK!
			BufferedReader input =  new BufferedReader(new FileReader(datafile));
			try {
				String line = null; //not declared within while loop
				int row = 0;
				while (( line = input.readLine()) != null){
					StringTokenizer st = new StringTokenizer(line,",");
					int col = 0;
					double[] xy = new double[2];
					while (st.hasMoreTokens())
					{
						try{
						number = Double.valueOf(st.nextToken());
						} catch (NumberFormatException e){
							col++;
							continue;
						}				
						if(col==x)
						{
							xy[0]=number;
						}else if (col == y) {
							xy[1]=number;
						}
						col++;
					}
					series1.add(xy[0],xy[1]);
					row++;
				}
			}
			finally {
				input.close();
			}
		}
		catch (IOException ex){
			ex.printStackTrace();
		}
		dataset.addSeries(series1);
		
		JFreeChart chart = ChartFactory.createScatterPlot(
				title, // title
				"X", "Y", // axis labels
				dataset, // dataset
				PlotOrientation.VERTICAL,
				true, // legend? yes
				true, // tooltips? yes
				false // URLs? no
		);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);
		
		float thickness=(float) 0.25;
		float length=3;
		Shape cross = ShapeUtilities.createDiagonalCross(length, thickness);
		XYItemRenderer renderer = chart.getXYPlot().getRenderer();
		renderer.setSeriesPaint(0, Color.blue);
		renderer.setSeriesShape(0, cross);
		
		this.pack();
		RefineryUtilities.centerFrameOnScreen(this);
		this.setVisible(true);
	}

	public ScatterPlot(String title, double[] x,double[] y) {

		super(title);
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series1 = new XYSeries("Series 1");
		for(int i=0;i<x.length;i++)
			series1.add(x[i],y[i]);
		
		dataset.addSeries(series1);
		JFreeChart chart = ChartFactory.createScatterPlot(
				title, // title
				"X", "Y", // axis labels
				dataset, // dataset
				PlotOrientation.VERTICAL,
				true, // legend? yes
				true, // tooltips? yes
				false // URLs? no
		);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);
		
		float thickness=(float) 0.25;
		float length=3;
		Shape cross = ShapeUtilities.createDiagonalCross(length, thickness);
		XYItemRenderer renderer = chart.getXYPlot().getRenderer();
		renderer.setSeriesPaint(0, Color.blue);
		renderer.setSeriesShape(0, cross);
		
		this.pack();
		RefineryUtilities.centerFrameOnScreen(this);
		this.setVisible(true);
	}

	public ScatterPlot(String title, double[] x1,double[] y1, double[] x2, double[] y2) {

		super(title);
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series1 = new XYSeries("Series 1");
		XYSeries series2 = new XYSeries("Series 2");
		for(int i=0;i<x1.length;i++){
			series1.add(x1[i],y1[i]);
		}
		for(int i=0;i<x2.length;i++){
			series2.add(x2[i],y2[i]);
		}
		
		dataset.addSeries(series1);
		dataset.addSeries(series2);
		JFreeChart chart = ChartFactory.createScatterPlot(
				title, // title
				"X", "Y", // axis labels
				dataset, // dataset
				PlotOrientation.VERTICAL,
				true, // legend? yes
				true, // tooltips? yes
				false // URLs? no
		);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);
		
		float thickness=(float) 0.25;
		float length=3;
		Shape cross = ShapeUtilities.createDiagonalCross(length, thickness);
		XYItemRenderer renderer = chart.getXYPlot().getRenderer();
		renderer.setSeriesPaint(0, Color.blue);
		renderer.setSeriesPaint(1, Color.red);
		renderer.setSeriesShape(0, cross);
		
		
		this.pack();
		RefineryUtilities.centerFrameOnScreen(this);
		this.setVisible(true);
	}

	public ScatterPlot(String title, double[] x1,double[] y1, double[] x2, double[] y2,
			double[] x3, double[] y3) {

		super(title);
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series1 = new XYSeries("Series 1");
		XYSeries series2 = new XYSeries("Series 2");
		XYSeries series3 = new XYSeries("Series 3");
		for(int i=0;i<x1.length;i++){
			series1.add(x1[i],y1[i]);
		}
		for(int i=0;i<x2.length;i++){
			series2.add(x2[i],y2[i]);
		}
		for(int i=0;i<x3.length;i++){
			series3.add(x3[i],y3[i]);
		}
		
		dataset.addSeries(series1);
		dataset.addSeries(series2);
		dataset.addSeries(series3);
		
		JFreeChart chart = ChartFactory.createScatterPlot(
				title, // title
				"X", "Y", // axis labels
				dataset, // dataset
				PlotOrientation.VERTICAL,
				true, // legend? yes
				true, // tooltips? yes
				false // URLs? no
		);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);
		
		float thickness=(float) 0.25;
		float length=3;
		Shape cross = ShapeUtilities.createDiagonalCross(length, thickness);
		Shape triangle = ShapeUtilities.createUpTriangle(length);
		XYItemRenderer renderer = chart.getXYPlot().getRenderer();
		renderer.setSeriesPaint(0, Color.blue);
		renderer.setSeriesPaint(1, Color.red);
		renderer.setSeriesPaint(2, Color.green);
		renderer.setSeriesShape(0, cross);
		renderer.setSeriesShape(2, triangle);
		
		
		this.pack();
		RefineryUtilities.centerFrameOnScreen(this);
		this.setVisible(true);
	}

	
	public ScatterPlot(String title, double[] x,double[] y, double[] z) {

		super(title);
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series1 = new XYSeries("Series 1");
		XYSeries series2 = new XYSeries("Series 2");
		for(int i=0;i<x.length;i++){
			series1.add(x[i],y[i]);
			series2.add(x[i],z[i]);
		}
		
		dataset.addSeries(series1);
		dataset.addSeries(series2);
		JFreeChart chart = ChartFactory.createScatterPlot(
				title, // title
				"X", "Y", // axis labels
				dataset, // dataset
				PlotOrientation.VERTICAL,
				true, // legend? yes
				true, // tooltips? yes
				false // URLs? no
		);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);
		
		float thickness=(float) 0.25;
		float length=3;
		Shape cross = ShapeUtilities.createDiagonalCross(length, thickness);
		XYItemRenderer renderer = chart.getXYPlot().getRenderer();
		renderer.setSeriesPaint(0, Color.blue);
		renderer.setSeriesPaint(1, Color.red);
		renderer.setSeriesShape(0, cross);
		
		
		this.pack();
		RefineryUtilities.centerFrameOnScreen(this);
		this.setVisible(true);
	}
	
	public static void main(String[] args) {
		if (args.length !=3) {
			System.err.println("Usage: java File2ScatterPlot filename ind1 ind2");
			System.exit(0);
		}

		File datafile= new File(args[0]);
		int x= Integer.valueOf(args[1]);
		int y= Integer.valueOf(args[2]);
		ScatterPlot demo = new ScatterPlot(datafile.toString(), datafile, x, y);
	}

}