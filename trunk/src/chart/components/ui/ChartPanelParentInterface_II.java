package chart.components.ui;

import java.awt.Graphics;

import java.util.EventObject;

public interface ChartPanelParentInterface_II
  extends ChartPanelParentInterface
{
  public abstract void chartPanelPaintComponentAfter(Graphics g);
  public abstract void afterEvent(EventObject eventobject, int i);
}
