package com.nutiteq.datasources.raster;

import java.io.File;
import java.util.List;

import org.mapsforge.core.model.Tag;
import org.mapsforge.core.model.Tile;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.layer.renderer.DatabaseRenderer;
import org.mapsforge.map.layer.renderer.RendererJob;
import org.mapsforge.map.model.DisplayModel;
import org.mapsforge.map.reader.MapDatabase;
import org.mapsforge.map.reader.Way;
import org.mapsforge.map.reader.header.FileOpenResult;
import org.mapsforge.map.rendertheme.XmlRenderTheme;

import android.app.Application;
import android.graphics.Bitmap;

import com.nutiteq.components.MapTile;
import com.nutiteq.components.TileBitmap;
import com.nutiteq.log.Log;
import com.nutiteq.projections.Projection;
import com.nutiteq.rasterdatasources.AbstractRasterDataSource;

/**
 * Data source for Mapsforge raster tiles. 
 * Compatible with MapsForge 0.4.0 (rescue). 
 *    Build: https://ci.mapsforge.org/job/mapsforge-rescue/60/
 *    Revision: a261718c5845178844703b40ef7a536073c72b49
 * @author jaak
 *
 */
public class MapsforgeRasterDataSource extends AbstractRasterDataSource {
    private static final float DEFAULT_TEXT_SCALE = 1;

    private XmlRenderTheme theme;
    private MapDatabase mapDatabase;
    private DatabaseRenderer databaseRenderer;
    private File mapFile;
    private boolean isTransparent;
    private DisplayModel displayModel;


    public MapsforgeRasterDataSource(Projection projection, int minZoom, int maxZoom, File path, MapDatabase db, XmlRenderTheme theme, Application app) {
        super(projection, minZoom, maxZoom);

        this.mapDatabase = db;
        this.mapFile = path;
        
        AndroidGraphicFactory.createInstance(app);
        this.databaseRenderer = new DatabaseRenderer(this.mapDatabase, AndroidGraphicFactory.INSTANCE);
 
        this.theme = theme;
        this.displayModel = new DisplayModel();
    }

    public MapDatabase getMapDatabase() {
        return mapDatabase;
    }

    @Override
    synchronized public TileBitmap loadTile(MapTile tile) {
        long startTime = System.currentTimeMillis();
        
        RendererJob rendererJob = new RendererJob(new Tile(tile.x, tile.y, (byte) tile.zoom), this.mapFile, this.theme, this.displayModel, DEFAULT_TEXT_SCALE,
                this.isTransparent);

        org.mapsforge.core.graphics.TileBitmap bitmapMf = this.databaseRenderer.executeJob(rendererJob);

        Bitmap bitmap =  AndroidGraphicFactory.getBitmap(bitmapMf);

        long endTime = System.currentTimeMillis();
        Log.debug("MapsforgeRasterDataSource: run time: " + (endTime-startTime) + " ms tile:" + tile);
        
        return new TileBitmap(bitmap);
    }
    
    

}
