package org.audiopulse.ui;

import java.io.IOException;

import javax.swing.JPanel;

import org.audiopulse.graphics.ChartRenderer;
import org.audiopulse.graphics.PlotUtils;
import org.audiopulse.graphics.SpectralPlot;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class SpectralPlotFrame extends ApplicationFrame implements ChartRenderer{

	private SpectralPlot plot;
	
	public SpectralPlotFrame(String title,double[][] XFFT,double Fres) {
		super(title);
		plot = SpectralPlot.fromData(title, XFFT, Fres);
		JPanel chartPanel = new ChartPanel(render());
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);
		this.pack();
		RefineryUtilities.centerFrameOnScreen(this);
		this.setVisible(true);
	}
	
	public SpectralPlotFrame(String title,double[][] XFFT,double Fres, String outFileName) {
		super(title);
		plot = SpectralPlot.fromData(title, XFFT, Fres);
		JPanel chartPanel = new ChartPanel(render());
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);
	}
	
	public JFreeChart render() {
		return plot.render();
	}
	
	public static JPanel createDemoPanel(double[][] XFFT, String title,double Fres, String outFileName) {
		SpectralPlot plot = SpectralPlot.fromData(title, XFFT, Fres);
		JFreeChart chart = PlotUtils.createDemoChart(plot, outFileName);
		return new ChartPanel(chart);
	}  
	
}
