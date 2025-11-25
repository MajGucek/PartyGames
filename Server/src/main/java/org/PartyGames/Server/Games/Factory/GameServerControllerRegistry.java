package org.PartyGames.Server.Games.Factory;

import org.PartyGames.Server.Games.GameServerController;
import org.reflections.Reflections;

import java.util.Optional;
import java.util.Set;

class GameServerControllerRegistry {
    private static volatile GameServerControllerRegistry instance;

    private final Set<Class<? extends GameServerController>> controllers;

    public GameServerControllerRegistry() {
        Reflections reflections = new Reflections("org.PartyGames.Server.Games");
        this.controllers = reflections.getSubTypesOf(GameServerController.class);
        this.controllers.forEach(System.out::println);
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
