package com.moodysalem.graphbuilder.guice.testmodules;

import static com.moodysalem.graphbuilder.guice.testmodules.CounterModule.COUNTER_NAME;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.concurrent.atomic.AtomicLong;

public class MutateCounterDataFetcher implements DataFetcher<Long> {

  private final AtomicLong counter;

  @Inject
  public MutateCounterDataFetcher(@Named(COUNTER_NAME) AtomicLong counter) {
    this.counter = counter;
  }

  @Override
  public Long get(DataFetchingEnvironment dataFetchingEnvironment) {
    Long by = dataFetchingEnvironment.getArgument("by");
    return counter.addAndGet(by);
  }
}
