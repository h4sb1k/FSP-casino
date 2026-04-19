package com.stoloto.vip.core.repository;

import com.stoloto.vip.core.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByPerformedById(Long userId);
    List<AuditLog> findByCreatedAtAfter(LocalDateTime date);
    List<AuditLog> findByAction(String action);
}
