package com.manning.hsia.dvdstore.util;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import org.apache.lucene.document.Document;
import org.hibernate.search.store.DirectoryProvider;
import org.hibernate.search.store.IndexShardingStrategy;

import com.manning.hsia.dvdstore.model.Item;

/**
 * Example 9.15
 */
public class DistributorShardingStrategy implements
		IndexShardingStrategy {   //implements the interface
	
	private static final String RADIX = "distributors.";
	private DirectoryProvider<?>[] providers;
	private HashMap<String, Integer> providerIdPerDistributor;

	public void initialize(  //initialize the strategy
			Properties properties, 
			DirectoryProvider<?>[] providers) {
		this.providers = providers;     //one directory provider per shard
		this.providerIdPerDistributor = new HashMap<String, Integer>();
		
		//find all properties starting with 'distributors.'
		//the suffix is the distributor id, the value is the shard id
		Enumeration<?> propertyNames = properties.propertyNames();   //read available property names
		
		while ( propertyNames.hasMoreElements() ) {
		
			Object key = propertyNames.nextElement();
			if ( ! String.class.isInstance( key ) ) continue;  //work around the poor design of Properties
			
			String propertyName = (String) key;
			if ( propertyName.startsWith(RADIX) ) {
				String distributorId = propertyName.substring(RADIX.length(), propertyName.length() );
				String providerId = properties.getProperty(propertyName);
				providerIdPerDistributor.put( 
						distributorId, 
						Integer.parseInt(providerId) 
				);
			}
		}
	}
	
	public DirectoryProvider<?>[] getDirectoryProvidersForAllShards() {
		return providers;   //providers for query and optimize
	}

	public DirectoryProvider<?> getDirectoryProviderForAddition(Class<?> entityType,
			Serializable id, String idInString, Document document) {
		//make sure it is used on the right class
		assert entityType.getName().equals( Item.class.getName() );
		
		String distributorId = document.get("distributor.id");  //read discriminator from document
		Integer providerIndex = providerIdPerDistributor.get(distributorId);
		
		if (providerIndex == null) {
			throw new IllegalArgumentException("Distributor id not found: " + distributorId);
		}
		if ( providerIndex > providers.length ) {
			throw new IllegalArgumentException("Shard " +  providerIndex + " does not exists");
		}
		return providers[providerIndex];  //provider where the document is added
	}

	public DirectoryProvider<?>[] getDirectoryProvidersForDeletion(Class<?> entity,
			Serializable id, String idInString) {
		return providers;    //provider where the document is deleted
	}

}
