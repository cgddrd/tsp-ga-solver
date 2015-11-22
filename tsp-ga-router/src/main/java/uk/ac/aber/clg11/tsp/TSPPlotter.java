package uk.ac.aber.clg11.tsp;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
	
	private boolean randomPointColour = false;
	private boolean randomLineColour = false;
	
	private Color lineColour = new Color(0, 0, 0);
	private Color pointColour = new Color(255, 0, 0);
	
	private boolean linesHide = false;
	private boolean pointsHide = false;
	
	LineRenderer lines = new DefaultLineRenderer2D();
	
	private TSPPlotter() {
		
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);

        data = new DataTable(Integer.class, Integer.class);
        
        this.plot = new XYPlot(data);
      
        this.panel = new InteractivePanel(this.plot);
        
        getContentPane().add(this.panel);
        
        setupPlot();
        
	}
	
	private TSPPlotter(int xAxisScaleRange, 
						int yAxisScaleRange, 
						boolean xAxisHide, 
						boolean yAxisHide,
						boolean pointsHide,
						boolean linesHide,
						boolean randomPointColour, 
						boolean randomLineColour, 
						Color pointColour,
						Color lineColour) {
		
		this();
		
		this.xAxisScaleRange = xAxisScaleRange;
		this.yAxisScaleRange = yAxisScaleRange;
		this.xAxisHide = xAxisHide;
		this.yAxisHide = yAxisHide;
		this.randomLineColour = randomLineColour;
		this.randomPointColour = randomPointColour;
		this.lineColour = lineColour;
		this.pointColour = pointColour;
		this.pointsHide = pointsHide;
		this.linesHide = linesHide;
		
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
	
	public void generatePlot() {
		this.redrawPlot(this.pointsHide, this.linesHide);
	}
	
	public void generatePlot(boolean pointsHide, boolean linesHide) {
		this.redrawPlot(pointsHide, linesHide);
	}

	private void redrawPlot(boolean hidePoints, boolean hideLines) {
		
		getContentPane().removeAll();
		
	    plot.clear();
	    plot.add(data);
	    
	    if (hidePoints) {
	    	plot.setPointRenderer(data, null);
	    } else {
	    	Color pointColour = this.randomPointColour ? generateRandomColour() : this.pointColour;
	    	plot.getPointRenderer(data).setColor(pointColour);
	    }
	    
	    if (hideLines) {
	    	plot.setLineRenderer(data, null);
	    } else {
	    	Color lineColour = this.randomLineColour ? generateRandomColour() : this.lineColour;
	    	plot.setLineRenderer(data, lines);
	    	plot.getLineRenderer(data).setColor(lineColour);
	    }
	    	
	    
	    getContentPane().add(panel);
	    getContentPane().revalidate();
	    getContentPane().repaint();
	    
	    if (!this.isVisible()) {
			this.setVisible(true);
		}
	  
	}
	
	public Color generateRandomColour() {
		
		Random rand = new Random();
		
		float r = rand.nextFloat();
		float g = rand.nextFloat();
		float b = rand.nextFloat();
		
		return new Color(r, g, b);
		
	}
	public <T extends Number, K extends Number> void updateData2(ArrayList<T> xData, ArrayList<K> yData) {
	
		this.data = new DataTable(Double.class, Double.class);
		
		for (int i = 0; i < xData.size(); i++) {		
			this.data.add(xData.get(i).doubleValue(), yData.get(i).doubleValue());
		}
		
	}
        
    public void updateData(ArrayList<TSPLocation> locations) {
    	
      this.data = new DataTable(Integer.class, Integer.class);
      
      for (TSPLocation loc : locations) {
    	  
    	  data.add(loc.xCoord, loc.yCoord);
    	  
      }
    	
      //for (int i = 0; i < 10; i++) {
    //	  data.add(ThreadLocalRandom.current().nextInt(100), ThreadLocalRandom.current().nextInt(100));
     // }

    }
    
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
		
		private boolean randomPointColour = false;
		private boolean randomLineColour = false;
		
		private Color lineColour = new Color(0, 0, 0);
		private Color pointColour = new Color(255, 0, 0);
		
		private boolean linesHide = false;
		private boolean pointsHide = false;
		
		public TSPPlotterBuilder() {
			
		}
	
		public TSPPlotter buildTSPPlotter() {
			
			return new TSPPlotter(xAxisScaleRange, yAxisScaleRange, xAxisHide, yAxisHide, pointsHide, linesHide, randomPointColour, randomLineColour, pointColour, lineColour);
			
		}
		
		public TSPPlotterBuilder setRandomPointColourActive(boolean randomPointColour) {
			
			this.randomPointColour = randomPointColour;
			return this;
			
		}
		
		public TSPPlotterBuilder setRandomLineColourActive(boolean randomLineColour) {
			
			this.randomLineColour = randomLineColour;
			return this;
			
		}
		
		public TSPPlotterBuilder setPointsHide(boolean pointsHide) {
			
			this.pointsHide = pointsHide;
			return this;
			
		}
		
		public TSPPlotterBuilder setLinesHide(boolean linesHide) {
			
			this.linesHide = linesHide;
			return this;
			
		}
		
		public TSPPlotterBuilder setPointColour(Color pointColour) {
			
			this.pointColour = pointColour;
			return this;
			
		}
		
		public TSPPlotterBuilder setLineColour(Color lineColour) {
			
			this.lineColour = lineColour;
			return this;
			
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
    
}
