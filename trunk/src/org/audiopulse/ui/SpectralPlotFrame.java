package org.audiopulse.ui;

import java.io.IOException;

import javax.swing.JPanel;

import org.audiopulse.analysis.DPOAEAnalysis;
import org.audiopulse.graphics.ChartRenderer;
import org.audiopulse.graphics.PlotUtils;
import org.audiopulse.graphics.SpectralPlot;
import org.audiopulse.utilities.SignalProcessing;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class SpectralPlotFrame extends ApplicationFrame implements ChartRenderer{

	private SpectralPlot plot;
	private double[][] XFFT;
	
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
	
	public double[][] getSpectrum(){
		return XFFT;
	}
	
	public double[][] getResponse(){
		return XFFT;
	}
	
	public SpectralPlotFrame(String title,short[] x,double Fs, int M) {
		super(title);
		double[][]XFFT=DPOAEAnalysis.getSpectrum(x,Fs,M);
		this.XFFT=XFFT;
        double Fres=(int) 2*M/Fs;
		plot = SpectralPlot.fromData(title, XFFT, Fres);
		JPanel chartPanel = new ChartPanel(render());
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);
        this.pack();
        RefineryUtilities.centerFrameOnScreen(this);
        this.setVisible(true);
	}
	
	public SpectralPlotFrame(String title,double[] x,double Fs, int M) {
		super(title);
		double[][]XFFT=SignalProcessing.getSpectrum(x,Fs,M);
        double Fres=(int) 2*M/Fs;
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
