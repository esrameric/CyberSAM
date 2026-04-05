# CyberSAM Frontend - React + Vite + Tailwind CSS

Professional defense industry dashboard with dark-mode theme and real-time asset monitoring.

## Quick Start

### Prerequisites
- Node.js 20+
- npm 10+ or yarn

### Development

```bash
# Install dependencies
npm install

# Start dev server (http://localhost:5173)
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview
```

### Environment Setup

Copy `.env.example` to `.env` and update API endpoint:
```bash
VITE_API_URL=http://localhost:8080/api/v1
```

## Project Structure

```
src/
├── components/
│   ├── DashboardCard.tsx    # KPI metric cards (4 variations)
│   └── AssetTable.tsx       # Sortable asset list with expandable rows
├── pages/
│   └── Dashboard.tsx        # Main dashboard container
├── services/
│   ├── axiosService.ts      # HTTP client with interceptors
│   └── assetService.ts      # API integration layer
├── types/
│   └── index.ts             # TypeScript interfaces
├── App.tsx                  # Root component
├── main.tsx                 # React entry point
└── index.css                # Tailwind + custom utilities

public/
└── vite.svg                 # Favicon (optional)

index.html                   # HTML entry (important for S3)
```

## Key Features

### DashboardCard Component
```tsx
<DashboardCard
  title="Vulnerable Assets"
  value={stats.vulnerableCount}
  icon="vulnerable"      // vulnerable | expired | critical | health
  description="CVE-flagged components"
/>
```
- 4 threat levels with custom colorization
- Trend indicators
- Professional defense industry styling
- Shadow/glow effects

### AssetTable Component
- Expandable rows for full details
- **Red highlighting** for critical assets
- Edit/Delete action buttons
- Responsive data display
- CVE notes display
- Timestamp information

### Dashboard Page
- Real-time statistics aggregation
- Critical asset alerts (red warning box)
- Dual-view (critical first, then all assets)
- Auto-refresh every 60 seconds
- Error handling with user feedback

### Axios Service
- Centralized HTTP configuration
- Request/Response interceptors
- Token management support
- Error handling
- Timeout configuration (30s)

### TypeScript Types
```typescript
interface SoftwareAsset {
  id: string;
  name: string;
  version: string;
  vendor: string;
  expiryDate: string;
  isVulnerable: boolean;
  cveNotes?: string;
  description?: string;
}

interface DashboardStats {
  totalAssets: number;
  vulnerableCount: number;
  expiredLicensesCount: number;
  criticalAssetsCount: number;
  healthPercentage: number;
}
```

## Dark Mode Defense Theme

Custom Tailwind color palette:
```
defense-dark:      #0a0e27  (main background)
defense-darker:    #050812  (header/footer)
defense-accent:    #0f7ba0  (interactive elements)
defense-danger:    #d32f2f  (critical/vulnerable)
defense-warning:   #f57c00  (expired/warning)
defense-success:   #388e3c  (safe/healthy)
```

Custom utilities:
```css
.defense-glow        /* accent shadow */
.defense-danger-glow /* danger shadow */
.defense-card        /* standard card styling */
.defense-badge       /* status badge */
```

## Build & Deployment

### Local Build
```bash
# Production build
npm run build

# Output in ./dist/
# - index.html (entry point)
# - js/ (versioned JavaScript)
# - css/ (versioned stylesheets)
# - images/ (optimized images)
```

### AWS S3 Deployment

**PowerShell (Windows):**
```powershell
cd frontend
.\scripts\deploy-s3.ps1 `
    -S3Bucket "cybersam-frontend" `
    -AWSRegion "us-east-1" `
    -CloudFrontDistributionId "E123456EXAMPLE"  # Optional
```

**Bash (Linux/Mac):**
```bash
cd frontend
chmod +x scripts/deploy-s3.sh
./scripts/deploy-s3.sh
```

Environment variables:
```bash
AWS_REGION=us-east-1
S3_BUCKET=cybersam-frontend
CLOUDFRONT_DISTRIBUTION_ID=E123456EXAMPLE  # Optional
```

### Cache Strategy
- **Assets (.js, .css, images)**: 1 year (immutable, versioned)
- **HTML & JSON**: No cache (must-revalidate)
- **index.html**: Always fresh for SPA routing

### S3 Configuration
```bash
# Enable static website hosting
aws s3 website s3://cybersam-frontend \
    --index-document index.html \
    --error-document index.html

