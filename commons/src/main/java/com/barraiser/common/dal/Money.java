/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.dal;

import lombok.*;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Money {
	private Double value;
	private String currency;
	private String symbol;
}
