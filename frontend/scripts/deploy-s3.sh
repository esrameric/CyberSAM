#!/bin/bash
# AWS S3 Deployment Script for CyberSAM Frontend
# This script builds and deploys the frontend to AWS S3

set -e

# Configuration
AWS_REGION="${AWS_REGION:-us-east-1}"
S3_BUCKET="${S3_BUCKET:-cybersam-frontend}"
CLOUDFRONT_DISTRIBUTION_ID="${CLOUDFRONT_DISTRIBUTION_ID:-}"
BUILD_DIR="dist"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}=== CyberSAM Frontend S3 Deployment ===${NC}"

# Check prerequisites
if ! command -v aws &> /dev/null; then
    echo -e "${RED}AWS CLI not found. Please install AWS CLI.${NC}"
    exit 1
fi

if ! command -v npm &> /dev/null; then
    echo -e "${RED}npm not found. Please install Node.js.${NC}"
    exit 1
fi

# Build application
echo -e "${YELLOW}Building frontend application...${NC}"
npm run build:s3

if [ ! -d "$BUILD_DIR" ]; then
    echo -e "${RED}Build directory not found: $BUILD_DIR${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Build completed${NC}"

# Sync to S3
echo -e "${YELLOW}Syncing built files to S3 bucket: $S3_BUCKET${NC}"

# Upload all files with proper cache headers
aws s3 sync "$BUILD_DIR" "s3://$S3_BUCKET/" \
    --region "$AWS_REGION" \
    --delete \
    --exclude "*.html" \
    --exclude "*.json" \
    --cache-control "public, max-age=31536000, immutable"

# Upload HTML and JSON with no-cache
aws s3 sync "$BUILD_DIR" "s3://$S3_BUCKET/" \
    --region "$AWS_REGION" \
    --include "*.html" \
    --include "*.json" \
    --cache-control "public, max-age=0, must-revalidate" \
    --content-type "application/json"

aws s3 cp "$BUILD_DIR/index.html" "s3://$S3_BUCKET/index.html" \
    --region "$AWS_REGION" \
    --cache-control "public, max-age=0, must-revalidate" \
    --content-type "text/html"

echo -e "${GREEN}✓ S3 sync completed${NC}"

# Invalidate CloudFront cache if distribution ID provided
if [ ! -z "$CLOUDFRONT_DISTRIBUTION_ID" ]; then
    echo -e "${YELLOW}Invalidating CloudFront distribution: $CLOUDFRONT_DISTRIBUTION_ID${NC}"
    aws cloudfront create-invalidation \
        --distribution-id "$CLOUDFRONT_DISTRIBUTION_ID" \
        --paths "/*" \
        --region "$AWS_REGION"
    echo -e "${GREEN}✓ CloudFront invalidation triggered${NC}"
fi

echo -e "${GREEN}=== Deployment completed successfully ===${NC}"
echo -e "${YELLOW}S3 Bucket URL: http://$S3_BUCKET.s3-website-$AWS_REGION.amazonaws.com${NC}"
