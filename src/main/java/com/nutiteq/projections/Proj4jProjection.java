package com.nutiteq.projections;

import java.util.Arrays;

import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.CoordinateTransform;
import org.osgeo.proj4j.CoordinateTransformFactory;
import org.osgeo.proj4j.ProjCoordinate;

import com.nutiteq.components.Bounds;
import com.nutiteq.components.Envelope;
import com.nutiteq.components.MapPos;
import com.nutiteq.components.MutableMapPos;
import com.nutiteq.log.Log;
import com.nutiteq.projections.Projection;
import com.nutiteq.utils.GeomUtils;
import com.nutiteq.utils.Utils;

/**
 * Abstract base class for Proj4j-based projections.
 * See http://trac.osgeo.org/proj4j/
 */
public abstract class Proj4jProjection extends Projection {
  private static final double DIVIDE_THRESHOLD_PER_UNIT = 1.0 / 360 * 10; // approx number of units (depends on bounds) for each additional tesselation level when calculating envelope
  private static final double DIVIDE_EPSILON = 1.0e-8;
  private static final double INTERNAL_DIVIDE_THRESHOLD = 10.0; // approx number of degrees in WGS84 for each additional tesselation level when calculating envelope
  private static final double INTERNAL_DIVIDE_EPSILON_PER_UNIT = 1.0e-8;

  private final ZScaleCalculator defaultZScaleCalculator = new ZScaleCalculator() {
    @Override
    public double getZScale(double lon, double lat) {
      return bounds.getWidth();
    }
  };

  private final GeomUtils.MapPosTransformation toInternalTransform = new GeomUtils.MapPosTransformation() {
    @Override
    public MapPos transform(MapPos src) {
      return toInternal(src.x, src.y);
    }    
  };

  private final GeomUtils.MapPosTransformation fromInternalTransform = new GeomUtils.MapPosTransformation() {
    @Override
    public MapPos transform(MapPos src) {
      return fromInternal(src.x, src.y);
    }    
  };

  private static final String WGS84_PARAM = "+title=long/lat:WGS84 +proj=longlat +datum=WGS84 +units=degrees";

  private final CoordinateReferenceSystem projection;
  private final Bounds bounds;
  private final String name;
  private final String[] args;
  private final ZScaleCalculator zScaleCalculator;
  
  private final CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
  private final CRSFactory crsFactory = new CRSFactory();
  private final CoordinateReferenceSystem WGS84;
  private CoordinateTransform trans;
  private CoordinateTransform invTrans;

  
  /**
   * Interface for calculating Z-coordinate (height) scale from latitude/longitude.
   */
  public static interface ZScaleCalculator {
    /**
     * Calculate height scale for given point.
     * 
     * @param lon
     *        longitude in WGS84 system (-180..180)
     * @param lat
     *        latitude in WGS84 system (-90..90)
     * @return height scale at given point
     */
    double getZScale(double lon, double lat);
  }

  /**
   * Default constructor. Construct projection from valid JavaProj.4 name and map bounds. Z coordinate is not changed during projection.
   * 
   * @param name
   *        JavaProj.4 projection name
   * @param bounds
   *        bounds for this projection
   */
  public Proj4jProjection(String name, Bounds bounds) {
    this.WGS84 = crsFactory.createFromParameters("WGS84", WGS84_PARAM);
    this.projection = crsFactory.createFromName(name);
    this.bounds = bounds;
    this.name = name;
    this.args = new String[0];
    this.zScaleCalculator = defaultZScaleCalculator;
    
    this.trans = ctFactory.createTransform(this.projection, WGS84);
    this.invTrans = ctFactory.createTransform(WGS84, this.projection);
  }

  /**
   * Construct projection from Proj.4 arguments and map bounds. Z coordinate is not changed during projection.
   * 
   * @param args
   *        array of Proj.4 projection specification arguments
   * @param bounds
   *        bounds for this projection
   */
  public Proj4jProjection(String[] args, Bounds bounds) {
    this.WGS84 = crsFactory.createFromParameters("WGS84", WGS84_PARAM);
    this.projection = crsFactory.createFromParameters("MyProjection", args);
    this.bounds = bounds;
    this.name = "";
    this.args = args.clone();
    this.zScaleCalculator = defaultZScaleCalculator;
    
    this.trans = ctFactory.createTransform(this.projection, WGS84);
    this.invTrans = ctFactory.createTransform(WGS84, this.projection);
  }
  
  /**
   * Construct projection from Proj.4 arguments, map bounds and Z-scale calculator.
   * 
   * @param args
   *        array of Proj.4 projection specification arguments
   * @param bounds
   *        bounds for this projection
   * @param zScaleCalculator
   *        Z-scale calculator for this projection.
   */
  public Proj4jProjection(String[] args, Bounds bounds, ZScaleCalculator zScaleCalculator) {
    this.WGS84 = crsFactory.createFromParameters("WGS84", WGS84_PARAM);
    this.projection = crsFactory.createFromParameters("MyProjection", args);
    this.bounds = bounds;
    this.name = "";
    this.args = args.clone();
    this.zScaleCalculator = zScaleCalculator;

    this.trans = ctFactory.createTransform(this.projection, WGS84);
    this.invTrans = ctFactory.createTransform(WGS84, this.projection);
  }

