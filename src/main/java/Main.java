import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        ExecutorService pool = Executors.newCachedThreadPool();
        //Callable
        Future<String> future = pool.submit(new Caller(new File("./out/production/String-searching-in-directory/dir"), "Qwe", pool));
        String result = future.get();
        System.out.println(result);
        System.out.printf("duration %,d (ms)%n", System.currentTimeMillis() - start);
        start = System.currentTimeMillis();
        //Runnable
        pool.submit(new Runner(new File("./out/production/String-searching-in-directory/dir"), "Qwe", pool));
        pool.shutdown();
        while (!pool.isTerminated()) ;
        System.out.printf("duration %,d (ms)%n", System.currentTimeMillis() - start);
    }
}
