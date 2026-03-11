# Technical Assessment: Digital Wallet Microservice

## Project Description
This is a robust Digital Wallet Microservice built with Spring Boot. It simulates user balances, peer-to-peer fund transfers, and wallet top-ups/debits.

The emphasis of this architecture is on **data integrity, scalability, and clean API design**.

### Key Architectural Decisions:
* **Decoupled Business Keys:** Internal database Primary Keys (`BIGINT`) are fully decoupled from Business Reference Numbers. Transaction IDs are formatted as an 18-digit string (`yyMMddHHmm` + 8-digit native PostgreSQL sequence with database-level CYCLE wrap-around logic).
* **ACID Transactions:** Financial operations utilize Spring's `@Transactional` boundaries. Transfers process both debit and credit within a single boundary to guarantee atomicity and prevent partial commits.
* **Archival Scheduler:** Features an automated batch-processing scheduler that moves completed transactions from the active ledger (`txn_master`) to cold storage (`txn_history`), keeping queries on active balances fast.
* **Global Exception Handling:** Clean, predictable JSON error responses (e.g., catching `InsufficientFundsException` and returning a `400 BAD_REQUEST`).

---

## Technology Stack
* **Language:** Java 17
* **Framework:** Spring Boot 3.5.x (Web, Data JPA, Validation)
* **Database:** PostgreSQL 16
* **Documentation:** Swagger / OpenAPI 3.0
* **Testing:** JUnit 5, Mockito
* **Build Tool:** Maven

---

## Database Schema Design

The database consists of three main tables, plus a sequence generator.

**1. `users` Table**
* `id` (VARCHAR(20), PK): Custom string-based identifier (e.g., "NAJMI-001")
* `name` (VARCHAR)
* `email` (VARCHAR, UNIQUE)
* `balance` (NUMERIC(15,2)): Stored precisely to prevent floating-point errors.

**2. `txn_master` Table (Active Ledger)**
* `id` (BIGSERIAL, PK): Surrogate internal key
* `reference_number` (VARCHAR(18), UNIQUE): The business-facing transaction ID
* `type` (VARCHAR): CREDIT or DEBIT
* `category` (VARCHAR): TOPUP, TRANSFER, PAYMENT
* `status` (VARCHAR): PENDING, SUCCESS, FAILED
* `amount` (NUMERIC(15,2))
* `source_user_id` / `destination_user_id` (VARCHAR(20), FK to users)
* `timestamp` (TIMESTAMP)

**3. `txn_history` Table (Archived Ledger)**
Identical to `txn_master`, but uses a standard `BIGINT` PK inherited from the master table to prevent ID collision. Added `move_date` to track when the scheduler archived the record.

---

## Instructions to Run Locally

### Prerequisites
1. Java 17+ installed
2. PostgreSQL running locally on port `5432`
3. Maven installed

### Setup Steps
1. **Clone the repository:**
   ```bash
   git clone [https://github.com/najmideraman2000/miniwallet.git](https://github.com/najmideraman2000/miniwallet.git)
   cd miniwallet

2. **Setup the Database:**
   * Create a database named `miniwallet` in PostgreSQL.
   * Run the SQL initialization script located at `src/main/resources/db/migration/V0.0.1__create_wallet_schema.sql` to generate the schema and sequences.

3. **Configure Application Properties:**
   * Update the `src/main/resources/application.yaml` file with your PostgreSQL username and password if they differ from the default.

4. **Run the Application:**
   ```bash
   ./mvnw spring-boot:run

## API Documentation & Testing

The application features interactive Swagger documentation. Once the application is running, navigate to:

http://localhost:8080/swagger-ui/index.html

### Core Endpoints & Example Requests

1. **Create a User**
   * POST `/api/wallet/user`
   ```json
   {
      "id": "NAJMI-001",
      "name": "Najmi Deraman",
      "email": "najmi@example.com"
   }
   ```
2. **Top-Up Wallet**
   * POST `/api/wallet/{userId}/top-up`
   ```json
   {
      "amount": 150.50
   }
   ```
3. **Transfer Funds**
   * POST `/api/wallet/transfer`
   ```json
   {
      "sourceUserId": "NAJMI-001",
      "destinationUserId": "VENDOR-999",
      "amount": 50.00
   }
   ```
4. **Debit/Payment**
    * POST `/api/wallet/{userId}/debit`
   ```json
   {
      "amount": 15.00
   }
   ```
5. **Check Balance**
    * GET `/api/wallet/{userId}/balance`
6. **Create a User**
    * GET `/api/wallet/{userId}/transactions`

   
