package au.com.greater.transaction.account;

import au.com.greater.transaction.model.Transaction;
import au.com.greater.transaction.model.TransactionFile;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Customer accounts are maintained in a {@link Map} for lookup
 * performance. If an account is not present in the map, a new instance is
 * added with the initial debit/credit amount.
 *
 * @author Justin Lewis Salmon
 */
@Service
public class CustomerAccountService {

  @Getter
  private Map<Integer, CustomerAccount> accounts = new HashMap<>();

  /**
   * Apply all transactions from a {@link TransactionFile} to their
   * corresponding customer accounts.
   *
   * @param file the {@link TransactionFile} containing transactions to be
   *             applied
   */
  public void applyTransactions(TransactionFile file) {
    for (Transaction transaction : file.getTransactions()) {
      applyTransaction(transaction);
    }
  }

  /**
   * Apply a single transaction to the corresponding customer account.
   *
   * @param transaction the {@link Transaction} to apply
   */
  public void applyTransaction(Transaction transaction) {
    CustomerAccount account = accounts.get(transaction.getCustomerAccountNumber());

    if (account == null) {
      account = new CustomerAccount(transaction.getCustomerAccountNumber());
    }

    account.apply(transaction);
    accounts.put(account.getAccountNumber(), account);
  }

  public double getAccountBalance(int customerAccountNumber) {
    CustomerAccount account = accounts.get(customerAccountNumber);

    if (account == null) {
      throw new RuntimeException("Customer account " + customerAccountNumber + " does not exist!");
    }

    return account.getBalance();
  }
}
