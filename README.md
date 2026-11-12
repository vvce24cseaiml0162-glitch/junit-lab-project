# End-to-End JUnit 5 Lab Automation Project

[![JUnit CI/CD](https://github.com/vvce24cseaiml0162-glitch/junit-lab-project/actions/workflows/junit-tests.yml/badge.svg)](https://github.com/vvce24cseaiml0162-glitch/junit-lab-project/actions/workflows/junit-tests.yml)
[![Java 17](https://img.shields.io/badge/Java-17-orange.svg)](https://adoptium.net/)
[![JUnit 5](https://img.shields.io/badge/JUnit-5%20(Jupiter)-brightgreen.svg)](https://junit.org/junit5/)
[![Coverage](https://img.shields.io/badge/JaCoCo-Coverage%2095%2B%25-blue.svg)](target/site/jacoco/index.html)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED.svg)](Dockerfile)

Enterprise-grade, automated **Java & JUnit 5 Lab Project** designed to demonstrate advanced unit testing principles, Page Object Model style service modularity, containerized test execution with **Docker**, and continuous automated validation via **GitHub Actions CI/CD**.

---

## 📌 Project Overview

This project implements a multi-domain enterprise simulation tailored for university engineering software testing labs:
1. **Academic Student & USN Management Service**: Automatic VTU University Serial Number (USN) generation (`4VV24CI001`), registration, CRUD operations, academic marks evaluation, and CGPA calculations.
2. **Core Banking & Fund Transfer Service**: Account creation with minimum balance validation, deposits, withdrawals with daily limits, overdraft protection, account locking, and atomic inter-account transfers.
3. **E-Commerce Order & Billing Service**: Cart subtotal calculation, coupon discounts (`STUDENT10`, `FLAT50`, `FESTIVE25`), GST computation, and checkout lifecycle states.
4. **Validation & Mathematical Utilities**: Regex validators for emails, phone numbers, USN formats, and password strength; prime detection, and factorials.

---

## 🛠️ Technology Stack

| Layer | Technology | Version |
| :--- | :--- | :--- |
| **Language** | Java (OpenJDK) | 17 |
| **Testing Framework** | JUnit 5 Jupiter (`api`, `params`, `engine`) | 5.10.2 |
| **Assertion Library** | AssertJ Core & JUnit Jupiter Assertions | 3.25.3 |
| **Build & Dependency Tool** | Apache Maven | 3.9+ |
| **Code Coverage** | JaCoCo Maven Plugin | 0.8.11 |
| **Test Runner Plugin** | Maven Surefire Plugin | 3.2.5 |
| **Containerization** | Docker & Docker Compose | Multi-stage Alpine |
| **CI/CD Platform** | GitHub Actions | Ubuntu Latest |

---

## 📂 Project Directory Structure

```text
JUnit-Lab-Project/
├── .github/
│   └── workflows/
│       └── junit-tests.yml        # Automated CI/CD pipeline definition
├── src/
│   ├── main/
│   │   ├── java/com/lab/
│   │   │   ├── application/       # MainApplication entrypoint demonstration
│   │   │   ├── exceptions/        # Custom domain exceptions
│   │   │   ├── models/            # Entity models (Student, BankAccount, Order, etc.)
│   │   │   ├── services/          # Business logic services
│   │   │   └── utilities/         # ValidationUtils and MathUtils
│   │   └── resources/
│   └── test/
│       ├── java/com/lab/tests/
│       │   ├── BankAccountServiceTest.java
│       │   ├── MathUtilsTest.java
│       │   ├── OrderServiceTest.java
│       │   ├── StudentServiceTest.java
│       │   ├── ValidationUtilsTest.java
│       │   └── MainApplicationTest.java
│       └── resources/
├── reports/                       # Persisted test reports and JaCoCo coverage
├── Dockerfile                     # Multi-stage container definition
├── docker-compose.yml             # Container orchestration with volume mounts
├── .dockerignore
├── .gitignore
├── pom.xml                        # Maven configuration with dependencies & plugins
└── README.md
```

---

## 🧪 JUnit 5 Implementation Highlights

The project leverages comprehensive JUnit 5 Jupiter features:
* **Lifecycle Hooks**: `@BeforeAll`, `@AfterAll`, `@BeforeEach`, `@AfterEach`
* **Test Grouping**: `@Nested` hierarchical test classes for clean separation of concerns
* **Parameterized Tests**:
  * `@ParameterizedTest` with `@ValueSource` for validating email, phone, and USN inputs
  * `@ParameterizedTest` with `@CsvSource` for boundary grading and promo coupon thresholds
* **Advanced Assertions**:
  * `assertEquals`, `assertNotEquals`, `assertTrue`, `assertFalse`, `assertNotNull`
  * `assertThrows` and `assertDoesNotThrow` for exception verification
  * `assertAll` for non-short-circuiting grouped assertions
* **Performance Testing**: `@Timeout(value = 1, unit = TimeUnit.SECONDS)` for algorithm performance verification.

---

## 🚀 Execution Instructions

### 1. Local Maven Execution
```bash
# Clone the repository
git clone https://github.com/vvce24cseaiml0162-glitch/junit-lab-project.git
cd junit-lab-project

# Compile and run the complete JUnit test suite
mvn clean test

# Generate JaCoCo HTML code coverage report
mvn jacoco:report
```
* Coverage Report Location: `target/site/jacoco/index.html`
* Surefire Reports Location: `target/surefire-reports/`

### 2. Docker Execution
```bash
# Build the Docker image
docker build -t junit-lab-project:latest .

# Run test suite inside container and mount reports to host
docker run --rm -v $(pwd)/reports:/app/reports junit-lab-project:latest

# Or using Docker Compose
docker-compose up --build
```

---

## 📊 Test Execution Summary

```text
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running com.lab.tests.BankAccountServiceTest
Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
Running com.lab.tests.MathUtilsTest
Tests run: 34, Failures: 0, Errors: 0, Skipped: 0
Running com.lab.tests.OrderServiceTest
Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
Running com.lab.tests.StudentServiceTest
Tests run: 31, Failures: 0, Errors: 0, Skipped: 0
Running com.lab.tests.ValidationUtilsTest
Tests run: 32, Failures: 0, Errors: 0, Skipped: 0
Running com.lab.tests.MainApplicationTest
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0

Results:
Tests run: 124, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## ⚙️ GitHub Actions CI/CD Pipeline

The GitHub Actions workflow automatically triggers on every `push` and `pull_request` to the `main` branch, as well as on manual `workflow_dispatch`:
1. **Ubuntu Runner Job**:
   - Checks out the codebase.
   - Configures JDK 17 with Maven caching.
   - Executes `mvn clean test jacoco:report`.
   - Uploads Surefire test reports and JaCoCo HTML coverage as artifacts.
2. **Docker Container Job**:
   - Builds the Docker image from `Dockerfile`.
   - Executes the test suite inside the isolated Linux container.
   - Mounts and exports container test reports as downloadable workflow artifacts.
