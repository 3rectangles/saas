package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBlacklistRepository extends JpaRepository<UserBlacklistDAO, String> {

    List<UserBlacklistDAO> findAllByUserType(String userType);
}
