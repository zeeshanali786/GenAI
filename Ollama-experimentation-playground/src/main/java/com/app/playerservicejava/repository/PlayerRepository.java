package com.app.playerservicejava.repository;
import com.app.playerservicejava.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, String> {
    @Override
    List<Player> findAllById(Iterable<String> strings);
}
