package chart.components.util;

import chart.components.ui.ChartPanelInterface;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
//import java.io.PrintStream;
import java.util.ArrayList;

import java.util.List;

import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLElement;
import oracle.xml.parser.v2.DOMParser;
//import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class World
{
  private static DOMParser parser = null;
  
  public static void drawChart(ChartPanelInterface cpi, Graphics gr)
  {
    drawChart(cpi, gr, 0, null);
  }

  public static void drawChart(ChartPanelInterface cpi, Graphics gr, Color c)
  {
    drawChart(cpi, gr, 0, c);
  }

  public static void drawChart(ChartPanelInterface cpi, Graphics gr, int sect, Color c)
  {
    drawChart(null, cpi, gr, 0, c);
  }
  
  public static void drawChart(DOMParser p, ChartPanelInterface cpi, Graphics gr, int sect, Color c)
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
//        if(c != null && i == sect)
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
}
