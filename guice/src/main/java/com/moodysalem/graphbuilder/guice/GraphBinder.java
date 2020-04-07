package com.moodysalem.graphbuilder.guice;

import com.moodysalem.graphbuilder.core.inputs.FieldDataFetcher;
import com.moodysalem.graphbuilder.core.inputs.FieldDataFetcherFactory;
import com.moodysalem.graphbuilder.core.inputs.FieldDefinition;
import com.moodysalem.graphbuilder.core.inputs.SchemaPosition;
import com.moodysalem.graphbuilder.guice.impl.ClazzDataFetcherBinding;
import com.moodysalem.graphbuilder.guice.impl.ClazzDataFetcherBindingProvider;
import com.moodysalem.graphbuilder.guice.impl.ClazzDataFetcherFactoryBinding;
import com.moodysalem.graphbuilder.guice.impl.ClazzDataFetcherFactoryBindingProvider;
import com.google.inject.Binder;
import com.google.inject.Provider;
import com.google.inject.multibindings.Multibinder;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetcherFactory;
import graphql.schema.GraphQLFieldDefinition;

/**
 * This binder helps in binding {@code GraphQLFieldDefinition}s, {@code DataFetcher} and {@code
 * DataFetcherFactory}s for consumption by the GraphQLSchemaProvider.
 */
public final class GraphBinder {

  public static final class FieldBinder {

    private final Binder binder;
    private final SchemaPosition position;

    private FieldBinder(Binder binder, SchemaPosition position) {
      this.binder = binder;
      this.position = position;
    }

    private Multibinder<FieldDefinition> fieldBinder() {
      return Multibinder.newSetBinder(binder, FieldDefinition.class);
    }

    private Multibinder<FieldDataFetcher> dataFetcherBinder() {
      return Multibinder.newSetBinder(binder, FieldDataFetcher.class);
    }

    private Multibinder<FieldDataFetcherFactory> dataFetcherFactoryBinder() {
      return Multibinder.newSetBinder(binder, FieldDataFetcherFactory.class);
    }

    public FieldBinder definition(GraphQLFieldDefinition.Builder definition) {
      fieldBinder().addBinding().toInstance(definitionBinding(definition));
      return this;
    }

    public FieldDefinition definitionBinding(GraphQLFieldDefinition.Builder definition) {
      return FieldDefinition.of(position, definition);
    }

    public FieldBinder dataFetcher(DataFetcher dataFetcher) {
      dataFetcherBinder().addBinding().toInstance(dataFetcherBinding(dataFetcher));
      return this;
    }

    public FieldDataFetcher dataFetcherBinding(DataFetcher dataFetcher) {
      return FieldDataFetcher.of(position, dataFetcher);
    }

    public FieldBinder dataFetcher(Class<? extends DataFetcher> dataFetcherClazz) {
      dataFetcherBinder().addBinding()
          .toProvider(dataFetcherBinding(dataFetcherClazz));
      return this;
    }

    public Provider<FieldDataFetcher> dataFetcherBinding(
        Class<? extends DataFetcher> dataFetcherClazz) {
      return new ClazzDataFetcherBindingProvider(
          binder.getProvider(ClazzDataFetcherBinding.Factory.class), position,
          dataFetcherClazz);
    }

    public FieldBinder dataFetcherFactory(DataFetcherFactory dataFetcherFactory) {
      dataFetcherFactoryBinder().addBinding()
          .toInstance(dataFetcherFactoryBinding(dataFetcherFactory));
      return this;
    }

    public FieldDataFetcherFactory dataFetcherFactoryBinding(
        DataFetcherFactory dataFetcherFactory) {
      return FieldDataFetcherFactory.of(position, dataFetcherFactory);
    }

    public FieldBinder dataFetcherFactory(
        Class<? extends DataFetcherFactory> dataFetcherFactoryClazz) {
      dataFetcherFactoryBinder().addBinding()
          .toProvider(dataFetcherFactoryBinding(dataFetcherFactoryClazz));
      return this;
    }

    public Provider<FieldDataFetcherFactory> dataFetcherFactoryBinding(
        Class<? extends DataFetcherFactory> dataFetcherFactoryClazz) {
      return new ClazzDataFetcherFactoryBindingProvider(
          binder.getProvider(ClazzDataFetcherFactoryBinding.Factory.class), position,
          dataFetcherFactoryClazz);
    }
  }

  private final Binder binder;

  private GraphBinder(Binder binder) {
    this.binder = binder;
  }

  public static GraphBinder newGraphBinder(Binder binder) {
    return new GraphBinder(binder);
  }

  public FieldBinder field(SchemaPosition position) {
    return new FieldBinder(binder, position);
  }

  public FieldBinder field(String parentType, String fieldName) {
    return new FieldBinder(binder, SchemaPosition.withinType(parentType, fieldName));
  }

  public FieldBinder query(String fieldName) {
    return field(SchemaPosition.query(fieldName));
  }

  public FieldBinder mutation(String fieldName) {
    return field(SchemaPosition.mutation(fieldName));
  }

  public FieldBinder subscription(String fieldName) {
    return field(SchemaPosition.subscription(fieldName));
  }
}
