package pu.fmi.wordle.web;

import static java.lang.String.format;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pu.fmi.wordle.logic.GameService;
import pu.fmi.wordle.logic.UnknownWordException;

@Controller
@RequestMapping("/games")
public class GameController {

  final GameService gameService;

  public GameController(GameService gameService) {
    this.gameService = gameService;
  }

  @PostMapping
  public String startNewGame(Model model) {
    var game = gameService.startNewGame();
    model.addAttribute("game", game);
    model.addAttribute("tries", game.getGuesses().size());
    return "redirect:/games/" + game.getId();
  }

  @GetMapping("/{gameId}")
  public String showGame(@PathVariable String gameId, Model model) {
    var game = gameService.getGame(gameId);
    model.addAttribute("game",game);
    model.addAttribute("tries", game.getGuesses().size());
    return "wordle";
  }

  @PostMapping(path = "/{gameId}/guesses")
  public String makeGuess(@PathVariable String gameId, @RequestParam String guess, Model model) {
    try {
      var game = gameService.makeGuess(gameId, guess);
      if(guess.equals(game.getWord())&& game.getGuesses().size()<game.getMaxGuesses())
      {
        model.addAttribute("game",game);
        model.addAttribute("won",true);
        model.addAttribute("lost",false);
        model.addAttribute("end",true);
        model.addAttribute("tries",game.getGuesses().size());
        return "wordle";
      }
      else if(game.getGuesses().size()<game.getMaxGuesses())
      {
        return format("redirect:/games/%s", game.getId());
      }

      model.addAttribute("game",game);
      model.addAttribute("lost",true);
      model.addAttribute("won",false);
      model.addAttribute("end",true);

      return "wordle";

    } catch (UnknownWordException e) {
      var game = gameService.getGame(gameId);
      model.addAttribute("game", game);
      model.addAttribute("error", format("[%s] word doesn't exist", guess));

      return "wordle";
    }
  }
}
