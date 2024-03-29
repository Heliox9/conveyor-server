package de.conveyor.server;

import com.google.gson.Gson;
import de.conveyor.game.GameState;
import de.conveyor.game.Item;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

class MainTest {
    Gson gson = new Gson();

    @Test
    public void givenClient1_whenServerResponds_thenCorrect() throws IOException {
        Main.props.load(new FileReader(Main.propsFile));
        if (false) { // allows to disable second client
            Thread t = new Thread(() -> {
                Socket client1 = null;
                try {
                    client1 = new Socket("127.0.0.1", Integer.parseInt(Main.props.getProperty("port")));

                    PrintWriter out = new PrintWriter(client1.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(client1.getInputStream()));
                    out.println("Test 1");
                    System.out.println(in.readLine());// game id
                    System.out.println(in.readLine());// opponent

                    GameState state = null;
                    Boolean first = null;

                    do {// iterate rounds
                        ItemSelection selection = gson.fromJson(in.readLine(), ItemSelection.class);// set
                        System.out.println("received:");
                        System.out.println(selection);

                        System.out.println("cash:");
                        System.out.println(in.readLine());

                        System.out.println("character:");
                        System.out.println(in.readLine());


                        // generate selection
                        ArrayList<Item> bought = new ArrayList<Item>();
                        bought.add(selection.getSelection().get(4));
                        ArrayList<Item> saved = new ArrayList<Item>();
                        saved.add(selection.getSelection().get(1));
                        selection.setBought(bought);
                        selection.setSaved(saved);

                        out.println(gson.toJson(selection));
                        System.out.println("sent:");
                        System.out.println(selection);

                        // read fight stats
                        state = gson.fromJson(in.readLine(), GameState.class);// read game state
                        System.out.println("received:");
                        System.out.println(state);
                        //after damage
                        state = gson.fromJson(in.readLine(), GameState.class);// read game state
                        System.out.println("received:");
                        System.out.println(state);

                        first = gson.fromJson(in.readLine(), Boolean.class);// read game state
                        System.out.println("received:" + first);

                        // end of round
                    } while (state.getPlayer().getHp() > 0 && state.getOpponent().getHp() > 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    client1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            t.start();
        }


        Socket client2 = null;
        try {
            client2 = new Socket("127.0.0.1", Integer.parseInt(Main.props.getProperty("port")));

            PrintWriter out = new PrintWriter(client2.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(client2.getInputStream()));

            // Enter Player name
            out.println("Test 2");
            System.out.println(in.readLine());// game id
            System.out.println(in.readLine());// opponent

            GameState state = null;
            Boolean first = null;

            do {// iterate rounds
                ItemSelection selection = gson.fromJson(in.readLine(), ItemSelection.class);// set
                System.out.println("received:");
                System.out.println(selection);

                System.out.println("cash:");
                System.out.println(in.readLine());

                System.out.println("character:");
                System.out.println(in.readLine());


                // generate selection
                ArrayList<Item> bought = new ArrayList<Item>();
                bought.add(selection.getSelection().get(4));
                ArrayList<Item> saved = new ArrayList<Item>();
                saved.add(selection.getSelection().get(1));
                selection.setBought(bought);
                selection.setSaved(saved);

                out.println(gson.toJson(selection));
                System.out.println("sent:");
                System.out.println(selection);

                // read fight stats
                state = gson.fromJson(in.readLine(), GameState.class);// read game state
                System.out.println("received:");
                System.out.println(state);
                //after damage
                state = gson.fromJson(in.readLine(), GameState.class);// read game state
                System.out.println("received:");
                System.out.println(state);

                first = gson.fromJson(in.readLine(), Boolean.class);// read game state
                System.out.println("received:" + first);

                // end of round
            } while (state.getPlayer().getHp() > 0 && state.getOpponent().getHp() > 0);
            System.out.println("execution finished");
        } catch (IOException e) {
            e.printStackTrace();
        }
        client2.close();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}