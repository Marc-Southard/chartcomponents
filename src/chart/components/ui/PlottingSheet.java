package chart.components.ui;

import astro.calc.GeoPoint;

import astro.calc.GreatCircle;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import java.text.DecimalFormat;

public class PlottingSheet
  extends ChartPanel
{
  private double chartLatitudeSpan = 1d;

  private double centerLat = 37d;
  private double sLat = centerLat - (chartLatitudeSpan / 2);
  private double nLat = sLat + chartLatitudeSpan;
  private double centerLong = -122D;
  
  private boolean withDistanceScale = true;
  
  private final static DecimalFormat DF15 = new DecimalFormat("##0.00000");

  public PlottingSheet(ChartPanelParentInterface chartPanelParentInterface, 
                       int w, 
                       int h, 
                       double cL, 
                       double cG, 
                       double ls)
  {
    super(chartPanelParentInterface, w, h);    
    centerLat = cL;
    chartLatitudeSpan = ls;
    sLat = centerLat - (chartLatitudeSpan / 2);
    nLat = sLat + chartLatitudeSpan;
    centerLong = cG;
    
    jbInit();
  }

  private void jbInit()
  {
    setPreferredSize(new Dimension(this.getW(), this.getH()));
//  setSize(new Dimension(this.getW(), this.getH()));

    double eLong    = this.calculateEastG(nLat, sLat, centerLong);
    double lngWidth = Math.abs(eLong - centerLong);
    double wLong    = centerLong - (lngWidth / 2d);
    eLong           = wLong + lngWidth;
    
    this.setEastG(eLong);
    this.setWestG(wLong);
    this.setNorthL(nLat);
    this.setSouthL(sLat);
    this.setHorizontalGridInterval(0.25d);
    this.setVerticalGridInterval(0.25d);
    this.setWithScale(false);
    this.setWithGrid(false);
    this.setMajorLatitudeTick(1d / 6d);
    this.setMinorLatitudeTick(1d / 60d);
    this.setPositionToolTipEnabled(true);
    this.setWithLngLabels(false); // Because this is a canvas    
  }
  
  public void chartPanelPaintComponent(Graphics gr)
  {
  //  MercatorUtil.drawMercatorScale(gr, 100, 100, 10, 10, Color.darkGray);
    
    // Mercator Plotting Sheet circle.
    Rectangle rect = null; // chartPanel.getVisibleRect();
    rect = new Rectangle(this.getSize());
    final int BETWEEN_CIRCLE = 5;
    int diameter = (int)(Math.min(rect.height, rect.width) * 0.6d);
    // Center
    Point center = null;
    try 
    { 
      center = this.getPanelPoint(centerLat, 
                                  this.getWestG() + ((this.getEastG() - this.getWestG()) / 2));    
    }
    catch (Exception npe)
    {
      System.err.println("PlottingSheet:" + npe.toString());
    }
    if (center == null)
      return;
    
    // Outer circle
    gr.drawOval(center.x - (diameter / 2), 
                center.y - (diameter / 2), 
                diameter, 
                diameter);
    // Inner circle
    gr.drawOval(center.x - (diameter / 2) + BETWEEN_CIRCLE, 
                center.y - (diameter / 2) + BETWEEN_CIRCLE, 
                diameter - (BETWEEN_CIRCLE * 2), 
                diameter - (BETWEEN_CIRCLE * 2));
    // Graduations on the circle
    final float increment = 11.25f; // TODO Parameter ?
    for (float i=0; i<360; i+=increment)
    {
      gr.drawLine(0 + (center.x) + (int)(Math.sin(Math.toRadians(i)) * ((diameter / 2) - (BETWEEN_CIRCLE))),
                  0 + (center.y) + (int)(Math.cos(Math.toRadians(i)) * ((diameter / 2) - (BETWEEN_CIRCLE))), 
                  0 + (center.x) + (int)(Math.sin(Math.toRadians(i)) * ((diameter / 2))),
                  0 + (center.y) + (int)(Math.cos(Math.toRadians(i)) * ((diameter / 2))));
    }
    // Vertical axis
    gr.drawLine(center.x, 0, center.x, rect.height);
    // TODO Vertical graduation
    // Horizontal axis
    gr.drawLine(0, center.y, rect.width, center.y);
    
    // Scale ?
    if (withDistanceScale)
    {
      Point p1 = new Point(10, rect.height - 10);
      Point p2 = new Point(10 + rect.width / 4, rect.height - 10);
      gr.drawLine(p1.x, p1.y, p2.x, p2.y);
      gr.drawLine(p1.x, p1.y, p1.x, p1.y - 5);
      gr.drawLine(p2.x, p2.y, p2.x, p2.y - 5);
      GeoPoint g1 = this.getGeoPos(p1.x, p1.y);
      GeoPoint g2 = this.getGeoPos(p2.x, p2.y);
      double rhumLineDistance = GreatCircle.calculateRhumLineDistance(g1, g2);
      gr.drawString(DF15.format(rhumLineDistance) + " nm", 10 + (rect.width / 4) + 10, rect.height - 10);
    }
  }

  public void setChartLatitudeSpan(double chartLatitudeSpan)
  {
    this.chartLatitudeSpan = chartLatitudeSpan;
    jbInit();
  }

  public void setCenterLat(double centerLat)
  {
    this.centerLat = centerLat;
    sLat = centerLat - (chartLatitudeSpan / 2);
    nLat = sLat + chartLatitudeSpan;
    jbInit();
  }

  public void setCenterLong(double centerLong)
  {
    this.centerLong = centerLong;
    jbInit();
  }

  public void setWithDistanceScale(boolean withScale)
  {
    this.withDistanceScale = withScale;
  }
}
