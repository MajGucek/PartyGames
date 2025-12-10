package org.PartyGames.Client.Terminal;


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
import java.util.stream.IntStream;

import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import com.sun.jdi.ClassNotLoadedException;
import org.PartyGames.Client.Sprites.Sprite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class IOController {
    private static final Logger logger = LoggerFactory.getLogger(IOController.class);
    // This is purely for rendering,
    private Screen screen;
    // modify "graphics" and then render the whole thing with screen.refresh()
    private TextGraphics graphics;

    public IOController() {
        screen = null;
        graphics = null;
    }


    public void start() {
        logger.info("IOController registered on OS: {}", System.getProperty("os.name").toLowerCase());
        DefaultTerminalFactory default_terminal_factory = new DefaultTerminalFactory();
        if (isLinuxOS()) {
            //defaultTerminalFactory.setForceTextTerminal(true);
        }
        default_terminal_factory.setTerminalEmulatorTitle("Party Games");
        try {
            Terminal terminal = default_terminal_factory.createTerminal();
            logger.info("Created terminal off type: {}", terminal.getClass().getSimpleName());
            if (terminal instanceof SwingTerminalFrame frame) {

                logger.info("Using SwingTerminalFrame!");
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                frame.setResizable(false);

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


    public void stop() {
        screen = null;
    }


    public void clearScreen() {
        if (screen != null) {
            screen.clear();
        } else {
            logger.error("Cannot clear screen, because screen == null!");
        }
    }

    @SuppressWarnings("unused")
    public void drawSquare(int x, int y, int w, int h, TextColor.RGB color) {
        graphics.setBackgroundColor(color);
        graphics.fillRectangle(new TerminalPosition(x, y), new TerminalSize(w, h), ' ');
    }

    public void drawText(String text, int x, int y, TextColor.RGB color) {
        graphics.setForegroundColor(color);
        graphics.putString(new TerminalPosition(x, y), text);
    }

    public void drawText(String text, int x, int y) {
        drawText(text, x, y, getRGB(255, 255, 255));
    }

    public void drawSprite(Sprite sprite, int index, int x, int y, TextColor.RGB color) {
        try {
            List<String> rows = sprite.getSprite().get(index);

            IntStream.range(0, rows.size()).forEach(i -> drawText(rows.get(i), x, y + i, color));
        } catch (ClassNotLoadedException e) {
            logger.error("Cannot getSprite: {}", String.valueOf(e));
        }
    }
    @SuppressWarnings("unused")
    public void drawSprite(Sprite sprite, int index, int x, int y) {
        drawSprite(sprite, index, x, y, getRGB(255, 255, 255));
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
        return screen.getTerminalSize().getRows();
    }
    public int getCharHeight() {
        return screen.getTerminalSize().getColumns();
    }


    public void render() {
        try {
            screen.refresh();
        } catch (IOException e) {
            logger.error("Error refreshing screen: {}", String.valueOf(e));
        }
    }


    public KeyStroke poll() {
        try {
            return screen.pollInput();
        } catch (IOException e) {
            logger.error("Error polling inputs: {}", String.valueOf(e));
        }
        return null;
    }

    @SuppressWarnings("unused")
    public static TextColor.RGB getRGB(int r, int g, int b) {
        return new TextColor.RGB(r, g, b);
    }


    @SuppressWarnings("unused")
    private static boolean isWindowsOS() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }
    private static boolean isLinuxOS() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }
}
