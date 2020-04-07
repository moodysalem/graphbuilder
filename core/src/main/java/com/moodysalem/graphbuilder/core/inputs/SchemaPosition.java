package com.moodysalem.graphbuilder.core.inputs;

import graphql.schema.FieldCoordinates;

/**
 * Similar to FieldCoordinates, but allows specifying a position that corresponds to top level
 * fields without knowing the name of the top level query, mutation or subscription types.
 */
public interface SchemaPosition {

  final class TopLevel implements SchemaPosition {

    enum Type {
      QUERY,
      MUTATION,
      SUBSCRIPTION
    }

    private final Type type;
    private final String fieldName;

    TopLevel(Type type, String fieldName) {
      this.type = type;
      this.fieldName = fieldName;
    }

    @Override
    public FieldCoordinates toCoordinates(SchemaOptions options) {
      switch (type) {
        case QUERY:
          return FieldCoordinates.coordinates(options.getQueryTypeName(), fieldName);
        case MUTATION:
          return FieldCoordinates.coordinates(options.getMutationTypeName(), fieldName);
        case SUBSCRIPTION:
          return FieldCoordinates.coordinates(options.getSubscriptionTypeName(), fieldName);
        default:
          throw new IllegalStateException("null type");
      }
    }
  }

  final class WithinType implements SchemaPosition {

    private final FieldCoordinates coordinates;

    private WithinType(FieldCoordinates coordinates) {
      this.coordinates = coordinates;
    }

    @Override
    public FieldCoordinates toCoordinates(SchemaOptions options) {
      return coordinates;
    }
  }

  FieldCoordinates toCoordinates(SchemaOptions options);

  static SchemaPosition query(String fieldName) {
    return new TopLevel(TopLevel.Type.QUERY, fieldName);
  }

  static SchemaPosition mutation(String fieldName) {
    return new TopLevel(TopLevel.Type.MUTATION, fieldName);
  }

  static SchemaPosition subscription(String fieldName) {
    return new TopLevel(TopLevel.Type.SUBSCRIPTION, fieldName);
  }

  static SchemaPosition withinType(String parentType, String fieldName) {
    return new WithinType(FieldCoordinates.coordinates(parentType, fieldName));
  }
}
