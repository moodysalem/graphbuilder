package com.moodysalem.graphbuilder.guice.impl;

import com.moodysalem.graphbuilder.core.inputs.FieldDataFetcherFactory;
import com.moodysalem.graphbuilder.core.inputs.SchemaPosition;
import com.moodysalem.graphbuilder.guice.impl.ClazzDataFetcherFactoryBinding.Factory;
import com.google.inject.Provider;
import graphql.schema.DataFetcherFactory;

/**
 * This is what is bound to the field data fetcher factory interface when a clazz data fetcher
 * factory is specified.
 */
public class ClazzDataFetcherFactoryBindingProvider implements
    Provider<FieldDataFetcherFactory> {

  private final Provider<Factory> factory;
  private final SchemaPosition position;
  private final Class<? extends DataFetcherFactory> clazz;

  public ClazzDataFetcherFactoryBindingProvider(
      Provider<Factory> factory,
      SchemaPosition position, Class<? extends DataFetcherFactory> clazz) {
    this.factory = factory;
    this.position = position;
    this.clazz = clazz;
  }

  @Override
  public FieldDataFetcherFactory get() {
    return factory.get().create(position, clazz);
  }
}
