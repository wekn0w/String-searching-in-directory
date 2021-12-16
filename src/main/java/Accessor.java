import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class Accessor implements Runnable {
    private Map<Path, AtomicInteger> map;
    private String keyText;

    public Accessor(Map<Path, AtomicInteger> map, String keyText) {
        this.map = map;
        this.keyText = keyText;
    }

    @Override
    public void run() {
        for (Map.Entry<Path, AtomicInteger> entry : this.map.entrySet()) {
            try {
                checkOneAndUpdate(entry);
            } catch (IOException e) {
                System.out.println("Empty directory or directory does not exists");
            }
        }
    }

    private synchronized void checkOneAndUpdate(Map.Entry<Path, AtomicInteger> entry) throws IOException {
        if (entry.getValue().intValue() == 0) {
            if (checkFile(entry.getKey(), keyText))
                System.out.println(entry.getKey());
            //map.put(entry.getKey(), new AtomicInteger(1));
            entry.getValue().incrementAndGet();
        }
    }

    public boolean checkFile(Path file, String text) throws IOException {
        final Scanner scanner = new Scanner(file);
        boolean result = false;
        while (scanner.hasNextLine()) {
            final String lineFromFile = scanner.nextLine();
            if (lineFromFile.contains(text)) {
                result = true;
                break;
            }
        }
        return result;
    }
}