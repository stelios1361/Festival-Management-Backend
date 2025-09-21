# Festival Management System Backend

## Academic Project Overview

This project was developed as part of the **Software Engineering course (321-4002)**. It focuses on building a **robust backend system** for managing music festivals, handling the entire lifecycle—from festival creation and performance submissions to final lineup announcements.

---

## Key Features

### Festival Management

* Complete festival lifecycle management with state transitions
* Budget tracking and venue layout planning
* Vendor management system
* Performance scheduling and lineup organization

### Performance Management

* Artist/Band performance submissions
* Technical requirements handling
* Merchandise management
* Band member coordination
* Review and approval system

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

1. Clone the repository:

   ```bash
   git clone https://github.com/stelios1361/Festival-Management-Backend
   ```
2. Configure your MySQL database in `application.properties`.
3. Build the project using Maven:

   ```bash
   mvn clean install
   ```
4. Run the application:

   ```bash
   mvn spring-boot:run
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

