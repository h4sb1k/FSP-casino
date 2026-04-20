package com.stoloto.vip.repository;

import com.stoloto.vip.domain.entity.AuditLog;
import com.stoloto.vip.domain.enums.AuditActionType;
import com.stoloto.vip.domain.enums.AuditActorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Репозиторий для аудита
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("SELECT al FROM AuditLog al WHERE al.actionType = :actionType ORDER BY al.timestamp DESC")
    List<AuditLog> findByActionType(@Param("actionType") AuditActionType actionType);

    @Query("SELECT al FROM AuditLog al WHERE al.actorId = :actorId AND al.actorType = :actorType ORDER BY al.timestamp DESC")
    List<AuditLog> findByActor(@Param("actorId") Long actorId, @Param("actorType") AuditActorType actorType);

    @Query("SELECT al FROM AuditLog al WHERE al.roomId = :roomId ORDER BY al.timestamp DESC")
    List<AuditLog> findByRoomId(@Param("roomId") Long roomId);

    @Query("SELECT al FROM AuditLog al WHERE al.roundId = :roundId ORDER BY al.timestamp DESC")
    List<AuditLog> findByRoundId(@Param("roundId") Long roundId);

    @Query("SELECT al FROM AuditLog al WHERE al.isCritical = true ORDER BY al.timestamp DESC")
    List<AuditLog> findCriticalLogs();

    @Query("SELECT al FROM AuditLog al WHERE al.timestamp BETWEEN :from AND :to ORDER BY al.timestamp DESC")
    List<AuditLog> findByTimeRange(@Param("from") Instant from, @Param("to") Instant to);

    long countByIsCriticalTrue();
}
