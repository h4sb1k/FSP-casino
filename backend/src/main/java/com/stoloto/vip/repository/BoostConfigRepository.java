package com.stoloto.vip.repository;

import com.stoloto.vip.domain.entity.BoostConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для конфигураций бустов
 */
@Repository
public interface BoostConfigRepository extends JpaRepository<BoostConfig, Long> {

    @Query("SELECT bc FROM BoostConfig bc WHERE bc.isActive = true ORDER BY bc.costInBonus ASC")
    List<BoostConfig> findAllActive();

    @Query("SELECT bc FROM BoostConfig bc WHERE bc.isActive = true AND bc.costInBonus <= :maxCost ORDER BY bc.winProbabilityBonusPercent DESC")
    List<BoostConfig> findAffordableActive(@Param("maxCost") Long maxCost);
}
