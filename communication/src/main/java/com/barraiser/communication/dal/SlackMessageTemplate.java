package com.barraiser.communication.dal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class SlackMessageTemplate {
    private String header;
    private String title;
    private Map<String, String> button;
    private String footer;
}
