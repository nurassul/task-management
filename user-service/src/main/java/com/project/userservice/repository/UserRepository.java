package com.project.userservice.repository;


import com.project.userservice.dto.User;
import com.project.userservice.entity.UserEntity;
import com.project.userservice.dto.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);


    @Deprecated
    @Modifying
    @Query("""
            update UserEntity u
                        set u.userStatus = :status
                        where u.id = :id
            """
    )
    void setStatus(
            @Param("id") Long id,
            @Param("status") UserStatus status);
}
