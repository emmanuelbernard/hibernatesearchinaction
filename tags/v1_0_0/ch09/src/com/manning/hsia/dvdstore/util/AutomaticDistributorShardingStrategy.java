package com.manning.hsia.dvdstore.util;

import java.io.Serializable;
import java.util.Properties;

import org.apache.lucene.document.Document;
import org.hibernate.search.store.DirectoryProvider;
import org.hibernate.search.store.IndexShardingStrategy;

import com.manning.hsia.dvdstore.model.Item;

/**
 * Example 9.16
 */
public class AutomaticDistributorShardingStrategy implements
		IndexShardingStrategy {
	private DirectoryProvider<?>[] providers;
	private int shardNbr;

	public void initialize(Properties properties, DirectoryProvider<?>[] providers) {
		this.providers = providers;
		this.shardNbr = Integer.parseInt( properties.getProperty("nbr_of_shards") );
	}
	
	public DirectoryProvider<?>[] getDirectoryProvidersForAllShards() {
		return providers;
	}

	public DirectoryProvider<?> getDirectoryProviderForAddition(Class<?> entityType,
			Serializable id, String idInString, Document document) {
		
		//make sure it is used on the right class
		assert entityType.getName().equals( Item.class.getName() );
		
		String distributorId = document.get("distributor.id");
		int providerIndex = Integer.parseInt(distributorId) - 1;
		
		//ensure we never go over
		assert providerIndex < shardNbr : "The number of distributor are higher than available shards";
		return providers[providerIndex];
	}

	public DirectoryProvider<?>[] getDirectoryProvidersForDeletion(Class<?> entity,
			Serializable id, String idInString) {
		return providers;
	}
}
