package com.moodysalem.graphbuilder.guice.impl;

import com.moodysalem.graphbuilder.core.inputs.FieldDataFetcher;
import com.moodysalem.graphbuilder.core.inputs.SchemaPosition;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.assistedinject.Assisted;
import graphql.schema.DataFetcher;

/**
 * A field data fetcher that uses the provided Guice injector to construct the data fetcher class.
 */
@SuppressWarnings("rawtypes")
public class ClazzDataFetcherBinding implements FieldDataFetcher {

  private final Provider<Injector> injector;
  private final SchemaPosition position;
  private final Class<? extends DataFetcher> clazz;

  public interface Factory {

    ClazzDataFetcherBinding create(
        SchemaPosition position,
        Class<? extends DataFetcher> clazz);
  }

  @Inject
  public ClazzDataFetcherBinding(
      Provider<Injector> injector, @Assisted SchemaPosition position,
      @Assisted Class<? extends DataFetcher> clazz) {
    this.injector = injector;
    this.position = position;
    this.clazz = clazz;
  }

  @Override
  public SchemaPosition getPosition() {
    return position;
  }

  @Override
  public DataFetcher getDataFetcher() {
    return injector.get().getProvider(clazz).get();
  }
}
