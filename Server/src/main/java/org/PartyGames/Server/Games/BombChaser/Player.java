package org.PartyGames.Server.Games.BombChaser;


import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

public class Player {
    private static final int SIZE = 5;
    private static final Gson gson = new Gson();
    private int x;
    private int y;
    private float speed_multiplier;
    private int health;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        this.speed_multiplier = 1.0F;
        this.health = 100;
    }

    static class PlayerNetwork {
        int size;
        int x;
        int y;
        int health;
    }

    @Override
    @NotNull
    public String toString() {
        PlayerNetwork p = new PlayerNetwork();
        p.size = SIZE;
        p.x = this.x;
        p.y = this.y;
        p.health = this.health;
        return gson.toJson(p);
    }


}
