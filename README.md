# Serenity Mental Health Therapy Center Management System

A digital, desktop-based enterprise management solution developed for **The Serenity Mental Health Therapy Center** in Sri Lanka. Built using Java, JavaFX, and Hibernate ORM technology , this system transitions the center's manual, paper-based patient registration and treatment tracking workflows  into a secure, transactional, and highly scalable digital system.

---

## 📖 Case Study Overview

The Serenity Mental Health Therapy Center serves approximately 3,000 patients annually , offering evidence-based clinical treatments alongside holistic wellness services. As manual tracking became a bottleneck due to growing enrollment , this system was engineered to automate operational workflows, covering interview completions, registration tracking, dynamic scheduling, and upfront payment processing.

---

## 🏗️ Architectural Pattern & Core Technologies

The application strictly enforces a **Layered Architecture** decoupled through professional design patterns:

* **Presentation Layer:** Developed using **JavaFX** and customized **JFoenix** Material Design components for user interfaces.
* 
**Business Logic Layer (BO):** Implements the **Façade Pattern** to handle transactional enterprise validation rules.


* 
**Data Access Layer (DAO):** Organized via the **Factory Pattern** , running purely on **Hibernate ORM** with zero raw JDBC dependencies.


* 
**Configuration:** Entirely dynamic schema generation and connection routing utilizing **Property File Configuration** (`hibernate.properties`).



---

## 🚀 Key System Features

1. Secure Role-Based Access Control (RBAC) 

* 
**Admin Privilege:** Full management capabilities over clinical practitioners (Therapists) and specialized Therapy Programs. Accesses advanced performance metrics and therapy usage analytics.


* 
**Receptionist Privilege:** Manages standard daily workflows: patient profiles, scheduling, appointment lifecycle, and transaction processing.


* 
**Authentication Security:** Implements **BCrypt Hashing** to ensure passwords are encrypted one-way before crossing boundaries into data layers. Supports visibility toggles on screens and secure in-session credential updates.



2. Therapist & Program Association (Admin Only) 

* Comprehensive CRUD services for Therapist profiles and schedules.


* Modular creation of localized, structured therapy program configurations:



| Program ID | Program Name | Duration | Fee (LKR) |
| --- | --- | --- | --- |
| **MT1001** | Cognitive Behavioral Therapy | 12 weeks | 80,000.00 |
| **MT1002** | Mindfulness-Based Stress Reduction | 8 weeks | 50,000.00 |
| **MT1003** | Dialectical Behavior Therapy | 16 weeks | 100,000.00 |
| **MT1004** | Group Therapy Sessions | 6 months | 120,000.00 |
| **MT1005** | Family Counseling | 3 months | 40,000.00 |

3. Patient Enrollment & Session Scheduling 

* Multi-program patient registration workflows (e.g., matching parallel tracks in CBT and Stress Management).


* Advanced scheduling logic preventing time-slot conflicts for active clinicians.


* Persistent history tracking maintaining medical notes and historic records securely.



4. Billing, Invoicing, & Financial Tracking 

* Processes transactional upfront state requirements upon intake tracking.


* Generates printable, customer-facing structural PDF invoices natively utilizing embedded JasperReports engines.


* Status monitoring across transaction lifecycles (`PENDING`, `UPFRONT`, `COMPLETED`).

---

## 🛠️ Data Modeling & Hibernate Mapping Specifications

The persistence framework maps Java entities to corresponding relational constructs through strict schema relationships:

```
   [Therapist] 1 <-------* [Therapy Session] *-------> 1 [Patient]
        1                       * 1
        |                       |                         |
        * |                         *
[Therapy Program] <-------------+------------------ * [Payment]

```

* 
**One-to-Many Relationships:** * `Therapists` $\rightarrow$ `Therapy Sessions` 


* 
`Patients` $\rightarrow$ `Therapy Sessions` / `Appointments` 




* 
**Cascade Configurations:** Active domain cascades (`CascadeType.ALL`, `orphanRemoval=true`) automatically clear orphaned relational states or sync child details across transaction boundaries.


* 
**Second-Level Cache Integration:** Utilizes high-performance Second-Level Cache regions (configured via **Ehcache 3 / JCache Providers**) alongside query caches to reduce structural load on persistent read loops.



---

## 🔬 Complex Query Architecture & Validation Examples

### Advanced HQL Join Query Example

To dynamically determine patients who have expanded their recovery tracks to encompass **every single program available** at the facility:

```hql
FROM Patient p 
WHERE NOT EXISTS (
    FROM TherapyProgram tp 
    WHERE tp NOT IN (
        SELECT enrollment.program 
        FROM p.enrollments enrollment
    )
)

```

Comprehensive Input Validation Suite 

All UI input states undergo strict asynchronous, client-side validation using optimized Regular Expressions (RegEx) to ensure structural integrity:

* 
**Email Verification:** `^[A-Za-z0-9+_.-]+@(.+)$` 


* 
**Phone Formats:** International/Localized validation constraints.



Standardized Exception Hierarchy 

Custom domain runtime exceptions isolate functional application layer failures cleanly:

* 
`RegistrationException`: Handles input omissions, constraint issues, or duplicate data bounds.


* 
`AuthenticationException`: Handles missing users, invalid passwords, or temporary locks.


* 
`ConflictException`: Halts overlapping schedules or booking anomalies.


* 
`PaymentException`: Fails broken balances or transaction rejections safely.



---

## ⚙️ Environment Setup & Installation

### Prerequisites

* **Java Development Kit (JDK):** Version 21 or higher
* **Build Automation Tool:** Apache Maven
* 
**Database Engine:** MySQL 8.x Server (Note: Explicit manipulation via graphical clients like MySQL Workbench during the execution sequence is restricted by implementation requirements).



### 1. Database Provisioning

Ensure your background MySQL service instance is online. The system will automatically create and update the target structural schema layouts upon first launch through internal configurations (`createDatabaseIfNotExist=true`).

### 2. Configuration Settings

Navigate to `src/main/resources/hibernate.properties` and provide your specific database infrastructure parameters:

```properties
hibernate.connection.driver_class=com.mysql.cj.jdbc.Driver
hibernate.connection.url=jdbc:mysql://localhost:3306/SerenityTherapyCenter?createDatabaseIfNotExist=true
hibernate.connection.username=YOUR_MYSQL_USERNAME
hibernate.connection.password=YOUR_MYSQL_PASSWORD
hibernate.dialect=org.hibernate.dialect.MySQLDialect
hibernate.hbm2ddl.auto=update

```

### 3. Compilation and Execution

Execute the application using the following terminal commands:

```bash
# Clean previous builds and package application dependencies
mvn clean package

# Run the primary JavaFX system launcher
mvn javafx:run

```

---

Developed as an academic course evaluation assignment exploring advanced Object-Relational Mapping concepts. All Rights Reserved.
