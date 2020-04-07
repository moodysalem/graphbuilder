package com.moodysalem.graphbuilder.core.inputs;

import graphql.schema.GraphQLFieldDefinition;

/**
 * Binds a GraphQL field definition to a position. This stores a {@code
 * GraphQLFieldDefinition.Builder} rather than the {@code GraphQLFieldDefinition} because the {@code
 * name} is not required nor used in the final schema-only the field name from the {@code
 * SchemaPosition} is used.
 */
public interface FieldDefinition extends FieldInformation {

  GraphQLFieldDefinition.Builder getFieldDefinitionBuilder();

  static FieldDefinition of(SchemaPosition position,
      GraphQLFieldDefinition.Builder dataFetcherFactory) {
    return new FieldDefinition.Impl(position, dataFetcherFactory);
  }

  class Impl implements FieldDefinition {

    private final SchemaPosition position;
    private final GraphQLFieldDefinition.Builder fieldDefinitionBuilder;

    private Impl(SchemaPosition position, GraphQLFieldDefinition.Builder fieldDefinitionBuilder) {
      this.position = position;
      this.fieldDefinitionBuilder = fieldDefinitionBuilder;
    }

    @Override
    public SchemaPosition getPosition() {
      return position;
    }

    @Override
    public GraphQLFieldDefinition.Builder getFieldDefinitionBuilder() {
      return fieldDefinitionBuilder;
    }
  }
}
