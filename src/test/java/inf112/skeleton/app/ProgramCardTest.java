package inf112.skeleton.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.app.cards.Deck;
import inf112.skeleton.app.cards.ProgramCard;
import inf112.skeleton.app.enums.Direction;
import inf112.skeleton.app.enums.Rotate;
import inf112.skeleton.app.objects.RotatePad;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ProgramCardTest {

    private Deck deck;
    private RallyGame game;
    private Player player;

    @Before
    public void setUp() {
        Gdx.gl = mock(GL20.class);
        //Make a headless application in order to initialize the board. Does not show.
        new HeadlessApplication(new EmptyApplication());
        this.game = new RallyGame();
        this.game.setupGame("assets/maps/Risky_Exchange.tmx");

        // Already 4 players on board.
        //TODO: Let setupGame take in playerNumber as arg
        player = new Player(new Vector2(0,0), 5);
        player.setDirection(Direction.EAST);
        Board board = game.getBoard();
        board.addPlayer(player);
        this.deck = new Deck();
    }

    @Test
    public void playerGetNineCardsWhenDrawingCardsTest() {
        player.drawCards(deck);
        assertEquals(9, player.getAllCards().size());
    }

    @Test
    public void canChooseFiveCardsFromDrawnCardsTest() {
        player.drawCards(deck);
        player.selectCards();
        assertEquals(5, player.getSelectedCards().size());
    }

    @Test
    public void playingUturnCardTest() {
        ProgramCard uturnCard = new ProgramCard(10, 0, Rotate.UTURN, "uturn");
        player.setSelectedCards(uturnCard);
        game.playCard(player);
        assertEquals(Direction.WEST, player.getDirection());
    }

    @Test
    public void playingRightRotateCardTest() {
        ProgramCard rightRotateCard = new ProgramCard(10, 0, Rotate.RIGHT, "right");
        player.setSelectedCards(rightRotateCard);
        game.playCard(player);
        assertEquals(Direction.SOUTH, player.getDirection());
    }

    @Test
    public void playingLeftRotateCardTest() {
        ProgramCard leftRotateCard = new ProgramCard(10, 0, Rotate.LEFT, "left");
        player.setSelectedCards(leftRotateCard);
        game.playCard(player);
        assertEquals(Direction.NORTH, player.getDirection());
    }


}
