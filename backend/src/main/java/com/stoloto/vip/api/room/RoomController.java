package com.stoloto.vip.api.room;

import com.stoloto.vip.api.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Контроллер для управления комнатами и участия в играх.
 */
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RoomController {
    
    /**
     * Получить список всех активных комнат с фильтрацией по типу.
     */
    @GetMapping
    public ResponseEntity<List<RoomResponse>> getRooms(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {
        // Заглушка - возвращаем пустой список
        return ResponseEntity.ok(List.of());
    }
    
    /**
     * Получить информацию о конкретной комнате.
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<RoomResponse> getRoom(@PathVariable Long roomId) {
        // Заглушка
        var response = new RoomResponse();
        response.setId(roomId);
        response.setName("Demo Room");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Создать новую комнату (для админов или авто-создание).
     */
    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(
            @Valid @RequestBody CreateRoomRequest request) {
        // Заглушка
        var response = new RoomResponse();
        response.setId(1L);
        response.setName(request.getName());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Присоединиться к комнате.
     * Резервирует ставку на балансе пользователя.
     */
    @PostMapping("/{roomId}/join")
    public ResponseEntity<RoomResponse> joinRoom(
            @PathVariable Long roomId,
            @Valid @RequestBody JoinRoomRequest request) {
        // Заглушка
        var response = new RoomResponse();
        response.setId(roomId);
        response.setName("Joined Room");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Купить буст для текущей игры.
     * Резервирует стоимость буста на балансе.
     */
    @PostMapping("/{roomId}/boost")
    public ResponseEntity<RoomResponse> buyBoost(
            @PathVariable Long roomId,
            @Valid @RequestBody BuyBoostRequest request) {
        // Заглушка
        var response = new RoomResponse();
        response.setId(roomId);
        response.setName("Boosted Room");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Покинуть комнату (до начала раунда).
     * Разблокирует зарезервированные средства.
     */
    @PostMapping("/{roomId}/leave")
    public ResponseEntity<RoomResponse> leaveRoom(
            @PathVariable Long roomId) {
        // Заглушка
        var response = new RoomResponse();
        response.setId(roomId);
        response.setName("Left Room");
        return ResponseEntity.ok(response);
    }
}
