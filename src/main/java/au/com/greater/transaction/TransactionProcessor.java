package au.com.greater.transaction;

import au.com.greater.transaction.account.CustomerAccountService;
import au.com.greater.transaction.model.TransactionFile;
import au.com.greater.transaction.parser.TransactionFileParser;
import au.com.greater.transaction.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * This component is responsible for orchestrating the transaction processing
 * workflow. The processing happens in four distinct stages:
 *
 * 1. Reading all pending transaction files
 * 2. Applying new transactions to customer accounts
 * 3. Writing a report file for each processed file
 * 4. Archiving each processed file
 *
 * @author Justin Lewis Salmon
 */
@Component
@Setter
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TransactionProcessor {

  private static final Logger log = LoggerFactory.getLogger(TransactionProcessor.class);

  private final CustomerAccountService accountService;

  @Value("${processing.pendingDir}")
  private String pendingDir;

  @Value("${processing.reportsDir}")
  private String reportsDir;

  @Value("${processing.archiveDir}")
  private String archiveDir;

  /**
   * This is the main entry point for the transaction file processing workflow.
   */
  public void execute() {
    // Read all pending transaction files
    List<TransactionFile> transactionFiles = readPendingFiles();

    for (TransactionFile file : transactionFiles) {
      // Apply transactions from to customer accounts
      accountService.applyTransactions(file);

      // Write the report, then archive the file
      writeReport(file);
      archiveFile(file);
    }
  }

  /**
   * Read all pending transaction files into {@link TransactionFile} instances.
   *
   * This method assumes that {@literal pendingDir} contains only customer
   * transaction files and no other types of file.
   */
  private List<TransactionFile> readPendingFiles() {
    log.info("Loading pending transactions");
    List<TransactionFile> transactionFiles = new ArrayList<>();

    for (Path path : FileUtils.listFiles(Paths.get(pendingDir))) {
      transactionFiles.add(TransactionFileParser.fromPath(path));
    }

    return transactionFiles;
  }

  /**
   * Write a report file summarising the given {@link TransactionFile}.
   *
   * @param file the {@link TransactionFile} to be written
   */
  private void writeReport(TransactionFile file) {
    String filename = file.getPath().getFileName().toString();

    // It is assumed that the files are well-named in advance
    String datetime = filename.replace("finance_customer_transactions-", "").replace(".csv", "");
    Path path = Paths.get(reportsDir,  "finance_customer_transactions_report-" + datetime + ".txt");

    log.info("Writing report to {}", path);
    FileUtils.writeFile(path, file.generateReport());
  }

  /**
   * Archive the given {@link TransactionFile}. An archived file represents
   * one that has already been processed, and will therefore not be processed
   * more than once.
   *
   * @param file the {@link TransactionFile} to be archived
   */
  private void archiveFile(TransactionFile file) {
    log.info("Archiving transaction file {}", file.getPath().toString());
    FileUtils.moveFile(file.getPath(), Paths.get(archiveDir, file.getPath().getFileName().toString()));
  }
}
