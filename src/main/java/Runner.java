import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;

public class Runner implements Runnable {
    private final ExecutorService pool;
    private String text;
    private File folder;

    public Runner(File folder, String text, ExecutorService pool) {
        this.text = text;
        this.folder = folder;
        this.pool = pool;
    }

    @Override
    public void run() {
        try {
            listFilesForText(folder, text);
        } catch (IOException e) {
            System.out.print("Directory of file not found or empty");
        }
    }

    public void listFilesForText(final File folder, String text) throws IOException {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                Thread thread = new Thread(new Runner(fileEntry, text, pool));
                thread.start();
            } else {
                if (checkFile(fileEntry, text))
                    System.out.println(fileEntry.getName());
            }
        }
    }

    public boolean checkFile(File file, String text) throws IOException {
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
