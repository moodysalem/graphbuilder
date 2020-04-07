package com.moodysalem.graphbuilder.core;


import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import com.moodysalem.graphbuilder.core.inputs.SchemaOptions;
import graphql.schema.GraphQLSchema;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SchemaConstructorTest {

  @Test
  public void testCreateEmptySchema() {
    GraphQLSchema schema =
        SchemaConstructor.createSchema(
            SchemaOptions.DEFAULT,
            ImmutableList.of()
        );

    assertThat(schema.getQueryType().getFieldDefinitions()).isEmpty();
    assertThat(schema.getMutationType()).isNull();
    assertThat(schema.getSubscriptionType()).isNull();
  }

}
