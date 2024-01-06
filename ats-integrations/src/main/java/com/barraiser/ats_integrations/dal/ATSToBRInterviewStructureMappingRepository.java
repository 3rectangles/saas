/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.dal;

import com.barraiser.common.ats_integrations.ATSProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ATSToBRInterviewStructureMappingRepository
		extends JpaRepository<ATSToBRInterviewStructureMappingDAO, String> {

	Optional<ATSToBRInterviewStructureMappingDAO> findByAtsProviderAndAtsInterviewStructureId(
			final ATSProvider atsProvider, final String atsInterviewStructureId);

	Optional<ATSToBRInterviewStructureMappingDAO> findByAtsInterviewStructureId(final String atsInterviewStructureId);

	List<ATSToBRInterviewStructureMappingDAO> findAllByAtsInterviewStructureId(final String atsInterviewStructureId);

	void deleteByBrInterviewStructureId(String interviewStructureId);

	@Query(nativeQuery = true, value = "SELECT *\n" +
			"FROM (\n" +
			"  SELECT *, ROW_NUMBER() OVER (PARTITION BY ats_interview_structure_id ORDER BY created_on DESC) AS row_num\n"
			+
			"  FROM ats_to_br_interview_structure_mapping\n" +
			"  where partner_id = :partner_id and ats_interview_structure_id is not null \n" +
			") t\n" +
			"WHERE row_num = 1\n")
	List<ATSToBRInterviewStructureMappingDAO> findAllByPartnerId(@Param("partner_id") final String partnerId);

	Optional<ATSToBRInterviewStructureMappingDAO> findByBrInterviewStructureId(String interviewStructureId);
}
