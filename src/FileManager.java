import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/*
 * FileManager controls all synchronization between readers and writer.
 * It also manages the three file replicas and logging.
 */
public class FileManager {

    // Fair lock ensures threads acquire lock in order
    private final ReentrantLock lock = new ReentrantLock(true);

    // Condition variables used to control reader and writer waiting
    private final Condition canRead = lock.newCondition();
    private final Condition canWrite = lock.newCondition();

    private int activeReaders = 0;
    private int waitingWriters = 0;
    private boolean writerActive = false;

    // Number of readers currently reading each replica
    private int[] readersPerFile = new int[3];

    // Three replicas of the file
    private String[] files = {"Initial content", "Initial content", "Initial content"};

    /*
     * Chooses the replica with the smallest number of readers.
     * This implements load balancing.
     */
    private int chooseFile() {

        int index = 0;

        for (int i = 1; i < 3; i++) {
            if (readersPerFile[i] < readersPerFile[index]) {
                index = i;
            }
        }

        return index;
    }

    /*
     * Called by a reader before reading.
     * Ensures writer priority and balanced replica selection.
     */
    public int startRead() throws InterruptedException {

        lock.lock();

        try {

            /*
             * Readers must wait if:
             * 1. A writer is currently writing
             * 2. A writer is waiting (writer priority)
             */
            while (writerActive || waitingWriters > 0) {
                canRead.await();
            }

            // Select replica while lock is held to avoid race conditions
            int fileIndex = chooseFile();

            activeReaders++;
            readersPerFile[fileIndex]++;

            return fileIndex;

        } finally {
            lock.unlock();
        }
    }

    /*
     * Called when a reader finishes reading.
     */
    public void endRead(int fileIndex) {

        lock.lock();

        try {

            activeReaders--;
            readersPerFile[fileIndex]--;

            // If this was the last reader, writers may proceed
            if (activeReaders == 0) {
                canWrite.signal();
            }

        } finally {
            lock.unlock();
        }
    }

    /*
     * Called by the writer before writing.
     * Ensures exclusive access.
     */
    public void startWrite() throws InterruptedException {

        lock.lock();

        try {

            waitingWriters++;

            // Writer waits until no readers or other writer are active
            while (activeReaders > 0 || writerActive) {
                canWrite.await();
            }

            waitingWriters--;
            writerActive = true;

        } finally {
            lock.unlock();
        }
    }

    /*
     * Updates all three file replicas.
     */
    public void writeFiles(String newContent) {

        for (int i = 0; i < 3; i++) {
            files[i] = newContent;
        }
    }

    /*
     * Called after writing finishes.
     * Releases waiting readers and writers.
     */
    public void endWrite(String newContent) {

        lock.lock();

        try {

            // Log the write operation
            log(
                    "Writer updated files | Readers: [" +
                    readersPerFile[0] + ", " +
                    readersPerFile[1] + ", " +
                    readersPerFile[2] + "]" +
                    " | Writer active: true | New Content: " +
                    newContent
            );

            writerActive = false;

            // Allow readers and writers to proceed
            canRead.signalAll();
            canWrite.signal();

        } finally {
            lock.unlock();
        }
    }

    public String readFile(int index) {
        return files[index];
    }

    public int[] getReadersPerFile() {
        return readersPerFile;
    }

    public boolean isWriterActive() {
        return writerActive;
    }

    /*
     * Thread-safe logging method.
     */
    public synchronized void log(String message) {

        try (FileWriter fw = new FileWriter("log.txt", true)) {

            fw.write(message + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}