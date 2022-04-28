package com.setcardgameserver.Controller;

import com.setcardgameserver.DTO.Game;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void start() {

    }

    @Test
    void connect() {
    }

    @Test
    void connectRandom() {
    }

    @Test
    void gamePlay() {
    }

    @Test
    void buttonPress() {
    }

    @Test
    void destroyGame() {
    }

    @Test
    void startGame() {
    }
}