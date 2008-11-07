package com.manning.hsia.dvdstore.model.bridge;

import org.hibernate.search.bridge.StringBridge;

import com.manning.hsia.dvdstore.model.Category;
import com.manning.hsia.dvdstore.model.Item;

/**
 * Add "yes" to the dedicated field if item contains the children category
 * 
 * Example 8.7
 */
public class ChildrenFlagBridge implements StringBridge {
	public String objectToString(Object object) {
		assert object instanceof Item;
		Item item = (Item) object;
		
		boolean hasChildrenCategory = false;
		for ( Category category : item.getCategories() ) {  //retrieve unindexed data
			if ("Children".equalsIgnoreCase( category.getName() ) ) {
				hasChildrenCategory = true;
				break;
			}
		}
		return hasChildrenCategory ? "yes" : "no";  //index useful flag
	}
}
