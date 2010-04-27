package chart.components.ui;

import java.awt.Graphics;
import java.awt.Point;

import java.util.EventObject;

public interface ChartPanelParentInterface
{
  public abstract void chartPanelPaintComponent(Graphics g);
  public abstract boolean onEvent(EventObject eventobject, int i); // return false to override the default behavior
  public abstract String getMessForTooltip();
  public abstract boolean replaceMessForTooltip();
  public abstract void videoCompleted();
  public abstract void videoFrameCompleted(Graphics g, Point p);
  public abstract void zoomFactorHasChanged(double d);
  public abstract void chartDDZ(double top, double bottom, double left, double right);
}
