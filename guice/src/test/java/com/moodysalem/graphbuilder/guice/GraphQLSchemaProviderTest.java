package com.moodysalem.graphbuilder.guice;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableMap;
import com.moodysalem.graphbuilder.guice.testmodules.ConstructionCounterQueryModule;
import com.moodysalem.graphbuilder.guice.testmodules.CounterModule;
import com.moodysalem.graphbuilder.guice.testmodules.DynamicSchemaModule;
import com.moodysalem.graphbuilder.guice.testmodules.DynamicSchemaModuleSingleton;
import com.moodysalem.graphbuilder.guice.testmodules.GetCounterQueryModule;
import com.moodysalem.graphbuilder.guice.testmodules.MutateCounterQueryModule;
import com.moodysalem.graphbuilder.guice.testmodules.RecursiveTypeModule;
import com.moodysalem.graphbuilder.guice.testmodules.StaticQueryMeModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.multibindings.MultibindingsScanner;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GraphQLSchemaProviderTest {

  @Test
  public void testBuildEmptySchema() {
    Injector injector = Guice.createInjector(GraphBuilderModule.singletonScopedSchema().build());
    GraphQLSchema schema = injector.getInstance(GraphQLSchema.class);
    GraphQLObjectType query = schema.getQueryType();
    GraphQLObjectType mutation = schema.getMutationType();
    GraphQLObjectType subscription = schema.getSubscriptionType();

    assertThat(query.getName()).isEqualTo("QueryType");
    assertThat(query.getFieldDefinitions()).isEmpty();

    assertThat(mutation).isNull();
    assertThat(subscription).isNull();
  }

  @Test
  public void testAddFieldDefinitionToQueryGraphBinder() {
    GraphQLSchemaExecutorTester executor = new GraphQLSchemaExecutorTester(
        GraphBuilderModule.singletonScopedSchema().build(), new StaticQueryMeModule());

    assertThat(executor.<String>executeAndGet("{me{name}}", "me.name")).isEqualTo("bob");
    assertThat(executor.<Integer>executeAndGet("{me{id}}", "me.id")).isEqualTo(1);
    assertThat(executor.<Map<String, Object>>executeAndGet("{me{id name}}", "me")).isEqualTo(
        ImmutableMap.of("id", 1, "name", "bob"));
  }

  @Test
  public void testSchemaConstructedEachInjection() {
    GraphQLSchemaExecutorTester executor = new GraphQLSchemaExecutorTester(
        GraphBuilderModule.unscopedSchema().build(),
        MultibindingsScanner.asModule(),
        new CounterModule(),
        new ConstructionCounterQueryModule());

    assertThat(executor.<Long>executeAndGet("{constructions}", "constructions")).isEqualTo(1L);
    assertThat(executor.<Long>executeAndGet("{constructions}", "constructions")).isEqualTo(2L);
  }

  @Test
  public void testSchemaConstructedOnce() {
    GraphQLSchemaExecutorTester executor = new GraphQLSchemaExecutorTester(
        GraphBuilderModule.singletonScopedSchema().build(),
        MultibindingsScanner.asModule(),
        new CounterModule(),
        new ConstructionCounterQueryModule());

    assertThat(executor.<Long>executeAndGet("{constructions}", "constructions")).isEqualTo(1L);
    assertThat(executor.<Long>executeAndGet("{constructions}", "constructions")).isEqualTo(1L);
  }

  @Test
  public void testSchemaComposition() {
    GraphQLSchemaExecutorTester executor = new GraphQLSchemaExecutorTester(
        GraphBuilderModule.singletonScopedSchema().build(),
        MultibindingsScanner.asModule(),
        new CounterModule(),
        new StaticQueryMeModule(),
        new GetCounterQueryModule());

    assertThat(executor.<Long>executeAndGet("{counter}", "counter")).isEqualTo(0L);
    assertThat(executor.<String>executeAndGet("{me{name}}", "me.name")).isEqualTo("bob");
  }

  @Test
  public void testCounterMutate() {
    GraphQLSchemaExecutorTester executor = new GraphQLSchemaExecutorTester(
        GraphBuilderModule.singletonScopedSchema().build(),
        MultibindingsScanner.asModule(),
        new CounterModule(),
        new GetCounterQueryModule(),
        new MutateCounterQueryModule());

    assertThat(executor.<Long>executeAndGet("mutation { moveCounter(by: 5) }", "moveCounter"))
        .isEqualTo(5L);
    assertThat(executor.<Long>executeAndGet("{counter}", "counter")).isEqualTo(5L);
    assertThat(executor.<Long>executeAndGet("mutation { moveCounter(by: -6) }", "moveCounter"))
        .isEqualTo(-1L);
  }

  @Test
  public void testRecursiveType() {
    GraphQLSchemaExecutorTester executor = new GraphQLSchemaExecutorTester(
        GraphBuilderModule.singletonScopedSchema().build(),
        new RecursiveTypeModule());

    assertThat(executor.<String>executeAndGet("{root{name nodes{name nodes{name}}}}", "root.name"))
        .isEqualTo("a");
    assertThat(
        executor.<String>executeAndGet("{root{name nodes{name nodes{name}}}}", "root.nodes.0.name"))
        .isEqualTo("b");
    assertThat(executor.<String>executeAndGet("{root{name nodes{name nodes{name}}}}",
        "root.nodes.0.nodes.1.name"))
        .isEqualTo("f");
  }

  @Test
  public void testDynamicSchemaChangesEachQuery() {
    GraphQLSchemaExecutorTester executor = new GraphQLSchemaExecutorTester(
        GraphBuilderModule.unscopedSchema().build(),
        MultibindingsScanner.asModule(),
        new CounterModule(),
        new DynamicSchemaModule());

    assertThat(executor.<Long>executeAndGet("{query_1}", "query_1"))
        .isEqualTo(1L);
    assertThat(executor.execute("{query_2}").getErrors())
        .hasSize(1);
    assertThat(executor.<Long>executeAndGet("{query_3}", "query_3"))
        .isEqualTo(3L);
  }

  @Test
  public void testDynamicSchemaSingletonSchemaProviderDoesNotChange() {
    GraphQLSchemaExecutorTester executor = new GraphQLSchemaExecutorTester(
        GraphBuilderModule.unscopedSchema().build(),
        MultibindingsScanner.asModule(),
        new CounterModule(),
        new DynamicSchemaModuleSingleton());

    assertThat(executor.<Long>executeAndGet("{query_1}", "query_1"))
        .isEqualTo(1L);
    assertThat(executor.<Long>executeAndGet("{query_1}", "query_1"))
        .isEqualTo(1L);
  }

}
