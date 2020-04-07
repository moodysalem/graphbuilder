package com.moodysalem.graphbuilder.guice;

import static com.google.inject.multibindings.OptionalBinder.newOptionalBinder;

import com.moodysalem.graphbuilder.core.inputs.FieldDataFetcher;
import com.moodysalem.graphbuilder.core.inputs.FieldDataFetcherFactory;
import com.moodysalem.graphbuilder.core.inputs.FieldDefinition;
import com.moodysalem.graphbuilder.core.inputs.SchemaBundle;
import com.moodysalem.graphbuilder.core.inputs.SchemaOptions;
import com.moodysalem.graphbuilder.guice.impl.ClazzDataFetcherBinding;
import com.moodysalem.graphbuilder.guice.impl.ClazzDataFetcherFactoryBinding;
import com.google.inject.AbstractModule;
import com.google.inject.Scope;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.Multibinder;
import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;

/**
 * The GraphBuilder module is the core module that composes the GraphQLSchema.
 */
public final class GraphBuilderModule extends AbstractModule {

  /**
   * Builder class for constructing this module.
   */
  public static class Builder {

    private final Scope scope;

    private Builder(Scope scope) {
      this.scope = scope;
    }

    /**
     * Build the GraphBuilderModule.
     */
    @SuppressWarnings("WeakerAccess")
    public GraphBuilderModule build() {
      return new GraphBuilderModule(scope);
    }
  }

  private final Scope scope;

  private GraphBuilderModule(Scope scope) {
    this.scope = scope;
  }

  /**
   * Return a GraphBuilderModule that is scoped by the given custom scope.
   *
   * @param scope scope in which to build a graph
   * @return GraphBuilderModule with the given custom scope.
   */
  @SuppressWarnings("WeakerAccess")
  public static GraphBuilderModule.Builder customScopedSchema(Scope scope) {
    return new GraphBuilderModule.Builder(scope);
  }

  /**
   * Construct a GraphBuilderModule that is not scoped, i.e. constructs a Graph for every
   * injection.
   *
   * @return GraphBuilderModule that is not scoped.
   */
  @SuppressWarnings("WeakerAccess")
  public static GraphBuilderModule.Builder unscopedSchema() {
    return customScopedSchema(Scopes.NO_SCOPE);
  }

  /**
   * Construct a GraphBuilderModule in the singleton scope.
   *
   * @return the GraphBuilderModule with the singleton scope, i.e. constructs the GraphQLSchema once
   * per injector.
   */
  @SuppressWarnings("WeakerAccess")
  public static GraphBuilderModule.Builder singletonScopedSchema() {
    return customScopedSchema(Scopes.SINGLETON);
  }

  protected void configure() {
    // These bindings are used for determining field coordinates for the top level types.
    newOptionalBinder(binder(), SchemaOptions.class).setDefault().toInstance(
        SchemaOptions.DEFAULT);

    // Set up the set binders.
    Multibinder.newSetBinder(binder(), SchemaBundle.class);
    Multibinder.newSetBinder(binder(), FieldDefinition.class);
    Multibinder.newSetBinder(binder(), FieldDataFetcherFactory.class);
    Multibinder.newSetBinder(binder(), FieldDataFetcher.class);
    Multibinder.newSetBinder(binder(), GraphQLType.class);
    Multibinder.newSetBinder(binder(), GraphQLDirective.class);

    // These are used for constructing the clazz data fetchers and data fetcher factories.
    install(
        new FactoryModuleBuilder().build(ClazzDataFetcherBinding.Factory.class));
    install(
        new FactoryModuleBuilder().build(ClazzDataFetcherFactoryBinding.Factory.class));

    // Bind the GraphQLSchema to the provider implementation in the given scope.
    bind(GraphQLSchema.class).toProvider(GraphQLSchemaProvider.class).in(scope);
  }
}
