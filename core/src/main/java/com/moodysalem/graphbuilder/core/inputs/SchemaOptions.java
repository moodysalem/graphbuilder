package com.moodysalem.graphbuilder.core.inputs;

/**
 * Contains GraphQL schema level options for configuring the schema generation.
 */
public final class SchemaOptions {

  private static final String DEFAULT_SUBSCRIPTION_TYPE_NAME = "SubscriptionType";
  private static final String DEFAULT_MUTATION_TYPE_NAME = "MutationType";
  private static final String DEFAULT_QUERY_TYPE_NAME = "QueryType";

  public static SchemaOptions DEFAULT = SchemaOptions.builder().build();

  // These are the names of the top level query, mutation and subscription types provided by the module.
  private final String queryTypeName;
  private final String mutationTypeName;
  private final String subscriptionTypeName;

  private SchemaOptions(String queryTypeName, String mutationTypeName,
      String subscriptionTypeName) {
    this.queryTypeName = queryTypeName;
    this.mutationTypeName = mutationTypeName;
    this.subscriptionTypeName = subscriptionTypeName;
  }

  public String getQueryTypeName() {
    return queryTypeName;
  }

  public String getMutationTypeName() {
    return mutationTypeName;
  }

  public String getSubscriptionTypeName() {
    return subscriptionTypeName;
  }

  /**
   * Construct the builder with default values.
   */
  @SuppressWarnings("WeakerAccess")
  public static Builder builder() {
    return new Builder();
  }

  public final static class Builder {

    private Builder() {
    }

    private String queryTypeName = DEFAULT_QUERY_TYPE_NAME;
    private String mutationTypeName = DEFAULT_MUTATION_TYPE_NAME;
    private String subscriptionTypeName = DEFAULT_SUBSCRIPTION_TYPE_NAME;

    public String getQueryTypeName() {
      return queryTypeName;
    }

    public Builder withQueryTypeName(String queryTypeName) {
      this.queryTypeName = queryTypeName;
      return this;
    }

    public String getMutationTypeName() {
      return mutationTypeName;
    }

    public Builder withMutationTypeName(String mutationTypeName) {
      this.mutationTypeName = mutationTypeName;
      return this;
    }

    public String getSubscriptionTypeName() {
      return subscriptionTypeName;
    }

    public Builder withSubscriptionTypeName(String subscriptionTypeName) {
      this.subscriptionTypeName = subscriptionTypeName;
      return this;
    }

    /**
     * Build the schema constructor options.
     */
    @SuppressWarnings("WeakerAccess")
    public SchemaOptions build() {
      return new SchemaOptions(queryTypeName, mutationTypeName, subscriptionTypeName);
    }
  }
}
