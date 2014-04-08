package com.nutiteq.projections;

import com.nutiteq.components.Bounds;

/**
 * EPSG:31370 - Belge 1972 / Belgian Lambert 72
 * params from http://spatialreference.org/ref/epsg/31370/
 */

public class EPSG31370 extends Proj4jProjection {
  private static final String ARGS[] = "+proj=lcc +lat_1=51.16666723333333 +lat_2=49.8333339 +lat_0=90 +lon_0=4.367486666666666 +x_0=150000.013 +y_0=5400088.438 +ellps=intl +towgs84=106.869,-52.2978,103.724,-0.33657,0.456955,-1.84218,1 +units=m +no_defs".split(" ");

  // These are global bounds in Google Mercator, change if different tiling is used
  private static final Bounds BOUNDS = new Bounds(-20037508.34f, 20037508.34f, 20037508.34f, -20037508.34f);

  // EPSG31370 projected should be 17736.0314, 23697.0977, 297289.9391, 245375.4223
  //   but if (as) this is not square, then it would mess up tiling. So better use global tiling BOUNDS or modify following to make it square
  //  private static final Bounds BOUNDS = new Bounds(17736.0314, 23697.0977, 297289.9391, 245375.4223);

  /**
   * Default constructor.
   */
  public EPSG31370() {
    super(ARGS, BOUNDS);
  }
  
  @Override
  public String name() {
    return "EPSG:31370";
  }
}
