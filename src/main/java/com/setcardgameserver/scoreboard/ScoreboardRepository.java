package com.setcardgameserver.scoreboard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScoreboardRepository extends JpaRepository<Scoreboard, Long> {

    @Query("SELECT s FROM Scoreboard s WHERE s.scoreId = ?1")
    Optional<Scoreboard> findByScoreId(Long scoreId);

    List<Scoreboard> findByPlayerId(UUID playerId);

    List<Scoreboard> findFirst100ByPlayerIdOrderByDifficultyDescScoreDescTimeAsc(UUID playerId);

    List<Scoreboard> findFirst100ByOrderByDifficultyDescScoreDescTimeAsc();
}
