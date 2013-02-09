package org.audiopulse.ui;

import javax.swing.JPanel;

import org.audiopulse.graphics.ChartRenderer;
import org.audiopulse.graphics.PlotAudiogram;
import org.audiopulse.graphics.PlotUtils;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;

public class PlotAudiogramFrame extends ApplicationFrame implements ChartRenderer{

	private final PlotAudiogram plot;
	
	public PlotAudiogramFrame(String title, double[] DPOAEData, 
			double[] noiseFloor, double[] f1Data, double[] f2Data, String outFileName) {
		super(title);
		plot = new PlotAudiogram(outFileName, f2Data, f2Data, f2Data, f2Data);
		JPanel chartPanel = new ChartPanel(render());
		//JPanel chartPanel = createDemoPanel(title, DPOAEData,noiseFloor, f1Data, f2Data, outFileName);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);
	}
	
	public static JPanel createDemoPanel(String title, double[] DPOAEData, 
			double[] noiseFloor, double[] f1Data, double[] f2Data, String outFileName) {
		PlotAudiogram plot = new PlotAudiogram(title, DPOAEData, noiseFloor, f1Data, f2Data);
		JFreeChart chart = PlotUtils.createDemoChart(plot, outFileName);
		return new ChartPanel(chart);
	}

	public JFreeChart render() {
		return plot.render();
	}
}
