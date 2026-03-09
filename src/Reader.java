import java.util.Random;

/*
 * Reader thread.
 * Each reader reads from one file replica once and then terminates.
 */
public class Reader extends Thread {

    private FileManager manager;
    private static int counter = 0; // used to give each reader a unique ID
    private int id;

    public Reader(FileManager manager) {
        this.manager = manager;
        id = ++counter;
    }

    @Override
    public void run() {

        try {

            // Request permission to start reading
            // FileManager returns the replica index assigned to this reader
            int fileIndex = manager.startRead();

            // Read the file content
            String content = manager.readFile(fileIndex);

            int[] readers = manager.getReadersPerFile();

            // Log the reading operation
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

            // Simulate reading time
            Thread.sleep(new Random().nextInt(1000));

            // Notify manager that the reader finished reading
            manager.endRead(fileIndex);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}