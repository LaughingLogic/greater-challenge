package au.com.greater.transaction.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * This model class represents a single customer transaction file.
 *
 * As transactions are added, they are applied to the corresponding customer
 * account by either debiting or crediting the account. Customer accounts are
 * maintained in a {@link Map} for lookup performance. If an account is not
 * present in the map, a new instance is added with the initial debit/credit
 * amount.
 *
 * Null transactions will be skipped and added to the total skipped transaction
 * count.
 *
 * @author Justin Lewis Salmon
 */
@Getter
@RequiredArgsConstructor
public class TransactionFile {

  private final Path path;

  private final Map<Integer, CustomerAccount> accounts = new HashMap<>();

  private int numSkippedTransactions = 0;

  /**
   * Add a single {@link Transaction} to this file.
   *
   * If the transaction is null, it will be skipped and the total skipped
   * transaction count will be incremented.
   *
   * @param transaction the transaction to add
   */
  public void addTransaction(Transaction transaction) {
    if (transaction == null) {
      numSkippedTransactions++;
      return;
    }

    CustomerAccount account = accounts.get(transaction.getCustomerAccountNumber());

    if (account == null) {
      account = new CustomerAccount(transaction.getCustomerAccountNumber());
    }

    Double transactionAmount = transaction.getTransactionAmount();

    if (transactionAmount < 0) {
      // Negative transaction amounts represent a debit against a customer
      // account, and the customer's account balance is increased.
      account.debit(transactionAmount);
    } else {
      // Positive transaction amounts represent a credit against a customer
      // account, and the customer's balance is decreased.
      account.credit(transactionAmount);
    }

    accounts.put(account.getAccountNumber(), account);
  }

  /**
   * @return the total credit amount for all accounts in this file
   */
  public double getTotalCredits() {
    return accounts.values().stream().mapToDouble(CustomerAccount::getCredits).sum();
  }

  /**
   *
   * @return the total debit amount for all accounts in this file
   */
  public double getTotalDebits() {
    return accounts.values().stream().mapToDouble(CustomerAccount::getDebits).sum();
  }

  @Override
  public String toString() {
    return "File Processed: " + getPath().getFileName().toString() +
         "\nTotal Accounts: " + String.format("%,d", accounts.size()) +
         "\nTotal Credits : " + String.format("$%,.2f", getTotalCredits()) +
         "\nTotal Debits  : " + String.format("$%,.2f", getTotalDebits()) +
         "\nSkipped Transactions: " + numSkippedTransactions;
  }
}