  @Override
  public Bounds getBounds() {
    return bounds;
  }

  @Override
  public MapPos fromWgs84(double lon, double lat) {
    ProjCoordinate src = new ProjCoordinate(lon, Utils.toRange(lat, -90, 90));
    ProjCoordinate dst = new ProjCoordinate();
   
    try {
      synchronized (invTrans) {
        invTrans.transform(src, dst);
      }
    } catch (RuntimeException e) {
      Log.error("Proj4jProjection: fromWgs84 " + lon + ", " + lat + ": " + e);
      throw e;
    }
    return new MapPos(dst.x, dst.y);
  }
  
  @Override
  public MapPos toWgs84(double x, double y) {
    ProjCoordinate src = new ProjCoordinate(x, y);
    ProjCoordinate dst = new ProjCoordinate();
  
    try {
      synchronized (trans) {
        trans.transform(src, dst);
      }
    } catch (RuntimeException e) {
      Log.error("Proj4jProjection: toWgs84 input " + x + ", " + y + ": " + e);
      throw e;
    }
    return new MapPos(dst.x, dst.y);
  }

  @Override
  public void fromInternal(double u, double v, double h, MutableMapPos pos) {
    MapPos projPos = fromWgs84(u, v);
    double zScale = zScaleCalculator.getZScale(u, v);
    pos.setCoords(projPos.x, projPos.y, h * zScale);
  }

  @Override
  public Envelope fromInternal(Envelope envInternal) {
    double divideEpsilon = INTERNAL_DIVIDE_EPSILON_PER_UNIT * Math.min(bounds.getWidth(), bounds.getHeight());
    MapPos[] hullPoints = GeomUtils.transformConvexHull(envInternal.getConvexHull(), fromInternalTransform, INTERNAL_DIVIDE_THRESHOLD, divideEpsilon);
    return new Envelope(hullPoints);
  }

  @Override
  public void toInternal(double x, double y, double z, MutableMapPos posInternal) {
    MapPos wgsPos = toWgs84(x, y);
    double zScale = zScaleCalculator.getZScale(wgsPos.x, wgsPos.y);
    posInternal.setCoords(wgsPos.x, wgsPos.y, z / zScale);
  }
  
  @Override
  public Envelope toInternal(Envelope env) {
    double divideThreshold = DIVIDE_THRESHOLD_PER_UNIT * Math.min(bounds.getWidth(), bounds.getHeight());
    MapPos[] hullPoints = GeomUtils.transformConvexHull(env.getConvexHull(), toInternalTransform, divideThreshold, DIVIDE_EPSILON);
    return new Envelope(hullPoints);
  }

  @Override
  public double[] getInternalFrameMatrix(double u, double v, double h) {
    final double DIFF_STEP = 1.0e-6;

    double zScale = zScaleCalculator.getZScale(u, v);

    double du = 360 * DIFF_STEP;
    double dv = 360 * DIFF_STEP;
    double u0 = Utils.toRange(u - du, -180, 180);
    double u1 = Utils.toRange(u + du, -180, 180);
    double v0 = Utils.toRange(v - dv, -90, 90);
    double v1 = Utils.toRange(v + dv, -90, 90);
    MapPos pu0 = fromWgs84(u0, v);
    MapPos pu1 = fromWgs84(u1, v);
    MapPos pv0 = fromWgs84(u, v0);
    MapPos pv1 = fromWgs84(u, v1);
    
    double c = Math.cos(v * Math.PI / 180);
    double dx = 360 / bounds.getWidth();
    double dy = 360 / bounds.getHeight();

    double[] transform = new double[16];
    transform[0]  = (pu1.x - pu0.x) / (u1 - u0) * dx * dx / c;
    transform[1]  = (pu1.y - pu0.y) / (u1 - u0) * dy * dy;
    transform[4]  = (pv1.x - pv0.x) / (v1 - v0) * dx * dx / c;
    transform[5]  = (pv1.y - pv0.y) / (v1 - v0) * dy * dy;
    transform[10] = 1 / zScale;
    transform[15] = 1;

    return transform;
  }
  
  @Override
  public int hashCode() {
    return name.hashCode() + Arrays.hashCode(args) * 2;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) return false;
    if (this.getClass() != o.getClass()) return false;
    Proj4jProjection other = (Proj4jProjection) o;
    if (!bounds.equals(other.bounds)) return false;
    if (!name.equals(other.name)) return false;
    if (!Arrays.deepEquals(args, other.args)) return false;
    if (!zScaleCalculator.equals(other.zScaleCalculator)) return false;
    return true;
  }
}
