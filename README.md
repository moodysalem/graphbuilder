# GraphBuilder
![Java CI with Maven](https://github.com/moodysalem/graphbuilder/workflows/Java%20CI%20with%20Maven/badge.svg?branch=master)

Produces a [graphql-java](https://github.com/graphql-java/graphql-java) 
`GraphQLSchema` from `Guice` bindings.

## Objective

Enable the user to compose a GraphQL schema from fragments produced _dynamically_ at runtime,
_statically_ at compile time, or some combination of the two via dependency injection.

## Installation

Add the following to your dependencies in `pom.xml`.

```xml
  <dependency>
    <groupId>com.moodysalem.graphbuilder</groupId>
    <artifactId>graphbuilder-guice</artifactId>
    <version>...</version>
  </dependency>
```

## Usage

There are two important classes for users of this module:

- `GraphBuilderModule`
- `GraphBinder`

The first is the Guice module that must be installed to provide the GraphQL
schema binding. The second allows for binding `GraphQLFieldDefinition.Builder`s
and `DataFetcher`/`DataFetcherFactory`s to positions in the final GraphQL 
schema.

## Scope

The GraphQL schema is bound to a scope provided by the user. If you wish to
construct the schema only once when injected, use a singleton scoped schema.
Use the eager singleton scope to construct the schema at injector creation time.

Schema components provided by modules may be bound to different scopes. This 
means that some parts of schema can be dynamic, i.e. depend on other Guice keys, 
and other parts can be singleton scoped. GraphBuilder exposes all the 
flexibility of Guice in constructing each part of your schema.

## Building a schema

There are multiple ways to provide schema definitions to GraphBuilder. 

### Annotations

You can write provider methods on your Guice modules using the annotation 
`@ProvidesIntoSet` and return any of  `FieldDataFetcher`, 
`FieldDataFetcherFactory`, `FieldDefinition`, `GraphQLType`, `GraphQLDirective`
or any combination via `SchemaBundle`.

This must be combined with the Guice `MultibindingsScanner.asModule()` module 
in order for the `@ProvidesIntoSet` annotations to be processed.

### GraphBinder
 
`GraphBinder` is a helper class that allows you to construct bindings of
positions in the schema to data fetchers, data fetcher factories, and field 
definitions. The interface is similar to the Guice `MapBinder`/`Multibinder`.

### Examples

Examples can be found at [docs/example](docs/examples.md).
