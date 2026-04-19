package com.stoloto.vip.core.service;

import com.stoloto.vip.core.entity.AuditLog;
import com.stoloto.vip.core.entity.User;
import com.stoloto.vip.core.repository.AuditLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void logAction(String action, User performedBy, String entityType, Long entityId,
                          Object oldValues, Object newValues, String ipAddress, String userAgent) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .performedBy(performedBy)
                    .oldValues(oldValues != null ? objectMapper.writeValueAsString(oldValues) : null)
                    .newValues(newValues != null ? objectMapper.writeValueAsString(newValues) : null)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .success(true)
                    .build();

            auditLogRepository.save(auditLog);
            log.debug("Audit log saved: action={}, entity={}, entityId={}", action, entityType, entityId);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize audit log values", e);
            throw new RuntimeException("Failed to create audit log", e);
        }
    }

    public void logError(String action, User performedBy, String entityType, Long entityId,
                         String errorMessage, String ipAddress, String userAgent) {
        AuditLog auditLog = AuditLog.builder()
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .performedBy(performedBy)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .success(false)
                .errorMessage(errorMessage)
                .build();

        auditLogRepository.save(auditLog);
        log.warn("Audit error logged: action={}, error={}", action, errorMessage);
    }

    public void logSystemAction(String action, String entityType, Long entityId,
                                Map<String, Object> metadata) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .newValues(objectMapper.writeValueAsString(metadata))
                    .success(true)
                    .build();

            auditLogRepository.save(auditLog);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize system audit log", e);
        }
    }
}
