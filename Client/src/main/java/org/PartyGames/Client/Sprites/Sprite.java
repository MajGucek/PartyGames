package org.PartyGames.Client.Sprites;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;


public class Sprite {
    protected String file_name;
    protected boolean is_loaded;
    public RawSprite sprite;

    public Sprite(String file_name) {
        this.file_name = file_name;
        this.is_loaded = false;
    }

    private static String getFullFileName(String file_name) {
        return System.getProperty("user.dir") + "/assets/" + file_name + ".guspr.json";
    }

    @SuppressWarnings("unused")
    public final static class RawSprite {
        public int frame_count;
        public int width;
        public int height;
        public List<List<List<Character>>> frames;
    }


    public boolean isLoaded() { return this.is_loaded; }

    public void loadSprite() throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(getFullFileName(this.file_name)));
        Gson gson = new Gson();
        this.sprite = gson.fromJson(reader, RawSprite.class);
        this.is_loaded = true;
    }
}














































