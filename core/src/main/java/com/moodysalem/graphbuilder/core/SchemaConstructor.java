package com.moodysalem.graphbuilder.core;

import static graphql.schema.GraphQLSchema.newSchema;
import static java.util.stream.Collectors.mapping;

import com.moodysalem.graphbuilder.core.inputs.SchemaBundle;
import com.moodysalem.graphbuilder.core.inputs.SchemaOptions;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The SchemaConstructor creates a GraphQL schema from lists of bindings.
 */
public final class SchemaConstructor {

  private SchemaConstructor() {
  }

  /**
   * Given a base type and a map of the type names to bound field definitions, return a transformed
   * type that also contains all the mapped children types.
   */
  private static GraphQLObjectType constructType(GraphQLObjectType base,
      Map<String, List<GraphQLFieldDefinition>> childrenFieldsByParentTypeName) {
    return base.transform(objectTypeBuilder -> {
      List<GraphQLFieldDefinition> originalFields = base.getFieldDefinitions();
      List<GraphQLFieldDefinition> addedFields = childrenFieldsByParentTypeName
          .getOrDefault(base.getName(), Collections.emptyList());

      objectTypeBuilder.clearFields().fields(
          Stream.concat(originalFields.stream(), addedFields.stream())
              .map(definition -> {
                if (definition.getType() instanceof GraphQLObjectType) {
                  return definition.transform(
                      definitionBuilder -> definitionBuilder
                          .type(constructType((GraphQLObjectType) definition.getType(),
                              childrenFieldsByParentTypeName))
                  );
                } else {
                  return definition;
                }
              })
              .collect(Collectors.toList())
      );
    });
  }

  /**
   * Create a GraphQL schema from the given inputs and options.
   *
   * @param options options to apply
   * @param schemaBundles bundles of schema to combine into the schema
   * @return GraphQLSchema composed of the input bundles
   */
  public static GraphQLSchema createSchema(
      SchemaOptions options,
      Collection<SchemaBundle> schemaBundles) {
    GraphQLSchema.Builder builder = newSchema();

    // Construct a map of parent type name to the list of child field definition bindings.
    Map<String, List<GraphQLFieldDefinition>> childrenFieldsByParentTypeName =
        schemaBundles.stream()
            .flatMap(bundle -> bundle.getFieldDefinitions().stream())
            .map(binding -> {
              FieldCoordinates coordinates = binding.getPosition().toCoordinates(options);

              GraphQLFieldDefinition fieldDefinition = binding
                  .getFieldDefinitionBuilder().name(coordinates.getFieldName()).build();

              if (fieldDefinition == null) {
                return null;
              }

              return new AbstractMap.SimpleImmutableEntry<>(coordinates, fieldDefinition);
            })
            .filter(Objects::nonNull)
            .collect(Collectors
                .groupingBy(entry -> entry.getKey().getTypeName(),
                    mapping(Map.Entry::getValue, Collectors.toList())));

    GraphQLObjectType queryType = constructType(
        GraphQLObjectType.newObject().name(options.getQueryTypeName()).build(),
        childrenFieldsByParentTypeName);
    GraphQLObjectType mutationType = constructType(
        GraphQLObjectType.newObject().name(options.getMutationTypeName()).build(),
        childrenFieldsByParentTypeName);
    GraphQLObjectType subscriptionType = constructType(
        GraphQLObjectType.newObject().name(options.getSubscriptionTypeName()).build(),
        childrenFieldsByParentTypeName);

    GraphQLCodeRegistry.Builder registry = GraphQLCodeRegistry.newCodeRegistry();

    // Map the data fetchers.
    schemaBundles.stream()
        .flatMap(bundle -> bundle.getFieldDataFetcherFactories().stream())
        .forEach((binding) -> {
          FieldCoordinates fieldCoordinates = binding.getPosition().toCoordinates(options);

          if (registry.hasDataFetcher(fieldCoordinates)) {
            throw new IllegalStateException(String.format(
                "Duplicate DataFetcher/DataFetcherFactory binding at coordinate %s",
                fieldCoordinates));
          }

          registry.dataFetcher(fieldCoordinates,
              binding.getDataFetcherFactory());
        });

    // Map the data fetcher factories.
    schemaBundles.stream().flatMap(bundle -> bundle.getFieldDataFetchers().stream())
        .forEach((binding) -> {
          FieldCoordinates fieldCoordinates = binding.getPosition().toCoordinates(options);

          if (registry.hasDataFetcher(fieldCoordinates)) {
            throw new IllegalStateException(String.format(
                "Duplicate DataFetcher/DataFetcherFactory binding at coordinate %s",
                fieldCoordinates));
          }

          registry.dataFetcher(fieldCoordinates, binding.getDataFetcher());
        });

    // Insert the additional types.
    schemaBundles.stream().flatMap(bundle -> bundle.getAdditionalTypes().stream())
        .forEach(builder::additionalType);

    // Query type cannot be null.
    builder.query(queryType);
    if (!mutationType.getFieldDefinitions().isEmpty()) {
      builder.mutation(mutationType);
    }
    if (!subscriptionType.getFieldDefinitions().isEmpty()) {
      builder.subscription(subscriptionType);
    }

    return builder.codeRegistry(registry.build()).build();
  }
}
