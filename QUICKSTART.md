# CyberSAM Quick Start Guide

## 🚀 30-Second Setup

```bash
# Clone and navigate
cd blm3522

# Start all services (requires Docker & Docker Compose)
docker-compose up -d

# Wait 30 seconds for services to start...
```

### Access Points
- **Frontend**: http://localhost
- **Backend API**: http://localhost:8080
- **API Docs**: http://localhost:8080/swagger-ui.html
- **Database Manager**: http://localhost:5050 (admin@cybersam.local / admin123)

---

## 📋 Prerequisites Check

### For Docker Deployment
- [x] Docker Desktop
- [x] Docker Compose
- [x] 4GB RAM available

### For Local Development (Optional)
- [x] Java 17+
- [x] Node.js 20+
- [x] Maven 3.9+
- [x] PostgreSQL 16 (or Docker)

---

## 🏗️ Project Structure

```
CyberSAM/
├── backend/                 # Java Spring Boot API
│   ├── src/main/java/      # Application source
│   ├── pom.xml             # Maven config
│   ├── Dockerfile          # Multi-stage build
│   └── README.md           # Backend docs
│
├── frontend/               # React + Vite
│   ├── src/               # Components & pages
│   ├── package.json       # npm config
│   ├── Dockerfile         # Nginx production
│   ├── scripts/           # Deploy scripts
│   └── README.md          # Frontend docs
│
├── docker-compose.yml     # Local environment
├── init-db.sql           # Database initialization
├── README.md             # Main documentation
├── AWS_DEPLOYMENT.md     # S3/CloudFront setup
├── DEPLOYMENT.md         # Full deployment guide
├── CONTRIBUTING.md       # Contribution guidelines
└── QUICKSTART.md         # This file
```

---

## 💻 Development Commands

### Using Docker Compose (Recommended)

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f backend    # Backend logs
docker-compose logs -f frontend   # Frontend logs
docker-compose logs -f postgres   # Database logs

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

### Backend Local Development

```bash
# Navigate to backend
cd backend

# Build with Maven
mvn clean package

# Run application
mvn spring-boot:run

# Run tests
mvn test

# Access at http://localhost:8080
```

### Frontend Local Development

```bash
# Navigate to frontend
cd frontend

# Install dependencies
npm install

# Start dev server
npm run dev

# Build for production
npm run build

# Access at http://localhost:5173
```

---

## 📊 First Steps

### 1. Check Backend Health
```bash
curl http://localhost:8080/swagger-ui.html
# Should show Swagger UI documentation
```

### 2. Create a Test Asset

```bash
curl -X POST http://localhost:8080/api/v1/software-assets \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Software",
    "version": "1.0.0",
    "vendor": "Test Vendor",
    "expiryDate": "2025-12-31",
    "isVulnerable": false
  }'
```

### 3. View in Dashboard

Open http://localhost in your browser
- Should see dashboard with your asset
- Statistics automatically calculated

---

## 🔧 Common Tasks

### Database Management

```bash
# Connect to PostgreSQL
psql -h localhost -U cybersam_user -d cybersam_db

# Using Docker
docker exec -it cybersam-postgres psql -U cybersam_user -d cybersam_db

# View tables
\dt

# Exit
\q
```

### View Application Logs

```bash
# All services
docker-compose logs

# Specific service
docker-compose logs backend
docker-compose logs frontend
docker-compose logs postgres

# Follow logs (real-time)
docker-compose logs -f backend
```

### Rebuild After Changes

```bash
# Rebuild containers
docker-compose up -d --build

# Or specific service
docker-compose up -d --build backend
```

### Reset Database

```bash
# WARNING: Deletes all data
docker-compose down -v
docker-compose up -d
```

---

## 🐛 Troubleshooting

### "Port 80 already in use"
```bash
# Either stop other services or change port in docker-compose.yml
# Change: ports: - "80:3000" to "8000:3000"
```

### "Cannot connect to Docker daemon"
- Ensure Docker Desktop is running
- On Linux: may need `sudo` or add user to docker group

### "Backend not responding"
```bash
# Check container status
docker-compose ps

# View backend logs
docker-compose logs -f backend

# Restart backend
docker-compose restart backend
```

