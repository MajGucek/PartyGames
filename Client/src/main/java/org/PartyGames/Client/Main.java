package org.PartyGames.Client;

import org.PartyGames.Client.ConnectionHandlers.WebSocketConnectionHandler;
import org.PartyGames.Client.Terminal.TerminalClient;
import org.PartyGames.Client.Terminal.TerminalIOHandler;
import org.PartyGames.Common.Shared.Games;

import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        String server_address = "ws://localhost:8887";
        WebSocketConnectionHandler connection_handler = new WebSocketConnectionHandler(server_address);
        TerminalIOHandler io_handler = new TerminalIOHandler();
        TerminalClient client = new TerminalClient(connection_handler, io_handler);

        client.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {}

        final int TPS = 60;
        final long PERIOD_NANO = 1_000_000_000L / TPS;

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        executor.scheduleAtFixedRate(client::update, 0, PERIOD_NANO, TimeUnit.NANOSECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executor.shutdown();
            client.shutdown();
        }));
    }
}
