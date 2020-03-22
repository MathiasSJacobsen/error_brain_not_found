package inf112.skeleton.app;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import inf112.skeleton.app.cards.Deck;
import inf112.skeleton.app.cards.ProgramCard;
import inf112.skeleton.app.enums.Direction;
import inf112.skeleton.app.objects.Laser;
import inf112.skeleton.app.screens.GifScreen;
import inf112.skeleton.app.screens.LoadingScreen;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class RallyGame extends Game {

    public Board board;
    public Deck deck;
    public Player currentPlayer;
    public ArrayList<Player> players;
    public Semaphore waitForCards;
    public boolean playing;
    public Sound laserSound;
    public SpriteBatch batch;
    private int numberOfLifeTokens = 3;
    private Texture lifeTokens;
    private Player player;
    private Tokens token;


    
    public void create() {
        this.setScreen(new LoadingScreen(this));
        startMusic();
    }

    public void setupGame(String mapPath) {
        this.board = new Board(mapPath, 4);
        this.deck = new Deck();
        this.currentPlayer = board.getPlayer1();
        this.players = new ArrayList<>();
        this.players = board.getPlayers();

        this.waitForCards = new Semaphore(1);
        this.waitForCards.tryAcquire();
        this.playing = true;
        //this.token = new Tokens(player);


        lifeTokens = new Texture("assets/images/lifeToken.png");

        new Thread(this::doTurn).start();




        setInputProcessor();
        dealCards();
        selectCards();
    }
    public void getNumberOfLifeTokens(){
      this.numberOfLifeTokens = player.getLifeTokens();
        System.out.println(numberOfLifeTokens);
        //return numberOfLifeTokens;

    }

    public void setInputProcessor() {
      //  getNumberOfLifeTokens();
       System.out.println(numberOfLifeTokens+ "Set inputproserers");
         loadTokens();

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyUp(int keycode) {
                Player player = board.getPlayer1();
                if (player == null) {
                    return false;
                }

                removeLasers();

                if (keycode == Input.Keys.RIGHT) {
                    player.setDirection(Direction.EAST);
                    board.movePlayer(player);
                } else if (keycode == Input.Keys.LEFT) {
                    player.setDirection(Direction.WEST);
                    board.movePlayer(player);
                } else if (keycode == Input.Keys.UP) {
                    player.setDirection(Direction.NORTH);
                    board.movePlayer(player);
                } else if (keycode == Input.Keys.DOWN) {
                    player.setDirection(Direction.SOUTH);
                    board.movePlayer(player);
                } else if (keycode == Input.Keys.ESCAPE) {
                    Gdx.app.exit();
                } else if (keycode == Input.Keys.SPACE) {
                    cardsReady();
                    return super.keyDown(keycode);
                } else {
                    return super.keyDown(keycode);
                }

                if (player.hasAllFlags(board.getFlags().size())) {
                    setWinScreen();
                }

                fireLasers();

                return super.keyDown(keycode);
            }
        });
    }

    public void startMusic() {
        Music music = Gdx.audio.newMusic(Gdx.files.internal("assets/sound/menu_music.mp3"));
        music.setLooping(true);
        music.setVolume(1f);
        music.play();
    }

    private void cardsReady() {
        waitForCards.release();
    }

    public void doTurn() {


        // TODO: Alle velger kort
        // TODO: Første kort spilles for alle i riktig rekkefølge
        // TODO: Gears roterer
        // TODO: Express belt flytter én
        // TODO: Express belt og vanlig belt flytter én
        // TODO: Spiller skyter
        // TODO: Laser skyter
        while (playing) {
            //loadTokens();
            try {
                waitForCards.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (Thread.interrupted()) {
                return;
            }
            for (int i = 0; i < 5; i++) {
                System.out.println("Runde " + (i + 1));
                allPlayersPlayCard();

            }
            dealCards();
            selectCards();
        }
    }

    public void selectCards() {
        for (Player player : players) {
            player.selectCards();
        }
    }

    public void dealCards() {
        for (Player player : players) {
            player.drawCards(deck);
        }
    }

    public void allPlayersPlayCard() {
        ArrayList<Player> playerOrder = new ArrayList<>(players);
        // Add all players to order list, and remove players with no cards left
        playerOrder.removeIf(p -> p.getSelectedCards().isEmpty());
        playerOrder.sort(new PlayerSorter());
        for (Player player : playerOrder) {
            playCard(player);
            // Wait 1 second for each player
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void playCard(Player player) {
        ProgramCard card = player.getSelectedCards().remove(0);
        System.out.println(player.toString() + " played " + card.toString());
        switch (card.getRotate()) {
            case RIGHT:
                player.setDirection(player.getDirection().turnRight());
                board.rotatePlayer(player);
                break;
            case LEFT:
                player.setDirection(player.getDirection().turnLeft());
                board.rotatePlayer(player);
                break;
            case UTURN:
                player.setDirection(player.getDirection().turnAround());
                board.rotatePlayer(player);
                break;
            case NONE:
                for (int i = 0; i < card.getDistance(); i++) {
                    board.movePlayer(player);
                    // Wait 500 ms for each move except last one
                    if (i < card.getDistance() - 1) {
                        try {
                            Thread.sleep(500);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            default:
                break;
        }
        deck.addCardToDiscardPile(card);
    }

    public void setWinScreen() {
        this.dispose();
        this.setScreen(new GifScreen(this));
    }

    public void removeLasers() {
        for (int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                board.laserLayer.setCell(x, y, null);
            }
        }
    }

    public void fireLasers() {

        for (Laser laser : board.lasers) {
            laserSound = Gdx.audio.newSound(Gdx.files.internal("assets/Sound/LaserShot.mp3"));
            //laserSound.play();
            laser.fire(this);
        }
    }


    public void render() {
        /*batch.begin();
        for (int i = 0; i < currentPlayer.getLifeTokens();i++){
            batch.draw(lifeTokens,2,2);
        } */
        super.render();




    }

    public void dispose() {
        this.screen.dispose();
    }

    public void disposeTokens(){
        this.batch.dispose();
    }

   public void renderTokens(){
//        System.out.println(player.getLifeTokens());


       batch.begin();
        for (int i =1; i <= numberOfLifeTokens; i++){
            batch.draw(lifeTokens,i*15,i*2);
        }
      // batch.end();
    }
    public void loadTokens(){
        batch = new SpriteBatch();
       // getNumberOfLifeTokens();
//        this.numberOfLifeTokens = player.getLifeTokens();

    }
    public void renderNewTokens(int tokens){
        batch.begin();
        for (int i =1; i <= tokens; i++){
            batch.draw(lifeTokens,i*15,i*2);
        }
        batch.end();
    }

    public Board getBoard() {
        return this.board;
    }
}
