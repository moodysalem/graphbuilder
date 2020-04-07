package com.moodysalem.graphbuilder.guice.testmodules;

import static com.moodysalem.graphbuilder.guice.testmodules.CounterModule.COUNTER_NAME;

import com.moodysalem.graphbuilder.core.inputs.FieldDataFetcher;
import com.moodysalem.graphbuilder.core.inputs.FieldDefinition;
import com.moodysalem.graphbuilder.core.inputs.SchemaBundle;
import com.moodysalem.graphbuilder.core.inputs.SchemaPosition;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.multibindings.ProvidesIntoSet;
import com.google.inject.name.Named;
import graphql.Scalars;
import graphql.schema.GraphQLFieldDefinition;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Provides schema dynamically based on the injected construction counter.
 */
public class DynamicSchemaModule extends AbstractModule {

  @Override
  protected void configure() {
  }

  @Inject
  @ProvidesIntoSet
  SchemaBundle provideSchema(@Named(COUNTER_NAME) AtomicLong counter) {
    long count = counter.incrementAndGet();
    boolean isOdd = count % 2 == 1;

    if (isOdd) {
      SchemaPosition position = SchemaPosition.query(String.format("query_%d", count));

      return SchemaBundle.combine(
          SchemaBundle.ofDefinitions(
              Collections.singleton(
                  FieldDefinition.of(position, GraphQLFieldDefinition.newFieldDefinition().type(
                      Scalars.GraphQLLong)))
          ),
          SchemaBundle.ofDataFetchers(
              Collections.singleton(FieldDataFetcher.of(position, (environment) -> counter))
          )
      );
    } else {
      return SchemaBundle.EMPTY;
    }
  }

}
