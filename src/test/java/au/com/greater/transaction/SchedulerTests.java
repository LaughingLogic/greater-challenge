package au.com.greater.transaction;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration tests for {@link Scheduler} within the Spring context
 *
 * @author Justin Lewis Salmon
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = Main.class,
    properties = {
        "processing.baseDir=/",
        "processing.pendingDir=/tmp"
    }
)
public class SchedulerTests {

  @Autowired
  private Scheduler scheduler;

  @Test
  public void schedulerRuns() {
    scheduler.executeTransactionProcessor();
  }
}
