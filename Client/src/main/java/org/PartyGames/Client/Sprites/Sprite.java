package org.PartyGames.Client.Sprites;

import com.google.gson.Gson;
import com.sun.jdi.ClassNotLoadedException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class Sprite {
    protected String file_name;
    protected int frame_count;
    protected int width;
    protected int height;
    protected List<List<String>> sprite;
    protected int current_frame;

    public Sprite(String file_name) {
        this.file_name = file_name;
        this.frame_count = 0;
        this.width = 0;
        this.height = 0;
        this.sprite = null;
        this.current_frame = 0;
    }



    public List<List<String>> getSprite() throws ClassNotLoadedException {
        if (this.sprite == null) {
            throw new ClassNotLoadedException("Sprite was not loaded");
        }
        return Collections.unmodifiableList(this.sprite);
    }

    @SuppressWarnings("unused")
    public void incrementFrame() throws IllegalCallerException {
        if (frame_count == 1) {
            throw new IllegalCallerException("Cannot increment Frame on a Sprite with only 1 frame");
        }

        if (current_frame + 1 < frame_count) {
            current_frame++;
        } else {
            current_frame = 0;
        }
    }

    public void loadSprite() throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(getFullFileName(this.file_name)));
        Gson gson = new Gson();
        RawSprite raw = gson.fromJson(reader, RawSprite.class);
        this.frame_count = raw.frame_count;
        this.width = raw.width;
        this.height = raw.height;
        this.sprite =
                raw.sprite.stream().map(frame ->
                        frame.stream().map(row ->
                                row.stream()
                                        .map(code -> String.valueOf((char) code.intValue()))
                                        .collect(Collectors.joining())
                        ).toList()
                ).toList();
    }





    private static String getFullFileName(String file_name) {
        return System.getProperty("user.dir") + "/assets/" + file_name + ".guspr.json";
    }

    private final static class RawSprite {
        public int frame_count;
        public int width;
        public int height;
        public List<List<List<Integer>>> sprite;
    }
}














































