package inf112.skeleton.app.LAN;

import inf112.skeleton.app.RallyGame;
import inf112.skeleton.app.cards.Deck;
import inf112.skeleton.app.cards.ProgramCard;
import inf112.skeleton.app.enums.Messages;
import inf112.skeleton.app.objects.player.Player;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.concurrent.Semaphore;

/**
 * A server for handling connection between players.
 * @author Jenny
 */
public class GameServer {

    private ArrayList<GameServerThreads> clients;
    private RallyGame game;
    private Converter converter;
    private Semaphore haveSentPlayerNumberAndNumberOfPlayers;
    private boolean allClientsHaveSelectedCards;
    private Deck deck;

    public GameServer(RallyGame game) {
        this.clients = new ArrayList<>();
        this.game = game;
        this.converter = new Converter();
        this.haveSentPlayerNumberAndNumberOfPlayers = new Semaphore(1);
        haveSentPlayerNumberAndNumberOfPlayers.tryAcquire();
        this.deck = new Deck();
        deck.shuffleDeck();
    }

    /**
     * Establish a connection at given portnumber, waiting for
     * number of clients to connect. Create a new thread for each client.
     * Close socket after connection.
     * @param port to open connection
     * @param numberOfClients how many clients allowed to connect before closing welcoming socket
     */
    public void connect(int port, int numberOfClients) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            // Connect to several clients
            int connected = 0;
            while (connected < numberOfClients) {
                Socket socket = serverSocket.accept();
                // Server is player 1
                int playerNumber = connected+2;
                GameServerThreads client = new GameServerThreads(this, game, socket, playerNumber, numberOfClients+1);
                System.out.println("I have connected to player" + playerNumber);
                client.start();
                sendStartValues(client, numberOfClients+1, playerNumber, this.deck);
                clients.add(client);
                connected++;
            }
            game.waitForAllClientsToConnect.release();
            System.out.println("Connected! :D");
            serverSocket.close();
            //try {
            //    haveSentPlayerNumberAndNumberOfPlayers.acquire();
            //} catch (InterruptedException e) {
            //    e.printStackTrace();
            //}

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send values so client can start game:
     * Playernumber, number of players and a deck.
     * @param client
     */
    public void sendStartValues(GameServerThreads client, int numberOfPlayers, int playerNumber, Deck deck) {
        client.sendMessage(playerNumber+"");
        client.sendMessage(numberOfPlayers+"");
        sendDeck(client, deck);
    }

    /**
     * Create a new deck, update deck in game and send this deck to the other players.
     */
    public void createAndSendDeckToAll() {
        this.deck = new Deck();
        deck.shuffleDeck();
        game.setDeck(deck.getDeck());
        sendDeckToAll(deck);
    }

    /**
     * Let server know that playernumber and numberofplayers have been sent,
     * so deck now can be sent.
     */
    public void haveSentPlayerNumberAndNumberOfPlayers() {
        haveSentPlayerNumberAndNumberOfPlayers.release();
    }

    /**
     * Send a message to all connecting clients.
     * @param message
     */
    public void sendToAll(String message) {
        for (GameServerThreads thread : clients) {
            thread.sendMessage(message);
        }
    }

    /**
     * Send a message to all clients except player specified.
     * @param player player not to send message to
     * @param message to send
     */
    public void sendToAllExcept(Player player, String message) {
        for (GameServerThreads thread : clients) {
            if (thread.getPlayerNumber() != player.getPlayerNr()) {
                thread.sendMessage(message);
            }
        }
    }

    /**
     * Disconnect this player from the server and close socket.
     * @param playerNumber
     */
    public void disconnect(int playerNumber) {
        for (GameServerThreads thread : clients) {
            if (thread.getPlayerNumber() == playerNumber) {
                thread.close();
            }
        }
    }

    /**
     * Remove a client from the list.
     * @param playerNumber number of player to remove.
     */
    public void remove(int playerNumber) {
        GameServerThreads playerToRemove = null;
        for (GameServerThreads thread : clients) {
            if (thread.getPlayerNumber() == playerNumber) {
                playerToRemove = thread;
            }
        }
        clients.remove(playerToRemove);
    }

    /**
     * Disconnect all players from server.
     */
    public void disconnectAll() {
        for (GameServerThreads thread : clients) {
            disconnect(thread.getPlayerNumber());
        }
    }

    /**
     * Send program to all clients.
     */
    public void sendSelectedCardsToAll() {
        for (GameServerThreads client : clients) {
            for (Player player : game.getBoard().getPlayers()) {
                client.sendSelectedCards(player);
            }
        }
    }

    /**
     * Let all serverthreads continue loop
     */
    public void continueAll() {
        for (GameServerThreads client : clients) {
            client.continueListening();
        }
    }

    /**
     * Send deck to client
     * @param client to send to
     * @param deck to send
     */
    public void sendDeck(GameServerThreads client, Deck deck) {
        Stack<ProgramCard> stack = deck.getDeck();
        Iterator iter = stack.iterator();
        client.sendMessage(Messages.DECK_BEGIN.toString());
        while (iter.hasNext()) {
            ProgramCard card = (ProgramCard) iter.next();
            client.sendMessage(converter.convertToString(card));
        }
        client.sendMessage(Messages.DECK_END.toString());
    }

    /**
     * Send deck to all players.
     * @param deck to send
     */
    public void sendDeckToAll(Deck deck) {
        for (GameServerThreads client : clients) {
            sendDeck(client, deck);
        }
    }

    /**
     * Tell server that all clients have selected their cards
     */
    public void setAllClientsHaveSelectedCards() {
        this.allClientsHaveSelectedCards = true;
    }

    /**
     *
     * @return true if all clients have selected cards
     */
    public boolean allClientsHaveSelectedCards() {
        return allClientsHaveSelectedCards;
    }
}