package com.stoloto.vip.repository;

import com.stoloto.vip.domain.entity.BotProfile;
import com.stoloto.vip.domain.enums.BotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для профилей ботов
 */
@Repository
public interface BotProfileRepository extends JpaRepository<BotProfile, Long> {

    @Query("SELECT bp FROM BotProfile bp WHERE bp.status = 'ACTIVE' ORDER BY bp.joinProbability DESC")
    List<BotProfile> findAllAvailable();

    @Query("SELECT bp FROM BotProfile bp WHERE bp.status = :status")
    List<BotProfile> findByStatus(@Param("status") BotStatus status);

    long countByStatus(BotStatus status);
}
