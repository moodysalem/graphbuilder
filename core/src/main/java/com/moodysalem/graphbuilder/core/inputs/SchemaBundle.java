package com.moodysalem.graphbuilder.core.inputs;

import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Contains a set of data fetcher, data fetcher factory and field definition bindings. Useful when a
 * batch of schema pieces are provided from a method.
 */
public interface SchemaBundle {

  Collection<FieldDefinition> getFieldDefinitions();

  Collection<FieldDataFetcherFactory> getFieldDataFetcherFactories();

  Collection<FieldDataFetcher> getFieldDataFetchers();

  Collection<GraphQLType> getAdditionalTypes();

  Collection<GraphQLDirective> getAdditionalDirectives();

  SchemaBundle EMPTY = of(null, null, null, null, null);

  /**
   * Creates a bundle of definitions.
   */
  static SchemaBundle ofDefinitions(Collection<FieldDefinition> definitions) {
    return of(Objects.requireNonNull(definitions), null, null, null, null);
  }

  /**
   * Creates a bundle of data fetchers.
   */
  static SchemaBundle ofDataFetchers(Collection<FieldDataFetcher> dataFetchers) {
    return of(null, Objects.requireNonNull(dataFetchers), null, null, null);
  }

  /**
   * Creates a bundle of data fetcher factories.
   */
  static SchemaBundle ofDataFetcherFactories(
      Collection<FieldDataFetcherFactory> dataFetcherFactories) {
    return of(null, null, Objects.requireNonNull(dataFetcherFactories), null, null);
  }

  /**
   * Creates a bundle of additional types.
   */
  static SchemaBundle ofAdditionalTypes(
      Collection<GraphQLType> additionalTypes) {
    return of(null, null, null, Objects.requireNonNull(additionalTypes), null);
  }

  /**
   * Creates a bundle of additional directives.
   */
  static SchemaBundle ofAdditionalDirectives(
      Collection<GraphQLDirective> additionalDirectives) {
    return of(null, null, null, null, Objects.requireNonNull(additionalDirectives));
  }

  /**
   * Creates a bundle of a combination of different types.
   */
  static SchemaBundle of(
      Collection<FieldDefinition> fieldDefinitions,
      Collection<FieldDataFetcher> dataFetchers,
      Collection<FieldDataFetcherFactory> dataFetcherFactories,
      Collection<GraphQLType> additionalTypes,
      Collection<GraphQLDirective> additionalDirectives) {
    return new Impl(fieldDefinitions, dataFetchers, dataFetcherFactories, additionalTypes,
        additionalDirectives);
  }

  static SchemaBundle combine(SchemaBundle... bundles) {
    List<FieldDefinition> fieldDefinitions = Stream.of(bundles).flatMap(
        bundle -> bundle.getFieldDefinitions().stream()
    ).collect(Collectors.toList());
    List<FieldDataFetcher> dataFetchers = Stream.of(bundles).flatMap(
        bundle -> bundle.getFieldDataFetchers().stream()
    ).collect(Collectors.toList());
    List<FieldDataFetcherFactory> dataFetcherFactories = Stream.of(bundles).flatMap(
        bundle -> bundle.getFieldDataFetcherFactories().stream()
    ).collect(Collectors.toList());
    List<GraphQLType> additionalTypes = Stream.of(bundles).flatMap(
        bundle -> bundle.getAdditionalTypes().stream()
    ).collect(Collectors.toList());
    List<GraphQLDirective> additionalDirectives = Stream.of(bundles).flatMap(
        bundle -> bundle.getAdditionalDirectives().stream()
    ).collect(Collectors.toList());

    return of(
        fieldDefinitions,
        dataFetchers,
        dataFetcherFactories,
        additionalTypes,
        additionalDirectives);
  }

  class Impl implements SchemaBundle {

    private final Collection<FieldDefinition> fieldDefinitions;
    private final Collection<FieldDataFetcher> dataFetchers;
    private final Collection<FieldDataFetcherFactory> dataFetcherFactories;
    private final Collection<GraphQLType> additionalTypes;
    private final Collection<GraphQLDirective> additionalDirectives;

    private Impl(
        Collection<FieldDefinition> fieldDefinitions,
        Collection<FieldDataFetcher> dataFetchers,
        Collection<FieldDataFetcherFactory> dataFetcherFactories,
        Collection<GraphQLType> additionalTypes,
        Collection<GraphQLDirective> additionalDirectives) {
      this.fieldDefinitions = copyToUnmodifiableList(fieldDefinitions);
      this.dataFetchers = copyToUnmodifiableList(dataFetchers);
      this.dataFetcherFactories = copyToUnmodifiableList(dataFetcherFactories);
      this.additionalTypes = copyToUnmodifiableList(additionalTypes);
      this.additionalDirectives = copyToUnmodifiableList(additionalDirectives);
    }

    /**
     * Helper method for copying an input collection into an unmodifiable list.
     */
    private static <T> Collection<T> copyToUnmodifiableList(Collection<T> input) {
      if (input == null) {
        return Collections.emptyList();
      }
      List<T> list = new ArrayList<>(input.size());
      list.addAll(input);
      return Collections.unmodifiableList(list);
    }

    @Override
    public Collection<FieldDataFetcher> getFieldDataFetchers() {
      return dataFetchers;
    }

    @Override
    public Collection<FieldDataFetcherFactory> getFieldDataFetcherFactories() {
      return dataFetcherFactories;
    }

    @Override
    public Collection<FieldDefinition> getFieldDefinitions() {
      return fieldDefinitions;
    }

    @Override
    public Collection<GraphQLType> getAdditionalTypes() {
      return additionalTypes;
    }

    @Override
    public Collection<GraphQLDirective> getAdditionalDirectives() {
      return additionalDirectives;
    }
  }
}
