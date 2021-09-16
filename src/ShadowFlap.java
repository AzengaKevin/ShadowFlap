import bagel.*;
import bagel.util.Colour;
import bagel.util.Point;

/**
 * Skeleton Code for SWEN20003 Project 1, Semester 2, 2021
 * <p>
 * Please filling your name below
 *
 * @author Student
 */
public class ShadowFlap extends AbstractGame {

    public static final int WINDOW_WIDTH = 1024, WINDOW_HEIGHT = 768;

    public ShadowFlap() {
        super(1024, 768, "ShadowFlap");
    }

    /**
     * The entry point for the program.
     */
    public static void main(String[] args) {
        ShadowFlap game = new ShadowFlap();
        game.run();
    }

    /**
     * Performs a state update.
     * allows the game to exit when the escape key is pressed.
     */
    @Override
    public void update(Input input) {

        // Exit the game when escape key is pressed
        if (input.wasPressed(Keys.ESCAPE)) System.exit(0);

        drawBackground();


    }

    private void drawBackground() {

        Image background = new Image("res/background.png");

        final double bgXScale = WINDOW_WIDTH / background.getWidth();
        final double bgYScale = WINDOW_HEIGHT / background.getHeight();

        DrawOptions options = new DrawOptions();
        options.setScale(bgXScale, bgYScale);

        background.drawFromTopLeft(0, 0, options);
    }

}
