package com.moodysalem.graphbuilder.guice.testmodules;

import static com.moodysalem.graphbuilder.guice.GraphBinder.newGraphBinder;

import com.moodysalem.graphbuilder.guice.GraphBinder;
import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import graphql.Scalars;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLTypeReference;
import graphql.schema.PropertyDataFetcher;
import java.util.List;

public class RecursiveTypeModule extends AbstractModule {

  static class TreeNode {

    final String name;
    final List<TreeNode> nodes;


    TreeNode(String name,
        TreeNode... nodes) {
      this.name = name;
      this.nodes = ImmutableList.copyOf(nodes);
    }
  }

  private static final TreeNode ROOT_NODE = new TreeNode(
      "a",
      new TreeNode("b", new TreeNode("e"), new TreeNode("f")),
      new TreeNode("c", new TreeNode("g"), new TreeNode("h"))
  );

  @Override
  protected void configure() {
    GraphBinder graphBinder = newGraphBinder(binder());
    graphBinder.query("root")
        .definition(GraphQLFieldDefinition.newFieldDefinition().type(
            GraphQLObjectType.newObject().name("TreeNode")))
        .dataFetcher((environment) -> ROOT_NODE);

    graphBinder.field("TreeNode", "name")
        .definition(GraphQLFieldDefinition.newFieldDefinition().type(
            Scalars.GraphQLString))
        .dataFetcher(PropertyDataFetcher.fetching((TreeNode t) -> t.name));

    graphBinder.field("TreeNode", "nodes")
        .definition(GraphQLFieldDefinition.newFieldDefinition().type(
            GraphQLList.list(GraphQLTypeReference.typeRef("TreeNode"))))
        .dataFetcher(
            PropertyDataFetcher.fetching((TreeNode t) -> t.nodes));
  }
}
