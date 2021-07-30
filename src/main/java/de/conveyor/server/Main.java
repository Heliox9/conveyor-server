package de.conveyor.server;

import de.conveyor.game.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    static ServerSocket serverSocket;
    static Properties props = new Properties();
    static File propsFile = new File("src/main/resources/config.properties");

    public static void main(String[] args) throws IOException {
        props.load(new FileReader(propsFile));
        // create wrapper objects
//        games = new ArrayList<>();
        Socket client;
        Game game = new Game();

        // create server socket
        logger.info("Server creating");
        serverSocket = new ServerSocket(Integer.parseInt(props.getProperty("port")));

        boolean up;
        // keep server up
        while (true) {
            logger.debug("Waiting for client");
            // accept client and add to game
            client = serverSocket.accept();
            logger.info("Client accepted");

            up = game.addClient(client);
            if (!up) break;

            // check if game is full and create new
            if (game.isFull()) {
//                games.add(game);
                game = new Game();
                logger.info("Creating new game");
            }
        }
        // kills server if client fails to add to game
        serverSocket.close();
    }
}
