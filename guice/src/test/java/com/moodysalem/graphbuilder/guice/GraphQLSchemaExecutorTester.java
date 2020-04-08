package com.moodysalem.graphbuilder.guice;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.base.Splitter;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Wraps an injector and provides helper methods for executing queries against the provided schema.
 */
public class GraphQLSchemaExecutorTester {

  private final Injector injector;

  GraphQLSchemaExecutorTester(Module... modules) {
    this.injector = Guice.createInjector(modules);
  }

  public GraphQLSchema getSchema() {
    return injector.getInstance(GraphQLSchema.class);
  }

  private GraphQL getGraphQL() {
    return GraphQL.newGraphQL(getSchema()).build();
  }

  ExecutionResult execute(String query) {
    return getGraphQL().execute(query);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  <T> T executeAndGet(String query, String field) {
    ExecutionResult result = execute(query);
    assertThat(result.getErrors()).isEmpty();

    Object data = result.getData();
    Iterator<String> pathIterator = Splitter.on(".").split(field).iterator();

    while (pathIterator.hasNext() && data != null) {
      String piece = pathIterator.next();
      if (data instanceof Map) {
        if (!((Map) data).containsKey(piece)) {
          throw new IllegalArgumentException(String.format("invalid path piece: %s", piece));
        }
        data = ((Map) data).get(piece);
      } else if (data instanceof List) {
        try {
          int index = Integer.parseInt(piece);
          data = ((List) data).get(index);
        } catch (NumberFormatException ex) {
          throw new IllegalArgumentException(
              String.format("path piece is not numeric for indexing into list: %s", piece));
        }
      } else {
        throw new IllegalArgumentException("data is not map");
      }
    }

    return (T) data;
  }
}
