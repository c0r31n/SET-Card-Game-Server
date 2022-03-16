package com.setcardgameserver.scoreboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class ScoreboardController {
    private final ScoreboardService scoreboardService;

    @Autowired
    public ScoreboardController(ScoreboardService scoreboardService) {
        this.scoreboardService = scoreboardService;
    }

    @GetMapping("/scoreboard")
    public List<Scoreboard> scoreboard(){
        return scoreboardService.scoreboard();
    }


    @GetMapping("/scoreboard/player/{id}")
    public List<Scoreboard> playerScores(@PathVariable("id") UUID playerId){
        return scoreboardService.findPlayerScores(playerId);
    }

    @GetMapping("/scoreboard/top")
    public List<Scoreboard> topScores(){
        return scoreboardService.findTopScores();
    }

    @PostMapping("/scoreboard/add")
    public Optional<Scoreboard> addScore(@RequestBody Scoreboard score){
        return scoreboardService.addScore(score);

    }

    @GetMapping("/scoreboard/clear")
    public void clearScoreboard(){
        scoreboardService.clearScoreboard();
    }
}
