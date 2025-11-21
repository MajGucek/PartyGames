package org.PartyGames.Server.Connections;

import com.fasterxml.uuid.Generators;

/**
 * Helper class for UUID of Clients
 */
public class Client {
    public static String getUUID() {
        return Generators.defaultTimeBasedGenerator().generate().toString();
    }
}
