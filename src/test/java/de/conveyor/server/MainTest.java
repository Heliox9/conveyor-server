package de.conveyor.server;

import com.google.gson.Gson;
import de.conveyor.game.GameState;
import de.conveyor.game.Item;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

class MainTest {
    Gson gson = new Gson();

    @Test
    public void givenClient1_whenServerResponds_thenCorrect() throws IOException {
        Thread t = new Thread(() -> {
            Socket client1 = null;
            try {
                client1 = new Socket("127.0.0.1", 88);

                PrintWriter out = new PrintWriter(client1.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(client1.getInputStream()));
                out.println("Test 1");
                in.readLine();// game id
                in.readLine();// opponent

                GameState state = null;

                do {// iterate rounds
                    ItemSelection selection = gson.fromJson(in.readLine(), ItemSelection.class);// set

                    // generate selection
                    ArrayList<Item> bought = new ArrayList<Item>();
                    bought.add(selection.getSelection().get(0));
                    ArrayList<Item> saved = new ArrayList<Item>();
                    saved.add(selection.getSelection().get(1));
                    selection.setBought(bought);
                    selection.setSaved(saved);

                    out.println(gson.toJson(selection));

                    // read fight stats
                    state = gson.fromJson(in.readLine(), GameState.class);// read Gamestate

                    // end of round
                } while (state.getPlayer().getHp() > 0 && state.getOpponent().getHp() > 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();


        Socket client1 = null;
        try {
            client1 = new Socket("127.0.0.1", 88);

            PrintWriter out = new PrintWriter(client1.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(client1.getInputStream()));

            // Enter Player name
            out.println("Test 2");
            System.out.println(in.readLine());
            System.out.println(in.readLine());
            System.out.println(in.readLine());// game id
            System.out.println(in.readLine());// opponent

            GameState state = null;

            do {// iterate rounds
                ItemSelection selection = gson.fromJson(in.readLine(), ItemSelection.class);// set

                // generate selection
                ArrayList<Item> bought = new ArrayList<Item>();
                bought.add(selection.getSelection().get(0));
                ArrayList<Item> saved = new ArrayList<Item>();
                saved.add(selection.getSelection().get(1));
                selection.setBought(bought);
                selection.setSaved(saved);

                out.println(gson.toJson(selection));

                // read fight stats
                state = gson.fromJson(in.readLine(), GameState.class);// read Gamestate

                // end of round
            } while (state.getPlayer().getHp() > 0 && state.getOpponent().getHp() > 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}