### "Database connection error"
```bash
# Wait for postgres to start (takes ~10 seconds)
docker-compose logs postgres

# After seeing "database system is ready to accept connections":
# Restart backend
docker-compose restart backend
```

### "Frontend showing API errors"
- Check backend is running: `docker-compose ps`
- Verify API is accessible: `curl http://localhost:8080/swagger-ui.html`
- Check .env file in frontend folder for correct API URL

---

## 🚢 Deployment

### Quick AWS S3 Deployment

```bash
# Configure AWS credentials
aws configure

# Deploy frontend
cd frontend
npm install && npm run build:s3
./scripts/deploy-s3.ps1  # PowerShell
# or
./scripts/deploy-s3.sh   # Bash
```

For full deployment instructions, see [DEPLOYMENT.md](DEPLOYMENT.md)

---

## 📚 Documentation

| Document | Purpose |
|----------|---------|
| [README.md](README.md) | Main project overview |
| [backend/README.md](backend/README.md) | Backend-specific docs |
| [frontend/README.md](frontend/README.md) | Frontend-specific docs |
| [AWS_DEPLOYMENT.md](AWS_DEPLOYMENT.md) | S3 & CloudFront setup |
| [DEPLOYMENT.md](DEPLOYMENT.md) | Complete deployment guide |
| [CONTRIBUTING.md](CONTRIBUTING.md) | How to contribute |

---

## 🎯 API Endpoints Quick Reference

### Core CRUD
```
POST   /api/v1/software-assets              Create
GET    /api/v1/software-assets              List all
GET    /api/v1/software-assets/{id}         Get by ID
PUT    /api/v1/software-assets/{id}         Update
DELETE /api/v1/software-assets/{id}         Delete
```

### Specialized Queries
```
GET    /api/v1/software-assets/expired/licenses
GET    /api/v1/software-assets/expiring/within?days=30
GET    /api/v1/software-assets/vulnerable
GET    /api/v1/software-assets/critical
GET    /api/v1/software-assets/vendor/{vendor}
GET    /api/v1/software-assets/search?q=pattern
```

### Dashboard
```
GET    /api/v1/software-assets/stats/dashboard
```

Full API documentation: http://localhost:8080/swagger-ui.html

---

## 💡 Tips & Tricks

### View Container Outputs
```bash
# Real-time container logs
docker-compose logs -f --tail=50 backend

# Timestamps
docker-compose logs -t backend
```

### Access Container Shell
```bash
# Backend shell
docker-compose exec backend sh

# Database shell
docker-compose exec postgres psql -U cybersam_user -d cybersam_db
```

### Performance Check
```bash
# Resource usage
docker stats

# Container health
docker-compose ps
```

### Clean Everything & Start Fresh
```bash
docker-compose down -v          # Remove containers and volumes
docker system prune -a --volumes # Deep clean
docker-compose up -d            # Fresh start
```

---

## 🔐 Security Notes

- Default PostgreSQL password: `CyberSAM@Secure123` (change in production!)
- PgAdmin credentials: admin@cybersam.local / admin123
- No external ports exposed for database (correct!)
- CORS enabled for frontend access

---

## 📞 Need Help?

1. Check the troubleshooting section above
2. Review detailed documentation in [DEPLOYMENT.md](DEPLOYMENT.md)
3. Check container logs: `docker-compose logs service-name`
4. Review component code: Each files has JSDoc/inline comments

---

## ✅ Success Checklist

After starting docker-compose, verify:

- [ ] Frontend loads at http://localhost
- [ ] Dashboard displays (even if empty)
- [ ] API documentation available at http://localhost:8080/swagger-ui.html
- [ ] Can create an asset via API or frontend
- [ ] Asset appears in dashboard
- [ ] Database manager accessible at http://localhost:5050
- [ ] No errors in `docker-compose logs`

---

**Ready to go!** 🎉

Update your backend environment variables, customize the frontend theme, or deploy to AWS – everything is ready for production use.

For next steps, see:
- Backend setup: [backend/README.md](backend/README.md)
- Frontend setup: [frontend/README.md](frontend/README.md)
- AWS Deployment: [AWS_DEPLOYMENT.md](AWS_DEPLOYMENT.md)
