package com.moodysalem.graphbuilder.guice.testmodules;

import com.moodysalem.graphbuilder.core.inputs.SchemaOptions;
import com.moodysalem.graphbuilder.core.inputs.FieldDataFetcher;
import com.moodysalem.graphbuilder.core.inputs.FieldDefinition;
import com.moodysalem.graphbuilder.core.inputs.SchemaPosition;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.multibindings.ProvidesIntoSet;
import graphql.Scalars;
import graphql.schema.GraphQLFieldDefinition;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Adds a field counter to the schema that increments each time the schema is constructed.
 */
public class GetCounterQueryModule extends AbstractModule {

  @Override
  protected void configure() {
    requireBinding(Key.get(AtomicLong.class, CounterModule.NAME));
  }

  private static final SchemaPosition POSITION = SchemaPosition.query("counter");

  @ProvidesIntoSet
  FieldDefinition provideCounterField(SchemaOptions options) {
    return FieldDefinition
        .of(POSITION, GraphQLFieldDefinition.newFieldDefinition().type(Scalars.GraphQLLong));
  }

  @ProvidesIntoSet
  FieldDataFetcher provideCounterDataFetcher(CounterDataFetcher fetcher) {
    return FieldDataFetcher.of(POSITION, fetcher);
  }
}
