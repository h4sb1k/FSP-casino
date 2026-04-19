package com.stoloto.vip.core.repository;

import com.stoloto.vip.core.entity.BoostConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoostConfigRepository extends JpaRepository<BoostConfig, Long> {
    Optional<BoostConfig> findByLevel(Integer level);
    List<BoostConfig> findByActiveTrue();
}
