package com.moodysalem.graphbuilder.guice.impl;

import com.moodysalem.graphbuilder.core.inputs.FieldDataFetcher;
import com.moodysalem.graphbuilder.core.inputs.SchemaPosition;
import com.moodysalem.graphbuilder.guice.impl.ClazzDataFetcherBinding.Factory;
import com.google.inject.Provider;
import graphql.schema.DataFetcher;

/**
 * This is what is bound to the field data fetcher interface when a clazz data fetcher is
 * specified.
 */
public class ClazzDataFetcherBindingProvider implements
    Provider<FieldDataFetcher> {

  private final Provider<ClazzDataFetcherBinding.Factory> factory;
  private final SchemaPosition position;
  private final Class<? extends DataFetcher> clazz;

  public ClazzDataFetcherBindingProvider(
      Provider<Factory> factory,
      SchemaPosition position, Class<? extends DataFetcher> clazz) {
    this.factory = factory;
    this.position = position;
    this.clazz = clazz;
  }

  @Override
  public FieldDataFetcher get() {
    return factory.get().create(position, clazz);
  }
}
