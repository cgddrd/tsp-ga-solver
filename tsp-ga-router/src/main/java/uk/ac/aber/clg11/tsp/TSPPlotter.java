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
 * Makes extensive use of the GRAL open-source plotting library available at: http://trac.erichseifert.de/gral/
 * 
 * @author Connor Goddard (clg11@aber.ac.uk)
 *
 */
public class TSPPlotter extends JFrame {

	private XYPlot plot;
	private DataTable data;
	private InteractivePanel panel;

	private double xAxisMinScaleRange = -1;
	private double yAxisMinScaleRange = -1;
	
	private double xAxisMaxScaleRange = -1;
	private double yAxisMaxScaleRange = -1;

	private boolean xAxisHide = false;
	private boolean yAxisHide = false;
	
	private String xAxisTitle = null;
	private String yAxisTitle = null;
	
	private boolean randomPointColour = false;
	private boolean randomLineColour = false;

	private Color lineColour = new Color(0, 0, 0);
	private Color pointColour = new Color(255, 0, 0);
	
	private Color[] lineColours = new Color[] {new Color(255,0,0)};
	private Color[] pointColours = new Color[] {new Color(0,0,0)};

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

		data = new DataTable(Double.class, Double.class);

		this.plot = new XYPlot(data);

		this.panel = new InteractivePanel(this.plot);

		getContentPane().add(this.panel);

