import java.util.Random;

public class Writer extends Thread {

    private FileManager manager;
    private Random rand = new Random();
    private int version = 1;

    public Writer(FileManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {

        while (true) {

            try {

                Thread.sleep(rand.nextInt(5000));

                manager.startWrite();

                String newContent = "Updated version " + version++;

                manager.writeFiles(newContent);

                manager.endWrite(newContent);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}