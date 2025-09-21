# Festival Management System Backend

## Academic Project Overview

This project was developed as part of the **Software Engineering course (321-4002)**. It focuses on building a **robust backend system** for managing music festivals, handling the entire lifecycle—from festival creation and performance submissions to final lineup announcements.

---
##Overview
This backend provides a comprehensive API for user, festival organizers, staff and artists. It supports all major operations required to run a music festival, including user authentication, role management, scheduling, budgeting, and many more.

## Key Features

### Festival Management

* Full festival lifecycle with state transitions
* Budget tracking and venue layout planning
* Vendor management and staff assignment
* Performance scheduling and lineup organization 

### Performance Management

* Artist/Band members performance submissions and approvals
* Technical requirements and merchandise management
* Band member coordination and review system

### User System

* Role-based access control (`ADMIN`, `ORGANIZER`, `STAFF`, `ARTIST`)
* Secure authentication with UUID tokens
* Festival-specific role assignments

---

## Tech Stack

* **Java 17**
* **Spring Boot 3.5.4**
* **MySQL Database**
* **Maven**
* **JUnit 5**

---

## Getting Started

### Prerequisites

* Java 17
* Maven
* MySQL Database

### Setup
#### 1. Clone the repository
```bash
git clone https://github.com/stelios1361/Festival-Management-Backend
```
#### 2. Navigate to the backend folder
```bash
cd Festival-Management-Backend/festivalBackend
```
#### 3. Configure your MySQL database in src/main/resources/application.properties (must crate a database named festivaldb)

#### 4. Build the project using Maven
```bash
mvn clean install
```
#### 5. Run the application
```bash
mvn spring-boot:run
```
---
### Reading javadoc for the project
#### 1. Navigate to the backend folder
```bash
cd Festival-Management-Backend/festivalBackend
```
#### 2. Generate javadoc (Ignore warnings)
```bash
mvn javadoc:javadoc
```

#### 3. Open the docs
```bash
start target\reports\apidocs\index.html
```
---

## Project Structure
```bash
src/
├── main/
│   ├── java/com/festivalmanager/
│   │   ├── controller/     # REST API endpoints
│   │   ├── dto/           # Data Transfer Objects
│   │   ├── model/         # Entity classes
│   │   ├── repository/    # Data access layer
│   │   ├── service/       # Business logic
│   │   └── security/      # Auth & security
│   └── resources/
└── test/
    └── java/              # Test classes
```
---

Configuration
* All application settings (DB, ports, etc.) are managed in application.properties.
* Uses Spring Data JPA for ORM and repository management.
* Security is handled via Spring Security and custom token-based authentication.

License
This project is with GPL-3.0 license.
