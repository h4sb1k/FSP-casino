package com.stoloto.vip.core.repository;

import com.stoloto.vip.core.entity.Room;
import com.stoloto.vip.core.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByStatus(RoomStatus status);
    List<Room> findByStatusOrderByCreatedAtDesc(RoomStatus status);
}
