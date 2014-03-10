package com.nutiteq.layers.vector.deprecated;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.nutiteq.components.Envelope;
import com.nutiteq.geometry.Geometry;
import com.nutiteq.geometry.Line;
import com.nutiteq.geometry.Point;
import com.nutiteq.geometry.Text;
import com.nutiteq.projections.Projection;
import com.nutiteq.style.StyleSet;
import com.nutiteq.style.TextStyle;
import com.nutiteq.utils.LongHashMap;
import com.nutiteq.utils.LongMap;
import com.nutiteq.vectorlayers.TextLayer;

/**
 * Special text layer that should be used together with SpatialiteLayer.
 * Requires "name" column values, if exists, and shows as automatic labels
 * 
 * @author jaak
 *
 */
@Deprecated
public abstract class SpatialiteTextLayer extends TextLayer {

    private static final String NAME_COLUMN = "name";

    private final SpatialiteLayer baseLayer;
    private int maxVisibleElements = Integer.MAX_VALUE;

    public SpatialiteTextLayer(Projection projection, SpatialiteLayer baseLayer) {
        super(projection);
        this.baseLayer = baseLayer;
    }

    public SpatialiteLayer getBaseLayer() {
        return baseLayer;
    }

    public void setMaxVisibleElements(int maxElements) {
        this.maxVisibleElements = maxElements;
    }

    public void calculateVisibleElements(List<Geometry> features, int zoom) {
        // Create id-based map from old visible elements. We will keep them if they remain visible
        LongMap<Text> oldVisibleElementsMap = new LongHashMap<Text>();
        List<Text> oldVisibleElementsList = getVisibleElements();
        if (oldVisibleElementsList != null) {
            for (Text text : oldVisibleElementsList) {
                oldVisibleElementsMap.put(text.getId(), text);
            }
        }

        // Create list of new visible elements 
        List<Text> newVisibleElementsList = new ArrayList<Text>();
        for (Geometry feature : features) { // first, add existing elements
            Text element = oldVisibleElementsMap.get(feature.getId());
            if (element != null && newVisibleElementsList.size() < maxVisibleElements) {
                newVisibleElementsList.add(element);
            }
        }
        for (Geometry feature : features) { // now add new elements
            Text element = oldVisibleElementsMap.get(feature.getId());
            if (element == null && newVisibleElementsList.size() < maxVisibleElements) {
                element = createText(feature, zoom);
                if (element != null) {
                    element.attachToLayer(this);
                    newVisibleElementsList.add(element);
                }
            }
        }
        for (Text element : newVisibleElementsList) {
            element.setActiveStyle(zoom);
        }

        // Update visible elements 
        setVisibleElements(newVisibleElementsList);
    }

    @Override
    public void calculateVisibleElements(Envelope env, int zoom) {
        // Do nothing here - other calculateVisibleElements is used instead
    }

    @SuppressWarnings("unchecked")
    protected Text createText(Geometry feature, int zoom) {
        String name = ((Map<String, String>)feature.userData).get(NAME_COLUMN);  

        if (name == null) {
            return null;
        }
        if (name.trim().equals("")) {
            return null;
        }

        // Create base element based on geometry type
        Text.BaseElement baseElement = null;
        if (feature instanceof Line) {
            baseElement = new Text.BaseLine(((Line) feature).getVertexList());
        } else if (feature instanceof Point){
            baseElement = new Text.BasePoint(((Point)feature).getMapPos());

        } else {
            return null;
        }

        // Create styleset for the feature
        StyleSet<TextStyle> styleSet = createStyleSet(feature, zoom);
        if (styleSet == null) {
            return null;
        }

        // Create text. Put unique id to userdata field, that will be used to identify the element later

        Text text = new  Text(baseElement, name, styleSet, null);
        text.setId(feature.getId());
        return text;
    }

    protected abstract StyleSet<TextStyle> createStyleSet(Geometry feature, int zoom);

}
