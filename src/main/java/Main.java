import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        final long start = System.currentTimeMillis();
        Thread thread = new Thread(new Runner(new File("./out"), "Qwe"));
        thread.start();
        System.out.printf("duration %,d (ms)%n", System.currentTimeMillis() - start);
    }
}
