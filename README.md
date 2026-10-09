# Gym Membership Management System (OOP Lab Project)
**KTU S3 CSE Syllabus (CSL 203 / CST 205 - Object Oriented Programming in Java)**

---

## 👥 Presentation & File Division (5 Members)

| Team Member | Assigned Files | Core OOP Concepts / Viva Topics |
| :--- | :--- | :--- |
| **Jeswin** | • `src/gym/Main.java`<br>• `src/gym/ui/MainFrame.java`<br>• `src/gym/service/GymService.java` | • System Architecture (UI -> Service -> DAO -> Database)<br>• Transaction Control (`setAutoCommit(false)`, `commit()`, `rollback()`)<br>• LookAndFeel Theme & Swing Thread Management (`SwingUtilities.invokeLater`) |
| **Pranav** | • `src/gym/model/Person.java`<br>• `src/gym/model/Member.java`<br>• `src/gym/model/Trainer.java`<br>• `src/gym/ui/MemberPanel.java`<br>• `src/gym/ui/TrainerPanel.java` | • **Abstraction**: Abstract base class `Person` with abstract method `displayDetails()`<br>• **Inheritance**: `Member` & `Trainer` extending `Person` (`super` keyword)<br>• **Polymorphism**: Dynamic method overriding of `displayDetails()`<br>• **Encapsulation**: Private fields with public getters/setters |
| **Abhijith** | • `sql/schema_mysql.sql`<br>• `src/gym/dao/DBConnection.java`<br>• `src/gym/dao/GymDAO.java` | • **JDBC Database Connectivity**: `DriverManager`, `Connection`, `PreparedStatement`, `ResultSet`<br>• SQL Injection Protection using Parameterized Queries (`?`)<br>• **Collections Framework**: Mapping ResultSets into `List<Member>` & `ArrayList<Trainer>`<br>• Relational Schema & Foreign Key constraints |
| **Lihan** | • `src/gym/model/Payable.java`<br>• `src/gym/model/Payment.java`<br>• `src/gym/ui/PaymentPanel.java`<br>• `src/gym/util/GymException.java` | • **Interface Abstraction**: Pure contract interface `Payable`<br>• **Interface Polymorphism**: Class `Payment` implementing `Payable`<br>• **Exception Handling**: Custom Checked Exception `GymException`, `try-catch` blocks<br>• Payment calculation & receipt generation |
| **Sreehari** | • `src/gym/model/Plan.java`<br>• `src/gym/model/Subscription.java`<br>• `src/gym/model/WorkoutPlan.java`<br>• `src/gym/ui/SubscriptionPanel.java`<br>• `src/gym/ui/WorkoutPanel.java`<br>• `src/gym/ui/ReportsPanel.java` | • Association & Relationships (Member to Plan, Trainer to Workout Plan)<br>• Date Calculations using `java.time.LocalDate`<br>• Swing Event Handling (`ActionListener`) & Summary Analytics |

---

## 🔄 Execution & File Data Flow

```
[1. MySQL Database Setup]  --->  [2. Application Launch]  --->  [3. Main Window GUI]
 (sql/schema_mysql.sql)          (src/gym/Main.java)           (src/gym/ui/MainFrame.java)
       Done by Abhijith                Done by Jeswin                Done by Jeswin
                                              |
                                              v
[6. Data Models Instantiation] <--- [5. Business Logic Service] <--- [4. Database Connection]
(Person, Member, Trainer,       (src/gym/service/GymService.java)  (src/gym/dao/DBConnection.java)
 Payment, Plan, Subscription)           Done by Jeswin                Done by Abhijith
  Done by Pranav, Lihan, Sreehari             |
                                              v
[8. Swing UI Tab Panels Interaction] <---> [7. SQL CRUD Execution]
 (Member, Trainer, Subscription,         (src/gym/dao/GymDAO.java)
  Workout, Payment, Reports Panels)            Done by Abhijith
Done by Pranav, Lihan, Sreehari
```

### Detailed Sequential Step-by-Step Flow:

1. **Database Schema Setup (`sql/schema_mysql.sql`) — Done by Abhijith**
   - Creates the `gym_management` database, relational tables (`members`, `trainers`, `plans`, `subscriptions`, `payments`, `workout_plans`), foreign key constraints, and default seed data.

