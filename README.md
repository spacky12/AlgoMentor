# AlgoMentor

A full-stack Java application for tracking coding problems solved by students. The system allows you to manage student profiles and track their problem-solving progress across different platforms (HackerRank, LeetCode, etc.).

## Tech Stack

- **Backend**: Spring Boot 3.2.0, Java 17
- **Database**: H2 (in-memory database)
- **Frontend**: React 18
- **Build Tool**: Maven

## Prerequisites

Before running the application, make sure you have the following installed:

1. **Java 17 or higher**
   ```bash
   java -version
   ```

2. **Maven 3.6+**
   ```bash
   mvn -version
   ```

3. **Node.js 16+ and npm**
   ```bash
   node -version
   npm -version
   ```

## Project Structure

```
AlgoMentor/
├── src/
│   └── main/
│       ├── java/com/algomentor/
│       │   ├── model/          # Entity classes (Student, Problem)
│       │   ├── repository/     # JPA repositories
│       │   ├── service/         # Business logic
│       │   ├── controller/     # REST controllers
│       │   └── dto/            # Data Transfer Objects
│       └── resources/
│           └── application.properties
├── frontend/
│   ├── src/
│   │   ├── App.js
│   │   ├── App.css
│   │   └── index.js
│   └── package.json
└── pom.xml
```

## Setup and Running

### Step 1: Start the Backend (Spring Boot)

1. Navigate to the project root directory:
   ```bash
   cd /Volumes/Private/PROJECTS/AlgoMentor
   ```

2. Build and run the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```

   Or if you prefer to build first and then run:
   ```bash
   mvn clean install
   java -jar target/algomentor-1.0.0.jar
   ```

3. The backend will start on `http://localhost:8080`

4. You can access the H2 database console at `http://localhost:8080/h2-console`
   - JDBC URL: `jdbc:h2:mem:algomentor`
   - Username: `sa`
   - Password: (leave empty)

### Step 2: Start the Frontend (React)

1. Open a new terminal window and navigate to the frontend directory:
   ```bash
   cd /Volumes/Private/PROJECTS/AlgoMentor/frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the React development server:
   ```bash
   npm start
   ```

4. The frontend will start on `http://localhost:3000` and automatically open in your browser.

## API Endpoints

### Students
- `GET /api/students` - Get all students
- `GET /api/students/{id}` - Get student by ID
- `POST /api/students` - Create a new student
- `PUT /api/students/{id}` - Update a student
- `DELETE /api/students/{id}` - Delete a student

### Problems
- `GET /api/students/{studentId}/problems` - Get all problems for a student
- `POST /api/students/{studentId}/problems` - Create a new problem for a student
- `GET /api/problems/{id}` - Get problem by ID
- `PUT /api/problems/{id}` - Update a problem
- `DELETE /api/problems/{id}` - Delete a problem

### HackerRank Integration
- `GET /api/hackerrank/fetch/{username}` - Fetch HackerRank data for a username
- `POST /api/hackerrank/students/{studentId}/fetch` - Fetch and update HackerRank data for a student
- `POST /api/hackerrank/students/fetch-all` - Fetch HackerRank data for all students

### Statistics
- `GET /api/stats` - Get overall statistics (total students, total problems, problem count per student)

## Features

1. **Student Management**
   - Create, read, update, and delete student profiles
   - Each student has a name, email, and hacker profile identifier
   - View problem count for each student

2. **Problem Tracking**
   - Add problems solved by students
   - Track platform (HackerRank, LeetCode, etc.)
   - Track difficulty level (Easy, Medium, Hard)
   - Track status (solved, in_progress, failed)
   - Edit and delete problems

3. **HackerRank Integration**
   - Automatically fetch problem data from HackerRank profiles
   - Sync problems solved by students from their HackerRank accounts
   - View HackerRank statistics (total solved, difficulty breakdown)
   - **Note**: Uses web scraping which may violate HackerRank's Terms of Service. Use only for educational purposes or with proper authorization.

4. **Statistics Dashboard**
   - View total number of students
   - View total number of problems
   - See problem count for each student

4. **HackerRank Integration**
   - Automatically fetch problem-solving data from HackerRank profiles
   - Update student problem counts from their HackerRank profiles
   - View solved problems, badges, and rank information
   - One-click fetch button for individual students or all students

## Usage

1. **Add a Student**: Click the "+ Add Student" button and fill in the student's information. Make sure to enter their HackerRank username in the "Hacker Profile" field.

2. **View Student Problems**: Click on a student card to view their problems. The problem count is displayed on each student card.

3. **Add a Problem**: Select a student, then click "+ Add Problem" to add a new problem they've solved.

4. **Sync from HackerRank**: 
   - Click the "📥 Fetch HR" button on a student card, or
   - Select a student and click "📥 Fetch from HackerRank" in the problems panel
   - This will fetch their HackerRank profile data and automatically add solved problems to their list

5. **Edit/Delete**: Use the Edit and Delete buttons on student or problem cards to modify or remove entries.

### HackerRank Integration Notes

- The HackerRank integration uses web scraping to fetch profile data
- Make sure students have their correct HackerRank username in their profile
- The sync will only add problems that don't already exist for that student
- Web scraping may be rate-limited or blocked by HackerRank - if you encounter issues, try again later
- For production use, consider using HackerRank's official APIs if you have enterprise access

5. **Fetch from HackerRank**: Click the "📥 Fetch HR" button on a student card or "📥 Fetch from HackerRank" in the problems panel to automatically fetch and update problem data from the student's HackerRank profile. This will add new problems they've solved and update their statistics.

## Development

### Backend Development
- The backend uses Spring Boot with JPA for database operations
- H2 database is configured for easy development (data is stored in memory)
- To use a persistent database (like PostgreSQL or MySQL), update `application.properties`

### Frontend Development
- React app with Axios for API calls
- The frontend is configured to proxy API requests to `http://localhost:8080`

## Troubleshooting

1. **Port already in use**: If port 8080 is already in use, change it in `src/main/resources/application.properties`:
   ```
   server.port=8081
   ```

2. **Frontend can't connect to backend**: Make sure the backend is running on port 8080, or update the `API_BASE_URL` in `frontend/src/App.js`

3. **Maven build fails**: Make sure you have Java 17+ installed and Maven is properly configured.

## License

This project is for educational purposes.
