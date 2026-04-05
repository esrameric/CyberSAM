# CyberSAM: Secure Software Asset Management

**Defense Industry Standard - Enterprise Software Asset Management Platform**

![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green.svg)
![React](https://img.shields.io/badge/React-18-blue.svg)
![Node](https://img.shields.io/badge/Node-20-green.svg)

## 📋 Project Overview

CyberSAM is a comprehensive Software Asset Management (SAM) platform designed for defense industry standards compliance. It provides:

- **Real-time Asset Tracking**: Monitor all software components with comprehensive metadata
- **Vulnerability Management**: CVE flagging and critical asset identification
- **License Compliance**: Automatic expiry date monitoring and alerting
- **Defense-Grade Security**: Encrypted data, secure authentication, compliance reporting
- **Enterprise Dashboard**: Professional dark-mode UI with threat visualization

## 🏗️ Architecture

```
CyberSAM/
├── backend/                 # Java Spring Boot API
│   ├── src/main/java/       # Application source code
│   ├── pom.xml              # Maven dependencies
│   └── Dockerfile           # Multi-stage build
├── frontend/                # React + Vite + Tailwind CSS
│   ├── src/                 # React components and pages
│   ├── package.json         # Node dependencies
│   ├── Dockerfile           # Nginx production container
│   └── scripts/             # AWS S3 deployment scripts
├── docker-compose.yml       # Local development environment
└── README.md                # This file
```

## 🚀 Quick Start

### Prerequisites
- Docker & Docker Compose
- Java 17+ (for local backend development)
- Node.js 20+ (for local frontend development)
- PostgreSQL 16+ (or use docker-compose)
- Maven 3.9+

### Local Development

1. **Clone and Setup**
```bash
cd blm3522
```

2. **Start with Docker Compose**
```bash
docker-compose up -d
```

3. **Access the Application**
- Frontend: http://localhost (development) or http://localhost:5173 (Vite dev server)
- Backend API: http://localhost:8080
- API Documentation: http://localhost:8080/swagger-ui.html

4. **Stop Services**
```bash
docker-compose down
```

## 📦 Backend Setup (Spring Boot)

### Project Structure
```
backend/
├── src/main/java/com/cybersam/
│   ├── entity/              # JPA entities (SoftwareAsset)
│   ├── repository/          # Data access layer with custom queries
│   ├── service/             # Business logic
│   ├── controller/          # REST API endpoints
│   ├── dto/                 # Data transfer objects
│   └── config/              # Spring configuration (OpenAPI)
├── src/main/resources/      # Configuration files
└── pom.xml                  # Maven configuration
```

### Key Features
- **Entity: SoftwareAsset** - Complete software component model
  - ID, Name, Version, Vendor
  - ExpiryDate (license tracking)
  - IsVulnerable (CVE flag)
  - CreatedAt, UpdatedAt timestamps
  - CVE notes and description

- **Specialized Repository Methods**
  - `findExpiredLicenses()` - Licenses past expiry date
  - `findLicensesExpiringWithin(int days)` - Upcoming expirations
  - `findVulnerableAssets()` - CVE-flagged components
  - `findCriticalAssets()` - Expired OR vulnerable items
  - Count methods for dashboard statistics

- **CRUD Controller Endpoints**
  - POST /api/v1/software-assets - Create
  - GET /api/v1/software-assets - List all
  - GET /api/v1/software-assets/{id} - Get by ID
  - PUT /api/v1/software-assets/{id} - Update
  - DELETE /api/v1/software-assets/{id} - Delete
  - Specialized endpoints for queries (expired, vulnerable, critical)

- **Swagger Integration**
  - Complete OpenAPI 3.0 specification
  - Interactive UI at /swagger-ui.html
  - Automated API documentation

### Build & Run Locally
```bash
cd backend

# Build with Maven
mvn clean package

# Run application
java -jar target/cybersam-backend-1.0.0.jar

# Or use Spring Boot Maven plugin
mvn spring-boot:run
```

### Database Configuration
Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/cybersam_db
spring.datasource.username=cybersam_user
spring.datasource.password=CyberSAM@Secure123
```

For AWS RDS:
```properties
spring.datasource.url=jdbc:postgresql://your-rds-endpoint:5432/cybersam_db
spring.datasource.username=admin
spring.datasource.password=your-secure-password
```

## 🎨 Frontend Setup (React + Vite)

### Project Structure
```
frontend/
├── src/
│   ├── components/
│   │   ├── DashboardCard.tsx    # KPI metric cards
│   │   └── AssetTable.tsx       # Sortable assets table
│   ├── pages/
│   │   └── Dashboard.tsx        # Main dashboard page
│   ├── services/
│   │   ├── axiosService.ts      # HTTP client configuration
│   │   └── assetService.ts      # API service methods
│   ├── types/
│   │   └── index.ts             # TypeScript interfaces
│   ├── App.tsx                  # Root component
│   ├── main.tsx                 # React entry point
│   └── index.css                # Tailwind styles
├── index.html                   # HTML entry point (for S3)
├── vite.config.ts               # Vite build configuration
├── tailwind.config.ts           # Tailwind customization
├── tsconfig.json                # TypeScript configuration
└── package.json                 # Dependencies
```

### Key Components

**DashboardCard** - Professional metric display
- 4 variations: Total Assets, Vulnerable, Expired, Health
- Icon integration with threat severity coloring
- Trend indicators and descriptions

**AssetTable** - Interactive asset listing
- Expandable rows for detailed information
- Red highlighting for critical/vulnerable assets
- CVE notes and timestamps
- Edit/Delete actions
- Responsive design

**Dashboard Page**
- Real-time statistics aggregation
- Critical assets alerting
- Asset filtering and search
- 60-second auto-refresh

### Dark Mode Defense Theme
```
Colors:
- defense-dark: #0a0e27 (main background)
- defense-darker: #050812 (header/footer)
- defense-accent: #0f7ba0 (interactive elements)
- defense-danger: #d32f2f (critical/vulnerable)
- defense-warning: #f57c00 (expired/warning)
- defense-success: #388e3c (safe/healthy)
```

### Build & Run Locally
```bash
cd frontend

# Install dependencies
npm install

# Development server
npm run dev
# Access at http://localhost:5173

# Production build
npm run build

# Preview production build
npm run preview
```

## ☁️ AWS S3 Deployment

### Prerequisites
- AWS Account with S3 access
- AWS CLI configured (aws configure)
- S3 bucket created for frontend
- Optional: CloudFront distribution for CDN

### Deployment Steps

**Using PowerShell (Windows):**
```powershell
cd frontend
.\scripts\deploy-s3.ps1 `
    -S3Bucket "cybersam-frontend" `
    -AWSRegion "us-east-1" `
    -CloudFrontDistributionId "E123456EXAMPLE"
```

**Using Bash (Linux/Mac):**
```bash
cd frontend
chmod +x scripts/deploy-s3.sh
./scripts/deploy-s3.sh \
    S3_BUCKET=cybersam-frontend \
    AWS_REGION=us-east-1 \
    CLOUDFRONT_DISTRIBUTION_ID=E123456EXAMPLE
```

### Configuration

**S3 Bucket Setup:**
```bash
# Enable static website hosting
aws s3 website s3://cybersam-frontend \
    --index-document index.html \
    --error-document index.html

# Block public access if using CloudFront
aws s3api put-bucket-public-access-block \
    --bucket cybersam-frontend \
    --public-access-block-configuration \
    "BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true"
```

**CloudFront Distribution (optional but recommended):**
```bash
# Create distribution pointing to S3 bucket
# Enable HTTP/2, compression, and caching
# Add WAF rules for defense industry compliance
```

### Cache Strategy
- **Versioned assets** (.js, .css, images): 1 year cache
- **HTML & JSON**: No cache (max-age=0, must-revalidate)
- **index.html**: Always fetched fresh from S3

### Index.html Requirements
- Single-Page Application routing
- Located in root of S3 bucket
- Defines viewport and meta tags
- No build output directory prefix needed

## 🔒 Security Features

### Backend
- PostgreSQL with encrypted connections
- JPA entity validation
- CORS configuration
- SQL injection prevention (prepared statements)
- Swagger/OpenAPI security definitions

### Frontend
- React security best practices
- XSS prevention (output encoding)
- CSRF token handling
- Secure JWT token storage
- CSP headers via Nginx

### Docker
- Non-root user execution
- Multi-stage builds (minimal image size)
- Health checks on containers
- Network isolation with docker-compose

## 📊 API Endpoints

### Core CRUD Operations
```
POST   /api/v1/software-assets              # Create asset
GET    /api/v1/software-assets              # List all assets
GET    /api/v1/software-assets/{id}         # Get specific asset
PUT    /api/v1/software-assets/{id}         # Update asset
DELETE /api/v1/software-assets/{id}         # Delete asset
```

### Specialized Queries
```
GET    /api/v1/software-assets/expired/licenses           # Expired licenses
GET    /api/v1/software-assets/expiring/within?days=30    # Expiring soon
GET    /api/v1/software-assets/vulnerable                 # CVE-flagged assets
GET    /api/v1/software-assets/critical                   # Critical (expired OR vulnerable)
GET    /api/v1/software-assets/vendor/{vendor}            # By vendor
GET    /api/v1/software-assets/search?q=pattern           # Search by name
```

### Dashboard
```
GET    /api/v1/software-assets/stats/dashboard            # Aggregated statistics
```

## 🧪 Testing

### Backend Unit Tests
```bash
cd backend
mvn test
```

### Frontend Unit Tests
```bash
cd frontend
npm test
```

### Integration Tests
```bash
# With docker-compose running
curl http://localhost:8080/swagger-ui.html
curl http://localhost/
```

## 📝 Environment Variables

### Backend (.env or system variables)
```
SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/db
SPRING_DATASOURCE_USERNAME=user
SPRING_DATASOURCE_PASSWORD=password
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

### Frontend (.env)
```
VITE_API_URL=http://localhost:8080/api/v1
VITE_APP_NAME=CyberSAM
VITE_APP_VERSION=1.0.0
```

## 🐳 Docker Containers

### Backend Container
- **Image**: Multi-stage build from Maven + JRE alpine
- **Port**: 8080
- **Health Check**: /actuator/health endpoint
- **User**: Non-root (cybersam:cybersam)

### Frontend Container
- **Image**: Node alpine (build) + Nginx alpine (runtime)
- **Port**: 3000 (or 80)
- **Health Check**: HTTP GET /
- **Compression**: Gzip enabled

### Database Container
- **Image**: PostgreSQL 16 alpine
- **Port**: 5432
- **Volume**: postgres_data (persistent)
- **Health Check**: pg_isready

## 📚 Documentation

- **API Docs**: http://localhost:8080/swagger-ui.html
- **API Schema**: http://localhost:8080/v3/api-docs
- **Frontend Components**: JSDoc comments in .tsx files
- **Backend Classes**: JavaDoc comments

## 🔧 Troubleshooting

### Backend Issues
```bash
# Check PostgreSQL connection
psql -h localhost -U cybersam_user -d cybersam_db

# View logs
docker-compose logs backend

# Rebuild after dependency changes
mvn clean install
```

### Frontend Issues
```bash
# Clear node modules cache
rm -rf node_modules package-lock.json
npm install

# Clear build artifacts
rm -rf dist

# Check Vite config
npm run build
```

## 🛠️ Development Workflow

1. **Feature Branch**
   ```bash
   git checkout -b feature/asset-filtering
   ```

2. **Local Development**
   ```bash
   # Terminal 1: Backend
   cd backend && mvn spring-boot:run
   
   # Terminal 2: Frontend
   cd frontend && npm run dev
   ```

3. **Commit & Push**
   ```bash
   git commit -m "Add asset filtering feature"
   git push origin feature/asset-filtering
   ```

4. **Production Build**
   ```bash
   # Backend
   cd backend && mvn clean package
   docker build -t cybersam:backend .
   
   # Frontend (S3)
   cd frontend && npm run build:s3
   ./scripts/deploy-s3.sh
   ```

## 📈 Performance Optimization

- **Frontend**: Code splitting, lazy loading, asset versioning
- **Backend**: Connection pooling, query optimization, caching
- **Database**: Indexed columns (id, vendor, expiry_date)
- **Deployment**: CloudFront CDN, Gzip compression, minification

## 🤝 Contributing

1. Fork the repository
2. Create feature branch
3. Make changes with clear commits
4. Submit pull request with description
5. Ensure tests pass and code quality checks succeed

## 📄 License

Defense Industry License - Classified Use Only

## 📞 Support

- **Documentation**: See this README
- **API Support**: Swagger UI at /swagger-ui.html
- **Issues**: Check existing documentation first

---

**Version**: 1.0.0  
**Last Updated**: April 2026  
**Defense Industry Compliance**: NIST, FedRAMP Ready
