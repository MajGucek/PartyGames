package org.PartyGames.GameHandlers;

import org.PartyGames.ConnectionHandlers.WebSocketConnectionHandler;
import org.PartyGames.GameHandlers.Hangman.HangmanGame;
import org.PartyGames.GameHandlers.Lobby.Lobby;
import org.PartyGames.GameHandlers.Meteor.MeteorGame;
import org.PartyGames.Shared.Games;
import org.PartyGames.Terminal.TerminalIOHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameFactory {
    public static final Logger logger = LoggerFactory.getLogger(GameFactory.class);
    public static GameHandler createGameHandler(Games game, TerminalIOHandler io_handler, WebSocketConnectionHandler connection, String uuid) {
        switch (game) {
            case Games.Null -> {
                logger.warn("Used factory to create NullGame, Beware!");
                return new NullGame(io_handler, connection, uuid);
            }
            case Games.Lobby -> {
                return new Lobby(io_handler, connection, uuid);
            }
            case Games.Hangman -> {
                return new HangmanGame(io_handler, connection, uuid);
            }
            case Games.Meteor -> {
                return new MeteorGame(io_handler, connection, uuid);
            }

            default -> {
                logger.error("You haven't registered your game: {}, in GameHandlerFactory!", game.toString());
                return null;
            }
        }
    }
}
