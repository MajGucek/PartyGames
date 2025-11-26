package org.PartyGames.Client;

import org.PartyGames.Client.Connections.WebSocketConnection;
import org.PartyGames.Client.Terminal.IOController;

import java.util.concurrent.*;

public class Main {
    public static void main(String args[]) {
        String server_address = "ws://localhost:8887";
        WebSocketConnection connection_handler = new WebSocketConnection(server_address);
        IOController io_handler = new IOController();
        ClientController client = new ClientController(connection_handler, io_handler);

        client.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {}

        final int TPS = 60;
        final long PERIOD_NANO = 1_000_000_000L / TPS;

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            client.shutdown();
            executor.shutdownNow();
        }));

        executor.scheduleAtFixedRate(client::update, 0, PERIOD_NANO, TimeUnit.NANOSECONDS);

    }
}
