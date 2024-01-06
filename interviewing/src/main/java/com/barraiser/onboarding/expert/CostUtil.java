/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.expert;

import com.barraiser.onboarding.dal.CurrencyDAO;
import com.barraiser.onboarding.dal.CurrencyRepository;

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
@Deprecated(forRemoval = true)
public class CostUtil {
	private final CurrencyRepository currencyRepository;
	private final List<CurrencyDAO> currencies = new ArrayList<>();
	private final Map<String, CurrencyDAO> currencyToINRConversionMapping = new HashMap<>();

	public Double convertToINR(final Double cost, final String currencyCode) {
		return cost * this.currencyToINRConversionMapping.get(currencyCode).getINRConversionRate();
	}

	public CurrencyDAO getCurrencyForCurrencyCode(final String currencyCode) {
		return this.currencyToINRConversionMapping.get(currencyCode);
	}

	public CurrencyDAO getCurrency(final String id) {
		return this.currencies.stream()
				.filter(x -> x.getId().equals(id)).findFirst().get();
	}

	public List<CurrencyDAO> getCurrencies(final List<String> ids) {
		return this.currencies.stream()
				.filter(x -> ids.contains(x.getId())).collect(Collectors.toList());
	}

	@PostConstruct
	public void init() {
		currencies.addAll(this.currencyRepository.findAllByDisabledOnIsNull());
		currencies.forEach(x -> currencyToINRConversionMapping.put(x.getCurrencyCode(), x));
	}
}
