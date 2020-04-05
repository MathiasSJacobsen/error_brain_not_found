package inf112.skeleton.app;

import inf112.skeleton.app.cards.ProgramCard;
import java.io.*;
import java.net.Socket;

/**
 * Own thread for a client so client can get continous updates from server.
 * @author Jenny
 */
public class GameClientThread extends Thread {

    private Socket clientSocket;
    private int myPlayerNumber;
    private int numberOfPlayers;
    private InputStream input;
    private OutputStream output;
    private BufferedReader reader;
    private PrintWriter writer;
    private RallyGame game;
    private Converter converter;

    public GameClientThread(RallyGame game, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.game = game;
        this.converter = new Converter();
        try {
            this.input = this.clientSocket.getInputStream();
            this.reader = new BufferedReader(new InputStreamReader(input));
            this.output = this.clientSocket.getOutputStream();
            this.writer = new PrintWriter(output, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Listen for incoming messages from server when thread is started.
     */
    @Override
    public void run() {
        while (true) {
            String message = getMessage();
            if (message == null) {
                break;
            }
            ProgramCard card = converter.convertToCardAndExtractPlayer(message);
            int playerNumber = converter.getPlayerNumber();
            Player player = game.getBoard().getPlayer(playerNumber);
            game.playCard(player, card);
            //int playerNumber = Character.getNumericValue(message.charAt(0));
            //game.movePlayer(playerNumber, message);
            System.out.print(message);


        }
    }

    /**
     * Get number of the player who is sending message. Is at position 0.
     */
    public int getPlayerNumber(String message) {
        return Character.getNumericValue(message.charAt(0));
    }

    /**
     * Get prioritation for this card. Is at position 5 in string.
     */
    public int getPrioritation(String message) {
        return Character.getNumericValue(message.charAt(5));
    }

    /**
     * Get number of steps player is supposed to move. Is at position 3 in string.
     * @param message
     * @return int steps
     */
    private int getSteps(String message) {
        return Character.getNumericValue(message.charAt(3));
    }



    /**
     *
     * @return message from this socket. Close socket if error.
     */
    public String getMessage() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            try {
                // Close socket if exception
                clientSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Read the playerNumber and numberOfPlayers that server has given to this player.
     */
    public void storeInitializationValuesFromSocket() {
        this.myPlayerNumber = Integer.parseInt(getMessage());
        System.out.println("From server, my playernumber: "+myPlayerNumber);
        this.numberOfPlayers = Integer.parseInt(getMessage());
        System.out.println("From server, numberofplayers "+numberOfPlayers);
    }

    /**
     * Send a message to server.
     * @param message message to be sent
     */
    public void sendMessage(String message) {
        writer.println(message);
    }

    /**
     * @return playernumber assigned to this client by server
     */
    public int getMyPlayerNumber() {
        return this.myPlayerNumber;
    }

    /**
     *
     * @return numberOfPlayers in this game
     */
    public int getNumberOfPlayers() {
        return this.numberOfPlayers;
    }

    /**
     * Close the socket.
     */
    public void close() {
        try {
            System.out.println("Closed socket " + myPlayerNumber + " clientside.");
            this.clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
