package au.com.greater.transaction.parser;

import au.com.greater.transaction.model.TransactionFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link TransactionFileParser}
 *
 * @author Justin Lewis Salmon
 */
@RunWith(JUnit4.class)
public class TransactionFileParserTests {

  @Test
  public void corruptLinesAreSkipped() throws IOException {
    Path path = new ClassPathResource("pending/finance_customer_transactions-corrupt_lines.csv").getFile().toPath();
    TransactionFile file = TransactionFileParser.fromPath(path);

    // There should be one good line and six corrupt lines
    assertEquals(1, file.getAccounts().size());
    assertEquals(6, file.getNumSkippedTransactions());
  }

  @Test
  public void emptyFileHandledCorrectly() throws IOException {
    Path path = new ClassPathResource("pending/finance_customer_transactions-empty.csv").getFile().toPath();
    TransactionFile file = TransactionFileParser.fromPath(path);

    assertEquals(0, file.getAccounts().size());
    assertEquals(0, file.getNumSkippedTransactions());
  }

  @Test
  public void headerOnlyHandledCorrectly() throws IOException {
    Path path = new ClassPathResource("pending/finance_customer_transactions-header_only.csv").getFile().toPath();
    TransactionFile file = TransactionFileParser.fromPath(path);

    assertEquals(0, file.getAccounts().size());
    assertEquals(0, file.getNumSkippedTransactions());
  }
}
