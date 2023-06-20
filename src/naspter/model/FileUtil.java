package naspter.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
  public static List<String> getAllFileNamesInDirectory(String directoryPath) throws IOException {
    List<String> fileNames = new ArrayList<>();
    Path directory = Paths.get(directoryPath);
    Files.walk(directory)
        .filter(Files::isRegularFile)
        .forEach(path -> fileNames.add(path.getFileName().toString()));
    return fileNames;
  }
}