package com.stoloto.vip.api.room;

import com.stoloto.vip.api.dto.*;
import com.stoloto.vip.service.RoomService;
import com.stoloto.vip.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    
    private final RoomService roomService;
    private final UserService userService;
    
    /**
     * Получить список всех активных комнат с фильтрацией по типу.
     */
    @GetMapping
    public ResponseEntity<List<RoomResponse>> getRooms(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {
        var rooms = roomService.findAllRooms(type, status);
        return ResponseEntity.ok(rooms);
    }
    
    /**
     * Получить информацию о конкретной комнате.
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<RoomResponse> getRoom(@PathVariable Long roomId) {
        var room = roomService.findRoomById(roomId);
        return ResponseEntity.ok(room);
    }
    
    /**
     * Создать новую комнату (для админов или авто-создание).
     */
    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(
            @Valid @RequestBody CreateRoomRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        var user = userService.findByEmail(userDetails.getUsername());
        var room = roomService.createRoom(request, user);
        return ResponseEntity.ok(room);
    }
    
    /**
     * Присоединиться к комнате.
     * Резервирует ставку на балансе пользователя.
     */
    @PostMapping("/{roomId}/join")
    public ResponseEntity<RoomResponse> joinRoom(
            @PathVariable Long roomId,
            @Valid @RequestBody JoinRoomRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        var user = userService.findByEmail(userDetails.getUsername());
        // roomId может дублироваться в теле запроса, приоритет у path variable
        var room = roomService.joinRoom(roomId, request.getBetAmount(), user);
        return ResponseEntity.ok(room);
    }
    
    /**
     * Купить буст для текущей игры.
     * Резервирует стоимость буста на балансе.
     */
    @PostMapping("/{roomId}/boost")
    public ResponseEntity<RoomResponse> buyBoost(
            @PathVariable Long roomId,
            @Valid @RequestBody BuyBoostRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        var user = userService.findByEmail(userDetails.getUsername());
        var room = roomService.buyBoost(roomId, request.getBoostConfigId(), user);
        return ResponseEntity.ok(room);
    }
    
    /**
     * Покинуть комнату (до начала раунда).
     * Разблокирует зарезервированные средства.
     */
    @PostMapping("/{roomId}/leave")
    public ResponseEntity<RoomResponse> leaveRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetails userDetails) {
        var user = userService.findByEmail(userDetails.getUsername());
        var room = roomService.leaveRoom(roomId, user);
        return ResponseEntity.ok(room);
    }
}
