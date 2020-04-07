package com.moodysalem.graphbuilder.core.inputs;

/**
 * All binding interfaces that contain some information about a specific field inherit from this
 * interface.
 */
interface FieldInformation {

  /**
   * @return the position in the GraphQL schema where the information is associated.
   */
  SchemaPosition getPosition();
}
