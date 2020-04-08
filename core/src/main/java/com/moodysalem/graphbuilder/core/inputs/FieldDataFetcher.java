package com.moodysalem.graphbuilder.core.inputs;

import graphql.schema.DataFetcher;

/**
 * Binds a data fetcher to a schema position.
 */
@SuppressWarnings("rawtypes")
public interface FieldDataFetcher extends FieldInformation {

  DataFetcher getDataFetcher();

  static FieldDataFetcher of(SchemaPosition position, DataFetcher dataFetcher) {
    return new Impl(position, dataFetcher);
  }

  class Impl implements FieldDataFetcher {

    private final SchemaPosition position;
    private final DataFetcher dataFetcher;

    private Impl(SchemaPosition position, DataFetcher dataFetcher) {
      this.position = position;
      this.dataFetcher = dataFetcher;
    }

    @Override
    public DataFetcher getDataFetcher() {
      return dataFetcher;
    }

    @Override
    public SchemaPosition getPosition() {
      return position;
    }
  }
}
