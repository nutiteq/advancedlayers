package com.nutiteq.datasources.vector;

import java.util.Collection;

import com.nutiteq.geometry.VectorElement;
import com.nutiteq.vectordatasources.VectorDataSource;

/**
 * Interface for data sources that support text-based queries (for example, SQL databases).
 * This interface contains only a single method for querying elements based on filter.
 *
 * @author mtehver
 */
public interface QueryableVectorDataSource<T extends VectorElement> extends VectorDataSource<T> {

    /**
     * Query data source elements based on filter.
     * 
     * @param filter
     *          text-based filter in data-source specific format. Can be null, in that case all data source elements are returned.
     * @return collection of data source elements corresponding to query.
     */
    Collection<T> queryElements(String filter);
}
