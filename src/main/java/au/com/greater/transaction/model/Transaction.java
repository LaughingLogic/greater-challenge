package au.com.greater.transaction.model;

import lombok.Getter;

/**
 * This model class represents a single, immutable transaction for a single
 * customer account.
 *
 * @author Justin Lewis Salmon
 */
@Getter
public class Transaction {

  private final int customerAccountNumber;

  private final double transactionAmount;

  public Transaction(int customerAccountNumber, double transactionAmount) {
    this.customerAccountNumber = customerAccountNumber;
    this.transactionAmount = transactionAmount;
  }
}
