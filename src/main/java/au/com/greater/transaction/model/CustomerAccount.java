package au.com.greater.transaction.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * This mutable model class is used to keep track of credits and debits for a
 * single customer account.
 *
 * @author Justin Lewis Salmon
 */
@Getter
@RequiredArgsConstructor
public class CustomerAccount {

  private final int accountNumber;

  private double credits = 0.0;

  private double debits = 0.0;

  public void debit(double amount) {
    debits += Math.abs(amount);
  }

  public void credit(double amount) {
    credits += amount;
  }
}
