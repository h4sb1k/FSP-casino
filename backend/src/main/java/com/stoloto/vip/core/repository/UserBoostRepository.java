package com.stoloto.vip.core.repository;

import com.stoloto.vip.core.entity.UserBoost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBoostRepository extends JpaRepository<UserBoost, Long> {
    List<UserBoost> findByUserId(Long userId);
    List<UserBoost> findByUserIdAndUsedFalse(Long userId);
    List<UserBoost> findByUserIdAndUsedTrue(Long userId);
    long countByUserIdAndUsedTrue(Long userId);
}
