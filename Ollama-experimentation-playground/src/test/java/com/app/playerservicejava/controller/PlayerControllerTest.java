package com.app.playerservicejava.controller;

import com.app.playerservicejava.model.Player;
import com.app.playerservicejava.model.Players;
import com.app.playerservicejava.service.PlayerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerControllerTest {

    @InjectMocks
    PlayerController playerController;
    @Mock
    PlayerService playerService;


    @Test
    void testGetPlayers_ReturnListOfPlayers() {

        Players mockPlayers = new Players();

        when(playerService.getPlayers()).thenReturn(mockPlayers);
        ResponseEntity<Players>  response =  playerController.getPlayers();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockPlayers, response.getBody());
        verify(playerService, times(1)).getPlayers();
    }

    @Test
    void testGetPlayerById_PlayerNotFound() {
        // Arrange
        String playerId = "999";
        when(playerService.getPlayerById(playerId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Player> response = playerController.getPlayerById(playerId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(playerService, times(1)).getPlayerById(playerId); // Verify service method was called once
    }

}