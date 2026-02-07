# AlgoMentor

A production-ready full-stack application for tracking coding problems solved by students. The system allows teachers to manage student profiles and track their problem-solving progress across different platforms (HackerRank, LeetCode, etc.) with automated data synchronization.

## 🚀 Features

### Core Functionality
- **Student Management**: Create, read, update, and delete student profiles
- **Problem Tracking**: Track problems solved across multiple platforms
- **Automated Sync**: Fetch and sync data from HackerRank and LeetCode profiles
- **Analytics Dashboard**: Visualize progress with interactive charts
- **Progress Tracking**: Comprehensive breakdown by difficulty and platform
- **Role-Based Access**: Separate interfaces for teachers and students

### Security & Authentication
- JWT-based authentication with secure token management
- Role-based authorization (Teacher/Student)
- Password strength validation
- BCrypt password encryption
- CORS configuration for production
- Environment-based secret management

### Production Features
- PostgreSQL database support
- Database migrations with Flyway
- Comprehensive error handling
- API documentation with Swagger/OpenAPI
- Pagination and search functionality
- Docker containerization
- CI/CD pipeline with GitHub Actions
- Comprehensive test coverage

## Tech Stack

- **Backend**: Spring Boot 3.2.0, Java 17
- **Database**: H2 (development), PostgreSQL (production)
- **Frontend**: React 18
- **Build Tool**: Maven
- **Security**: Spring Security, JWT
- **API Docs**: Swagger/OpenAPI
- **Testing**: JUnit 5, Mockito
- **CI/CD**: GitHub Actions
- **Containerization**: Docker

## Prerequisites

Before running the application, ensure you have:

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

4. **PostgreSQL 14+** (for production)
   ```bash
   psql --version
   ```

## Quick Start (Development)

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/algomentor.git
cd algomentor
```

### 2. Set Up Environment Variables
```bash
cp .env.example .env
# Edit .env with your configuration
```

### 3. Start the Backend
```bash
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### 4. Start the Frontend
```bash
cd frontend
npm install
npm start
```

The frontend will start on `http://localhost:3000`

### 5. Access the Application
- **Frontend**: http://localhost:3000
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **H2 Console** (dev only): http://localhost:8080/h2-console

### Default Credentials
- **Teacher Account**: 
  - Email: `teacher@algomentor.com`
  - Password: `teacher123`

## Production Deployment

See [DEPLOYMENT.md](DEPLOYMENT.md) for comprehensive production deployment instructions including:
- PostgreSQL setup
- Environment configuration
- Docker deployment
- Nginx configuration
- Security checklist
- Monitoring setup

## API Documentation

Once the application is running, access the interactive API documentation at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## Project Structure

```
AlgoMentor/
├── src/
│   ├── main/
│   │   ├── java/com/algomentor/
│   │   │   ├── config/          # Security, CORS, OpenAPI config
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── exception/       # Global exception handling
│   │   │   ├── model/           # Entity classes
│   │   │   ├── repository/      # JPA repositories
│   │   │   ├── security/        # JWT authentication
│   │   │   ├── service/         # Business logic
│   │   │   └── util/            # Utility classes
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-prod.properties
│   │       └── db/migration/    # Flyway migrations
│   └── test/                    # Unit and integration tests
├── frontend/
│   ├── src/
│   │   ├── components/          # React components
│   │   ├── App.js
│   │   └── App.css
│   ├── Dockerfile
│   └── nginx.conf
├── .github/workflows/           # CI/CD pipelines
├── Dockerfile                   # Backend Docker config
├── docker-compose.yml
├── DEPLOYMENT.md
└── pom.xml
```

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
