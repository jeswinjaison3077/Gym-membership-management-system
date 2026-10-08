# Gym Membership Management System
### Java (Swing GUI) + MySQL (JDBC) — OOP Mini Project

A desktop GUI application to register members, assign trainers, manage
subscription plans, assign workout plans, and track payments — backed by a
real MySQL database and built with a clean layered architecture (GUI → Service
→ DAO → Database).

> **Update:** the frontend was changed from a console menu to a Swing desktop
> GUI to satisfy the requirement that the final output be an installable
> executable desktop application. Only the UI layer changed — models, DAOs,
> and the service layer are untouched.

---

## What It Does

- Register and manage members, with optional trainer assignment
- Add and manage trainers
- Subscribe members to plans (Basic / Premium / Platinum) and track status
- Assign workout plans to members
- Record and track payments, including outstanding dues
- View simple reports (total revenue, member/trainer counts)

All of the above is done through clickable tabs, forms, buttons, and tables —
no typing menu numbers into a terminal.

---

## Tech Stack

- **Frontend:** Java Swing (desktop GUI — JFrame, JTabbedPane, JTable, forms)
- **Backend:** Java + JDBC
- **Database:** MySQL
- **Architecture:** GUI (`ui/*Panel.java`) → Service (`GymService.java`) → DAO (`*DAO.java`) → MySQL

---

## Project Structure

```
gym-management-system/
├── sql/
│   └── schema_mysql.sql
├── src/gym/
│   ├── Main.java                  # GUI entry point
│   ├── ui/
│   │   ├── MainFrame.java         # main window, holds all tabs
│   │   ├── MemberPanel.java
│   │   ├── TrainerPanel.java
│   │   ├── SubscriptionPanel.java
│   │   ├── WorkoutPanel.java
│   │   ├── PaymentPanel.java
│   │   └── ReportsPanel.java
│   ├── model/
│   │   ├── Person.java  Member.java  Trainer.java
│   │   ├── Payable.java  Payment.java
│   │   └── Plan.java  Subscription.java  WorkoutPlan.java
│   ├── dao/
│   │   ├── DBConnection.java
│   │   ├── MemberDAO.java  TrainerDAO.java
│   │   ├── PlanDAO.java  SubscriptionDAO.java  PaymentDAO.java
│   │   └── WorkoutPlanDAO.java
│   ├── service/
│   │   └── GymService.java
│   └── util/
│       └── GymException.java
```

---

## Team & Work Division

Split so each person builds the GUI screen for the exact data/logic they
already own — you already know the fields and method names because you wrote
the layer underneath it.

| Member | Responsibility | Files Owned |
|---|---|---|
| **Jeswin** | Model Layer + Main Window | `Person.java`, `Member.java`, `Trainer.java`, `Payable.java`, `Payment.java`, `Plan.java`, `Subscription.java`, `WorkoutPlan.java`, `ui/MainFrame.java` |
| **Lihan** | People DAOs + their GUI screens | `DBConnection.java`, `MemberDAO.java`, `TrainerDAO.java`, `ui/MemberPanel.java`, `ui/TrainerPanel.java` |
| **Pilla** | Money DAOs + their GUI screens | `PlanDAO.java`, `SubscriptionDAO.java`, `PaymentDAO.java`, `ui/SubscriptionPanel.java`, `ui/PaymentPanel.java` |
| **Sreehari** | Workout DAO + Service Layer + their GUI screens | `WorkoutPlanDAO.java`, `GymService.java`, `GymException.java`, `ui/WorkoutPanel.java`, `ui/ReportsPanel.java` |
| **Pranav** | Database Schema + App Launcher + Packaging | `schema_mysql.sql`, `Main.java`, building the installable executable (see below) |

**Why this split:** `MainFrame` just assembles the six tabs, so it's a natural
extension of the model layer (Jeswin already understands the whole object
model). Each DAO owner writes the screen for the data they already know the
shape of — e.g. Lihan wrote `MemberDAO`, so `MemberPanel`'s form fields map
directly onto methods Lihan already wrote. Pranav's role shifts from "console
UI" to "schema + final packaging," since the GUI screens themselves are now
spread across the team.

### Build Order

1. **Jeswin** — models, merged first (nothing else compiles without them)
2. **Lihan & Pilla** — DAOs + their two GUI panels each, in parallel, once models are merged
3. **Sreehari** — service layer + workout DAO + Workout/Reports panels, once all DAOs exist
4. **Jeswin** — `MainFrame.java` wiring all six tabs together, once all panels exist
5. **Pranav** — final schema check + packaging into an installable app

---

## How to Run (development)

1. Create the database:
   ```
   mysql -u root -p < sql/schema_mysql.sql
   ```
2. Download the MySQL Connector/J jar into a `lib/` folder.
3. Set your MySQL password in `DBConnection.java`.
4. Compile:
   ```
   javac -d out $(find src -name "*.java")
   ```
5. Run:
   ```
   java -cp "out:lib/mysql-connector-j-8.x.x.jar" gym.Main
   ```

---

## Packaging as an Installable Executable (Pranav's task)

