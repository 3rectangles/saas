package com.barraiser.common.graphql.types;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class Document {
    private String id;

    private String url;

    private String fileName;
}
