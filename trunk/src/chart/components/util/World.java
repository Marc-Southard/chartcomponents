package chart.components.util;

import astro.calc.GeoPoint;

import chart.components.ui.ChartPanelInterface;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Area;

import java.util.ArrayList;
import java.util.List;

import oracle.xml.parser.v2.DOMParser;
import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLElement;

import org.w3c.dom.NodeList;

public class World
{
  private static DOMParser parser = null;
  private static List<Polygon> lp = null;
  
  public static void drawChart(ChartPanelInterface cpi, Graphics gr)
  {
    drawChart(null, cpi, gr);
  }
  
  public static void drawChart(DOMParser p, ChartPanelInterface cpi, Graphics gr)
  {
    Color origColor = gr.getColor();
    try
    {
      java.net.URL data = World.class.getResource("data.xml");
      if (parser == null)
      {
        if (p == null)
          parser = new DOMParser();
        else
          parser = p;
      }
      XMLDocument doc = null;
      synchronized (parser)
      {
        parser.parse(data);
        doc = parser.getDocument();
      }
      NodeList nl = doc.selectNodes("//section");
      
//      double minL = Double.MAX_VALUE,
//             maxL = -Double.MAX_VALUE,
//             minG = Double.MAX_VALUE,
//             maxG = -Double.MAX_VALUE;
//      int minX = Integer.MAX_VALUE,
//          maxX = Integer.MIN_VALUE,
//          minY = Integer.MAX_VALUE,
//          maxY = Integer.MIN_VALUE;
      
      for (int i = 0; i < nl.getLength(); i++)
      {
        gr.setColor(origColor);
//        if (c != null && i == sect)
//        {
//          System.out.println("Section " + sect + " in RED");
//          gr.setColor(c);
//        }
        XMLElement section = (XMLElement)nl.item(i);
        /*
        String id = section.getAttribute("id");
        if ("148".equals(id))
          gr.setColor(Color.red);
        */
        NodeList nl2 = section.selectNodes("./point");
        Point previous = null;
        Point first = null;
        for(int j = 0; j < nl2.getLength(); j++)
        {
          XMLElement pt = (XMLElement)nl2.item(j);
          String latValue = pt.getElementsByTagName("Lat").item(0).getFirstChild().getNodeValue();
          String lngValue = pt.getElementsByTagName("Lng").item(0).getFirstChild().getNodeValue();
          double l = Double.parseDouble(latValue);
          double g;
          for (g = Double.parseDouble(lngValue); g > 180D; g -= 180D);
          for (; g < -180D; g += 360D);
          
//          if (g < minG) minG = g;
//          if (g > maxG) maxG = g;
//          if (l < minL) minL = l;
//          if (l > maxL) maxL = l;
          
          Point gpt = cpi.getPanelPoint(l, g); // Get the point, based on the projection
          int w = cpi.getWidth();
          int h = cpi.getHeight();

//          if (gpt.x > maxX) maxX = gpt.x;
//          if (gpt.x < minX) minX = gpt.x;
//          if (gpt.y > maxY) maxY = gpt.y;
//          if (gpt.y < minY) minY = gpt.y;

          boolean drawIt = true;
          if (cpi.getProjection() == ChartPanelInterface.GLOBE_VIEW && !cpi.isTransparentGlobe() && cpi.isBehind(l, g - cpi.getGlobeViewLngOffset()))
            drawIt = false;
          else if (cpi.getProjection() == ChartPanelInterface.GLOBE_VIEW && !cpi.isAntiTransparentGlobe() && !cpi.isBehind(l, g - cpi.getGlobeViewLngOffset()))
            drawIt = false;
          if (cpi.getProjection() == ChartPanelInterface.SATELLITE_VIEW && !cpi.isTransparentGlobe() && cpi.isBehind(l, g))
            drawIt = false;
          else if (cpi.getProjection() == ChartPanelInterface.SATELLITE_VIEW && !cpi.isAntiTransparentGlobe() && !cpi.isBehind(l, g))
            drawIt = false;
          if (!drawIt)
          {
            previous = null;
//          first = null;
          }
          else
          {
            if (previous != null && (cpi.contains(new astro.calc.GeoPoint(l, g)) && 
                                     Math.abs(gpt.x - previous.x) < w / 2 && 
                                     Math.abs(gpt.y - previous.y) < h / 2 ||
                                     cpi.getProjection() == ChartPanelInterface.SATELLITE_VIEW))
              gr.drawLine(previous.x, previous.y, gpt.x, gpt.y);
            previous = gpt;
            if (first == null)
              first = gpt;
          }
        }
        
        // Close the loop
        if (previous != null) // && !(cpi.getProjection() == ChartPanelInterface.GLOBE_VIEW && !cpi.isTransparentGlobe()) )
        {
          int w = cpi.getWidth();
          int h = cpi.getHeight();
          if (Math.abs(first.x - previous.x) < w / 2 && Math.abs(first.y - previous.y) < h / 2)
            gr.drawLine(first.x, first.y, previous.x, previous.y);
        }
      }
//      System.out.println("----------------");
//      System.out.println("Min Lat :" + minL);
//      System.out.println("Max Lat :" + maxL);
//      System.out.println("Min Long:" + minG);
//      System.out.println("Max Long:" + maxG);
//      System.out.println("Min X   :" + minX);
//      System.out.println("Max X   :" + maxX);
//      System.out.println("Min Y   :" + minY);
//      System.out.println("Max Y   :" + maxY);
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  public static void paintChart(DOMParser p, ChartPanelInterface cpi, Graphics2D g2, Color c)
  {
    Color origColor = g2.getColor();
                                   
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    // Get the Polygons, from the data.xml and the ChartPanelInterface
    List<Polygon> listPolygon = new ArrayList<Polygon>();
    try
    {
      java.net.URL data = World.class.getResource("data.xml");
      if (parser == null)
      {
        if (p == null)
          parser = new DOMParser();
        else
          parser = p;
      }
      parser.parse(data);
      XMLDocument doc = parser.getDocument();
      NodeList nl = doc.selectNodes("//section");
      Point previousPp = null;
      for (int i = 0; i < nl.getLength(); i++)
      {
        Polygon polygon = new Polygon();
        XMLElement section = (XMLElement)nl.item(i);
        NodeList nl2 = section.selectNodes("./point");
        Point firstPoint = null;
        for (int j = 0; j < nl2.getLength(); j++)
        {
          XMLElement pt = (XMLElement)nl2.item(j);
          String latValue = pt.getElementsByTagName("Lat").item(0).getFirstChild().getNodeValue();
          String lngValue = pt.getElementsByTagName("Lng").item(0).getFirstChild().getNodeValue();
          double l = Double.parseDouble(latValue);
          double g;
          for (g = Double.parseDouble(lngValue); g > 180D; g -= 180D);
          for (; g < -180D; g += 360D);
          Point pp = cpi.getPanelPoint(l, g);
          if (previousPp != null && Math.abs(pp.x - previousPp.x) > (cpi.getWidth() * 0.9)) // Avoid stokes across the full chart...
            pp.x = previousPp.x;
          polygon.addPoint(pp.x, pp.y);
          previousPp = pp;
          if (j == 0)
            firstPoint = pp;
        }
        // Close the loop?
        if (false && firstPoint != null)
          polygon.addPoint(firstPoint.x, firstPoint.y);          
        
        listPolygon.add(polygon);
      }
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
    // listPolygon is ready
    Area area = null;
    for (Polygon polygon : listPolygon)
    {
      if (area == null)
        area = new Area(polygon);
      else
      {
        Area nextArea = new Area(polygon);
        area.exclusiveOr(nextArea);
      }
    }
    g2.setPaint(c);
    g2.fill(area);
    // Draw the outline of the resulting Area.
    g2.setPaint(Color.black);
    g2.draw(area);

    g2.setColor(origColor);
  }

  public static List<List<Point>> getChartPoints(ChartPanelInterface cpi)
  {
    return getChartPoints(cpi, null);
  }
  
  public static List<List<Point>> getChartPoints(ChartPanelInterface cpi, DOMParser parser)
  {
    List<List<Point>> chartPoints = null;
    try
    {
      java.net.URL data = World.class.getResource("data.xml");
      if (parser == null)
        parser = new DOMParser();
      parser.parse(data);
      XMLDocument doc = parser.getDocument();
      NodeList nl = doc.selectNodes("//section");
      chartPoints = new ArrayList<List<Point>>(nl.getLength());
      for (int i = 0; i < nl.getLength(); i++)
      {
        XMLElement section = (XMLElement)nl.item(i);
        NodeList nl2 = section.selectNodes("./point");
        List<Point> sectionPts = new ArrayList<Point>(nl2.getLength());
        Point previous = null;
        Point first = null;
        for (int j = 0; j < nl2.getLength(); j++)
        {
          XMLElement pt = (XMLElement)nl2.item(j);
          String latValue = pt.getElementsByTagName("Lat").item(0).getFirstChild().getNodeValue();
          String lngValue = pt.getElementsByTagName("Lng").item(0).getFirstChild().getNodeValue();
          double l = Double.parseDouble(latValue);
          double g;
          for (g = Double.parseDouble(lngValue); g > 180D; g -= 180D);
          for (; g < -180D; g += 360D);
          Point gpt = cpi.getPanelPoint(l, g);
          if (gpt.x > 0 && gpt.y > 0  && gpt.x < cpi.getWidth() && gpt.y < cpi.getHeight())
          {
            int w = cpi.getWidth();
            int h = cpi.getHeight();
            boolean drawIt = true;
            if (cpi.getProjection() == ChartPanelInterface.GLOBE_VIEW && !cpi.isTransparentGlobe() && cpi.isBehind(l, g - cpi.getGlobeViewLngOffset()))
              drawIt = false;
            else if (cpi.getProjection() == ChartPanelInterface.GLOBE_VIEW && !cpi.isAntiTransparentGlobe() && !cpi.isBehind(l, g - cpi.getGlobeViewLngOffset()))
              drawIt = false;
            if (cpi.getProjection() == ChartPanelInterface.SATELLITE_VIEW && !cpi.isTransparentGlobe() && cpi.isBehind(l, g))
              drawIt = false;
            else if (cpi.getProjection() == ChartPanelInterface.SATELLITE_VIEW && !cpi.isAntiTransparentGlobe() && !cpi.isBehind(l, g))
              drawIt = false;
            if (!drawIt)
            {
              previous = null; 
              // Reset section
              chartPoints.add(sectionPts);
              sectionPts = new ArrayList<Point>(nl2.getLength());
            }
            else
            {
              if (previous == null)
                sectionPts.add(gpt);
              if (previous != null && cpi.contains(new astro.calc.GeoPoint(l, g)) && 
                                      Math.abs(gpt.x - previous.x) < w / 2 && 
                                      Math.abs(gpt.y - previous.y) < h / 2)
              {                                    
  //            gr.drawLine(previous.x, previous.y, gpt.x, gpt.y);
                sectionPts.add(gpt);
              }
              previous = gpt;
              if (first == null)
                first = gpt;
            }
          }
        }
        // Close the loop
        if (previous != null) // && !(cpi.getProjection() == ChartPanelInterface.GLOBE_VIEW && !cpi.isTransparentGlobe()) )
        {
          int w = cpi.getWidth();
          int h = cpi.getHeight();
          if (Math.abs(first.x - previous.x) < w / 2 && 
              Math.abs(first.y - previous.y) < h / 2)
          {              
//          gr.drawLine(first.x, first.y, previous.x, previous.y);
            sectionPts.add(first);
          }
        }
        chartPoints.add(sectionPts);
      }
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
    return chartPoints;
  }
  
  private static List<Polygon> getChartPolygon()
  {
    List<Polygon> listPolygon = new ArrayList<Polygon>();
    try
    {
      java.net.URL data = World.class.getResource("data.xml");
      if (parser == null)
        parser = new DOMParser();
      parser.parse(data);
      XMLDocument doc = parser.getDocument();
      NodeList nl = doc.selectNodes("//section");
      for (int i = 0; i < nl.getLength(); i++)
      {
        Polygon polygon = new Polygon();
        XMLElement section = (XMLElement)nl.item(i);
        NodeList nl2 = section.selectNodes("./point");
        for (int j = 0; j < nl2.getLength(); j++)
        {
          XMLElement pt = (XMLElement)nl2.item(j);
          String latValue = pt.getElementsByTagName("Lat").item(0).getFirstChild().getNodeValue();
          String lngValue = pt.getElementsByTagName("Lng").item(0).getFirstChild().getNodeValue();
          double l = Double.parseDouble(latValue);
          double g;
          for (g = Double.parseDouble(lngValue); g > 180D; g -= 180D);
          for (; g < -180D; g += 360D);
          polygon.addPoint((int)(g * 1000), (int)(l * 1000));
        }
        listPolygon.add(polygon);
      }
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
    }
    return listPolygon;
  }
  
  public static boolean isInLand(GeoPoint gp)
  {
    if (lp == null)
      lp = getChartPolygon();
    boolean b = false;
    Point p = new Point((int)(gp.getG() * 1000), (int)(gp.getL() * 1000));
    for (Polygon poly : lp)
    {
      if (poly.contains(p))
      {
        b = true;
        break;
      }
    }
    return b;
  }
  
  private final static int NB_ITERATION = 100;
  
  public static Polygon lineIntersectsPolygon(Point from, Point to, Polygon poly)
  {
    Polygon pg = null;
    
    int fromX  = from.x;
    int fromY  = from.y;
    int width  = to.x - from.x;
    int height = to.y - from.y;
    
    for (int i=0; i<NB_ITERATION; i++)
    {
      Point p = new Point(fromX + (int)(i * ((double)width / NB_ITERATION)),
                          fromY + (int)(i * ((double)height / NB_ITERATION)));
      if (poly.contains(p.x, p.y))
      {
//        GeoPoint gp = new GeoPoint((double)p.y / 1000D, (double)p.x / 1000D);
//        System.out.print(" (i=" + i + ", " + gp.toString() + ") ");
//        System.out.print(" fromX:" + fromX + ", fromY:" + fromY + ", w:" + width + ", h:" + height + " ");
//        System.out.print("FromPt:" + from + ", ToPt:" + to + " CurrPt:" + p + " ");
        pg = poly;
        break;
      }
    }
    return pg;
  }

  public static Polygon isRouteCrossingLand(GeoPoint from, GeoPoint to)
  {
    if (lp == null)
      lp = getChartPolygon();
    Polygon pg = null;
    
    Point pFrom = new Point((int)(from.getG() * 1000), (int)(from.getL() * 1000));
    Point pTo   = new Point((int)(to.getG() * 1000), (int)(to.getL() * 1000));
    for (Polygon poly : lp)
    {
      Polygon inter = lineIntersectsPolygon(pFrom, pTo, poly);
      if (inter != null)
      {
        pg = inter;
        break;
      }
    }
    return pg;
  }
  
  public static void main(String[] args)
  {
    Polygon poly = new Polygon(new int[] { -10, -10, 10, 10 },
                               new int[] { -10, 10, 10, -10 },
                               4);
    Point from = new Point(-20, -20);
    Point to   = new Point(20, 20);
    long before = System.currentTimeMillis();
    Polygon pg = lineIntersectsPolygon(from, to, poly);
    long after = System.currentTimeMillis();
    System.out.println("Intersection:" + Boolean.toString(pg != null));
    System.out.println("(" + Long.toString(after-before) + " ms)");

    from = new Point(-20, -20);
    to   = new Point(-20, 20);
    
    before = System.currentTimeMillis();
    pg = lineIntersectsPolygon(from, to, poly);
    after = System.currentTimeMillis();
    System.out.println("Intersection:" + Boolean.toString(pg != null));
    System.out.println("(" + Long.toString(after-before) + " ms)");
  }
}
