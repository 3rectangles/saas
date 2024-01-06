package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartnerWhitelistedDomainsRepository extends JpaRepository<PartnerWhitelistedDomainDAO, String> {
    Optional<PartnerWhitelistedDomainDAO> findByEmailDomainIgnoreCase(String domainOfEmail);

    List<PartnerWhitelistedDomainDAO> findAllByPartnerId(String partnerId);

    Optional<PartnerWhitelistedDomainDAO> findByPartnerIdAndEmailDomain(String partnerId, String emailDomain);
}
