/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.elasticsearch.config;

import com.barraiser.elasticsearch.config.common.BarraiserEsClientStaticAppConfigProperties;
import lombok.AllArgsConstructor;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@AllArgsConstructor
@Configuration
public class ElasticsearchConfig {

	private BarraiserEsClientStaticAppConfigProperties barraiserEsClientStaticAppConfigProperties;

	@Bean
	public RestHighLevelClient client() {
		final String esClusterDomain = this.barraiserEsClientStaticAppConfigProperties.getEsClusterDomain();
		final Integer esClusterPort = Integer
				.parseInt(this.barraiserEsClientStaticAppConfigProperties.getEsClusterPort());
		final String protocol = this.barraiserEsClientStaticAppConfigProperties.getProtocol();
		final RestClientBuilder httpClientBuilder = RestClient.builder(
				new HttpHost(esClusterDomain, esClusterPort, protocol));

		httpClientBuilder.setHttpClientConfigCallback(
				httpAsyncClientBuilder -> httpAsyncClientBuilder.setSSLHostnameVerifier((s, sslSession) -> true));

		final RestHighLevelClient esClient = new RestHighLevelClient(httpClientBuilder);
		return esClient;
	}
}