The demo requirement says the app must be **installed** on a PC, not just run
from a terminal with `java -cp ...`. The JDK includes a tool called
`jpackage` for exactly this — it bundles your compiled classes, the JDK
runtime, and an installer into one file, so the app runs like any other
desktop program.

1. First build a runnable JAR with the MySQL driver merged in (or shipped alongside it).
2. Then run:
   ```
   jpackage --input out/ --name GymManagementSystem --main-jar gym-management.jar ^
     --main-class gym.Main --type exe --win-menu --win-shortcut
   ```
   (use `--type exe` on Windows, `--type dmg` on macOS, `--type deb`/`--type rpm` on Linux)
3. This produces an actual installer — double-click it, it installs the app
   with a Start Menu / desktop shortcut like any normal software.

Practice this step **before** the demo day — `jpackage` needs some setup
(correct JDK version, MySQL driver bundled correctly) and it's easy to lose
time troubleshooting it live.

---

## OOP Concepts Demonstrated

| Concept | Where |
|---|---|
| Encapsulation | Private fields + getters/setters in every model class |
| Abstraction | `Person` (abstract class), `Payable` (interface) |
| Inheritance | `Member extends Person`, `Trainer extends Person` |
| Polymorphism | Overridden `displayDetails()` in `Member` and `Trainer` |
| Interfaces | `Payment implements Payable` |
| Custom Exceptions | `GymException` for business rule violations |
| Layered Design | GUI never contains SQL; DAOs never contain business rules |

---

## Why this project is strong for 60 marks

This project is structured to satisfy a typical KTU 3rd Semester OOP evaluation because it demonstrates both conceptual depth and practical implementation:

- Object-oriented modeling using classes, inheritance, abstraction, encapsulation, and interfaces
- Real database integration using MySQL and JDBC, not just static arrays or console-only logic
- Layered architecture: UI → service → DAO → database
- Proper exception handling with custom `GymException`
- Desktop GUI development in Java Swing for a realistic business application
- CRUD operations for members, trainers, subscriptions, workout plans, and payments
- Reporting features and summary calculations to show business logic beyond simple inserts
- Modular code organization, making it easier to explain and defend in viva and internal review

This project is not just a basic CRUD app — it shows proper software engineering practices expected in a higher-mark OOP assignment.

---

## File-by-file documentation

### Root files

- `README.md` — project overview, setup instructions, architecture summary, and assignment explanation.
- `sql/schema_mysql.sql` — MySQL schema definition, table creation, foreign key relationships, and sample seed data.

### `src/gym/Main.java`

- Application entry point.
- Starts the Swing desktop app by creating the main frame and making the window visible.
- Acts as the launcher for the whole system.

### `src/gym/model/` package

- `Person.java` — abstract base class for all people in the system, storing common attributes like name, phone, and email.
- `Member.java` — extends `Person` and adds member-specific fields such as age, gender, join date, and assigned trainer.
- `Trainer.java` — extends `Person` and stores specialization and experience details for trainers.
- `Payable.java` — interface used to define payment-related behavior.
- `Payment.java` — models a payment record, including amount, mode, date, and status.
- `Plan.java` — stores subscription plan details such as name, duration, and price.
- `Subscription.java` — represents a member's plan assignment with dates and status.
- `WorkoutPlan.java` — stores assigned workout information, including title, description, and weekly schedule.

### `src/gym/dao/` package

- `DBConnection.java` — central JDBC connection manager; handles the database URL, username, password, and connection creation.
- `MemberDAO.java` — contains SQL logic for member registration, update, delete, lookup, and search by name or ID.
- `TrainerDAO.java` — manages trainer CRUD operations in the database.
- `PlanDAO.java` — handles plan lookup and listing operations.
- `SubscriptionDAO.java` — performs subscription-related queries, including creation, cancellation, and expiring subscription checks.
- `PaymentDAO.java` — manages payment records, revenue totals, and due payment queries.
- `WorkoutPlanDAO.java` — inserts and fetches workout plans assigned to members.

### `src/gym/service/` package

- `GymService.java` — core business logic layer. It coordinates DAOs and enforces validation rules before database operations.
- This is the main service orchestrator used by all GUI panels.

### `src/gym/ui/` package

- `MainFrame.java` — holds all feature tabs in one desktop window and composes the overall application layout.
- `MemberPanel.java` — handles member registration, updating, deletion, search, and table display.
- `TrainerPanel.java` — handles trainer creation, listing, and deletion from the UI.
- `SubscriptionPanel.java` — lets users subscribe members to plans, view subscriptions, and cancel them.
- `WorkoutPanel.java` — handles workout plan assignment and lookup for members.
- `PaymentPanel.java` — records payments and shows member payment history.
- `ReportsPanel.java` — displays summary information such as total revenue and counts of members and trainers.

### `src/gym/util/` package

- `GymException.java` — custom checked/unchecked-style exception used for validation and business rule faults such as invalid member age or missing records.

---

## Conclusion

This project combines Java OOP principles, Swing UI design, JDBC database logic, and practical business features into one complete mini-project. With the proper explanation in viva and a clean README, it is fully suitable for a strong 60-mark KTU assignment.
