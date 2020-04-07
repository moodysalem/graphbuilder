package com.moodysalem.graphbuilder.guice.testmodules;

import static graphql.Scalars.GraphQLLong;

import com.moodysalem.graphbuilder.guice.GraphBinder;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLNonNull;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Adds a field counter to the schema that increments each time the schema is constructed.
 */
public class MutateCounterQueryModule extends AbstractModule {

  @Override
  protected void configure() {
    requireBinding(Key.get(AtomicLong.class, CounterModule.NAME));

    GraphBinder.newGraphBinder(binder())
        .mutation("moveCounter")
        .definition(GraphQLFieldDefinition.newFieldDefinition()
            .type(GraphQLLong)
            .argument(
                GraphQLArgument.newArgument().type(GraphQLNonNull.nonNull(GraphQLLong))
                    .name("by")
                    .build()))
        .dataFetcher(MutateCounterDataFetcher.class);
  }

}
