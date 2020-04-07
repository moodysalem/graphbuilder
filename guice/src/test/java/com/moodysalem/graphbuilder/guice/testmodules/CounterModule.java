package com.moodysalem.graphbuilder.guice.testmodules;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Provides a binding to {@code Long} that increments by one each time it is provided.
 *
 * Also provides a binding to the underlying {@code AtomicLong}.
 */
public class CounterModule extends AbstractModule {

  static final String COUNTER_NAME = "counter";
  static final Named NAME = Names.named(COUNTER_NAME);

  private final AtomicLong counter = new AtomicLong();

  @Override
  protected void configure() {
    bind(AtomicLong.class).annotatedWith(NAME)
        .toInstance(counter);

    bind(Long.class).annotatedWith(NAME)
        .toProvider(counter::get)
        .in(Scopes.NO_SCOPE);
  }
}
