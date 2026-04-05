#!/usr/bin/env pwsh
# AWS S3 Deployment Script for CyberSAM Frontend (PowerShell)
# This script builds and deploys the frontend to AWS S3

param(
    [string]$AWSRegion = "us-east-1",
    [string]$S3Bucket = "cybersam-frontend",
    [string]$CloudFrontDistributionId = "",
    [string]$BuildDir = "dist"
)

$ErrorActionPreference = 'Stop'

Write-Host "=== CyberSAM Frontend S3 Deployment ===" -ForegroundColor Yellow

# Check prerequisites
if (-not (Get-Command aws -ErrorAction SilentlyContinue)) {
    Write-Host "AWS CLI not found. Please install AWS CLI." -ForegroundColor Red
    exit 1
}

if (-not (Get-Command npm -ErrorAction SilentlyContinue)) {
    Write-Host "npm not found. Please install Node.js." -ForegroundColor Red
    exit 1
}

# Build application
Write-Host "Building frontend application..." -ForegroundColor Yellow
npm run build:s3

if (-not (Test-Path $BuildDir)) {
    Write-Host "Build directory not found: $BuildDir" -ForegroundColor Red
    exit 1
}

Write-Host "✓ Build completed" -ForegroundColor Green

# Sync to S3
Write-Host "Syncing built files to S3 bucket: $S3Bucket" -ForegroundColor Yellow

# Upload all files with proper cache headers
aws s3 sync $BuildDir "s3://$S3Bucket/" `
    --region $AWSRegion `
    --delete `
    --exclude "*.html" `
    --exclude "*.json" `
    --cache-control "public, max-age=31536000, immutable"

# Upload HTML and JSON with no-cache
aws s3 sync $BuildDir "s3://$S3Bucket/" `
    --region $AWSRegion `
    --include "*.html" `
    --include "*.json" `
    --cache-control "public, max-age=0, must-revalidate" `
    --content-type "application/json"

aws s3 cp "$BuildDir/index.html" "s3://$S3Bucket/index.html" `
    --region $AWSRegion `
    --cache-control "public, max-age=0, must-revalidate" `
    --content-type "text/html"

Write-Host "✓ S3 sync completed" -ForegroundColor Green

# Invalidate CloudFront cache if distribution ID provided
if ($CloudFrontDistributionId) {
    Write-Host "Invalidating CloudFront distribution: $CloudFrontDistributionId" -ForegroundColor Yellow
    aws cloudfront create-invalidation `
        --distribution-id $CloudFrontDistributionId `
        --paths "/*" `
        --region $AWSRegion
    Write-Host "✓ CloudFront invalidation triggered" -ForegroundColor Green
}

Write-Host "=== Deployment completed successfully ===" -ForegroundColor Green
Write-Host "S3 Bucket URL: http://$S3Bucket.s3-website-$AWSRegion.amazonaws.com" -ForegroundColor Yellow
