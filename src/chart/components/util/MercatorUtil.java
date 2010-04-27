package chart.components.util;

public final class MercatorUtil
{

  public MercatorUtil()
  {
  }

  /**
   * Computes the Increasing Latitude. Mercator formula.
   * @param lat in degrees
   * @return Increasing Latitude, in degrees.
   */
  public static double getIncLat(double lat)
  {
    double il = Math.log(Math.tan((Math.PI / 4D) + Math.toRadians(lat) / 2D));
    return Math.toDegrees(il);
  }

  public static double getInvIncLat(double il)
  {
    double ret = 0.0D;
    ret = Math.toRadians(il);
    ret = Math.exp(ret);
    ret = Math.atan(ret);
    ret -= (Math.PI / 4d); // 0.78539816339744828D; 
    ret *= 2;
    ret = Math.toDegrees(ret);
    return ret;
  }

  public static astro.calc.GeoPoint deadReckoning(astro.calc.GeoPoint p, double d, double r)
  {
    return deadReckoning(p.getL(), p.getG(), d, r);
  }
  
  public static astro.calc.GeoPoint deadReckoning(double l, double g, double d, double r)
  {
    double deltaL = (d / 60D) * Math.cos(Math.toRadians(r));
    double l2 = l + deltaL;
    double lc1 = getIncLat(l);
    double lc2 = getIncLat(l2);
    double deltaLc = lc2 - lc1;
    double deltaG = deltaLc * Math.tan(Math.toRadians(r));
    double g2 = g + deltaG;
    return new astro.calc.GeoPoint(l2, g2);
  }
  
  public static void main(String[] args)
  {
    double d = getIncLat(45D);
    System.out.println("IncLat(45)=" + d);
    System.out.println("Rad(45)=" + Math.toRadians(45D));
  }  
}