2. **Application Startup (`src/gym/Main.java`) — Done by Jeswin**
   - Entry point `main()`. Configures Nimbus LookAndFeel custom colors/fonts and initializes GUI on Swing's Event Dispatch Thread (`SwingUtilities.invokeLater`).

3. **Main Frame Window Construction (`src/gym/ui/MainFrame.java`) — Done by Jeswin**
   - Builds the main application window (`JFrame`) and creates tabbed navigation hosting UI panels for Members, Trainers, Subscriptions, Workouts, Payments, and Reports.

4. **Database Connection Handshake (`src/gym/dao/DBConnection.java`) — Done by Abhijith**
   - Dynamically loads `com.mysql.cj.jdbc.Driver` and hands out singleton JDBC `Connection` instances using `DriverManager.getConnection()`.

5. **Service Layer Orchestration (`src/gym/service/GymService.java`) — Done by Jeswin**
   - Intermediary layer between UI and DAO. Applies validation rules (e.g. minimum member age $\ge 12$), handles database transactions (`commit()` / `rollback()`), and delegates SQL queries to `GymDAO`.

6. **Domain Data Models Construction**
   - **`Person.java` (Abstract Base), `Member.java`, `Trainer.java` — Done by Pranav**: Encapsulates person attributes, parent-child inheritance (`super`), and dynamic method overriding (`displayDetails()`).
   - **`Payable.java` (Interface), `Payment.java`, `GymException.java` — Done by Lihan**: Defines payment abstraction, custom exception handling, and fee calculations.
   - **`Plan.java`, `Subscription.java`, `WorkoutPlan.java` — Done by Sreehari**: Encapsulates membership plan options, member subscriptions, and assigned workout schedules.

7. **Data Access & SQL Query Execution (`src/gym/dao/GymDAO.java`) — Done by Abhijith**
   - Executes parameterized SQL `PreparedStatement` queries to insert, update, search, and delete records, returning typed Collections (`List<Member>`, `List<Trainer>`).

8. **User Interface Interaction & Tab Operations**
   - **`MemberPanel.java` & `TrainerPanel.java` — Done by Pranav**: Handles member registration, editing, search, deletion, and trainer management.
   - **`PaymentPanel.java` — Done by Lihan**: Records payments, views transaction history, and queries due fees.
   - **`SubscriptionPanel.java`, `WorkoutPanel.java`, `ReportsPanel.java` — Done by Sreehari**: Subscribes members to plans, assigns workout routines, and displays total revenue analytics.

---

## 🛠️ Project Setup & Database Configuration

### 1. Database Setup (MySQL)
Run the schema script in MySQL Workbench or CLI:
```sql
SOURCE sql/schema_mysql.sql;
```

### 2. Database Connection Credentials
Edit `src/gym/dao/DBConnection.java` if your local MySQL configuration differs:
```java
private static final String URL      = "jdbc:mysql://localhost:3306/gym_management";
private static final String USER     = "root";
private static final String PASSWORD = "Hello";
```

### 3. Compilation & Execution
Compile and run the project with MySQL Connector in classpath:
```bash
javac -cp "lib/*:." -d out src/gym/*.java src/gym/*/*.java
java -cp "lib/*:out" gym.Main
```

---

## 💡 Key OOP Pillars Covered for KTU Lab Viva

1. **Inheritance**: `Person` $\rightarrow$ `Member`, `Trainer`
2. **Polymorphism**:
   - Method Overriding (`displayDetails()`)
   - Interface Polymorphism (`Payable` implemented by `Payment`)
3. **Abstraction**: Abstract Class `Person`, Interface `Payable`
4. **Encapsulation**: Private members with getters and setters
5. **Exception Handling**: Custom Exception `GymException`, JDBC `SQLException`
6. **Collections Framework**: `List`, `ArrayList`
7. **Database Connectivity**: JDBC (`PreparedStatement`, `ResultSet`, `Transaction Management`)
8. **GUI (Swing)**: `JFrame`, `JTabbedPane`, `JPanel`, `JTable`, `DefaultTableModel`
