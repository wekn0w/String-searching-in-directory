import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

public class RecursiveExecutor extends RecursiveTask<String> {
    private String text;
    private File folder;

    public RecursiveExecutor(String text, File folder) {
        this.text = text;
        this.folder = folder;
    }

    @Override
    protected String compute() {
        String result = null;
        try {
            StringJoiner builder = listFilesForText(folder, text);
            result = builder != null ? builder.toString() : null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public StringJoiner listFilesForText(final File folder, String text) throws IOException {
        StringJoiner builder = new StringJoiner(", ");
        //System.out.println(Thread.currentThread().getName());
        List<RecursiveExecutor> tasks = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                //add subtasks
                tasks.add(new RecursiveExecutor(text, fileEntry));
            } else {
                //or check file
                if (checkFile(fileEntry, text))
                    builder.add(fileEntry.getName());
            }
        }
        if (!tasks.isEmpty()){
            for (RecursiveExecutor subtask : tasks){
                subtask.fork();
            }
            String s = invokeAll(tasks).stream().map(ForkJoinTask::join).collect(Collectors.joining());
            if (!s.isBlank())
                builder.add(s);
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
