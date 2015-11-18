package uk.ac.aber.clg11.tsp;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JFrame;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.io.plots.DrawableWriterFactory;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;

public class LinePlotTest extends JFrame {
	
	public LinePlotTest() throws FileNotFoundException, IOException {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);

        DataTable data = new DataTable(Double.class, Double.class);
        
        for (double x = -5.0; x <= 5.0; x+=0.25) {
            double y = 5.0*Math.sin(x);
            data.add(x, y);
        }
        
//        DataTable data = new DataTable(Integer.class, Integer.class);
//        
//        for (int i = 0; i < 100; i++) {
//        	
//        	data.add(ThreadLocalRandom.current().nextInt(100), ThreadLocalRandom.current().nextInt());
//        	
//        }
        
        //data.add(50, 50);
        //data.add(0, 75);
        //data.add(100, 25);
        //data.add(100, 25);
        
     // Style the plot
//        double insetsTop = 20.0,
//               insetsLeft = 20.0,
//               insetsBottom = 20.0,
//               insetsRight = 20.0;
        
        
        
        XYPlot plot = new XYPlot(data);
        
//        plot.setInsets(new Insets2D.Double(
//                insetsTop, insetsLeft, insetsBottom, insetsRight));
        
        getContentPane().add(new InteractivePanel(plot));
        LineRenderer lines = new DefaultLineRenderer2D();
        plot.setLineRenderer(data, lines);
        Color color = new Color(0.0f, 0.3f, 1.0f);
        plot.getPointRenderer(data).setColor(color);
        plot.setPointRenderer(data, null);
        plot.getLineRenderer(data);
        
        plot.getAxisRenderer(XYPlot.AXIS_X).setIntersection(-Double.MAX_VALUE);
        plot.getAxisRenderer(XYPlot.AXIS_X).setShapeVisible(false);
        plot.getAxisRenderer(XYPlot.AXIS_X).setTicksVisible(false);
        
       
        plot.getAxisRenderer(XYPlot.AXIS_Y).setIntersection(-Double.MAX_VALUE);
        plot.getAxisRenderer(XYPlot.AXIS_Y).setShapeVisible(false);
        plot.getAxisRenderer(XYPlot.AXIS_Y).setTicksVisible(false);
        
        try {
			DrawableWriterFactory.getInstance().get("image/jpeg").write(plot,
				    new FileOutputStream("test.jpg"), 500, 200);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        LinePlotTest frame = new LinePlotTest();
        frame.setVisible(true);
    }
    
}