# Block public access (if using CloudFront)
aws s3api put-bucket-public-access-block \
    --bucket cybersam-frontend \
    --public-access-block-configuration \
    BlockPublicAcls=true,...

# Add CloudFront OAI for private access
```

## Docker Deployment

### Multi-Stage Build
```dockerfile
# Stage 1: Build with Node
# Stage 2: Runtime with Nginx + security headers
```

Build image:
```bash
docker build -t cybersam-frontend:1.0.0 .
docker run -p 3000:3000 cybersam-frontend:1.0.0
```

Docker features:
- Gzip compression
- SPA routing via `try_files`
- Security headers (X-Frame-Options, CSP, etc.)
- API proxy to backend
- Health checks

## API Integration

### Available Methods
```typescript
// CRUD
assetService.getAllAssets()
assetService.getAssetById(id)
assetService.createAsset(data)
assetService.updateAsset(id, data)
assetService.deleteAsset(id)

// Queries
assetService.getExpiredLicenses()
assetService.getLicensesExpiringWithin(30)
assetService.getVulnerableAssets()
assetService.getCriticalAssets()
assetService.getAssetsByVendor(vendor)
assetService.searchAssets(query)

// Dashboard
assetService.getDashboardStats()
```

### Error Handling
- HTTP interceptor catches 401 (redirect to login)
- Error messages shown in UI
- Console logging for debugging
- Request timeout after 30s

## Styling

### Tailwind CSS
- Dark mode enabled by default
- Custom color palette (defense industry)
- Responsive breakpoints
- Animation utilities

### Custom Utilities
```css
@layer utilities {
  .defense-glow { /* Accent colored shadow */ }
  .defense-danger-glow { /* Red threat shadow */ }
  .defense-card { /* Standard card with borders */ }
  .defense-badge { /* Status indicator */ }
}
```

## Performance Features

- **Build Optimization**:
  - Tree-shaking (unused code removal)
  - Minification (Terser)
  - Code splitting by route
  - Asset versioning (cache busting)

- **Runtime Optimization**:
  - Component lazy loading
  - Image optimization
  - Gzip compression (Nginx)
  - Browser caching

## TypeScript

- Strict mode enabled
- No implicit any
- No unused locals/parameters
- JSX as react-jsx

## Development Workflow

```bash
# Start development server
npm run dev

# Type checking
npm run type-check

# Linting
npm run lint

# Build & test
npm run build
npm run preview

# Deploy to S3
cd frontend && npm run build:s3 && ./scripts/deploy-s3.sh
```

## Dependencies

### Runtime
- **react** (18.2): UI library
- **react-dom** (18.2): DOM rendering
- **axios** (1.6): HTTP client
- **lucide-react** (0.294): Icon library

### Dev
- **vite** (5.0): Build tool
- **typescript** (5.3): Type system
- **tailwindcss** (3.4): Styling
- **postcss** (8.4): CSS processing
- **eslint** (8.55): Code linting

## Features

### Dashboard
- Real-time statistics
- Critical asset highlight
- Asset count display
- System health percentage

### Asset Management
- Complete asset list
- Vulnerability flagging
- License expiry tracking
- Status badges (Critical/Safe)

### UX
- Dark mode theme
- Professional styling
- Expandable details
- Responsive layout
- Loading indicators
- Error messages

## Browser Support

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

## Troubleshooting

### API Connection Issues
```bash
# Check backend is running
curl http://localhost:8080/swagger-ui.html

# Update API URL in .env
VITE_API_URL=http://your-backend-url/api/v1

# Clear browser cache and restart dev server
```

### Build Issues
```bash
# Clear cache
rm -rf node_modules package-lock.json dist

# Reinstall
npm install

# Rebuild
npm run build
```

### Styling Issues
```bash
# Rebuild Tailwind
npm run build

# Check tailwind.config.ts includes all src files
# Verify dark mode class is on <html>
```

## Contributing

1. Create feature branch
2. Make changes with clear commits
3. Run `npm run type-check && npm run lint` before submitting
4. Submit PR with description

---

**Version**: 1.0.0  
**Node**: 20+  
**React**: 18.2  
**Vite**: 5.0  
**Status**: Production Ready
