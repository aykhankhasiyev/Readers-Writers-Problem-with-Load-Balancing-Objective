import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class FileManager {

    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition canRead = lock.newCondition();
    private final Condition canWrite = lock.newCondition();

    private int activeReaders = 0;
    private int waitingWriters = 0;
    private boolean writerActive = false;

    private int[] readersPerFile = new int[3];
    private String[] files = {"Initial content", "Initial content", "Initial content"};

    private int chooseFile() {

        int index = 0;

        for (int i = 1; i < 3; i++) {
            if (readersPerFile[i] < readersPerFile[index]) {
                index = i;
            }
        }

        return index;
    }

    public int startRead() throws InterruptedException {

        lock.lock();

        try {

            while (writerActive || waitingWriters > 0) {
                canRead.await();
            }

            int fileIndex = chooseFile();

            activeReaders++;
            readersPerFile[fileIndex]++;

            return fileIndex;

        } finally {
            lock.unlock();
        }
    }

    public void endRead(int fileIndex) {

        lock.lock();

        try {

            activeReaders--;
            readersPerFile[fileIndex]--;

            if (activeReaders == 0) {
                canWrite.signal();
            }

        } finally {
            lock.unlock();
        }
    }

    public void startWrite() throws InterruptedException {

        lock.lock();

        try {

            waitingWriters++;

            while (activeReaders > 0 || writerActive) {
                canWrite.await();
            }

            waitingWriters--;
            writerActive = true;

        } finally {
            lock.unlock();
        }
    }

    public void writeFiles(String newContent) {

        for (int i = 0; i < 3; i++) {
            files[i] = newContent;
        }
    }

    public void endWrite(String newContent) {

        lock.lock();

        try {

            log(
                    "Writer updated files | Readers: [" +
                    readersPerFile[0] + ", " +
                    readersPerFile[1] + ", " +
                    readersPerFile[2] + "]" +
                    " | Writer active: true | New Content: " +
                    newContent
            );

            writerActive = false;

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

    public synchronized void log(String message) {

        try (FileWriter fw = new FileWriter("log.txt", true)) {

            fw.write(message + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}