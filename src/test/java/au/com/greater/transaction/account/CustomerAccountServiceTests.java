package au.com.greater.transaction.account;

import au.com.greater.transaction.model.Transaction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

/**
 * @author Justin Lewis Salmon
 */
@RunWith(JUnit4.class)
public class CustomerAccountServiceTests {

  private CustomerAccountService accountService = new CustomerAccountService();

  @Test
  public void debitIncreasesBalance() {
    accountService.applyTransaction(new Transaction(1, -100.0));
    assertEquals(100.0, accountService.getAccountBalance(1), 0);
  }

  @Test
  public void creditDecreasesBalance() {
    accountService.applyTransaction(new Transaction(1, 100.0));
    assertEquals(-100.0, accountService.getAccountBalance(1), 0);
  }
}
