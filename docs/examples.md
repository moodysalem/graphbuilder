# Examples

All examples are tested and are found in the package:
[guice/src/test/java/com/moodysalem/graphbuilder/guice/testmodules](../guice/src/test/java/com/moodysalem/graphbuilder/guice/testmodules)
 
#### [StaticQueryMeModule](../guice/src/test/java/com/moodysalem/graphbuilder/guice/testmodules/StaticQueryMeModule.java)
 
This module builds the following schema:

```graphql
type MeType { 
    id: Int
    name: String 
}

type Query { 
    me: MeType
} 
```

The query `{ me { id name } }` always returns `{ data: { me: { id: 1, name: "bob" } } }`