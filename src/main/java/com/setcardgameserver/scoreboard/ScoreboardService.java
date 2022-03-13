package com.setcardgameserver.scoreboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ScoreboardService {

    private final ScoreboardRepository scoreboardRepository;

    @Autowired
    public ScoreboardService(ScoreboardRepository scoreboardRepository) {
        this.scoreboardRepository = scoreboardRepository;
    }

    public List<Scoreboard> scoreboard(){
        return scoreboardRepository.findAll();
    }

    public void addScore(Scoreboard newScore) {
        Optional<Scoreboard> optionalScoreboard = scoreboardRepository.findByScoreId(newScore.getScoreId());

        if (optionalScoreboard.isEmpty()){
            scoreboardRepository.save(newScore);
        }
    }

    public List<Scoreboard> findPlayerScores(UUID playerId) {
        return scoreboardRepository.findFirst100ByPlayerIdOrderByDifficultyDescScoreDescTimeAsc(playerId);
    }

    public List<Scoreboard> findTopScores() {
        return scoreboardRepository.findFirst100ByOrderByDifficultyDescScoreDescTimeAsc();
    }
}
