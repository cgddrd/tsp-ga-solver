package uk.ac.aber.clg11.tsp;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
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

import org.apache.commons.io.FilenameUtils;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.data.statistics.Statistics;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.io.plots.DrawableWriter;
import de.erichseifert.gral.io.plots.DrawableWriterFactory;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.plots.points.DefaultPointRenderer2D;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.Insets2D;
import uk.ac.aber.clg11.tsp.exception.TSPPlotterException;

/**
 * Plots TSP status and results to graphs that are subsequently displayed on
 * screen, or exported to file.
 * 
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
	
	private String xAxisTitle = null;
	private String yAxisTitle = null;
	
	private boolean randomPointColour = false;
	private boolean randomLineColour = false;

	private Color lineColour = new Color(0, 0, 0);
	private Color pointColour = new Color(255, 0, 0);

	private boolean linesHide = false;
	private boolean pointsHide = false;
	private boolean legendHide = true;
	
	private String[] seriesLabels;
	
	private String plotTitle = null;
	
	private boolean displayGUI = true;

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

	private TSPPlotter(boolean displayGUI, int xAxisScaleRange, int yAxisScaleRange, boolean xAxisHide, boolean yAxisHide,
			String xAxisTitle, String yAxisTitle, boolean pointsHide, boolean linesHide, boolean legendHide, boolean randomPointColour, boolean randomLineColour,
			Color pointColour, Color lineColour, String[] seriesLabels, String plotTitle) {

		this();
		
		this.displayGUI = displayGUI;
		this.xAxisScaleRange = xAxisScaleRange;
		this.yAxisScaleRange = yAxisScaleRange;
		this.xAxisHide = xAxisHide;
		this.yAxisHide = yAxisHide;
		this.xAxisTitle = xAxisTitle;
		this.yAxisTitle = yAxisTitle;
		this.randomLineColour = randomLineColour;
		this.randomPointColour = randomPointColour;
		this.lineColour = lineColour;
		this.pointColour = pointColour;
		this.pointsHide = pointsHide;
		this.linesHide = linesHide;
		this.legendHide = legendHide;
		this.seriesLabels = seriesLabels;
		this.plotTitle = plotTitle;

		this.setupPlot();

	}

	public void setupPlot() {

		plot.setInsets(new Insets2D.Double(20, 70, 60, 40));

		if (xAxisHide) {

			plot.getAxisRenderer(XYPlot.AXIS_X).setShapeVisible(false);
			plot.getAxisRenderer(XYPlot.AXIS_X).setTicksVisible(false);

		} else {

			if (xAxisScaleRange != -1) {
				plot.getAxis(XYPlot.AXIS_X).setRange(0, this.xAxisScaleRange);
				plot.getAxis(XYPlot.AXIS_X).setAutoscaled(false);
			}

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
			
			plot.getAxisRenderer(XYPlot.AXIS_Y).setIntersection(-Double.MAX_VALUE);

		}
		
		plot.setLegendVisible(!this.legendHide);
		
		// Move the legend to the right-hand side of the plot.
		plot.getLegend().setAlignmentX(1.0);

	}

	public void generatePlot() {
		this.redrawPlot(this.pointsHide, this.linesHide, this.displayGUI);
	}

	public void generatePlot(boolean pointsHide, boolean linesHide) {
		this.redrawPlot(pointsHide, linesHide, this.displayGUI);
	}
	
	public void generatePlot(boolean pointsHide, boolean linesHide, String plotTitle) {
		this.setPlotTitle(plotTitle);
		this.redrawPlot(pointsHide, linesHide, this.displayGUI);
	}
	
	public void generatePlot(boolean pointsHide, boolean linesHide, boolean displayGUI) {
		this.redrawPlot(pointsHide, linesHide, displayGUI);
	}
	
	public void generatePlot(boolean pointsHide, boolean linesHide, boolean displayGUI, String plotTitle) {
		this.setPlotTitle(plotTitle);
		this.redrawPlot(pointsHide, linesHide, displayGUI);
	}

	public void exportToFile(String exportFilePath, String fileName) {
		
		File file = new File(FilenameUtils.concat(exportFilePath, fileName));
		
		File parent = file.getParentFile();

		if (parent != null && !parent.exists() && !parent.mkdirs()) {
			throw new IllegalStateException("Error: Unable to create directory path: " + parent);
		}
		
		try {
			DrawableWriterFactory.getInstance().get("image/png").write(plot, new FileOutputStream(file.getCanonicalPath()), 600, 400);
			//DrawableWriterFactory.getInstance().get("image/svg+xml").write(plot, new FileOutputStream(file.getCanonicalPath()), 200, 200);
	        
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void redrawPlot(boolean hidePoints, boolean hideLines, boolean displayGUI) {

		if (data.getColumnCount() > 2) {
			
			ArrayList<DataSeries> dataSeriesCollection = new ArrayList<DataSeries>();
			
			for (int i = 1; i < data.getColumnCount(); i++) {
				
				dataSeriesCollection.add(new DataSeries(this.seriesLabels[i], data, 0, i));
				
			}
			
			plot = new XYPlot(dataSeriesCollection.toArray(new DataSeries[dataSeriesCollection.size()]));
			
//			PointRenderer points1 = new DefaultPointRenderer2D();
//			points1.setShape(new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0));
//			points1.setColor(new Color(0.0f, 0.3f, 1.0f, 0.3f));
//			plot.setPointRenderer(test.get(0), points1);
			
			plot.setPointRenderer(dataSeriesCollection.get(0), null);
			plot.setPointRenderer(dataSeriesCollection.get(1), null);
			
			LineRenderer line1 = new DefaultLineRenderer2D();
			//Color lineColour = this.randomLineColour ? generateRandomColour() : this.lineColour;
			line1.setColor(new Color(255, 0, 0));
			plot.setLineRenderer(dataSeriesCollection.get(0), line1);
			
			LineRenderer line2 = new DefaultLineRenderer2D();
			line2.setColor(new Color(0,0,255));
			plot.setLineRenderer(dataSeriesCollection.get(1), line2);
			
//			PointRenderer points3 = new DefaultPointRenderer2D();
//			points3.setShape(new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0));
//			points3.setColor(new Color(0.2f, 0.6f, 1.0f, 0.3f));
//			plot.setPointRenderer(test.get(1), points3);
			
			this.setupPlot();
			
			double minYValue = Double.MAX_VALUE;
			double maxYValue = Double.MIN_VALUE;
			
			for (int i = 1; i < data.getColumnCount(); i++) {
				
				if (minYValue > data.getColumn(i).getStatistics(Statistics.MIN)) {
					minYValue = data.getColumn(i).getStatistics(Statistics.MIN);
				}
				
				if (maxYValue <= data.getColumn(i).getStatistics(Statistics.MAX)) {
					maxYValue = data.getColumn(i).getStatistics(Statistics.MAX);
				}
				
			}
			
			plot.getAxis(XYPlot.AXIS_Y).setRange(minYValue - (minYValue* 0.1), maxYValue);
			
		} else {
			
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
			
			plot.getAxis(XYPlot.AXIS_Y).setRange(data.getColumn(1).getStatistics(Statistics.MIN) - (data.getColumn(1).getStatistics(Statistics.MIN) * 0.1), 
					data.getColumn(1).getStatistics(Statistics.MAX));
		}
		
		if (xAxisTitle != null) {
			plot.getAxisRenderer(XYPlot.AXIS_X).setLabel(xAxisTitle);
		}
		
		if (yAxisTitle != null) {
			plot.getAxisRenderer(XYPlot.AXIS_Y).setLabel(yAxisTitle);
		}
		
		if (plotTitle != null) {
			plot.getTitle().setText(plotTitle);
		}

		if (displayGUI) {
			getContentPane().removeAll();
			getContentPane().add(panel);
			getContentPane().revalidate();
			getContentPane().repaint();

			if (!this.isVisible()) {
				this.setVisible(true);
			}
		}

	}

	private Color generateRandomColour() {

		Random rand = new Random();

		float r = rand.nextFloat();
		float g = rand.nextFloat();
		float b = rand.nextFloat();

		return new Color(r, g, b);

	}

	public void updateData(ArrayList<TSPLocation> locations) {

		this.data = new DataTable(Integer.class, Integer.class);

		for (TSPLocation loc : locations) {

			data.add(loc.xCoord, loc.yCoord);

		}
		
		data.add(locations.get(0).xCoord, locations.get(0).yCoord);

	}
	
	public void updateData(Class<? extends Comparable<?>> type, String[] seriesLabels, ArrayList<? extends Number>... seriesData) throws TSPPlotterException {

		if (seriesData.length <= 0) {
			throw new TSPPlotterException("Cannot plot with empty data source collections. Aborting.");
		}
		
		if (seriesLabels.length != seriesData.length) {
			throw new TSPPlotterException("Mismatch in the number of labels specified for the number of series data collections. Aborting.");
		}
		
		int dataSourceLength = seriesData[0].size();
		
		for (int i = 1; i < seriesData.length; i++) {
			
			if (seriesData[i].size() != dataSourceLength) {
				throw new TSPPlotterException("Data source sizes do not match. Aborting.");
			}
		}

		this.data = new DataTable(seriesData.length, type);
		
		for (int j = 0; j < seriesData[0].size(); j++) {
			
			Comparable[] test = new Double[seriesData.length];
			
			for(int i = 0; i < seriesData.length; i++) {
				test[i] = (Comparable) seriesData[i].get(j).doubleValue();
			}
			
			data.add(test);
			
		}
		
		this.seriesLabels = seriesLabels;
		
	}
	
	public void setPlotTitle(String plotTitle) {
		this.plotTitle = plotTitle;
	}

	/**
	 * 'Builder'-pattern implementation class providing a standardized mechanism
	 * by which new 'TSPPlotter' instances can be created with the appropriate
	 * optional parameters.
	 * 
	 * @author connorgoddard
	 *
	 */
	public static class TSPPlotterBuilder {

		private int xAxisScaleRange = -1;
		private int yAxisScaleRange = -1;

		private boolean xAxisHide = false;
		private boolean yAxisHide = false;
		
		private String xAxisTitle = null;
		private String yAxisTitle = null;

		private boolean randomPointColour = false;
		private boolean randomLineColour = false;

		private Color lineColour = new Color(0, 0, 0);
		private Color pointColour = new Color(255, 0, 0);

		private boolean linesHide = false;
		private boolean pointsHide = false;
		
		private boolean legendHide = true;
		
		private String[] seriesLabels = new String[0];
		
		private String plotTitle = null;
		
		private boolean displayGUI = true;

		public TSPPlotterBuilder() {

		}

		public TSPPlotter buildTSPPlotter() {

			return new TSPPlotter(displayGUI, xAxisScaleRange, yAxisScaleRange, xAxisHide, yAxisHide, xAxisTitle, yAxisTitle, pointsHide, linesHide, legendHide,
					randomPointColour, randomLineColour, pointColour, lineColour, seriesLabels, plotTitle);

		}

		public TSPPlotterBuilder setRandomPointColourActive(boolean randomPointColour) {

			this.randomPointColour = randomPointColour;
			return this;

		}

		public TSPPlotterBuilder setRandomLineColourActive(boolean randomLineColour) {

			this.randomLineColour = randomLineColour;
			return this;

		}
		
		public TSPPlotterBuilder setPlotTitle(String plotTitle) {

			this.plotTitle = plotTitle;
			return this;

		}
		
		public TSPPlotterBuilder setDisplayGUI(boolean displayGUI) {

			this.displayGUI = displayGUI;
			return this;

		}

		public TSPPlotterBuilder setPointsHide(boolean pointsHide) {

			this.pointsHide = pointsHide;
			return this;

		}
		
		public TSPPlotterBuilder setShowLegend(boolean legendShow) {

			this.legendHide = !legendShow;
			return this;

		}
		
		public TSPPlotterBuilder setXAxisTitle(String title) {

			this.xAxisTitle = title;
			return this;

		}
		
		public TSPPlotterBuilder setYAxisTitle(String title) {

			this.yAxisTitle = title;
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
		
		public TSPPlotterBuilder setSeriesLabels(String[] seriesLabels) {

			this.seriesLabels = seriesLabels;
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
