/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Repository
public interface AvailabilityRepository extends JpaRepository<AvailabilityDAO, Long> {

	AvailabilityDAO findByUserIdAndStartDate(String userId, long startDate);

	List<AvailabilityDAO> findByUserIdInAndStartDateLessThanAndEndDateGreaterThan(List<String> userIds,
			Long availabilityStartDate, Long availabilityStartDate1);

	List<AvailabilityDAO> findAllByUserIdInAndStartDateGreaterThanEqualAndStartDateLessThan(List<String> userIds,
			Long availabilityStartDate, Long availabilityEndDate);

	@Query(value = "SELECT * FROM availability as av inner join user_details ud ON av.user_id = ud.id WHERE  av.start_date <= ?1 AND av.end_date >= ?2 AND ud.role = ?3 and av.user_id NOT IN ?4", nativeQuery = true)
	List<AvailabilityDAO> findAllByStartDateLessThanEqualAndEndDateGreaterThanEqualAndRoleAndUserIdNotIn(
			Long availabilityStartDate, Long availabilityEndDate, String role, List<String> bookedUserIds);

	AvailabilityDAO findByUserIdAndEndDate(String userId, Long startDate);

	List<AvailabilityDAO> findByUserIdAndStartDateLessThanAndEndDateGreaterThan(String userId, Long endTime,
			Long startDate);

	@Transactional
	void deleteByIdIn(List<Long> ids);
}
