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

public class TSPGraph extends JFrame {
	
	private XYPlot plot;
	private DataTable data;
	private InteractivePanel panel;
	
	LineRenderer lines = new DefaultLineRenderer2D();
	
	public TSPGraph() throws FileNotFoundException, IOException {
		
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);
        setTitle("TSP - GA Results");

        data = new DataTable(Integer.class, Integer.class);
        
        this.plot = new XYPlot(data);
      
        this.panel = new InteractivePanel(this.plot);
        
        getContentPane().add(this.panel);
        
        setupPlot();
        
//        double insetsTop = 20.0,
//        	       insetsLeft = 60.0,
//        	       insetsBottom = 60.0,
//        	       insetsRight = 40.0;
//        
//        	plot.setInsets(new Insets2D.Double(
//        	    insetsTop, insetsLeft, insetsBottom, insetsRight));

        //plot.setLineRenderer(data, lines);
        //Color color = new Color(0.0f, 0.3f, 1.0f);
        //plot.getPointRenderer(data).setColor(color);
        //plot.setPointRenderer(data, null);
        //plot.getLineRenderer(data);
        
//        plot.getAxis(XYPlot.AXIS_X).setRange(0, 100);
//        plot.getAxis(XYPlot.AXIS_X).setAutoscaled(false);
//        plot.getAxisRenderer(XYPlot.AXIS_X).setIntersection(-Double.MAX_VALUE);
//        plot.getAxisRenderer(XYPlot.AXIS_X).setTickSpacing(10.0);
        
        //plot.getAxisRenderer(XYPlot.AXIS_X).setShapeVisible(false);
        //plot.getAxisRenderer(XYPlot.AXIS_X).setTicksVisible(false);
        
//        plot.getAxis(XYPlot.AXIS_Y).setRange(0, 100);
//        plot.getAxis(XYPlot.AXIS_Y).setAutoscaled(false);
//        plot.getAxisRenderer(XYPlot.AXIS_Y).setIntersection(-Double.MAX_VALUE);
//        plot.getAxisRenderer(XYPlot.AXIS_Y).setTickSpacing(10.0);
        
        //plot.getAxisRenderer(XYPlot.AXIS_Y).setShapeVisible(false);
        //plot.getAxisRenderer(XYPlot.AXIS_Y).setTicksVisible(false);
        
//        try {
//			DrawableWriterFactory.getInstance().get("image/jpeg").write(plot,
//				    new FileOutputStream("test.jpg"), 500, 200);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        
	}
	
	public void setupPlot() {
		
		double insetsTop = 20.0,
     	       insetsLeft = 60.0,
     	       insetsBottom = 60.0,
     	       insetsRight = 40.0;
		
		plot.setInsets(new Insets2D.Double(insetsTop, insetsLeft, insetsBottom, insetsRight));
		
		plot.getAxis(XYPlot.AXIS_X).setRange(0, 100);
        plot.getAxis(XYPlot.AXIS_X).setAutoscaled(false);
        plot.getAxisRenderer(XYPlot.AXIS_X).setIntersection(-Double.MAX_VALUE);
        plot.getAxisRenderer(XYPlot.AXIS_X).setTickSpacing(10.0);
        
        plot.getAxis(XYPlot.AXIS_Y).setRange(0, 100);
        plot.getAxis(XYPlot.AXIS_Y).setAutoscaled(false);
        plot.getAxisRenderer(XYPlot.AXIS_Y).setIntersection(-Double.MAX_VALUE);
        plot.getAxisRenderer(XYPlot.AXIS_Y).setTickSpacing(10.0);
		
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
        
    public static void main(String[] args) throws FileNotFoundException, IOException {
    	
        TSPGraph frame = new TSPGraph();
        
        //Timer timer = new Timer();
        
        frame.updatePlot();
        
//        timer.scheduleAtFixedRate(new TimerTask() {
//        	  @Override
//        	  public void run() {
//        	   //frame.updatePlot();
//        	  }
//        	}, 1*1000, 1*1000);
//        
        
    }
    
}
