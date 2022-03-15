package com.setcardgameserver.scoreboard;

//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.List;
//import java.util.UUID;
//
//@Configuration
//public class ScoreboardConfig {
//
//    UUID id1 = UUID.randomUUID();
//    UUID id2 = UUID.randomUUID();
//
//    @Bean
//    CommandLineRunner commandLineRunner(ScoreboardRepository scoreboardRepository){
//        return args ->{
//            Scoreboard s1 = new Scoreboard(
//                    1L,
//                    id1,
//                    "Normal",
//                    9,
//                    44
//            );
//
//            Scoreboard s2 = new Scoreboard(
//                    2L,
//                    id2,
//                    "Easy",
//                    9,
//                    24
//            );
//
//            scoreboardRepository.saveAll(List.of(s1,s2));
//        };
//    }
//}
