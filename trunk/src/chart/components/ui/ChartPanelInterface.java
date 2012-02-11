package chart.components.ui;

import astro.calc.GeoPoint;
import java.awt.Color;
import java.awt.Point;

@SuppressWarnings("serial")
public interface ChartPanelInterface
{
  public static final int ANAXIMANDRE         = 0;
  public static final int MERCATOR            = 1;
  public static final int LAMBERT             = 2;
  public static final int GLOBE_VIEW          = 3;
  public static final int SATELLITE_VIEW      = 4;
  public static final int CONIC_EQUIDISTANT   = 5;
  public static final int STEREOGRAPHIC       = 6;
  public static final int POLAR_STEREOGRAPHIC = 7;
  
  public static enum PROJECTIONS
  {
    ANAXIMANDRE,
    MERCATOR,
    LAMBERT,
    GLOBE_VIEW,
    SATELLITE_VIEW,
    CONIC_EQUIDISTANT,
    STEREOGRAPHIC,
    POLAR_STEREOGRAPHIC
  }

  public abstract void setZoomFactor(double d);
  public abstract double getZoomFactor();
  public abstract void zoomIn();
  public abstract void zoomOut();
  public abstract double getEastG();
  public abstract double getWestG();
  public abstract double getNorthL();
  public abstract double getSouthL();
  public abstract double getContactParallel();
  public abstract void setEastG(double d);
  public abstract void setWestG(double d);
  public abstract void setNorthL(double d);
  public abstract void setSouthL(double d);
  public abstract void setContactParallel(double d);
  public abstract void setVerticalGridInterval(double d);
  public abstract void setHorizontalGridInterval(double d);
  public abstract double getVerticalGridInterval();
  public abstract double getHorizontalGridInterval();
  public abstract Point getPanelPoint(double d, double d1);
  public abstract boolean contains(GeoPoint geopoint);
  public abstract void setChartColor(Color color);
  public abstract void setGridColor(Color color);
  public abstract void setDdRectColor(Color color);
  public abstract Color getChartColor();
  public abstract Color getGridColor();
  public abstract Color getDdRectColor();
  public abstract void setProjection(int i);
  public abstract int getProjection();
  public abstract int getWidth();
  public abstract int getHeight();
  public void setGlobeViewLngOffset(double d);
  public double getGlobeViewLngOffset();
  public void setTransparentGlobe(boolean b);
  public boolean isTransparentGlobe();
  public void setAntiTransparentGlobe(boolean b);
  public boolean isAntiTransparentGlobe();
  public void setGlobeViewRightLeftRotation(double d);
  public double getGlobeViewRightLeftRotation();
  public void setGlobeViewForeAftRotation(double d);
  public double getGlobeViewForeAftRotation();
  public boolean isBehind(double l, double g);
  
  public void setSatelliteAltitude(double rhoS);
  public double getSatelliteAltitude();
  public void setSatelliteLongitude(double thetaP);
  public double getSatelliteLongitude();
  public void setSatelliteLatitude(double satLat);
  public double getSatelliteLatitude();
}
