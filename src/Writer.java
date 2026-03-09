import java.util.Random;

/*
 * Writer thread.
 * Only one writer exists and it periodically updates all file replicas.
 */
public class Writer extends Thread {

    private FileManager manager;
    private Random rand = new Random();
    private int version = 1;

    public Writer(FileManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {

        // Writer runs continuously
        while (true) {

            try {

                // Sleep for a random time before attempting to write
                Thread.sleep(rand.nextInt(5000));

                // Request exclusive access for writing
                manager.startWrite();

                // Create new content
                String newContent = "Updated version " + version++;

                // Update all replicas
                manager.writeFiles(newContent);

                // Finish writing and release readers
                manager.endWrite(newContent);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}