package pu.fmi.wordle.logic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import org.springframework.stereotype.Component;
import pu.fmi.wordle.model.Game;
import pu.fmi.wordle.model.GameRepo;
import pu.fmi.wordle.model.Guess;
import pu.fmi.wordle.model.WordRepo;

@Component
public class GameServiceImpl implements GameService {

  final GameRepo gameRepo;

  final WordRepo wordRepo;

  public GameServiceImpl(GameRepo gameRepo, WordRepo wordRepo) {
    this.gameRepo = gameRepo;
    this.wordRepo = wordRepo;
  }

  @Override
  public Game startNewGame() {
    var game = new Game();
    game.setId(UUID.randomUUID().toString());
    game.setStartedOn(LocalDateTime.now());
    game.setWord(wordRepo.getRandom());
    game.setGuesses(new ArrayList<>(game.getMaxGuesses()));
    return game;
  }

  @Override
  public Game getGame(String id) {
    var game = gameRepo.get(id);
    if (game == null) throw new GameNotFoundException(id);
    return game;
  }

  @Override
  public Game makeGuess(String id, String word) {
    checkIfWordIsValid(word);

    Game currentGame = checkIfGameIsValid(id);
    String currentWord = currentGame.getWord();

    Guess guess = new Guess();

    guess.setWord(word);
    guess.setMadeAt(LocalDateTime.now());
    guess.setMatches(getMatches(currentWord,word));

    currentGame.getGuesses().add(guess);


    return currentGame;
  }

  public void checkIfWordIsValid(String word){

    if (!wordRepo.exists(word))
    {
      throw new UnknownWordException(word);
    }

  }
  public Game checkIfGameIsValid(String id){

    if(gameRepo.get(id)==null)
    {
      throw new GameNotFoundException(id);
    }
    return gameRepo.get(id);

  }

  public String getMatches(String guessWord,String gameWord){

    StringBuilder matches = new StringBuilder();

    for(int i=0;i<5;i++)
    {
      int indexOfChar = guessWord.indexOf(gameWord.toCharArray()[i]);

      if(indexOfChar !=-1)
      {
        if(indexOfChar ==i)
        {
          matches.append("P");
        }
        else
        {
          matches.append("L");
        }
      }
      else
      {
        matches.append("N");
      }
    }
    return matches.toString();
  }
}
