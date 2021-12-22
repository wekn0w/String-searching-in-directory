import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class Caller implements Callable<String> {
    private final ExecutorService pool;
    private String text;
    private File folder;

    public Caller(File folder, String text, ExecutorService pool) {
        this.text = text;
        this.folder = folder;
        this.pool = pool;
    }

    @Override
    public String call() {
        String result = null;
        try {
            StringJoiner stringBuilder = listFilesForText(folder, text);
            result = stringBuilder != null ? stringBuilder.toString() : null;
        } catch (IOException e) {
            System.out.print("Directory of file not found or empty");
        }
        return result;
    }

    public StringJoiner listFilesForText(final File folder, String text) throws IOException {
        StringJoiner builder = new StringJoiner(", ");
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                Future<String> future = pool.submit(new Caller(fileEntry, text, pool));
                try {
                    String s = future.get();
                    if (s != null && !s.isBlank())
                        builder.add(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            } else {
                if (checkFile(fileEntry, text))
                    builder.add(fileEntry.getName());
            }
        }
        return builder;
    }

    public boolean checkFile(File file, String text) throws IOException {
        boolean result;
        try (var stream = Files.lines(file.toPath())) {
            Optional<String> first = stream.filter(line -> line.contains(text)).findFirst();
            result = first.isPresent();
        }
        return result;
    }
}
