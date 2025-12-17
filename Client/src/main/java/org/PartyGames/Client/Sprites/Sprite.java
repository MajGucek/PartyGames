package org.PartyGames.Client.Sprites;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class Sprite {
    private static final Logger logger = LoggerFactory.getLogger(Sprite.class);
    protected String file_name;
    protected int frame_count;
    protected int width;
    protected int height;
    protected List<List<String>> sprite;
    protected int current_frame;

    public Sprite() {
        this.file_name = null;
        this.frame_count = 0;
        this.width = 0;
        this.height = 0;
        this.sprite = null;
        this.current_frame = 0;
    }

    public int getWidth() { return this.width; }
    public int getHeight() { return height; }

    public List<String> getSprite() {
        if (this.sprite == null) {
            logger.error("Sprite wasn't correctly loaded, returning null!");
        }
        return Collections.unmodifiableList(this.sprite.get(current_frame));
    }

    @SuppressWarnings("unused")
    public void incrementFrame() {
        if (frame_count == 1) {
            logger.warn("Cannot increment Frame on a Sprite: {} with only 1 frame", this.file_name);
            return;
        }

        if (current_frame + 1 < frame_count) {
            current_frame++;
        } else {
            current_frame = 0;
        }
    }

    public void loadSprite(String file_name) throws ParseException, FileNotFoundException {
        this.file_name = file_name;
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
        if (this.frame_count != this.sprite.size()) {
            throw new ParseException("frame_count does not match actual sprite size!", 21);
            //
        }
        logger.info("Loaded Sprite: {}, successfully", file_name);
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