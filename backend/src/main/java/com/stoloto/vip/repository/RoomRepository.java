package com.stoloto.vip.repository;

import com.stoloto.vip.domain.entity.Room;
import com.stoloto.vip.domain.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для активных комнат
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT r FROM Room r WHERE r.status = :status ORDER BY r.createdAt DESC")
    List<Room> findAllByStatus(@Param("status") RoomStatus status);

    @Query("SELECT r FROM Room r WHERE r.status IN :statuses ORDER BY r.createdAt DESC")
    List<Room> findAllByStatusIn(@Param("statuses") List<RoomStatus> statuses);

    @Query("SELECT r FROM Room r JOIN r.config rc WHERE rc.type = :roomType AND r.status = :status")
    List<Room> findByRoomTypeAndStatus(@Param("roomType") com.stoloto.vip.domain.enums.RoomType roomType,
                                        @Param("status") RoomStatus status);

    @Query("SELECT COUNT(r) FROM Room r WHERE r.status = :status")
    long countByStatus(@Param("status") RoomStatus status);

    Optional<Room> findFirstByStatusOrderByCreatedAtAsc(RoomStatus status);
}
