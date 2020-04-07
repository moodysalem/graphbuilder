package com.moodysalem.graphbuilder.guice;

import com.moodysalem.graphbuilder.core.SchemaConstructor;
import com.moodysalem.graphbuilder.core.inputs.FieldDataFetcher;
import com.moodysalem.graphbuilder.core.inputs.FieldDataFetcherFactory;
import com.moodysalem.graphbuilder.core.inputs.FieldDefinition;
import com.moodysalem.graphbuilder.core.inputs.SchemaBundle;
import com.moodysalem.graphbuilder.core.inputs.SchemaOptions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class GraphQLSchemaProvider implements Provider<GraphQLSchema> {

  private final Provider<SchemaOptions> optionsProvider;
  private final Provider<Collection<Provider<FieldDefinition>>> fieldDefinitionBindingProviders;
  private final Provider<Collection<Provider<FieldDataFetcherFactory>>> dataFetcherFactoryBindingProviders;
  private final Provider<Collection<Provider<FieldDataFetcher>>> dataFetcherBindingProviders;
  private final Provider<Collection<Provider<SchemaBundle>>> schemaBundleBindingProviders;
  private final Provider<Collection<Provider<GraphQLType>>> additionalTypeProviders;
  private final Provider<Collection<Provider<GraphQLDirective>>> additionalDirectiveProviders;

  @Inject
  public GraphQLSchemaProvider(
      Provider<SchemaOptions> optionsProvider,
      Provider<Collection<Provider<FieldDefinition>>> fieldDefinitionBindingProviders,
      Provider<Collection<Provider<FieldDataFetcherFactory>>> dataFetcherFactoryBindingProviders,
      Provider<Collection<Provider<FieldDataFetcher>>> dataFetcherBindingProviders,
      Provider<Collection<Provider<SchemaBundle>>> schemaBundleBindingProviders,
      Provider<Collection<Provider<GraphQLType>>> additionalTypeProviders,
      Provider<Collection<Provider<GraphQLDirective>>> additionalDirectiveProviders) {
    this.optionsProvider = optionsProvider;
    this.fieldDefinitionBindingProviders = fieldDefinitionBindingProviders;
    this.dataFetcherFactoryBindingProviders = dataFetcherFactoryBindingProviders;
    this.dataFetcherBindingProviders = dataFetcherBindingProviders;
    this.schemaBundleBindingProviders = schemaBundleBindingProviders;
    this.additionalTypeProviders = additionalTypeProviders;
    this.additionalDirectiveProviders = additionalDirectiveProviders;
  }

  /**
   * Turns the collection of providers of T into a collection of T.
   *
   * @param <T> type of the providers
   */
  private static <T> Collection<T> mapProviders(Provider<Collection<Provider<T>>> providers) {
    return providers.get().stream()
        .map(Provider::get)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  /**
   * Produce the GraphQL schema by combining all the set bindings.
   */
  public GraphQLSchema get() {
    SchemaOptions options = optionsProvider.get();

    Collection<SchemaBundle> schemaBundles = mapProviders(schemaBundleBindingProviders);
    Collection<FieldDefinition> fieldDefinitions = mapProviders(fieldDefinitionBindingProviders);
    Collection<FieldDataFetcher> dataFetchers = mapProviders(dataFetcherBindingProviders);
    Collection<FieldDataFetcherFactory> dataFetcherFactories = mapProviders(
        dataFetcherFactoryBindingProviders);
    Collection<GraphQLType> additionalTypes = mapProviders(additionalTypeProviders);
    Collection<GraphQLDirective> additionalDirectives = mapProviders(additionalDirectiveProviders);

    return SchemaConstructor.createSchema(
        options,
        Stream.concat(
            Stream.of(SchemaBundle
                .of(fieldDefinitions, dataFetchers, dataFetcherFactories, additionalTypes,
                    additionalDirectives)),
            schemaBundles.stream()
        ).collect(Collectors.toList())
    );
  }
}
