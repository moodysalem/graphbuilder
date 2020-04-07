package com.moodysalem.graphbuilder.guice.testmodules;

import static com.moodysalem.graphbuilder.guice.GraphBinder.newGraphBinder;

import com.moodysalem.graphbuilder.guice.GraphBinder;
import com.google.inject.AbstractModule;
import graphql.Scalars;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;

/**
 * Constructs schema of the following format:
 *
 * ```graphql type Me { id: Int name: String }
 *
 * type Query { me: MeType } ```
 */
public class StaticQueryMeModule extends AbstractModule {

  static class Me {

    Integer id;
    String name;
  }

  @Override
  protected void configure() {
    GraphBinder graphBinder = newGraphBinder(binder());

    // Define some properties on the query field `me`.
    graphBinder.query("me")
        .definition(
            GraphQLFieldDefinition.newFieldDefinition()
                .type(GraphQLObjectType.newObject()
                    .name("MeType") // This type name is referenced below.
                    .field(GraphQLFieldDefinition.newFieldDefinition().name("id")
                        .type(Scalars.GraphQLInt))))
        .dataFetcher((environment) -> {
          Me me = new Me();
          me.id = 1;
          me.name = "bob";
          return me;
        });

    // This is another way of binding fields to a type.
    graphBinder.field("MeType", "name")
        .definition(GraphQLFieldDefinition.newFieldDefinition().type(Scalars.GraphQLString));

    // This is not strictly necessary because the default will fetch the property name from the
    // source object.
    graphBinder.field("MeType", "name")
        .dataFetcher((environment) -> environment.<Me>getSource().name);
  }
}
