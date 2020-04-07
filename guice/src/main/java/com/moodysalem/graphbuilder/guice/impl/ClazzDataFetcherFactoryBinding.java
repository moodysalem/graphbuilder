package com.moodysalem.graphbuilder.guice.impl;

import com.moodysalem.graphbuilder.core.inputs.FieldDataFetcherFactory;
import com.moodysalem.graphbuilder.core.inputs.SchemaPosition;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import graphql.schema.DataFetcherFactory;

/**
 * A field data fetcher factory that uses the provided Guice injector to construct the data fetcher
 * class.
 */
public class ClazzDataFetcherFactoryBinding implements FieldDataFetcherFactory {

  private final Provider<Injector> injector;
  private final SchemaPosition position;
  private final Class<? extends DataFetcherFactory> clazz;

  public interface Factory {

    ClazzDataFetcherFactoryBinding create(
        SchemaPosition position,
        Class<? extends DataFetcherFactory> clazz);
  }

  @Inject
  public ClazzDataFetcherFactoryBinding(
      Provider<Injector> injector,
      @Assisted SchemaPosition position,
      @Assisted Class<? extends DataFetcherFactory> clazz) {
    this.injector = injector;
    this.position = position;
    this.clazz = clazz;
  }

  @Override
  public SchemaPosition getPosition() {
    return position;
  }

  @Override
  public DataFetcherFactory getDataFetcherFactory() {
    return injector.get().getProvider(clazz).get();
  }
}
