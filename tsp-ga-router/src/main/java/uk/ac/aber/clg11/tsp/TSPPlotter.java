package uk.ac.aber.clg11.tsp;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JFrame;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.io.plots.DrawableWriterFactory;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;

/**
 * Plots TSP status and results to graphs that are subsequently displayed on screen, or exported to file.
 * @author connorgoddard
 *
 */
public class TSPPlotter extends JFrame {
	
	private XYPlot plot;
	private DataTable data;
	private InteractivePanel panel;
	
	private int xAxisScaleRange = -1;
	private int yAxisScaleRange = -1;
	
	private boolean xAxisHide = false;
	private boolean yAxisHide = false;
	
	LineRenderer lines = new DefaultLineRenderer2D();
	
	/**
	 * 'Builder'-pattern implementation class providing a standardized mechanism by which new 'TSPPlotter' instances
	 * can be created with the appropriate optional parameters.
	 * @author connorgoddard
	 *
	 */
	public static class TSPPlotterBuilder {

		private int xAxisScaleRange = -1;
		private int yAxisScaleRange = -1;
		
		private boolean xAxisHide = false;
		private boolean yAxisHide = false;
		
		public TSPPlotterBuilder() {
			
		}
	
		public TSPPlotter buildTSPPlotter() {
			
			return new TSPPlotter(xAxisScaleRange, yAxisScaleRange, xAxisHide, yAxisHide);
			
		}
		
		public TSPPlotterBuilder setAxisHideSettings(boolean xAxisHide, boolean yAxisHide) {
			
			this.xAxisHide = xAxisHide;
			this.yAxisHide = yAxisHide;
			
			// Return the current instance to allow for "method chaining".
			return this;
			
		}
		
		public TSPPlotterBuilder setAxisMaxRangeSettings(int xAxisMax, int yAxisMax) {
			
			this.xAxisScaleRange = xAxisMax;
			this.yAxisScaleRange = yAxisMax;
			
			// Return the current instance to allow for "method chaining".
			return this;
			
		}
		
	}

	private TSPPlotter() {
		
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);

        data = new DataTable(Integer.class, Integer.class);
        
        this.plot = new XYPlot(data);
      
        this.panel = new InteractivePanel(this.plot);
        
        getContentPane().add(this.panel);
        
        setupPlot();
        
	}
	
	private TSPPlotter(int xAxisScaleRange, int yAxisScaleRange, boolean xAxisHide, boolean yAxisHide) {
		
		this();
		
		this.xAxisScaleRange = xAxisScaleRange;
		this.yAxisScaleRange = yAxisScaleRange;
		this.xAxisHide = xAxisHide;
		this.yAxisHide = yAxisHide;
		
		this.setupPlot();
		
	}
	
	public void setupPlot() {
		
		plot.setInsets(new Insets2D.Double(20, 60, 60, 40));
		
		if (xAxisHide) {
			
			plot.getAxisRenderer(XYPlot.AXIS_X).setShapeVisible(false);
	        plot.getAxisRenderer(XYPlot.AXIS_X).setTicksVisible(false);
	        
		} else {
			
			if (xAxisScaleRange != -1) {
				plot.getAxis(XYPlot.AXIS_X).setRange(0, this.xAxisScaleRange);
		        plot.getAxis(XYPlot.AXIS_X).setAutoscaled(false);
			}
			
			//plot.getAxisRenderer(XYPlot.AXIS_X).setTickSpacing(10.0);
			
			plot.getAxisRenderer(XYPlot.AXIS_X).setIntersection(-Double.MAX_VALUE);
			
		}
		
		if (yAxisHide) {
			
			plot.getAxisRenderer(XYPlot.AXIS_Y).setShapeVisible(false);
	        plot.getAxisRenderer(XYPlot.AXIS_Y).setTicksVisible(false);
	        
		} else {
			
			if (yAxisScaleRange != -1) {
				plot.getAxis(XYPlot.AXIS_Y).setRange(0, this.yAxisScaleRange);
		        plot.getAxis(XYPlot.AXIS_Y).setAutoscaled(false);
			}
			
			
			//plot.getAxisRenderer(XYPlot.AXIS_Y).setTickSpacing(10.0);
			
			plot.getAxisRenderer(XYPlot.AXIS_Y).setIntersection(-Double.MAX_VALUE);
			
		}
		
	}
	
	public void updatePlot() {
		this.updateData();
		this.redrawPlot();
	}
	
	public void redrawPlot() {
		
		getContentPane().removeAll();
		
	    plot.clear();
	    plot.add(data);
	    
	    Color color = new Color(255, 0, 0);
	    
	    plot.getPointRenderer(data).setColor(color);
	    plot.setLineRenderer(data, lines);
	    
	    getContentPane().add(panel);
	    getContentPane().revalidate();
	    getContentPane().repaint();
	    
	    if (!this.isVisible()) {
			this.setVisible(true);
		}
	  
	}
        
    public void updateData() {
    	
      this.data = new DataTable(Integer.class, Integer.class);
    	
      for (int i = 0; i < 10; i++) {
    	  data.add(ThreadLocalRandom.current().nextInt(100), ThreadLocalRandom.current().nextInt(100));
      }

    }
    
}
