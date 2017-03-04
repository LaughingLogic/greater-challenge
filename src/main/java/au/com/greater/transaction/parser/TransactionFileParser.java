package au.com.greater.transaction.parser;

import au.com.greater.transaction.model.Transaction;
import au.com.greater.transaction.model.TransactionFile;
import au.com.greater.transaction.utils.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.nio.file.Path;
import java.util.List;

/**
 * This class is responsible for reading customer transaction files and
 * processing them into {@link TransactionFile} instances.
 *
 * @author Justin Lewis Salmon
 */
public class TransactionFileParser {

  /**
   * Read the customer transaction file at {@literal path} and process it into
   * a {@link TransactionFile} object.
   *
   * @param path the path to the customer transaction file
   * @return a {@link TransactionFile} instance representing the contents of
   * the customer transaction file
   */
  public static TransactionFile fromPath(Path path) {
    TransactionFile transactionFile = new TransactionFile(path);

    List<String> lines = FileUtils.readLinesFromFile(path);

    // Skip the header line
    if (lines.size() > 0) {
      lines.remove(0);
    }

    for (String line : lines) {
      transactionFile.addTransaction(processLine(line));
    }

    return transactionFile;
  }

  /**
   * Process a single line from a customer transaction file and return a
   * {@link Transaction} instance representing the transaction. The line should
   * be of the following format:
   *
   *   123456789, 100.00
   *
   * where the first comma-delimited token is a customer account number, and
   * the second is a transaction amount.
   *
   * If the customer account number is non-numeric or the format is violated in
   * any other way, the transaction will be skipped (null returned).
   *
   * @param line the transaction line to process
   * @return a {@link Transaction} object, or null if the transaction is to be
   * skipped
   */
  private static Transaction processLine(String line) {
    String[] parts = line.replace(" ", "").split(",");

    if (parts.length != 2) {
      return null;
    }

    String customerAccountNumber = parts[0];
    if (!StringUtils.isNumeric(customerAccountNumber)) {
      return null;
    }

    String transactionAmount = parts[1];
    if (!NumberUtils.isCreatable(transactionAmount)) {
      return null;
    }

    return new Transaction(Integer.parseInt(customerAccountNumber), Double.parseDouble(transactionAmount));
  }
}
