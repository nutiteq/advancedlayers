package com.nutiteq.datasources.raster;

import java.util.List;

import com.nutiteq.components.MapTile;
import com.nutiteq.components.TileBitmap;
import com.nutiteq.projections.Projection;
import com.nutiteq.rasterdatasources.AbstractRasterDataSource;
import com.nutiteq.rasterdatasources.RasterDataSource;

/**
 * Data source that combines list of existing tile sources by trying to read the tiles from the first datasource,
 * if that fails then from the second datasource, and so on.
 * The main use case is to combine offline (fast) data source with online (slow) data source,
 * the offline data source acts as cache. The second case is to use primary server as the first data source and back up
 * server as the second source.
 */
public class FallbackRasterDataSource extends AbstractRasterDataSource {
  private final List<RasterDataSource> dataSources;
  
  private static Projection getProjection(List<RasterDataSource> dataSources) {
    return dataSources.get(0).getProjection();
  }
  
  private static int getMinZoom(List<RasterDataSource> dataSources) {
    int minZoom = Integer.MAX_VALUE;
    for (RasterDataSource dataSource : dataSources) {
      minZoom = Math.min(minZoom, dataSource.getMinZoom());
    }
    return minZoom;
  }
  
  private static int getMaxZoom(List<RasterDataSource> dataSources) {
    int maxZoom = 0;
    for (RasterDataSource dataSource : dataSources) {
      maxZoom = Math.max(maxZoom, dataSource.getMaxZoom());
    }
    return maxZoom;
  }
  
  /**
   * Default constructor.
   * 
   * @param dataSources
   *          list of tile datasources. Note: the list must be non-empty!
   */
  public FallbackRasterDataSource(List<RasterDataSource> dataSources) {
    super(getProjection(dataSources), getMinZoom(dataSources), getMaxZoom(dataSources));
    this.dataSources = dataSources;
  }

  /**
   * Constructor with explicit list of datasources.
   * 
   * @param dataSources
   *          tile datasources to combine
   */
  public FallbackRasterDataSource(RasterDataSource... dataSources) {
    this(java.util.Arrays.asList(dataSources));
  }
  
  @Override
  public TileBitmap loadTile(MapTile tile) {
    for (RasterDataSource dataSource : dataSources) {
      if (tile.zoom >= dataSource.getMinZoom() && tile.zoom <= dataSource.getMaxZoom()) {
        TileBitmap tileBitmap = dataSource.loadTile(tile);
        if (tileBitmap != null) {
          return tileBitmap;
        }
      }
    }
    return null;
  }

  @Override
  public void addOnChangeListener(OnChangeListener listener) {
    super.addOnChangeListener(listener);
    for (RasterDataSource dataSource : dataSources) {
      dataSource.addOnChangeListener(listener);
    }
  }

  @Override
  public void removeOnChangeListener(OnChangeListener listener) {
    for (RasterDataSource dataSource : dataSources) {
      dataSource.removeOnChangeListener(listener);
    }
    super.removeOnChangeListener(listener);
  }
}
