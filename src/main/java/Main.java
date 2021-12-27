import java.io.File;
import java.util.concurrent.*;

public class Main {

    /**
     * resources содержит каталог с файлами, в которых содержится текст
     * программа проверяет содержимое каталога на присутствие в нем строки match максимально быстро
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String path = "./out/production/String-searching-in-directory/dir";
        String match = "Qwe";
        long start = System.currentTimeMillis();
        //ForkJoinPool
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool(); //new ForkJoinPool(4);
        RecursiveExecutor recursive = new RecursiveExecutor(match, new File(path));
        String recursiveResult = forkJoinPool.invoke(recursive);
        System.out.println(recursiveResult);
        System.out.printf("duration %,d (ms)%n", System.currentTimeMillis() - start);

        //Callable
        start = System.currentTimeMillis();
        ExecutorService pool = Executors.newCachedThreadPool();
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
