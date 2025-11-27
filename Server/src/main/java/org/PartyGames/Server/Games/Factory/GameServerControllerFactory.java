package org.PartyGames.Server.Games.Factory;


import org.PartyGames.Server.Games.GameServerController;
import org.PartyGames.Server.Games.Lobby.Lobby;
import org.PartyGames.Server.Connections.WebSocketServer;

import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GameServerControllerFactory {
    public static final Logger logger = LoggerFactory.getLogger(GameServerControllerFactory.class);

    public String getRandomGame() {
        List<Class<? extends GameServerController>> clazz_list =
                GameServerControllerRegistry.getInstance().getControllers().stream()
                        .filter(c -> !c.equals(Lobby.class)
        ).toList();

        if (clazz_list.isEmpty()) {
            logger.error("GameServerController besides Lobby not found!");
            return null;
        }

        return clazz_list.get(ThreadLocalRandom.current().nextInt(clazz_list.size())).getSimpleName();
    }

    @NotNull
    public GameServerController createGameServerController(String game_controller, WebSocketServer connection)
            throws IllegalArgumentException
    {
        try {
            return
                    GameServerControllerRegistry
                            .getInstance()
                            .find(game_controller)
                            .orElseThrow(() -> {
                                logger.error("game_controller not found, {}", game_controller);
                                return new IllegalArgumentException("game_controller not found");
                            })
                            .getDeclaredConstructor().newInstance()
                            .attachWebSocketServer(connection);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.error("Cannot create newInstance of: {}", game_controller);
        } catch (NoSuchMethodException e) {
            logger.error("No such Constructor: {}", game_controller);
        }
        throw new IllegalArgumentException("This shouldn't ever get thrown, contact author please!");

    }

}
