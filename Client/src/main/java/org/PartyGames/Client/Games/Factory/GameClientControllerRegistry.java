package org.PartyGames.Client.Games.Factory;

import org.PartyGames.Client.Games.GameClientController;
import org.reflections.Reflections;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.Set;

/** Thread-Safe Singleton for getting all Classes that Extend GameClientController */
class GameClientControllerRegistry {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(GameClientControllerRegistry.class);
    private static volatile GameClientControllerRegistry instance;

    private final Set<Class<? extends GameClientController>> controllers;

    public GameClientControllerRegistry() {
        Reflections reflections = new Reflections("org.PartyGames.Client.Games");
        this.controllers = reflections.getSubTypesOf(GameClientController.class);
        this.controllers.forEach(c -> {
            logger.info("Registered controller: {}", c.getName());
        });
    }

    public static GameClientControllerRegistry getInstance() {
        GameClientControllerRegistry result = instance;
        if (result != null) {
            return result;
        }
        synchronized (GameClientControllerRegistry.class) {
            if (instance == null) {
                instance = new GameClientControllerRegistry();
            }
            return instance;
        }
    }

    public Optional<Class<? extends GameClientController>> find(String class_name) {
        return this.controllers
                .stream()
                .filter(
                        c ->
                                c.getName().equalsIgnoreCase(class_name) || c.getSimpleName().equalsIgnoreCase(class_name)
                )
                    .findFirst();
    }
}
