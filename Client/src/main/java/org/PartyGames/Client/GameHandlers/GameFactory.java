package org.PartyGames.Client.GameHandlers;

import org.PartyGames.Client.ConnectionHandlers.WebSocketConnectionHandler;
import org.PartyGames.Client.GameHandlers.Lobby.Lobby;
import org.PartyGames.Common.Shared.Games;
import org.PartyGames.Client.Terminal.TerminalIOHandler;
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

            default -> {
                logger.error("You haven't registered your game: {}, in GameHandlerFactory!", game.toString());
                return null;
            }
        }
    }
}
