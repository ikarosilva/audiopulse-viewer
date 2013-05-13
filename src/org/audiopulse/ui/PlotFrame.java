package org.audiopulse.ui;

import java.util.ArrayList;

import javax.swing.JPanel;

import org.audiopulse.graphics.ChartRenderer;
import org.audiopulse.graphics.Plot;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class PlotFrame  extends ApplicationFrame  implements ChartRenderer{

	private final Plot plot;
	
	public PlotFrame(String title, String timeLabel, 
			String amplitudeLabel, ArrayList[] data) {
		super(title);
		plot = Plot.fromData(amplitudeLabel, amplitudeLabel, amplitudeLabel, data);
		JPanel chartPanel = new ChartPanel(render());
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);
	}
	
	public PlotFrame(String title, String timeLabel,
			String amplitudeLabel, short[] rawData) {
		super(title);
		plot = Plot.fromData(amplitudeLabel, amplitudeLabel, amplitudeLabel, rawData);
		JPanel chartPanel = new ChartPanel(render());
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);
	}
	
	public PlotFrame(String title, String timeLabel,
			String amplitudeLabel, double[] rawData) {
		super(title);
		plot = Plot.fromData(amplitudeLabel, amplitudeLabel, amplitudeLabel, rawData);
		JPanel chartPanel = new ChartPanel(render());
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);
	}
	
	public void showPlot(){
		this.pack();
		RefineryUtilities.centerFrameOnScreen(this);
		this.setVisible(true);
	}
	
	public JFreeChart render() {
		return plot.render();
	}

	/**
	 * Creates a panel for the demo (used by SuperDemo.java).
	 *
	 * @return A panel.
	 */
	public static JPanel createDemoPanel(String title,String timeLabel, 
			String amplitudeLabel,ArrayList[] data) {
		Plot plot = Plot.fromData(amplitudeLabel, amplitudeLabel, amplitudeLabel, data);
		JFreeChart chart = plot.render();
		return new ChartPanel(chart);
	}

	public static JPanel createDemoPanel(String title,String timeLabel, 
			String amplitudeLabel,short[] data) {
		Plot plot = Plot.fromData(amplitudeLabel, amplitudeLabel, amplitudeLabel, data);
		JFreeChart chart = plot.render();
		return new ChartPanel(chart);
	}

}
