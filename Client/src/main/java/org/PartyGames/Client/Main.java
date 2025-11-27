package org.PartyGames.Client;

import org.PartyGames.Client.Connections.WebSocketConnection;
import org.PartyGames.Client.Games.Factory.GameClientControllerRegistry;
import org.PartyGames.Client.Terminal.IOController;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


final class Main {
    static void main() {
        String server_address = "ws://localhost:8887";
        final int TPS = 60;
        GameClientControllerRegistry.getInstance();
        WebSocketConnection connection_controller = new WebSocketConnection(server_address);
        IOController io_controller = new IOController();
        ClientController client = new ClientController(connection_controller, io_controller, TPS);

        client.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) { }

        final long PERIOD_NANO = 1_000_000_000L / TPS;

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            client.shutdown();
            executor.shutdownNow();
        }));
        executor.scheduleAtFixedRate(client::update, 0, PERIOD_NANO, TimeUnit.NANOSECONDS);


    }

    private Main() { }
}
