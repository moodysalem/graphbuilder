package com.moodysalem.graphbuilder.core.inputs;

import graphql.schema.DataFetcherFactory;

/**
 * Binds a data fetcher factory to a schema position.
 */
public interface FieldDataFetcherFactory extends FieldInformation {

  DataFetcherFactory getDataFetcherFactory();

  static FieldDataFetcherFactory of(SchemaPosition position,
      DataFetcherFactory dataFetcherFactory) {
    return new FieldDataFetcherFactory.Impl(position, dataFetcherFactory);
  }

  class Impl implements FieldDataFetcherFactory {

    private final SchemaPosition position;
    private final DataFetcherFactory dataFetcherFactory;

    private Impl(SchemaPosition position, DataFetcherFactory dataFetcherFactory) {
      this.position = position;
      this.dataFetcherFactory = dataFetcherFactory;
    }

    @Override
    public DataFetcherFactory getDataFetcherFactory() {
      return dataFetcherFactory;
    }

    @Override
    public SchemaPosition getPosition() {
      return position;
    }
  }
}
