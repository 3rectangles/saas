/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookedSlotRepository extends JpaRepository<BookedSlotsDAO, Long> {

	List<BookedSlotsDAO> findByStartDateLessThanAndEndDateGreaterThanAndDeletedOnIsNull(Long availabilityStartDate,
			Long availabilityStartDate1);

	List<BookedSlotsDAO> findAllByStartDateGreaterThanEqualAndStartDateLessThanAndDeletedOnIsNull(
			Long availabilityStartDate,
			Long availabilityEndDate);

}
