package org.PartyGames.GameHandlers;

import org.PartyGames.ConnectionHandlers.WebSocketConnectionHandler;
import org.PartyGames.Shared.Games;
import org.PartyGames.Terminal.TerminalIOHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameHandlerFactory {
    public static final Logger logger = LoggerFactory.getLogger(GameHandlerFactory.class);
    public static GameHandler createGameHandler(Games game, TerminalIOHandler io_handler, WebSocketConnectionHandler connection, String uuid) {
        switch (game) {
            case Games.Null -> {
                logger.warn("Used factory to create NullGame, Beware!");
                return new NullGame(io_handler, connection, uuid);
            }
            case Games.Lobby -> {
                return new Lobby(io_handler, connection, uuid);
            }

            default -> {
                logger.error("You haven't registered your game: {}, in GameHandlerFactory!", game.toString());
                return null;
            }
        }
    }
}
