import java.util.Random;

public class Main {

    public static void main(String[] args) {

        FileManager manager = new FileManager();

        Writer writer = new Writer(manager);
        writer.start();

        Random rand = new Random();

        while (true) {

            try {

                Thread.sleep(rand.nextInt(2000));

                Reader reader = new Reader(manager);
                reader.start();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}