		setupPlot();

	}

	private TSPPlotter(boolean displayGUI, double xAxisMinScaleRange, double yAxisMinScaleRange, double xAxisMaxScaleRange, double yAxisMaxScaleRange, boolean xAxisHide, boolean yAxisHide,
			String xAxisTitle, String yAxisTitle, boolean pointsHide, boolean linesHide, boolean legendHide, boolean randomPointColour, boolean randomLineColour,
			Color[] pointColours, Color[] lineColours, String[] seriesLabels, String plotTitle) {

		this();
		
		this.displayGUI = displayGUI;
		this.xAxisMinScaleRange = xAxisMinScaleRange;
		this.yAxisMinScaleRange = yAxisMinScaleRange;
		this.xAxisMaxScaleRange = xAxisMaxScaleRange;
		this.yAxisMaxScaleRange = yAxisMaxScaleRange;
		this.xAxisHide = xAxisHide;
		this.yAxisHide = yAxisHide;
		this.xAxisTitle = xAxisTitle;
		this.yAxisTitle = yAxisTitle;
		this.randomLineColour = randomLineColour;
		this.randomPointColour = randomPointColour;
		this.pointColours = pointColours;
		this.lineColours = lineColours;
		this.pointsHide = pointsHide;
		this.linesHide = linesHide;
		this.legendHide = legendHide;
		this.seriesLabels = seriesLabels;
		this.plotTitle = plotTitle;

		this.setupPlot();

	}
	
	/**
	 * Initialises and configures for current plot ready for generation. 
	 */
	public void setupPlot() {
		
		// Set the spacing along the outside of the plotting space (required to display axis values, titles etc.)
		plot.setInsets(new Insets2D.Double(20, 70, 60, 40));

		if (xAxisHide) {

			plot.getAxisRenderer(XYPlot.AXIS_X).setShapeVisible(false);
			plot.getAxisRenderer(XYPlot.AXIS_X).setTicksVisible(false);

		} else {

			if (xAxisMinScaleRange != -1 && xAxisMaxScaleRange != -1) {
				plot.getAxis(XYPlot.AXIS_X).setAutoscaled(false);
				plot.getAxis(XYPlot.AXIS_X).setRange(this.xAxisMinScaleRange, this.xAxisMaxScaleRange);
			} else {
				plot.getAxis(XYPlot.AXIS_X).setAutoscaled(true);
			}

			plot.getAxisRenderer(XYPlot.AXIS_X).setIntersection(-Double.MAX_VALUE);

		}

		if (yAxisHide) {

			plot.getAxisRenderer(XYPlot.AXIS_Y).setShapeVisible(false);
			plot.getAxisRenderer(XYPlot.AXIS_Y).setTicksVisible(false);

		} else {

			if (yAxisMinScaleRange != -1 && yAxisMaxScaleRange != -1) {
				plot.getAxis(XYPlot.AXIS_Y).setAutoscaled(false);
				plot.getAxis(XYPlot.AXIS_Y).setRange(this.yAxisMinScaleRange, this.yAxisMaxScaleRange);
			} else {
				plot.getAxis(XYPlot.AXIS_Y).setAutoscaled(true);
			}
			
			plot.getAxisRenderer(XYPlot.AXIS_Y).setIntersection(-Double.MAX_VALUE);

		}
		
		if (pointsHide) {
			plot.setPointRenderer(data, null);
		} else {
			Color pointColour = this.randomPointColour ? generateRandomColour() : this.pointColours[0];
			plot.getPointRenderer(data).setColor(pointColour);
		}

		if (linesHide) {
			plot.setLineRenderer(data, null);
		} else {
			Color lineColour = this.randomLineColour ? generateRandomColour() : this.lineColours[0];
			plot.setLineRenderer(data, lines);
			plot.getLineRenderer(data).setColor(lineColour);
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

		
		// If we have no series labels defined, we don't want to display the legend regardless of the user's choice on whether to display it or not.
		plot.setLegendVisible(this.seriesLabels != null ? (!this.legendHide) : false);
		
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

	/**
	 * Exports a the current plot to a PNG image in the specified location.
	 * @param exportFilePath The path to export the PNG file to.
	 * @param fileName Name of the new PNG file.
	 */
	public void exportToFile(String exportFilePath, String fileName) {
		
		File file = new File(FilenameUtils.concat(exportFilePath, fileName));
		
		File parent = file.getParentFile();

		if (parent != null && !parent.exists() && !parent.mkdirs()) {
			throw new IllegalStateException("Error: Unable to create directory path: " + parent);
		}
		
		try {
			
			// DrawableWriterFactory is a function defined within the GRAL library.
			// There is currently an issue with rendering plots to JPG, need to investigate further.
			DrawableWriterFactory.getInstance().get("image/png").write(plot, new FileOutputStream(file.getCanonicalPath()), 600, 400);
	        
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Re-draws the plot using the current data contained within the internal DataTable structure.
	 * @param hidePoints Determines whether to hide the data points within the plot.
	 * @param hideLines Determines whether to hide the lines connecting each data point within the plot.
	 * @param displayGUI Determines whether to display the plot to the user wihtin a new JFrame GUI window.
	 */
	private void redrawPlot(boolean hidePoints, boolean hideLines, boolean displayGUI) {
		
		this.pointsHide = hidePoints;
		this.linesHide = hideLines;
		
		if (data.getColumnCount() > 2) {
			
			ArrayList<DataSeries> dataSeriesCollection = new ArrayList<DataSeries>();
			
			for (int i = 1; i < data.getColumnCount(); i++) {
				
				if (seriesLabels != null) {
					dataSeriesCollection.add(new DataSeries(this.seriesLabels[i], data, 0, i));
				} else {
					dataSeriesCollection.add(new DataSeries("", data, 0, i));
				}
				
			}
			
			plot = new XYPlot(dataSeriesCollection.toArray(new DataSeries[dataSeriesCollection.size()]));
			
			for (int i = 0; i < dataSeriesCollection.size(); i++) {
				
				if (pointsHide) {
					plot.setPointRenderer(dataSeriesCollection.get(i), null);
				} else {
					
					PointRenderer newPointsRenderer = new DefaultPointRenderer2D();
					
					if (this.randomPointColour) {
						newPointsRenderer.setColor(generateRandomColour());
					} else {
						newPointsRenderer.setColor(this.pointColours[i] != null ? this.pointColours[i] : this.pointColours[0]);
					}
					
					plot.setPointRenderer(dataSeriesCollection.get(i), newPointsRenderer);
				}
				
				if (linesHide) {
					plot.setLineRenderer(dataSeriesCollection.get(i), null);
				} else {
					
					LineRenderer newLineRenderer = new DefaultLineRenderer2D();
					
					if (this.randomLineColour) {
						newLineRenderer.setColor(generateRandomColour());
					} else {
						newLineRenderer.setColor(this.lineColours[i] != null ? this.lineColours[i] : this.lineColours[0]);
					}
					
					plot.setLineRenderer(dataSeriesCollection.get(i), newLineRenderer);
				}
				
			}
			
		
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
			
			this.setYAxisScaleRange(minYValue - (minYValue* 0.1), maxYValue);
			
		} else {
			
			plot.clear();
			plot.add(data);
				
		}
		
		this.setupPlot();
		
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
	
	/**
	 * Generates a new Color set to random RGB values.
	 * @return A new 'Color' object set to randomly assigned RGB values.
	 */
	private Color generateRandomColour() {

		Random rand = new Random();

		float r = rand.nextFloat();
		float g = rand.nextFloat();
		float b = rand.nextFloat();

		return new Color(r, g, b);

	}
	
	/**
	 * Convenience method for automatically plotting a graphical representation of a given TSP solution.
	 * @param solutionLocations Collection of TSPLocation items to plot.
	 * @throws TSPPlotterException
	 */
	public void plotTSPSolution(ArrayList<TSPLocation> solutionLocations) throws TSPPlotterException {
		
		ArrayList<Integer> xCoords = new ArrayList<Integer>();
		ArrayList<Integer> yCoords = new ArrayList<Integer>();
		
		for (TSPLocation loc : solutionLocations) {
			
			xCoords.add(loc.xCoord);
			yCoords.add(loc.yCoord);

		}
		
		// We need to make sure to append the starting location at the end of the route to complete the route.
		TSPLocation startingLocation = solutionLocations.get(0);
		
		xCoords.add(startingLocation.xCoord);
		yCoords.add(startingLocation.yCoord);
		
		// Update the internal data collection and render out the results.
		this.updatePlotData(null, xCoords, yCoords);
		this.generatePlot(false, false, false);
		
	}
	
	/**
	 * Updates the internal DataTable structure representing the plot data. (Note: All numeric series values are converted to the 'Double' type prior to insertion into the internal DataTable structure)
	 * @param seriesLabels Collection of String labels with an individual element referencing the matching element position within 'seriesData'
	 * @param seriesData VarArgs collection of ArrayLists, with each ArrayList representing an individual column within the internal DataTable structure.
	 * @throws TSPPlotterException
	 */
	public void updatePlotData(String[] seriesLabels, ArrayList<? extends Number>... seriesData) throws TSPPlotterException {

		if (seriesData.length <= 0) {
			throw new TSPPlotterException("Cannot plot with empty data source collections. Aborting.");
		}
		
		if (seriesLabels != null && (seriesLabels.length != seriesData.length)) {
			throw new TSPPlotterException("Mismatch in the number of labels specified for the number of series data collections. Aborting.");
		}
		
		this.data = new DataTable(seriesData.length, Double.class);
		int dataSourceLength = seriesData[0].size();
		
		for (int i = 1; i < seriesData.length; i++) {
			
			if (seriesData[i].size() != dataSourceLength) {
				throw new TSPPlotterException("Data source sizes do not match. Aborting.");
			}
		}
		
		// Create a new row for each item in the specified column collections.
		for (int j = 0; j < seriesData[0].size(); j++) {
			
			Comparable[] newSeriesRow = new Double[seriesData.length];
			
			// Set the value of each column along the row.
			for(int i = 0; i < seriesData.length; i++) {
				newSeriesRow[i] = (Comparable) seriesData[i].get(j).doubleValue();
			}
			
			data.add(newSeriesRow);
			
		}
		
		// Update the labels for the new series (if applicable)
		this.seriesLabels = seriesLabels;
		
	}
	
	public void setPlotTitle(String plotTitle) {
		this.plotTitle = plotTitle;
	}
	
	private void setXAxisScaleRange(double axisMin, double axisMax) {
		
		this.xAxisMinScaleRange = axisMin;
		this.xAxisMaxScaleRange = axisMax;
	}

	private void setYAxisScaleRange(double axisMin, double axisMax) {
		
		this.yAxisMinScaleRange = axisMin;
		this.yAxisMaxScaleRange = axisMax;
	}

	/**
	 * 'Builder' pattern implementation class providing a standardized mechanism
	 * by which new 'TSPPlotter' instances can be created with the appropriate
	 * optional parameters.
	 * 
	 * @author Connor Goddard (clg11@aber.ac.uk)
	 *
	 */
	public static class TSPPlotterBuilder {

		private double xAxisMinScaleRange = -1;
		private double yAxisMinScaleRange = -1;
		
		private double xAxisMaxScaleRange = -1;
		private double yAxisMaxScaleRange = -1;

		private boolean xAxisHide = false;
		private boolean yAxisHide = false;
		
		private String xAxisTitle = null;
		private String yAxisTitle = null;

		private boolean randomPointColour = false;
		private boolean randomLineColour = false;
		
		private Color[] lineColours = new Color[] {new Color(0,0,0)};
		private Color[] pointColours = new Color[] {new Color(255,0,0)};

		private boolean linesHide = false;
		private boolean pointsHide = false;
		
		private boolean legendHide = true;
		
		private String[] seriesLabels = new String[0];
		
		private String plotTitle = null;
		
		private boolean displayGUI = true;

		public TSPPlotterBuilder() {

		}

		public TSPPlotter buildTSPPlotter() {

			return new TSPPlotter(displayGUI, xAxisMinScaleRange, yAxisMinScaleRange, xAxisMaxScaleRange, yAxisMaxScaleRange, xAxisHide, yAxisHide, xAxisTitle, yAxisTitle, pointsHide, linesHide, legendHide,
					randomPointColour, randomLineColour, pointColours, lineColours, seriesLabels, plotTitle);

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
		
		public TSPPlotterBuilder setPointColours(Color[] pointColours) {

			this.pointColours = pointColours;
			return this;

		}

		public TSPPlotterBuilder setLineColours(Color[] lineColours) {

			this.lineColours = lineColours;
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
		
		public TSPPlotterBuilder setXAxisRangeSettings(int axisMin, int axisMax) {

			this.xAxisMinScaleRange = axisMin;
			this.xAxisMaxScaleRange = axisMax;

			// Return the current instance to allow for "method chaining".
			return this;

		}

		public TSPPlotterBuilder setYAxisRangeSettings(int axisMin, int axisMax) {

			this.yAxisMinScaleRange = axisMin;
			this.yAxisMaxScaleRange = axisMax;

			// Return the current instance to allow for "method chaining".
			return this;

		}

	}

}
