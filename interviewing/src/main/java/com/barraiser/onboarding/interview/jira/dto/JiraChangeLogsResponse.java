package com.barraiser.onboarding.interview.jira.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class JiraChangeLogsResponse {

    private List<ChangeLog> values;
    private Integer maxResults;
    private Integer startAt;
    private Integer total;
    private Boolean isLast;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class ChangeLog {
        private String id;
        private Person author;
        private List<Item> items;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX")
        private OffsetDateTime created;

        @Getter
        @AllArgsConstructor
        @NoArgsConstructor
        @Builder(toBuilder = true)
        public static class Item {
            private String field;
            private String toString;
            private String fromString;
            private String fieldtype;
            private String fieldId;
        }
    }
}
