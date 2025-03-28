package com.app.playerservicejava;

import com.app.playerservicejava.service.PlayerService;
import com.app.playerservicejava.service.chat.ChatClientService;
import com.app.playerservicejava.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PlayerServiceJavaApplicationTests {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private ChatClientService chatClientService;

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    void contextLoads() {
        // Assert that the application context loads the main components
        assertThat(playerService).isNotNull();
        assertThat(chatClientService).isNotNull();
        assertThat(playerRepository).isNotNull();
    }
}