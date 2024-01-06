/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.graphql;

import org.dataloader.DataLoader;

import java.util.Map;
import java.util.Set;

/**
 * The idea of making data loader factory is to make it automate registry of
 * data loaders and move
 * data loaders into separate class
 */
public interface DataLoaderFactory<K, V> {
	String dataLoaderName();

	DataLoader<K, V> getDataLoader();

	Map<K, V> getData(Set<K> entities);
}
