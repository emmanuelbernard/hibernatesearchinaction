package com.manning.hsia.dvdstore.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.transform.ResultTransformer;

/**
 * Example 6.18
 */
public class ProjectionToMapResultTransformer implements ResultTransformer {

	public Object transformTuple(Object[] tuple, String[] aliases) {  //method called for each result
        Map<String, Object> result = new HashMap<String, Object>( tuple.length );
        for (int i = 0; i < tuple.length; i++) {
            String key = aliases[i];
            if ( key != null ) {
                result.put( key, tuple[i] );
            }
        }
        return result;
    }

    public List transformList(List collection) {
        return collection;
    }

}
