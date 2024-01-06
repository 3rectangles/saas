package com.barraiser.elasticsearch.config.common;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class BarraiserEsClientStaticAppConfigProperties {
    @Value("${elasticsearch.cluster.domain}")
    private String esClusterDomain;

    @Value("${elasticsearch.cluster.port}")
    private String esClusterPort;

    @Value("${elasticsearch.cluster.protocol}")
    private String protocol;
}
