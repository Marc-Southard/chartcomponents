package chart.components.util;

import chart.components.util.local.ChartComponentsResourceBundle;

public class GnlUtilities
{
  public static String buildMessage(String id)
  {
    return buildMessage(id, null);
  }
  
  public static String buildMessage(String id, String[] data)
  {
    String mess = ChartComponentsResourceBundle.getChartComponentsResourceBundle().getString(id); 
    for (int i=0; data != null && i<data.length; i++)
    {
      String toReplace = "{$" + Integer.toString(i+1) + "}";
  //    System.out.println("Replacing " + toReplace + " with " + data[i] + " in " + mess);
      mess = replaceString(mess, toReplace, data[i]);
    }
    return mess;
  }
    
  public static String replaceString(String orig, String oldStr, String newStr)
  {
    String ret = orig;
    int indx = 0;
    for (boolean go = true; go;)
    {
      indx = ret.indexOf(oldStr, indx);
      if (indx < 0)
      {
        go = false;
      } 
      else
      {
        ret = ret.substring(0, indx) + newStr + ret.substring(indx + oldStr.length());
        indx += 1 + oldStr.length();
      }
    }
    return ret;
  }  
}
