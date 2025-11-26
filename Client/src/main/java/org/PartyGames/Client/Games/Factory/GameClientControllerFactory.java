package org.PartyGames.Client.Games.Factory;

import org.PartyGames.Client.Connections.WebSocketConnection;
import org.PartyGames.Client.Games.GameClientController;
import org.PartyGames.Client.Terminal.IOController;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

public class GameClientControllerFactory {
    private static final Logger logger = LoggerFactory.getLogger(GameClientControllerFactory.class);

    @NotNull
    public GameClientController createGameClientController(String game_controller, IOController io_controller, WebSocketConnection connection, String uuid)
    throws IllegalArgumentException
    {
        try {
            /*
            * Ok let me try to explain this masterpiece
            * 1st: GetInstance of the registry singleton
            * 2nd: Try to find a class that is named like the string (Upper/lower case doesn't matter)
            * 3rd: if it ISN'T found (orElseThrow(lambda) than log it and throw IAE
            * 4th: with constructor a new instance
            * 5th: attach everything that has to be attached
            * 6th: return it.
             */
            return
                    GameClientControllerRegistry
                    .getInstance()
                    .find(game_controller)
                    .orElseThrow(() -> {
                        logger.error("game_controller not found, {}", game_controller);
                        return new IllegalArgumentException("game_controller not found");
                    })
                    .getDeclaredConstructor().newInstance()
                    .attachIOController(io_controller).attachWebSocketConnection(connection).attachUUID(uuid);

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.error("Cannot create newInstance of: {}", game_controller);
        } catch (NoSuchMethodException e) {
            logger.error("No such Constructor: {}", game_controller);
        }
        throw new IllegalArgumentException("This shouldn't ever get thrown, contact author please!");
    }
}
