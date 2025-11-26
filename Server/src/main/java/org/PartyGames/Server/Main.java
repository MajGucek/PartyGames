package org.PartyGames.Server;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        int port = 8887;
        ServerController game = new ServerController(port);
        game.start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException _) {}

        final int TPS = 240;
        final long PERIOD_NANO = 1_000_000_000L / TPS;

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executor.shutdown();
            game.shutdown();
        }));

        executor.scheduleAtFixedRate(game::update, 0, PERIOD_NANO, TimeUnit.NANOSECONDS);


    }
}
