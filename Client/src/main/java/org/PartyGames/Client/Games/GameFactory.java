package org.PartyGames.Client.Games;

import org.PartyGames.Client.Connections.WebSocketConnection;
import org.PartyGames.Client.Games.Lobby.Lobby;
import org.PartyGames.Client.Games.NullGame.NullGame;
import org.PartyGames.Common.Shared.Games;
import org.PartyGames.Client.Terminal.IOController;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameFactory {
    public static final Logger logger = LoggerFactory.getLogger(GameFactory.class);
    @NotNull
    public static GameClientController createGameHandler(Games game, IOController io_handler, WebSocketConnection connection, String uuid) {
        switch (game) {
            case Games.Null -> {
                logger.warn("Used factory to create NullGame, Beware!");
                return new NullGame(io_handler, connection, uuid);
            }
            case Games.Lobby -> {
                return new Lobby(io_handler, connection, uuid);
            }

            default -> {
                logger.error("You haven't registered your game: {}, in GameHandlerFactory!", game);
                throw new AssertionError("Cannot create this Games, check factory method!");
            }
        }
    }
}
