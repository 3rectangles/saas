/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing;

import com.barraiser.pricing.dal.CurrencyPricingDAO;
import com.barraiser.pricing.dal.CurrencyPricingRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class CurrencyUtil {
	private final CurrencyPricingRepository currencyPricingRepository;
	private final List<CurrencyPricingDAO> currencies = new ArrayList<>();
	private final Map<String, CurrencyPricingDAO> currencyToINRConversionMapping = new HashMap<>();

	public Double convertToINR(final Double cost, final String currencyCode) {
		return cost * this.currencyToINRConversionMapping.get(currencyCode).getINRConversionRate();
	}

	public CurrencyPricingDAO getCurrencyForCurrencyCode(final String currencyCode) {
		return this.currencyToINRConversionMapping.get(currencyCode);
	}

	public CurrencyPricingDAO getCurrency(final String id) {
		return this.currencies.stream()
				.filter(x -> x.getId().equals(id)).findFirst().get();
	}

	public List<CurrencyPricingDAO> getCurrencies(final List<String> ids) {
		return this.currencies.stream()
				.filter(x -> ids.contains(x.getId())).collect(Collectors.toList());
	}

	public Double convertINRToCurrency(final Double cost, final String currencyCode) {
		return cost / this.currencyToINRConversionMapping.get(currencyCode).getINRConversionRate();
	}

	@PostConstruct
	public void init() {
		currencies.addAll(this.currencyPricingRepository.findAllByDisabledOnIsNull());
		currencies.forEach(x -> currencyToINRConversionMapping.put(x.getCurrencyCode(), x));
	}
}
