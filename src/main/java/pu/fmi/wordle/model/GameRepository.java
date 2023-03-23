package pu.fmi.wordle.model;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pu.fmi.wordle.model.Game.GameState;

public interface GameRepository extends JpaRepository<Game, String> {

  // TODO: At the moment the method returns all games with state not equal to the given one.
  // Rename the method to return the last 10 games with state not equal to the given one ordered
  // by startedOn descending

  @Query("select g from Game g where g.state <> ?1 order by g.startedOn DESC limit 10")
  Collection<Game> findLast10ByStateNot(GameState state);

  // TODO: Create method that returns all games with the given status that are started before the
  // given time

  @Query("select g from Game g where g.state = ?1 and g.startedOn < ?2")
  Collection<Game> findByStateAndStartedOnBefore(GameState state, LocalDateTime startedOn);

}
