package au.com.greater.transaction.model;

import au.com.greater.transaction.model.Transaction;
import au.com.greater.transaction.model.TransactionFile;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link TransactionFile}
 *
 * @author Justin Lewis Salmon
 */
@RunWith(JUnit4.class)
public class TransactionFileTests {

  private TransactionFile file;

  @Before
  public void before() {
    file = new TransactionFile(Paths.get("dummy.csv"));
  }

  @Test
  public void nullTransactionIsSkipped() {
    file.addTransaction(null);
    assertEquals(1, file.getNumSkippedTransactions());
    assertEquals(0, file.getAccounts().size());
  }

  @Test
  public void multipleTransactionsPerCustomer() {
    file.addTransaction(new Transaction(1, 100.0));
    file.addTransaction(new Transaction(1, -50.0));

    assertEquals(1, file.getAccounts().size());
    assertEquals(100.0, file.getAccounts().get(1).getCredits(), 0);
    assertEquals(50.0, file.getAccounts().get(1).getDebits(), 0);
  }

  @Test
  public void totalCredits() {
    file.addTransaction(new Transaction(1, 100.0));
    file.addTransaction(new Transaction(2, 50.0));

    assertEquals(2, file.getAccounts().size());
    assertEquals(150.0, file.getTotalCredits(), 0);
    assertEquals(0, file.getTotalDebits(), 0);
  }

  @Test
  public void totalDebits() {
    file.addTransaction(new Transaction(1, -100.0));
    file.addTransaction(new Transaction(2, -50.0));

    assertEquals(2, file.getAccounts().size());
    assertEquals(0, file.getTotalCredits(), 0);
    assertEquals(150.0, file.getTotalDebits(), 0);
  }
}
