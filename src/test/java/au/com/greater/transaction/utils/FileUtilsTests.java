package au.com.greater.transaction.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.nio.file.Paths;

/**
 * Unit tests for {@link FileUtils}
 *
 * @author Justin Lewis Salmon
 */
@RunWith(JUnit4.class)
public class FileUtilsTests {

  @Test(expected = RuntimeException.class)
  public void listingMissingDirectoryThrowsException() {
    FileUtils.listFiles(Paths.get("non-existent/"));
  }

  @Test(expected = RuntimeException.class)
  public void readingMissingFileThrowsException() {
    FileUtils.readLinesFromFile(Paths.get("non-existent.csv"));
  }

  @Test(expected = RuntimeException.class)
  public void writingToMissingDirectoryThrowsException() {
    FileUtils.writeFile(Paths.get("/non-existent/test.csv"), "");
  }

  @Test(expected = RuntimeException.class)
  public void movingMissingFileThrowsException() {
    FileUtils.moveFile(Paths.get("non-existent.csv"), Paths.get("non-existent2.csv"));
  }
}
