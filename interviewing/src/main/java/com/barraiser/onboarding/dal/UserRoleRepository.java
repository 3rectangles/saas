package com.barraiser.onboarding.dal;

import lombok.Builder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleDAO, String> {
    List<UserRoleDAO> findAllByUserIdAndDeletedOnIsNull(String userId);

    UserRoleDAO findByUserIdAndDeletedOnIsNull(String userId);
}
