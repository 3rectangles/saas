/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.VersionedEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRoleRepository extends JpaRepository<JobRoleDAO, String>, JpaSpecificationExecutor<JobRoleDAO> {

	Optional<JobRoleDAO> findTopByEntityIdIdOrderByEntityIdVersionDesc(String id);

	Optional<JobRoleDAO> findByEntityId(final VersionedEntityId id);

	Optional<JobRoleDAO> findByEntityIdAndDeprecatedOnIsNull(final String id);

	@Override
	@Query(value = "SELECT * FROM job_role WHERE id = (:id)", nativeQuery = true)
	Optional<JobRoleDAO> findById(@Param("id") String id);

	List<JobRoleDAO> findAllByEntityIdInOrderByCreatedOnDesc(final List<VersionedEntityId> ids);

	List<JobRoleDAO> findAllByEntityIdIn(List<VersionedEntityId> ids);

	@Query(value = "SELECT * FROM job_role where company_id = (:company_id) AND  deprecated_on is null AND " +
			"((id,version_id) IN (SELECT id,max(version_id) FROM job_role group by id))", nativeQuery = true)
	List<JobRoleDAO> findLatestByCompanyIdAndDeprecatedOnIsNull(@Param("company_id") String companyId);

	@Query(value = "SELECT * FROM job_role where company_id = (:company_id)  AND " +
			"((id,version_id) IN (SELECT id,max(version_id) FROM job_role group by id))", nativeQuery = true)
	List<JobRoleDAO> findLatestByCompanyId(@Param("company_id") String companyId);

	List<JobRoleDAO> findAllByDomainId(String domainId);

	List<JobRoleDAO> findAllByCategory(String category);

	List<JobRoleDAO> findByCompanyId(String id);

	List<JobRoleDAO> findAllByPartnerIdAndExtFullSync(String partnerId, boolean extFullSync);

	List<JobRoleDAO> findAllByPartnerId(String partnerId);

	@Query(value = "select distinct (country_code) from job_role where country_code is not null", nativeQuery = true)
	List<String> findDistinctCountryCodes();

	@Query(value = "SELECT * FROM job_role where partner_id = (:partner_id) AND  deprecated_on is null AND (is_draft IS NULL OR is_draft = false) AND "
			+
			"((id,version_id) IN (SELECT id,max(version_id) FROM job_role group by id))", nativeQuery = true)
	List<JobRoleDAO> findLatestByPartnerIdAndDeprecatedOnIsNullAndIsDraftNotTrue(@Param("partner_id") String partnerId);

	Optional<JobRoleDAO> findByPartnerIdNullAndIsDefault(boolean isDefault);

}
