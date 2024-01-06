package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpDAO, Long> {
    List<OtpDAO> findAllByPhone(String phone);

    List<OtpDAO> findAllByPhoneAndIsValidTrue(String phone);

    List<OtpDAO> findAllByEmailAndIsValidTrue(String email);

    Optional<OtpDAO> findFirstByPhoneOrderByCreatedOnDesc(String phone);

    Optional<OtpDAO> findFirstByEmailOrderByCreatedOnDesc(String email);
}
