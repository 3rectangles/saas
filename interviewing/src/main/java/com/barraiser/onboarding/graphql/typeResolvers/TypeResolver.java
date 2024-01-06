package com.barraiser.onboarding.graphql.typeResolvers;

public interface TypeResolver {
    String type();
    
    graphql.schema.TypeResolver getTypeResolver();
}
