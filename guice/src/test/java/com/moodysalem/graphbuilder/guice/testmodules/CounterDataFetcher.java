package com.moodysalem.graphbuilder.guice.testmodules;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.concurrent.atomic.AtomicLong;

class CounterDataFetcher implements DataFetcher<Long> {

  private final AtomicLong counter;

  @Inject
  public CounterDataFetcher(@Named("counter") AtomicLong counter) {
    this.counter = counter;
  }

  @Override
  public Long get(DataFetchingEnvironment dataFetchingEnvironment) {
    return counter.get();
  }
}
