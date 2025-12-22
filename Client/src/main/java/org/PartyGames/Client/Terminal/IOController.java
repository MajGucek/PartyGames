package org.PartyGames.Client.Terminal;


import com.google.errorprone.annotations.DoNotCall;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import com.sun.jdi.ClassNotLoadedException;
import org.PartyGames.Client.Sprites.Sprite;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

/** Class for handling Input/Output */
public class IOController {
    private static final Logger logger = LoggerFactory.getLogger(IOController.class);
    /** This is purely for rendering */
    private Screen screen;
    /** modify "graphics" and then render the whole thing with screen.refresh() */
    private TextGraphics graphics;

    public IOController() {
        screen = null;
        graphics = null;
    }

    public record Point(int x, int y) { }

    /** Starts the IOController, checks for OS and Terminal Emulator. */
    @DoNotCall
    public void start() {
        logger.info("IOController registered on OS: {}", System.getProperty("os.name").toLowerCase());
        DefaultTerminalFactory default_terminal_factory = new DefaultTerminalFactory();
        if (isLinuxOS()) {
            //default_terminal_factory.setForceTextTerminal(true);
        }
        default_terminal_factory.setTerminalEmulatorTitle("Party Games");
        try {
            Terminal terminal = default_terminal_factory.createTerminal();
            logger.info("Created terminal off type: {}", terminal.getClass().getSimpleName());

            if (terminal instanceof SwingTerminalFrame frame) {

                logger.info("Using SwingTerminalFrame!");
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                frame.setResizable(true);

                frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);

                    }
                });
            }
            screen = new TerminalScreen(terminal);
            screen.startScreen();
            hideCursor();
            graphics = screen.newTextGraphics();
        } catch (IOException e) {
            logger.error("Error starting: {}", String.valueOf(e));
        }
    }

    @DoNotCall
    public void stop() {
        try {
            screen.stopScreen();
        } catch (IOException e) {
            logger.error("Something went wrong when stopping screen: {}", String.valueOf(e));
            System.exit(0);
        } finally {
            screen = null;
        }
    }

    /** clears screen a.k.a. screen becomes black. */
    public void clearScreen() {
        if (screen != null) {
            screen.clear();
        } else {
            logger.error("Cannot clear screen, because screen == null!");
        }
    }

    @Deprecated
    @SuppressWarnings("unused")
    public void drawSquare(int x, int y, int w, int h, TextColor.RGB color) {
        graphics.setBackgroundColor(color);
        graphics.fillRectangle(new TerminalPosition(x, y), new TerminalSize(w, h), ' ');
    }

    /** Draws a String to the screen.
     * @param text The String that gets drawn.
     * @param point The starting position of the text.
     * @param color The color of the text. */
    public void drawText(@NotNull String text, @NotNull Point point, TextColor.RGB color) {
        graphics.setForegroundColor(color);
        graphics.putString(new TerminalPosition(point.x(), point.y()), text);
    }
    /** Draws a String to the screen with white color.
     * @param text The String that gets drawn.
     * @param point The starting position of the text. */
    public void drawText(@NotNull String text, @NotNull Point point) {
        drawText(text, point, getRGB(255, 255, 255));
    }
    /** Draws a Sprite to the screen.
     * @param sprite The Sprite that gets drawn.
     * @param point The starting position of the text.
     * @param color The color of the Sprite. */
    public void drawSprite(@NotNull Sprite sprite, @NotNull Point point, TextColor.RGB color) {
        drawSprite(sprite, point, color, false);
    }

    /** Draws a Sprite to the screen with white color.
     * @param sprite The Sprite that gets drawn.
     * @param point The starting position of the text. */
    public void drawSprite(@NotNull Sprite sprite, @NotNull Point point) {
        drawSprite(sprite, point, getRGB(255, 255, 255));
    }

    /** Draws a Sprite to the screen with white color.
     * @param sprite The Sprite that gets drawn.
     * @param point The starting position of the text.
     * @param color The color of the Sprite.
     * @param write_over_whitespace Draw the space character and override the background? */
    public void drawSprite(
            @NotNull Sprite sprite,
            @NotNull Point point,
            TextColor.RGB color,
            boolean write_over_whitespace
    ) {
        try {
            List<String> rows = sprite.getSprite();

            for (int i = 0; i < rows.size(); i++) {
                for (int j = 0; j < rows.get(i).length(); j++) {
                    // @Char
                    String ch = String.valueOf(rows.get(i).charAt(j));
                    if (ch.equalsIgnoreCase(" ") && !write_over_whitespace) {
                        continue;
                    }
                    drawText(ch, new Point(point.x() + j, point.y() + i), color);
                }
            }
        } catch (ClassNotLoadedException e) {
            logger.error("{}", String.valueOf(e));
        }
    }

    @SuppressWarnings("unused")
    public void drawLine(@NotNull String character, @NotNull Point start, @NotNull Point end) {
        if (character.length() != 1) {
            logger.error("Drawing Line with character of length > 1");
        } else {
            graphics.drawLine(start.x(), start.y(), end.x(), end.y(), character.charAt(0));
        }
    }


    public void hideCursor() {
        if (screen != null) {
            screen.setCursorPosition(null);
        } else {
            logger.error("Cannot hide cursor, because screen == null!");
        }
    }

    @SuppressWarnings("unused")
    public void setCursor(int x, int y) {
        if (screen != null) {
            screen.setCursorPosition(new TerminalPosition(x, y));
        } else {
            logger.error("Cannot set Cursor, because screen == null!");
        }
    }


    public int getCharWidth() {
        return screen.getTerminalSize().getColumns();
    }
    public int getCharHeight() {
        return screen.getTerminalSize().getRows();
    }
    public int getHorizontalCenter() { return getCharWidth() / 2; }
    public int getVerticalCenter() { return getCharHeight() / 2; }

    /** Draws to the screen. */
    public void render() {
        try {
            screen.doResizeIfNecessary();
            screen.refresh();
        } catch (IOException e) {
            logger.error("Error refreshing screen: {}", String.valueOf(e));
        }
    }

    /** Get the latest user input.
     * @return Option, KeyStroke or empty */
    @NotNull
    @CheckReturnValue
    public Optional<KeyStroke> poll() {
        try {
            return Optional.of(screen.pollInput());
        } catch (IOException e) {
            logger.error("Error polling inputs: {}", String.valueOf(e));
        }
        return Optional.empty();
    }

    /** Static method for creating an RGB object. */
    public static TextColor.RGB getRGB(int r, int g, int b) {
        return new TextColor.RGB(r, g, b);
    }

    @SuppressWarnings("unused")
    public static TextColor.RGB getRGB(int color) {
        return new TextColor.RGB(color, color, color);
    }


    @SuppressWarnings("unused")
    private static boolean isWindowsOS() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }
    private static boolean isLinuxOS() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }
}
