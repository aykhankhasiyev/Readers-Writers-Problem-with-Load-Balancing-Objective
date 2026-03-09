import java.util.Random;

public class Reader extends Thread {

    private FileManager manager;
    private static int counter = 0;
    private int id;

    public Reader(FileManager manager) {
        this.manager = manager;
        id = ++counter;
    }

    @Override
    public void run() {

        try {

            int fileIndex = manager.startRead();

            String content = manager.readFile(fileIndex);

            int[] readers = manager.getReadersPerFile();

            manager.log(
                    "Reader " + id +
                    " reading File " + fileIndex +
                    " | Readers: [" +
                    readers[0] + ", " +
                    readers[1] + ", " +
                    readers[2] + "]" +
                    " | Writer active: " +
                    manager.isWriterActive() +
                    " | Content: " + content
            );

            Thread.sleep(new Random().nextInt(1000));

            manager.endRead(fileIndex);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}