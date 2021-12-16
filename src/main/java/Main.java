import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws IOException {
        checkFiles(Paths.get("./out"), "Qwe");
    }

    public static void checkFiles(Path dir, String text) throws IOException {
        final long start = System.currentTimeMillis();
        try (Stream<Path> paths = Files.walk(dir)) {

            /*
            //singlethread
            paths
                    .filter(Files::isRegularFile)
                    .filter(i -> {
                        try {
                            return checkFile(i, text);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return false;
                    })
                    .forEach(System.out::println);
                    System.out.printf("duration %,d (ms)%n", duration);*/

            //multiply thread
            ConcurrentMap<Path, AtomicInteger> concurrentMap = paths
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toConcurrentMap(Path::toAbsolutePath, s -> new AtomicInteger()));

            ExecutorService executor = Executors.newCachedThreadPool();
            Accessor a1 = new Accessor(concurrentMap, "Qwe");
            Accessor a2 = new Accessor(concurrentMap, "Qwe");
            executor.execute(a1);
            executor.execute(a2);
            executor.shutdown();

            final long duration = System.currentTimeMillis() - start;
            System.out.printf("duration %,d (ms)%n", duration);
        } catch (NoSuchFileException e) {
            System.out.println("Empty directory or directory does not exists");
        }
    }

    public static boolean checkFile(Path file, String text) throws IOException {
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

    public static void check(Map<Path, Integer> map, String keyText) {
        for (Map.Entry<Path, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 0) {
                try {
                    if (checkFile(entry.getKey(), keyText)) {
                        System.out.println(entry.getKey());
                        entry.setValue(1);
                    }
                } catch (IOException e) {
                    System.out.println("Empty directory or directory does not exists");
                }
            }
        }
    }
}
