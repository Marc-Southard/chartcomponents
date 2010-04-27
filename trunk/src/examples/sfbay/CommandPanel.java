package examples.sfbay;

import astro.calc.GreatCircle;
import astro.calc.GreatCircleWayPoint;
import astro.calc.GeoPoint;
import chart.components.ui.ChartPanel;
import chart.components.ui.ChartPanelParentInterface;
import chart.components.util.Spatial;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class CommandPanel extends JPanel
  implements ChartPanelParentInterface
{

  public CommandPanel()
  {
    borderLayout1 = new BorderLayout();
    jScrollPane1 = new JScrollPane();
    chartPanel = new ChartPanel(this);
    bottomPanel = new JPanel();
    zoomInButton = new JButton();
    zoomOutButton = new JButton();
    zoomSlider = new JSlider();
    zoomValueFld = new JTextField();
    spatial = null;
    from = null;
    to = null;
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  private void jbInit()
    throws Exception
  {
    setLayout(borderLayout1);
    setPreferredSize(new Dimension(800, 600));
    setSize(new Dimension(800, 600));
    zoomInButton.setText("Zoom In");
    zoomInButton.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e)
      {
        jButton1_actionPerformed(e);
      }

    });
    zoomOutButton.setText("Zoom Out");
    zoomOutButton.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e)
      {
        jButton2_actionPerformed(e);
      }

    });
    zoomSlider.setPaintTicks(true);
    zoomSlider.addChangeListener(new ChangeListener() {

      public void stateChanged(ChangeEvent evt)
      {
        int slider = ((JSlider)evt.getSource()).getValue();
        double zoom = 50D / (double)slider;
        zoomValueFld.setText(Double.toString(zoom));
        chartPanel.applyZoom(zoom);
      }

    });
    zoomValueFld.setText("1");
    zoomValueFld.setPreferredSize(new Dimension(40, 20));
    zoomValueFld.setHorizontalAlignment(4);
    zoomValueFld.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e)
      {
        zoomValueFld_actionPerformed(e);
      }

    });
    jScrollPane1.getViewport().add(chartPanel, null);
    add(jScrollPane1, "Center");
    bottomPanel.add(zoomInButton, null);
    bottomPanel.add(zoomOutButton, null);
    bottomPanel.add(zoomSlider, null);
    bottomPanel.add(zoomValueFld, null);
    add(bottomPanel, "South");
    double nLat = 38.5D;
    double sLat = 37D;
    double wLong = -124D;
    double eLong = chartPanel.calculateEastG(nLat, sLat, wLong);
    chartPanel.setEastG(eLong);
    chartPanel.setWestG(wLong);
    chartPanel.setNorthL(nLat);
    chartPanel.setSouthL(sLat);
    chartPanel.setHorizontalGridInterval(0.5D);
    chartPanel.setVerticalGridInterval(0.5D);
    chartPanel.setWithScale(true);
  }

  private void jButton1_actionPerformed(ActionEvent e)
  {
    chartPanel.zoomIn();
  }

  private void jButton2_actionPerformed(ActionEvent e)
  {
    chartPanel.zoomOut();
  }

  public void chartPanelPaintComponent(Graphics gr)
  {
    if(spatial == null)
      spatial = new Spatial();
    spatial.drawChart(chartPanel, gr);
    if(from != null && to != null)
    {
      drawRhumbLine(gr, from.getL(), from.getG(), to.getL(), to.getG());
      plotGreatCircle(gr, from.getL(), from.getG(), to.getL(), to.getG());
    }
    
    Point pt = chartPanel.getPanelPoint(37.75, -124.1);
    gr.setColor(Color.red);
    gr.drawOval(pt.x - 2, pt.y - 2, 4, 4);    

    pt = chartPanel.getPanelPoint(37.75, -123.9);
    gr.drawOval(pt.x - 2, pt.y - 2, 4, 4);    
  }

  private void drawRhumbLine(Graphics g, double ls, double gs, double lf, 
      double gf)
  {
    g.setColor(Color.red);
    java.awt.Point start = chartPanel.getPanelPoint(ls, gs);
    java.awt.Point finish = chartPanel.getPanelPoint(lf, gf);
    if(start != null && finish != null)
      g.drawLine(start.x, start.y, finish.x, finish.y);
  }

  private void plotGreatCircle(Graphics g, double ls, double gs, double lf, 
      double gf)
  {
    g.setColor(Color.blue);
    GreatCircle gc = new GreatCircle();
    gc.setStart(new GeoPoint(Math.toRadians(ls), Math.toRadians(gs)));
    gc.setArrival(new GeoPoint(Math.toRadians(lf), Math.toRadians(gf)));
    gc.calculateGreatCircle(20);
    Vector route = gc.getRoute();
    Enumeration enumeration = route.elements();
    GreatCircleWayPoint gcwp;
    for(GreatCircleWayPoint previous = null; enumeration.hasMoreElements(); previous = gcwp)
    {
      gcwp = (GreatCircleWayPoint)enumeration.nextElement();
      java.awt.Point b = chartPanel.getPanelPoint(Math.toDegrees(gcwp.getPoint().getL()), Math.toDegrees(gcwp.getPoint().getG()));
      g.drawOval(b.x - 2, b.y - 2, 4, 4);
      if(previous != null)
      {
        java.awt.Point a = chartPanel.getPanelPoint(Math.toDegrees(previous.getPoint().getL()), Math.toDegrees(previous.getPoint().getG()));
        g.drawLine(a.x, a.y, b.x, b.y);
      }
    }

  }

  public boolean onEvent(EventObject e, int type)
  {
    if(type == 0)
    {
      MouseEvent me = (MouseEvent)e;
      int x = me.getX();
      int y = me.getY();
      if(to != null)
        from = to = null;
      GeoPoint gp = chartPanel.getGeoPos(x, y);
      if(from == null)
      {
        from = gp;
      } else
      {
        to = gp;
        GreatCircle gc = new GreatCircle();
        gc.setStart(new GeoPoint(Math.toRadians(from.getL()), Math.toRadians(from.getG())));
        gc.setArrival(new GeoPoint(Math.toRadians(to.getL()), Math.toRadians(to.getG())));
        gc.calculateGreatCircle(20);
        double gcDist = gc.getDistance();
        gc.calculateRhumLine();
        double rlDist = gc.getRhumbLineDistance();
        double rlZ = gc.getRhumbLineRoute();
        DecimalFormat df = new DecimalFormat("##0.00");
        String mess = "GC:" + df.format(Math.toDegrees(gcDist * 60D)) + "'\n" + "RL:" + df.format(rlDist) + "'\n" + " Z:" + df.format(Math.toDegrees(rlZ)) + " true";
        JOptionPane.showMessageDialog(this, mess, "Route", 1);
      }
    }
    return true;
  }

  private void zoomValueFld_actionPerformed(ActionEvent e)
  {
    System.out.println("Action performed on the field");
  }

  public String getMessForTooltip()
  {
    return null;
  }

  public boolean replaceMessForTooltip()
  {
    return false;
  }

  public void videoCompleted() {}
  public void videoFrameCompleted(Graphics g, Point p) {}

  private BorderLayout borderLayout1;
  private JScrollPane jScrollPane1;
  private ChartPanel chartPanel;
  private JPanel bottomPanel;
  private JButton zoomInButton;
  private JButton zoomOutButton;
  private JSlider zoomSlider;
  private JTextField zoomValueFld;
  Spatial spatial;
  GeoPoint from;
  GeoPoint to;


  public void zoomFactorHasChanged(double d)
  {
  }

  public void chartDDZ(double top, double bottom, double left, double right)
  {
  }
}
