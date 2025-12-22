package org.PartyGames.Server.Games;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface ReturnsNamesOnStop {
    @NotNull
    Map<String, String> getNames();
}
