package au.com.greater.transaction.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * This model class represents a single customer transaction file.
 *
 * @author Justin Lewis Salmon
 */
@Getter
@RequiredArgsConstructor
public class TransactionFile {

  private final Path path;

  private final List<Transaction> transactions = new ArrayList<>();

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

    transactions.add(transaction);
  }

  /**
   * @return the total number of unique customer accounts in this file
   */
  public long getNumAccounts() {
    return transactions.stream()
        .map(Transaction::getCustomerAccountNumber)
        .distinct()
        .count();
  }

  /**
   * @return the total credit amount for all accounts in this file
   */
  public double getTotalCredits() {
    return transactions.stream()
        .filter(t -> t.getTransactionAmount() > 0)
        .mapToDouble(Transaction::getTransactionAmount)
        .sum();
  }

  /**
   * @return the total debit amount for all accounts in this file
   */
  public double getTotalDebits() {
    return transactions.stream()
        .filter(t -> t.getTransactionAmount() < 0)
        .mapToDouble(t -> Math.abs(t.getTransactionAmount()))
        .sum();
  }

  /**
   * Generate a report from this file detailing:
   *
   * - The name of the file processed
   * - The number of accounts processed
   * - The total credit amount
   * - The total debit amount
   * - The number of lines that were skipped

   * @return the report as a string
   */
  public String generateReport() {
    return "File Processed: " + getPath().getFileName().toString() +
        "\nTotal Accounts: " + String.format("%,d", getNumAccounts()) +
        "\nTotal Credits : " + String.format("$%,.2f", getTotalCredits()) +
        "\nTotal Debits  : " + String.format("$%,.2f", getTotalDebits()) +
        "\nSkipped Transactions: " + getNumSkippedTransactions();
  }
}
