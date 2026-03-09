import java.util.Random;

/*
 * Main class starts the simulation.
 * It creates one writer thread and continuously spawns reader threads
 * at random intervals.
 */
public class Main {

    public static void main(String[] args) {

        // Shared manager that controls synchronization and file access
        FileManager manager = new FileManager();

        // Start the single writer thread
        Writer writer = new Writer(manager);
        writer.start();

        Random rand = new Random();

        // Continuously create readers at random intervals
        while (true) {

            try {

                // Random delay between reader creation
                Thread.sleep(rand.nextInt(2000));

                // Each reader reads once and terminates
                Reader reader = new Reader(manager);
                reader.start();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}