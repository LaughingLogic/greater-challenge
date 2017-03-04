package au.com.greater.transaction.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.stream.Collectors.toList;

/**
 * Utility class containing static methods for reading/writing files on the
 * filesystem.
 *
 * @author Justin Lewis Salmon
 */
public class FileUtils {

  private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

  /**
   * List all files in the given directory path, excluding subdirectories.
   *
   * @param path the directory to be listed
   * @return a list of discovered files
   */
  public static List<Path> listFiles(Path path) {
    log.info("Loading files from directory at {}", path);

    try (Stream<Path> paths = Files.walk(path)) {
      return paths.filter(Files::isRegularFile).collect(toList());
    } catch (IOException e) {
      throw new RuntimeException("Error reading files from " + path, e);
    }
  }

  /**
   * Read the file at the given path and return a list of strings representing
   * the lines of the file.
   *
   * @param path the file to be read
   * @return the file contents, one string per line
   */
  public static List<String> readLinesFromFile(Path path) {
    log.info("Loading lines from file at {}", path);

    try (Stream<String> lines = Files.lines(path)) {
      return lines.collect(toList());
    } catch (IOException e) {
      throw new RuntimeException("Error reading file " + path, e);
    }
  }

  /**
   * Write the given content as a file to the given path.
   *
   * @param path    the path at which to write the file
   * @param content the file content to write
   */
  public static void writeFile(Path path, String content) {
    log.info("Writing file at {}", path);

    try {
      Files.write(path, content.getBytes());
    } catch (IOException e) {
      throw new RuntimeException("Error writing file " + path, e);
    }
  }

  /**
   * Move a file from one position on the filesystem to another.
   *
   * @param from the source file path
   * @param to   the target file path
   */
  public static void moveFile(Path from, Path to) {
    try {
      Files.move(from, to, REPLACE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException("Error moving file from " + from + " to " + to, e);
    }
  }
}
