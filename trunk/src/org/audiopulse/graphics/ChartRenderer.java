package org.audiopulse.graphics;

import org.jfree.chart.JFreeChart;

/**
 * Interface which declares a class will create and return a JFreeChart object.
 */
public interface ChartRenderer {
	
	/**
	 * Renders this object's data as a chart.
	 */
	public JFreeChart render();

	
}
