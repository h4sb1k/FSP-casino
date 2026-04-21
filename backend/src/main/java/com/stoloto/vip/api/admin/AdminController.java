package com.stoloto.vip.api.admin;

import com.stoloto.vip.api.dto.RoomResponse;
import com.stoloto.vip.core.entity.AuditLog;
import com.stoloto.vip.core.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Контроллер для административных операций.
 * Доступ только для ролей ADMIN и SUPER_ADMIN.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@CrossOrigin(origins = "*")
public class AdminController {
    
    private final AuditLogRepository auditLogRepository;
    
    /**
     * Получить список всех комнат (включая неактивные).
     */
    @GetMapping("/rooms")
    public ResponseEntity<List<RoomResponse>> getAllRooms() {
        // Заглушка - возвращаем пустой список
        return ResponseEntity.ok(List.of());
    }
    
    /**
     * Принудительно запустить комнату.
     */
    @PostMapping("/rooms/{roomId}/start")
    public ResponseEntity<?> forceStartRoom(@PathVariable Long roomId) {
        // Заглушка
        return ResponseEntity.ok(Map.of("message", "Room started forcibly", "roomId", roomId));
    }
    
    /**
     * Принудительно завершить комнату.
     */
    @PostMapping("/rooms/{roomId}/complete")
    public ResponseEntity<?> forceCompleteRoom(@PathVariable Long roomId) {
        // Заглушка
        return ResponseEntity.ok(Map.of("message", "Room completed forcibly", "roomId", roomId));
    }
    
    /**
     * Получить логи аудита с фильтрацией.
     */
    @GetMapping("/audit")
    public ResponseEntity<List<AuditLog>> getAuditLogs(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long actorId,
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(defaultValue = "100") Integer limit) {
        
        var logs = auditLogRepository.findFiltered(type, actorId, roomId, from, to, limit);
        return ResponseEntity.ok(logs);
    }
    
    /**
     * Экспорт логов аудита в JSON.
     */
    @GetMapping("/audit/export")
    public ResponseEntity<String> exportAuditLogs(
            @RequestParam Instant from,
            @RequestParam Instant to) {
        
        var logs = auditLogRepository.findByTimestampBetween(from, to);
        // Простой JSON экспорт (в продакшене лучше использовать CSV или streaming)
        var json = logs.toString();
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .header("Content-Disposition", "attachment; filename=\"audit_export.json\"")
                .body(json);
    }
}
