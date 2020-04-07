package com.moodysalem.graphbuilder.guice.testmodules;

import com.moodysalem.graphbuilder.core.inputs.FieldDataFetcher;
import com.moodysalem.graphbuilder.core.inputs.FieldDefinition;
import com.moodysalem.graphbuilder.core.inputs.SchemaPosition;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.multibindings.ProvidesIntoSet;
import com.google.inject.name.Named;
import graphql.Scalars;
import graphql.schema.GraphQLFieldDefinition;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Adds a field counter to the schema that increments each time the schema is constructed.
 */
public class ConstructionCounterQueryModule extends AbstractModule {

  @Override
  protected void configure() {
    requireBinding(Key.get(Long.class, CounterModule.NAME));
  }

  private static final SchemaPosition POSITION = SchemaPosition.query("constructions");

  @ProvidesIntoSet
  FieldDefinition provideCounterField() {
    return FieldDefinition
        .of(POSITION, GraphQLFieldDefinition.newFieldDefinition().type(Scalars.GraphQLLong));
  }

  @Inject
  @ProvidesIntoSet
  FieldDataFetcher provideConstructionDataFetcher(
      @Named(CounterModule.COUNTER_NAME) AtomicLong counter) {
    long count = counter.incrementAndGet();
    return FieldDataFetcher.of(POSITION, (environment) -> count);
  }
}
