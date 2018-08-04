import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class test {
    private static ArrayList<Entry> actors = new ArrayList<Entry>();
    public static void main(String[] args) {
        File file = new File("src/main/movie_actors.dat");

        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            in.readLine();
            String curr;
            while ((curr=in.readLine())!=null) {
                System.out.println(curr);
                String[] attribs = curr.split("\t");
                Entry thisActor = null;
                if (attribs[1].equals("whittni-wright")) {
                    System.out.println("here");
                }
                // Check that we have all required attributes
                if (attribs.length != 4)
                    System.out.println("ERROR: parseActor requires four incoming attributes, but there are " + attribs.length);

                // If this is a new actor, add him or her to the actor list and the actor table. Else, find the actor we wish to link to.
                if (attribs[1].equals("robbie_coltrane")) {
                    System.out.println("Here");
                }
                for (Entry actor : actors) {
                    if (actor.getID().equals(attribs[1])) {
                        thisActor = actor;
                        break;
                    }
                }
                if (thisActor == null) {
                    Entry actor = new Entry(attribs[1], attribs[2]);
                    actors.add(actor);
                    thisActor = actor;
                }

            }

            in.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
        catch (IOException e) {
            System.out.println("IO");
        }
    }

}