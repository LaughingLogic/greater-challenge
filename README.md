## Greater Bank Coding Challenge

This repository contains [@jlsalmon](https://github.com/jlsalmon)'s source code 
for the Greater Bank Coding Challenge.

### Design discussion

From a high level perspective, the application could be designed in two ways:

- As a long-running daemon process that contains its own scheduling mechanism;
- As a one-shot program that is triggered by some external scheduler (e.g. cron).

It has been decided to choose the former scheme, as it is more suited to a
self-contained application design and will not require interaction with external
systems. However, the system should be decoupled such that it can be easily
reconfigured to use the latter scheme.

#### Separation of concerns

In general, the system should have a clear separation of concerns between logical
chunks. The following distinct areas of operation have been identified:

- Scheduling of processing job execution;
- High-level control flow orchestration;
- Parsing of customer transaction files;
- Structuring of data (customer accounts and their credit/debit amounts);
- Filesystem related methods (reading/writing/copying files).

### Implementation

The application has been written in Java (1.8) as it is the author's language 
of preference for enterprise-grade applications. The source tree is structured using 
the standard Maven project layout. Dependency management is also handled via Maven.

The Spring framework (most notably Spring Boot) is used throughout the application.
Spring Boot uses a "convention over configuration" approach to minimise boilerplate code
and allow rapid application development.

[Project Lombok](https://projectlombok.org/features/index.html)
is also employed to remove the need to write getters/setters, constructors, etc.

#### Architecture

For a diagrammatic overview of the application architecture, see the 
[Class Diagram](docs/class-diagram.pdf)

The main entry point of the application is the 
[`Main`](src/main/java/au/com/greater/transaction/Main.java) class. This class is
annotated as a `SpringBootApplication` which will instruct the Spring framework to 
instantiate its context and wire up any `@Component` classes found on the classpath.

The [`Scheduler`](src/main/java/au/com/greater/transaction/Scheduler.java) class is
responsible for executing scheduled jobs. It makes use of Spring's scheduling support
(namely the `@EnableScheduling` and `@Scheduled` annotations) which makes it very simple 
to ensure scheduled method execution with very little plumbing.

The scheduler makes the following assumptions:

* The time zone is always UTC
* Transaction files take one minute or less to be received

Therefore, the scheduler runs at 06:01am and 21:01pm UTC each day. This ensures that 
processing commences within 5 minutes of delivery.

The scheduler delegates to the 
[`TransactionProcessor`](src/main/java/au/com/greater/transaction/TransactionProcessor.java)
class, which encapsulates the high-level control flow orchestration. The processing happens 
in three distinct stages:

1. Reading and processing all pending customer transaction files
2. Writing a report file for each processed file
3. Archiving each processed file

The `TRANSACTION_PROCESSING` environment variable, which is referenced in 
[application.properties](src/main/resources/application.properties), is used to determine
the location of customer transaction files. This is another example of a "convention over
configuration" approach that helps to reduce boilerplate code. If this variable is missing,
the application will fail to start.

The `TransactionProcessor` makes use of the 
[`TransactionFileParser`](src/main/java/au/com/greater/transaction/parser/TransactionFileParser.java)
which encapsulates the logic for parsing customer transactions from the data files 
(and skipping corrupt lines).

Each customer transaction file is loaded into a data structure called 
[`TransactionFile`](src/main/java/au/com/greater/transaction/model/TransactionFile.java).
This structure maintains a map of customer accounts and their debit/credit amounts, and
provides methods to retrieve total debit/credit amounts for all accounts.

[`FileUtils`](src/main/java/au/com/greater/transaction/utils/FileUtils.java) encapsulates
all filesystem related methods (reading/writing/moving files).

#### Exception handling strategy

As a general rule, any checked exceptions are wrapped and re-thrown as unchecked 
exceptions, which are caught and logged at the topmost level.

#### Performance

The average processing time for a file with 500,000 transactions is currently around
~900ms. This can potentially be optimised by improving the performance of 
`FileUtils#readLinesFromFile`. 

### Testing strategy 

As a general rule, all methods that contain logic are unit tested. The main processing 
control flow logic is integration tested with the entire application.

As of [693e6a9](https://github.com/jlsalmon/greater-challenge/commit/693e6a9cf588f708a93e35953a649182875cefe8), 
test coverage is 96%.

### Roadmap

#### CI / Deployment
The next stage would be to establish a continuous integration and deployment strategy. One 
proposed strategy would be to use a CI service such as [Travis CI](https://travis-ci.org/)
to automate builds, then deploy to a staging environment hosted on e.g. AWS, Google Cloud 
or a self-hosted platform e.g. OpenStack, OpenShift. Containerisation (if desired) could 
be achieved via Docker.

#### Scalability

Currently, if one tried to run multiple instances of the application on a single machine
using the same pending transaction directory, there would be contention for files within
that directory. Hence, this problem would need to be solved via some kind of concurrency
control mechanism before single-machine scaling would be possible.

Scaling via multiple machines or using dedicated pending transaction directories would be
feasible, but this would require changes in the mechanism that delivers the transaction
files.

#### Extensibility

The application can currently produce the report output as specified in the original
specification. However, there are no provisions for actually applying the transaction
data to customer accounts. 

Assuming that an external account management system already exists, the 
`TransactionProcessor` would need to be modified to delegate to a new component that would
be responsible for interfacing with the external system.

### How to run the application

Requirements: Java 1.8, Maven 3

Ensure the `$TRANSACTION_PROCESSING` variable is set on the system, then run the following:

```
$ mvn package
$ java -jar target/transaction-processor*.jar
```