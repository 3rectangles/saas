package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserWhitelistRepository extends JpaRepository<UserWhitelistDAO, String> {

    List<UserWhitelistDAO> findAllByPartnerCompanyIdAndUserType(
            String companyId, String userTypeExpert);
}
