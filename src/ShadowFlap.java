import bagel.*;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * Skeleton Code for SWEN20003 Project 1, Semester 2, 2021
 * <p>
 * Please filling your name below
 *
 * @author Student
 */
public class ShadowFlap extends AbstractGame {


    final Image background = new Image("res/background.png");
    final Image birdWingDown = new Image("res/birdWingDown.png");
    final Image birdWingUp = new Image("res/birdWingUp.png");
    final Image pipe = new Image("res/pipe.png");
    Font font = new Font("res/slkscr.ttf", 48);

    public static final int WINDOW_WIDTH = 1024, WINDOW_HEIGHT = 768;
    public static final double BIRD_SPAWN_X_POS = 200.0;
    public static final double BIRD_SPAWN_Y_POS = 350.0;
    public static final double FLYING_VELOCITY = -6.0;
    public static final double FALLING_ACCELERATION = 0.4;
    public static final double PIPES_VELOCITY = 5.0;
    public static final int SPACE_BETWEEN_PIPES = 168;
    public final double TOP_PIPE_Y_POSITION = (WINDOW_HEIGHT - SPACE_BETWEEN_PIPES) / 2.0 - pipe.getHeight();
    public final double BOTTOM_PIPE_Y_POSITION = TOP_PIPE_Y_POSITION + pipe.getHeight() + SPACE_BETWEEN_PIPES;

    public static final String START_GAME_MESSAGE = "PRESS SPACE TO START";
    public static final String WIN_MESSAGE = "CONGRATULATIONS";
    public static final String GAME_OVER_MESSAGE = "GAME OVER";
    public static final String SCORE_COUNTER_FORMAT = "SCORE: %d";
    public static final String FINAL_SCORE_FORMAT = "FINAL SCORE: %d";

    public static final double SCORE_BOTTOM_X_POSITION = 100.0;
    public static final double SCORE_BOTTOM_Y_POSITION = 100.0;

    public static GameState currentGameState = GameState.Start;

    double birdXPos, birdYPos, pipesXPos;
    double birdsYVelocity = 0;
    int score;

    boolean scoreFlag = true;

    int birdUpFramesCount;

    public ShadowFlap() {
        super(WINDOW_WIDTH, WINDOW_HEIGHT, "ShadowFlap");

        resetFields();

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

        birdUpFramesCount += 1;

        drawBackground();

        if (currentGameState.equals(GameState.Action)) {

            checkForCollision();

            updateVariables(input);

        }

        if (currentGameState.equals(GameState.Start)) {
            if (input.wasPressed(Keys.SPACE)) {
                currentGameState = GameState.Action;
            }
        }

        if (currentGameState.equals(GameState.Over)) {
            if (input.wasPressed(Keys.SPACE)) {
                resetFields();
                currentGameState = GameState.Action;
            }
        }

        render();

    }

    private void checkForCollision() {

        // Get the birds bounding box
        Rectangle birdsBoundingBox;

        if (birdUpFramesCount == 10) {
            birdsBoundingBox = birdWingUp.getBoundingBoxAt(new Point(BIRD_SPAWN_X_POS, birdYPos));
        } else {
            birdsBoundingBox = birdWingDown.getBoundingBoxAt(new Point(BIRD_SPAWN_X_POS, birdYPos));
        }

        // Get the bounding box for pipes
        Rectangle topPipeBoundingBox = pipe.getBoundingBoxAt(new Point((pipesXPos + pipe.getWidth() / 2), (TOP_PIPE_Y_POSITION + pipe.getHeight() / 2)));
        Rectangle bottomPipeBoundingBox = pipe.getBoundingBoxAt(new Point((pipesXPos + pipe.getWidth() / 2), (BOTTOM_PIPE_Y_POSITION + pipe.getHeight() / 2)));

        // Check bird against the top pipe
        if (birdsBoundingBox.intersects(topPipeBoundingBox)) currentGameState = GameState.Over;

        // Check bird against the bottom ipe
        if (bottomPipeBoundingBox.intersects(birdsBoundingBox)) currentGameState = GameState.Over;

    }

    private void render() {

        if (currentGameState.equals(GameState.Start)) {
            drawStringFromLeftUsingCenter(START_GAME_MESSAGE, 0, font);
        }

        if (currentGameState.equals(GameState.Action)) {
            drawPipes();
            drawBird();
            renderScore();
        }

        if (currentGameState.equals(GameState.Over)) {
            drawStringFromLeftUsingCenter(GAME_OVER_MESSAGE, 0, font);

            drawStringFromLeftUsingCenter(String.format(FINAL_SCORE_FORMAT, score), 75, font);
        }
    }

    private void renderScore() {
        drawTextFromLeftUsingPoint(
                String.format(SCORE_COUNTER_FORMAT, score),
                new Point(SCORE_BOTTOM_X_POSITION, SCORE_BOTTOM_Y_POSITION),
                font
        );
    }

    private void drawBird() {

        if (birdUpFramesCount >= 10) {
            birdWingUp.draw(birdXPos, birdYPos);
            birdUpFramesCount = 0;
        } else {
            birdWingDown.draw(birdXPos, birdYPos);
        }
    }

    private void drawPipes() {

        // Draw top pipe
        pipe.drawFromTopLeft(pipesXPos, TOP_PIPE_Y_POSITION);

        // Draw bottom pipe
        DrawOptions drawOptions = new DrawOptions();
        drawOptions.setRotation(Math.PI);
        pipe.drawFromTopLeft(pipesXPos, BOTTOM_PIPE_Y_POSITION, drawOptions);
    }

    private void updateVariables(Input input) {
        updateBirdPosition(input);
        updatePipesPosition();

        updateScore();
    }

    private void updateScore() {
        if (scoreFlag && (pipesXPos < BIRD_SPAWN_X_POS)) {
            score += 1;
            scoreFlag = false;
        }
    }

    private void updatePipesPosition() {

        if ((pipesXPos - PIPES_VELOCITY) < (0.0 - pipe.getWidth())) {
            pipesXPos = WINDOW_WIDTH;
            scoreFlag = true;
        }

        pipesXPos -= PIPES_VELOCITY;

    }

    private void updateBirdPosition(Input input) {

        if (input.isDown(Keys.SPACE)) {

            birdsYVelocity = FLYING_VELOCITY;

        } else {

            if (birdsYVelocity == FLYING_VELOCITY) birdsYVelocity = 0.0;

            if (birdsYVelocity < 10) birdsYVelocity += FALLING_ACCELERATION;
        }

        birdYPos += birdsYVelocity;
    }

    private void drawBackground() {

        final double bgXScale = WINDOW_WIDTH / background.getWidth();
        final double bgYScale = WINDOW_HEIGHT / background.getHeight();

        DrawOptions options = new DrawOptions();
        options.setScale(bgXScale, bgYScale);

        background.drawFromTopLeft(0, 0, options);
    }

    private void resetFields() {
        birdXPos = BIRD_SPAWN_X_POS;
        birdYPos = BIRD_SPAWN_Y_POS;
        pipesXPos = WINDOW_WIDTH;

        birdUpFramesCount = 0;

        score = 0;
    }

    private void drawStringFromLeftUsingCenter(String text, double translate, Font font) {
        double textWidth = font.getWidth(text);
        double xPos = (WINDOW_WIDTH / 2.0 - textWidth / 2.0);
        double yPos = (WINDOW_HEIGHT / 2.0) + translate;

        font.drawString(text, xPos, yPos);
    }

    private void drawTextFromLeftUsingPoint(String text, Point point, Font font) {
        font.drawString(text, point.x, point.y);
    }

}
