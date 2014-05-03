package com.nutiteq.projections;

import com.nutiteq.components.Bounds;

/**
 * EPSG:31370 - Belge 1972 / Belgian Lambert 72
 * params from http://spatialreference.org/ref/epsg/31370/
 */

public class EPSG31370 extends Proj4jProjection {
  private static final String ARGS[] = "+proj=lcc +lat_1=51.16666723333333 +lat_2=49.8333339 +lat_0=90 +lon_0=4.367486666666666 +x_0=150000.013 +y_0=5400088.438 +ellps=intl +towgs84=106.869,-52.2978,103.724,-0.33657,0.456955,-1.84218,1 +units=m +no_defs".split(" ");
  private static final Bounds BOUNDS = new Bounds(17736.0314, 23697.0977, 297289.9391, 245375.4223);

  /**
   * Default constructor.
   */
  public EPSG31370() {
    super(ARGS, BOUNDS);
  }
  
  /**
   * Constructor with explicit bounds. This can be used for custom raster tiles requiring origin shift/scaling.
   * 
   * @param bounds projection bounds
   */
  public EPSG31370(Bounds bounds) {
    super(ARGS, bounds);
  }

  @Override
  public String name() {
    return "EPSG:31370";
  }
}
