
    package org.audiopulse.graphics;

import javax.swing.JPanel;

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

    public class Plot extends ApplicationFrame {

    	static String title;
        public Plot(String title, double[] timeSeries) {
            super(title);
            this.title=title;
            JPanel chartPanel = createDemoPanel(timeSeries);
            chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
            setContentPane(chartPanel);
        }
       
      
        public Plot(String title, double[] x, double[] y) {
			super(title);
            this.title=title;
            JPanel chartPanel = createDemoPanel(x,y);
            chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
            setContentPane(chartPanel);
		}


		private static JFreeChart createChart(XYDataset dataset) {
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
       
        private static XYDataset createDataset(double[] timeSeries) {
        	XYSeriesCollection result = new XYSeriesCollection();
        	XYSeries series = new XYSeries(1);
    		for(int n=0;n<timeSeries.length-1;n++){
    			//System.out.println("n=" +n + " data[n]=" + data[n]);
    			if(timeSeries[n] !=0)
    			series.add(n,timeSeries[n]);
    			//series.add(n,data[n]);
    		}
        	result.addSeries(series);
            return result;
        }
        
        private static XYDataset createDataset(double[] x,double[] y) {
        	XYSeriesCollection result = new XYSeriesCollection();
        	XYSeries series = new XYSeries(1);
    		for(int n=0;n<x.length-1;n++){
    			if(x[n] !=0)
    			series.add(x[n],y[n]);
    		}
        	result.addSeries(series);
            return result;
        }

        /**
         * Creates a panel for the demo (used by SuperDemo.java).
         *
         * @return A panel.
         */
        private static JPanel createDemoPanel(double[] timeSeries) {
            JFreeChart chart = createChart(createDataset(timeSeries));
            return new ChartPanel(chart);
        }
        private static JPanel createDemoPanel(double[] x, double[] y) {
            JFreeChart chart = createChart(createDataset(x,y));
            return new ChartPanel(chart);
        }
       


    }
