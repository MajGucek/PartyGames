package org.PartyGames.Server.Games.Factory;

import org.PartyGames.Server.Games.GameServerController;
import org.reflections.Reflections;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.Set;

class GameServerControllerRegistry {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(GameServerControllerRegistry.class);
    private static volatile GameServerControllerRegistry instance;

    private final Set<Class<? extends GameServerController>> controllers;

    private GameServerControllerRegistry() {
        Reflections reflections = new Reflections("org.PartyGames.Server.Games");
        this.controllers = reflections.getSubTypesOf(GameServerController.class);
        this.controllers.forEach(c -> {
            logger.info("Registered controller: {}", c.getName());
        });
    }

    public static GameServerControllerRegistry getInstance() {
        GameServerControllerRegistry result = instance;
        if (result != null) {
            return result;
        }
        synchronized (GameServerControllerRegistry.class) {
            if (instance == null) {
                instance = new GameServerControllerRegistry();
            }
            return instance;
        }
    }

    public Set<Class<? extends GameServerController>> getControllers() {
        return this.controllers;
    }

    public Optional<Class<? extends GameServerController>> find(String class_name) {
        return this.controllers
                .stream()
                .filter(
                        c ->
                                c.getName().equalsIgnoreCase(class_name) || c.getSimpleName().equalsIgnoreCase(class_name)
                )
                .findFirst();
    }
}
