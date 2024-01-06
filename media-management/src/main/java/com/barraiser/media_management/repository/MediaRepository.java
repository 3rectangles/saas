/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.media_management.repository;

import com.barraiser.media_management.dal.MediaDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<MediaDAO, String> {
	void deleteAllByEntityId(String entityId);

	void deleteAllByEntityIdAndFormat(final String entityId, final String format);

	List<MediaDAO> findAllByEntityTypeAndEntityIdOrderByCreatedOnDesc(String entityType, String entityId);
}
