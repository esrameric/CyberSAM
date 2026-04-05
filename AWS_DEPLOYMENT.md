# AWS S3 Deployment Configuration for CyberSAM Frontend

## Overview

This document describes how to deploy the CyberSAM frontend to AWS S3 as a static website with optional CloudFront CDN.

## Prerequisites

### AWS Requirements
- AWS Account with S3 access
- AWS CLI v2 configured locally (`aws configure`)
- S3 bucket created or permissions to create one

### Local Requirements
- Node.js 20+ and npm
- PowerShell 5.1+ (for Windows) or Bash (for Linux/Mac)
- Git (for version control)

## S3 Bucket Setup

### 1. Create S3 Bucket
```bash
AWS_REGION=us-east-1
S3_BUCKET=cybersam-frontend

aws s3api create-bucket \
    --bucket $S3_BUCKET \
    --region $AWS_REGION \
    --create-bucket-configuration LocationConstraint=$AWS_REGION
```

### 2. Enable Static Website Hosting
```bash
aws s3 website s3://$S3_BUCKET \
    --index-document index.html \
    --error-document index.html
```

### 3. Configure CORS (if needed for API access)
```bash
cat > cors.json << 'EOF'
{
    "CORSRules": [{
        "AllowedHeaders": ["*"],
        "AllowedMethods": ["GET", "PUT", "POST"],
        "AllowedOrigins": ["*"],
        "ExposeHeaders": ["ETag"],
        "MaxAgeSeconds": 3000
    }]
}
EOF

aws s3api put-bucket-cors \
    --bucket $S3_BUCKET \
    --cors-configuration file://cors.json
```

### 4. Set Bucket Policy (for public read access)
```bash
cat > bucket-policy.json << 'EOF'
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "PublicReadGetObject",
            "Effect": "Allow",
            "Principal": "*",
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::cybersam-frontend/*"
        }
    ]
}
EOF

aws s3api put-bucket-policy \
    --bucket $S3_BUCKET \
    --policy file://bucket-policy.json
```

**Note:** If using CloudFront, use OAI (Origin Access Identity) instead of public access.

## Deployment

### Build Locally
```bash
cd frontend
npm install
npm run build:s3
```

Output: `dist/` directory with optimized assets

### Deploy with Script (Recommended)

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

**Environment Variables:**
```bash
export AWS_REGION=us-east-1
export S3_BUCKET=cybersam-frontend
export CLOUDFRONT_DISTRIBUTION_ID=E123456EXAMPLE  # Optional
```

### Manual Deployment

```bash
# Upload all files
aws s3 sync dist/ s3://cybersam-frontend/ \
    --region us-east-1 \
    --delete

# Set cache headers for versioned assets
aws s3 sync dist/ s3://cybersam-frontend/ \
    --region us-east-1 \
    --exclude "*.html" \
    --exclude "*.json" \
    --cache-control "public, max-age=31536000, immutable"

# Set no-cache for HTML
aws s3 cp dist/index.html s3://cybersam-frontend/index.html \
    --region us-east-1 \
    --cache-control "public, max-age=0, must-revalidate" \
    --content-type "text/html"
```

## CloudFront CDN (Optional)

### Benefits
- Global content delivery
- HTTPS/TLS termination
- DDoS protection
- AWS WAF integration

### Create Distribution

```bash
aws cloudfront create-distribution \
    --origin-domain-name cybersam-frontend.s3.us-east-1.amazonaws.com \
    --default-root-object index.html
```

Or use AWS Console for advanced configuration.

### Update After Deployment
```bash
DISTRIBUTION_ID=E123456EXAMPLE

aws cloudfront create-invalidation \
    --distribution-id $DISTRIBUTION_ID \
    --paths "/*"
```

## URL Formats

### Direct S3 Static Website
```
http://cybersam-frontend.s3-website-us-east-1.amazonaws.com
```

### CloudFront Distribution
```
https://d1234567890abc.cloudfront.net
```

### Custom Domain (Route 53)
```
https://cybersam-frontend.yourdomain.com
```

## Caching Strategy

| File Type | Cache Duration | Headers |
|-----------|-----------------|---------|
| HTML | No cache | max-age=0, must-revalidate |
| JS/CSS | 1 year | max-age=31536000, immutable |
| Images | 1 year | max-age=31536000, immutable |
| JSON | No cache | max-age=0, must-revalidate |

## API Configuration

Update `.env` for production:
```
VITE_API_URL=https://api.cybersam.defense/api/v1
```

Or configure dynamically in `src/services/axiosService.ts`:
```typescript
const API_BASE_URL = window.location.hostname === 'localhost' 
  ? 'http://localhost:8080/api/v1'
  : 'https://api.cybersam.defense/api/v1';
```

## Security Considerations

### 1. S3 Block Public Access (with CloudFront)
```bash
aws s3api put-bucket-public-access-block \
    --bucket cybersam-frontend \
    --public-access-block-configuration \
    "BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true"
```

### 2. CloudFront Origin Access Identity
```bash
# Use OAI to protect S3 from direct access
aws cloudfront create-cloud-front-origin-access-identity \
    --cloud-front-origin-access-identity-config \
    CallerReference=cybersam-oai,Comment="CyberSAM OAI"
```

### 3. HTTPS Enforcement
- Enable SSL/TLS in CloudFront
- Use only HTTPS viewers protocol policy
- HTTP → HTTPS redirect

### 4. AWS WAF Integration
- Attach WAF ACL to CloudFront
- Block malicious patterns
- Rate limiting

## Monitoring

### CloudWatch Metrics
```bash
aws cloudwatch get-metric-statistics \
    --namespace AWS/CloudFront \
    --metric-name Requests \
    --start-time 2024-01-01T00:00:00Z \
    --end-time 2024-01-02T00:00:00Z \
    --period 3600 \
    --statistics Sum
```

### S3 Access Logs
```bash
aws s3api put-bucket-logging \
    --bucket cybersam-frontend \
    --bucket-logging-status \
    'LoggingEnabled={TargetBucket=cybersam-logs,TargetPrefix=frontend/}'
```

## Troubleshooting

### 403 Forbidden Error
```bash
# Check bucket policy
aws s3api get-bucket-policy --bucket cybersam-frontend

# Check object ACL
aws s3api get-object-acl --bucket cybersam-frontend --key index.html
```

### Cache Not Updating
```bash
# Invalidate CloudFront cache
aws cloudfront create-invalidation \
    --distribution-id E123456EXAMPLE \
    --paths "/*"

# Wait 10-15 minutes for global propagation
```

### 404 on Refresh
- Verify error document is set to `index.html`
- Check CloudFront error responses
- Ensure index.html is uploaded correctly

## Cost Optimization

- **S3**: ~$0.023 per GB storage + $0.0007 per 10k requests
- **CloudFront**: ~$0.085 per GB for first 10TB
- **Data Transfer**: Free (S3 to CloudFront)

Estimated monthly cost: $5-50 depending on traffic

## Rollback

```bash
# Delete all objects
aws s3 rm s3://cybersam-frontend --recursive

# Or restore from backup
aws s3 sync s3://cybersam-frontend-backup/ s3://cybersam-frontend/ --delete
```

## Automation with CI/CD

### GitHub Actions Example
```yaml
name: Deploy to S3

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
        with:
          node-version: '20'
      - run: cd frontend && npm install && npm run build:s3
      - uses: aws-actions/configure-aws-credentials@v2
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: us-east-1
      - run: |
          cd frontend
          ./scripts/deploy-s3.sh
```

---

**Last Updated**: April 2026  
**Version**: 1.0.0
