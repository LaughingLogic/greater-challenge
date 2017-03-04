package au.com.greater.transaction.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * This model class represents a single, immutable transaction for a single
 * customer account.
 *
 * @author Justin Lewis Salmon
 */
@Getter
@RequiredArgsConstructor
public class Transaction {

  private final int customerAccountNumber;
  private final double transactionAmount;
}
