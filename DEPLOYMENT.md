# AlgoMentor Deployment Guide

## Production Deployment

### Prerequisites

1. **Java 17+** installed
2. **PostgreSQL 14+** database
3. **Node.js 16+** and npm
4. **Maven 3.6+**

### Environment Variables

Create a `.env` file or set these environment variables:

```bash
# Database Configuration
DATABASE_URL=jdbc:postgresql://localhost:5432/algomentor
DATABASE_USERNAME=your_db_username
DATABASE_PASSWORD=your_db_password

# JWT Configuration (CRITICAL: Generate a secure random key)
JWT_SECRET=your-super-secret-jwt-key-at-least-256-bits-long
JWT_EXPIRATION=18000000

# CORS Configuration
CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com

# Server Port
PORT=8080
```

### Database Setup

1. Create PostgreSQL database:
```sql
CREATE DATABASE algomentor;
CREATE USER algomentor_user WITH ENCRYPTED PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE algomentor TO algomentor_user;
```

2. Run migrations (if using Flyway - recommended for production):
```bash
mvn flyway:migrate
```

### Backend Deployment

1. Build the application:
```bash
mvn clean package -DskipTests
```

2. Run with production profile:
```bash
java -jar target/algomentor-1.0.0.jar --spring.profiles.active=prod
```

Or with environment variables:
```bash
export SPRING_PROFILES_ACTIVE=prod
export JWT_SECRET="your-secret-key"
export DATABASE_URL="jdbc:postgresql://localhost:5432/algomentor"
export DATABASE_USERNAME="algomentor_user"
export DATABASE_PASSWORD="your_password"

java -jar target/algomentor-1.0.0.jar
```

### Frontend Deployment

1. Update API URL in `frontend/.env.production`:
```
REACT_APP_API_URL=https://api.yourdomain.com/api
```

2. Build the frontend:
```bash
cd frontend
npm install
npm run build
```

3. Serve the build folder using:
   - **Nginx** (recommended)
   - **Apache**
   - **Serve** (npm package)
   - Any static file hosting service

### Nginx Configuration Example

```nginx
server {
    listen 80;
    server_name yourdomain.com;

    # Frontend
    location / {
        root /var/www/algomentor/frontend/build;
        try_files $uri /index.html;
    }

    # Backend API
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### Docker Deployment (Recommended)

Create `docker-compose.yml`:

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:14
    environment:
      POSTGRES_DB: algomentor
      POSTGRES_USER: algomentor_user
      POSTGRES_PASSWORD: ${DATABASE_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

  backend:
    build: .
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DATABASE_URL: jdbc:postgresql://postgres:5432/algomentor
      DATABASE_USERNAME: algomentor_user
      DATABASE_PASSWORD: ${DATABASE_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
    ports:
      - "8080:8080"
    depends_on:
      - postgres

  frontend:
    build: ./frontend
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  postgres_data:
```

### Security Checklist

- [ ] Change default JWT secret
- [ ] Use strong database passwords
- [ ] Enable HTTPS/SSL
- [ ] Disable H2 console in production
- [ ] Set up firewall rules
- [ ] Enable rate limiting
- [ ] Regular security updates
- [ ] Backup database regularly
- [ ] Monitor application logs
- [ ] Use environment variables for secrets

### Monitoring

1. **Application Logs**: Check `logs/algomentor.log`
2. **Health Check**: `GET /actuator/health` (if Spring Actuator is enabled)
3. **Database Monitoring**: Use PostgreSQL monitoring tools
4. **API Documentation**: Access Swagger UI at `/swagger-ui.html`

### Troubleshooting

**Issue**: Application won't start
- Check database connection
- Verify environment variables
- Check logs for errors

**Issue**: JWT authentication fails
- Verify JWT_SECRET is set correctly
- Check token expiration time
- Ensure clocks are synchronized

**Issue**: CORS errors
- Update CORS_ALLOWED_ORIGINS
- Check frontend API URL configuration

### Backup Strategy

1. **Database Backup**:
```bash
pg_dump -U algomentor_user algomentor > backup_$(date +%Y%m%d).sql
```

2. **Automated Backups**: Set up cron job
```bash
0 2 * * * pg_dump -U algomentor_user algomentor > /backups/algomentor_$(date +\%Y\%m\%d).sql
```

### Scaling Considerations

- Use load balancer for multiple backend instances
- Implement Redis for session management
- Use CDN for frontend assets
- Database connection pooling
- Implement caching strategy

### Support

For issues or questions, contact: support@algomentor.com
