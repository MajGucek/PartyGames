package org.PartyGames;

import com.fasterxml.uuid.Generators;

import java.util.UUID;

public class Client {
    private final UUID uuid;
    public Client() {
        this.uuid = Generators.defaultTimeBasedGenerator().generate();
    }
    public String getUUID() { return this.uuid.toString(); }
}
