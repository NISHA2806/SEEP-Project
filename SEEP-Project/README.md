# Smart Emergency Evacuation Planner (SEEP)

CS5304 – Java Programming Project
A Java Swing desktop application that recommends safe evacuation routes
during emergencies (floods, fires, earthquakes, etc.) based on road
conditions, hazard levels, and shelter availability, using **Dijkstra's
Algorithm** for route optimization.

Matches the project scope from the submitted slides: Core Java, Java
Swing GUI, JDBC + MySQL, OOP concepts (class/object, encapsulation,
inheritance, polymorphism, abstraction), collections, exception
handling, file handling, and graph algorithms.

---

## 1. Prerequisites

- **JDK 17+** — [https://adoptium.net](https://adoptium.net)
- **MySQL Server 8.x** — [https://dev.mysql.com/downloads/mysql/](https://dev.mysql.com/downloads/mysql/)
- **VS Code** with these extensions:
  - *Extension Pack for Java* (Microsoft)
  - *Maven for Java* (usually bundled with the pack above)
- (Optional) **MySQL Workbench** to run the schema script visually.

---

## 2. Project Setup

### Step 1 — Create the database

Open a terminal / MySQL client and run:

```bash
mysql -u root -p < database/seep_db.sql
```

This creates the `seep_db` database, its tables (`users`, `shelters`,
`roads`, `alerts`), and inserts sample data — including a default
admin account.

### Step 2 — Configure database credentials

Edit `config.properties` in the project root and set your MySQL
username/password:

```properties
db.url=jdbc:mysql://localhost:3306/seep_db?useSSL=false&serverTimezone=UTC
db.user=root
db.password=YOUR_MYSQL_PASSWORD
```

### Step 3 — Open in VS Code

1. Open the `SEEP-Project` folder in VS Code (`File → Open Folder...`).
2. VS Code will detect the `pom.xml` and automatically download the
   MySQL JDBC driver dependency (`mysql-connector-j`) the first time
   you build — this requires an internet connection.
3. Wait for the Java extension to finish indexing the project
   (bottom-right status bar).

### Step 4 — Run the application

**Option A — Using VS Code (recommended)**
Open `src/main/java/com/seep/Main.java` and click **Run** above the
`main` method, or press `F5` (uses the provided `.vscode/launch.json`).

**Option B — Using Maven from the terminal**
```bash
mvn clean package
java -jar target/seep.jar
```
(The Maven Shade plugin bundles the MySQL driver into the jar, so this
single file is fully runnable on its own.)

---

## 3. Default Logins

| Role  | Username  | Password  |
|-------|-----------|-----------|
| Admin | admin     | admin123  |
| User  | citizen1  | pass123   |
| User  | citizen2  | pass123   |

New citizens can also self-register from the login screen.

---

## 4. Project Structure

```
SEEP-Project/
├── pom.xml                          Maven build file (MySQL driver dependency)
├── config.properties                Database connection settings
├── database/seep_db.sql             Schema + seed data
├── logs/app.log                     Auto-generated runtime log (file handling)
├── src/main/java/com/seep/
│   ├── Main.java                    Application entry point
│   ├── model/                       User, Admin, Shelter, Road, Route
│   ├── dao/                         UserDAO, ShelterDAO, RoadDAO, AlertDAO (JDBC)
│   ├── algorithm/DijkstraSolver.java   Graph + Dijkstra's shortest-path
│   ├── exception/SEEPException.java Custom checked exception
│   ├── util/DBConnection.java       JDBC connection helper
│   ├── util/AppLogger.java          File-based logger
│   └── gui/                         Swing screens (Login, Register, Dashboards, panels)
└── .vscode/                         VS Code run configuration
```

## 5. Key Features (mapped to the project slides)

- **User & Admin Login** — `LoginFrame`, `UserDAO.login()`
- **Safe Evacuation Route Recommendation** — `DijkstraSolver` + `RouteFinderPanel`
- **Nearest Shelter Identification** — `RouteFinderPanel.findRouteToNearestShelter()`
- **Shelter Capacity Management** — `ShelterManagementPanel`
- **Road Status & Hazard Updates** — `RoadManagementPanel`
- **Emergency Alert Notification System** — `AlertPanel`, `AlertDAO`
- **OOP concepts** — `Admin extends User` (inheritance), overridden
  `getRole()` (polymorphism), private fields with getters/setters
  throughout (encapsulation)
- **Collections Framework** — `ArrayList`, `HashMap`, `PriorityQueue`,
  `TreeSet` used across DAOs and the Dijkstra solver
- **Exception Handling** — custom `SEEPException`, try-catch-finally /
  try-with-resources throughout DAOs and GUI actions
- **File Handling** — `AppLogger` appends timestamped events to
  `logs/app.log`

## 6. Troubleshooting

- **"Could not connect to the MySQL database"** — make sure MySQL is
  running and the database was created via `database/seep_db.sql`,
  and double-check `config.properties`.
- **Maven can't resolve `mysql-connector-j`** — ensure the sandbox/VM
  running VS Code has internet access to Maven Central the first time
  you build.
- **`config.properties` not found at runtime** — run the app from the
  project root (VS Code's default working directory) so the relative
  path resolves; the built jar also carries a copy in its classpath.
