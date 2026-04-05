# CyberSAM Deployment Guide

Complete guide for deploying CyberSAM to various environments.

## Deployment Architectures

### 1. Local Development (Docker Compose)

Perfect for development and testing.

```bash
docker-compose up -d
# Access: http://localhost
```

**Services:**
- PostgreSQL database
- Spring Boot backend
- React frontend
- PgAdmin for database management (optional)

### 2. Docker Production (Self-Hosted)

For on-premises defense industry deployments.

```bash
# Build images
docker build -t cybersam-backend:1.0.0 ./backend
docker build -t cybersam-frontend:1.0.0 ./frontend

# Push to private registry
docker tag cybersam-backend:1.0.0 registry.example.com/cybersam-backend:1.0.0
docker push registry.example.com/cybersam-backend:1.0.0
docker push registry.example.com/cybersam-frontend:1.0.0

# Deploy with docker-compose
docker-compose -f docker-compose.prod.yml up -d
```

**Considerations:**
- Use external PostgreSQL (AWS RDS, managed database)
- Configure SSL/TLS termination
- Set up proper logging and monitoring
- Implement backup strategies

### 3. AWS Elastic Container Service (ECS)

For scalable cloud deployments.

#### Create Task Definitions

**Backend Task:**
```json
{
  "family": "cybersam-backend",
  "container_definitions": [
    {
      "name": "backend",
      "image": "123456789012.dkr.ecr.us-east-1.amazonaws.com/cybersam-backend:1.0.0",
      "portMappings": [{"containerPort": 8080}],
      "environment": [
        {
          "name": "SPRING_DATASOURCE_URL",
          "value": "jdbc:postgresql://cybersam-db.xxxxx.rds.amazonaws.com:5432/cybersam_db"
        }
      ],
      "secrets": [
        {
          "name": "SPRING_DATASOURCE_PASSWORD",
          "valueFrom": "arn:aws:secretsmanager:us-east-1:123456789012:secret:cybersam-db-password"
        }
      ]
    }
  ]
}
```

#### Create Fargate Service

```bash
aws ecs create-service \
    --cluster cybersam-prod \
    --service-name cybersam-backend \
    --task-definition cybersam-backend:1 \
    --launch-type FARGATE \
    --desired-count 2 \
    --network-configuration "awsvpcConfiguration={subnets=[subnet-xxx],securityGroups=[sg-xxx],assignPublicIp=DISABLED}"
```

### 4. AWS Kubernetes (EKS)

For container orchestration at scale.

#### Create Kubernetes Manifests

**Backend Deployment:**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: cybersam-backend
spec:
  replicas: 3
  selector:
    matchLabels:
      app: cybersam-backend
  template:
    metadata:
      labels:
        app: cybersam-backend
    spec:
      containers:
      - name: backend
        image: 123456789012.dkr.ecr.us-east-1.amazonaws.com/cybersam-backend:1.0.0
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_DATASOURCE_URL
          value: "jdbc:postgresql://cybersam-db.xxxxx.rds.amazonaws.com:5432/cybersam_db"
        envFrom:
        - secretRef:
            name: cybersam-secrets
        resources:
          requests:
            cpu: "500m"
            memory: "512Mi"
          limits:
            cpu: "1000m"
            memory: "1Gi"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: cybersam-backend
spec:
  selector:
    app: cybersam-backend
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: LoadBalancer
```

Deploy:
```bash
kubectl apply -f backend-deployment.yaml
```

### 5. AWS Frontend (S3 + CloudFront)

For globally distributed static assets.

See [AWS_DEPLOYMENT.md](AWS_DEPLOYMENT.md) for detailed S3 deployment guide.

```bash
cd frontend
npm run build:s3
./scripts/deploy-s3.sh
```

## AWS RDS PostgreSQL Setup

### Create RDS Instance

```bash
aws rds create-db-instance \
    --db-instance-identifier cybersam-db \
    --db-instance-class db.t3.micro \
    --engine postgres \
    --master-username admin \
    --master-user-password 'YourSecurePassword123!' \
    --allocated-storage 20 \
    --storage-type gp3 \
    --storage-encrypted \
    --backup-retention-period 7 \
    --multi-az false \
    --publicly-accessible false \
    --enable-cloudwatch-logs-exports '["postgresql"]' \
    --region us-east-1
```

### Initialize Database

```bash
# Get RDS endpoint
RDS_ENDPOINT=$(aws rds describe-db-instances \
    --db-instance-identifier cybersam-db \
    --query 'DBInstances[0].Endpoint.Address' \
    --output text)

# Connect and initialize
psql -h $RDS_ENDPOINT -U admin -d postgres -f init-db.sql
```

### Backup Strategy

```bash
# Enable automated backups (7 days retention)
aws rds modify-db-instance \
    --db-instance-identifier cybersam-db \
    --backup-retention-period 7 \
    --preferred-backup-window "03:00-04:00"

# Create manual snapshot before deployment
aws rds create-db-snapshot \
    --db-instance-identifier cybersam-db \
    --db-snapshot-identifier cybersam-db-pre-deployment
```

## Networking & Security

### VPC Configuration

```bash
# Create VPC
aws ec2 create-vpc --cidr-block 10.0.0.0/16

# Create public subnet (for frontend)
aws ec2 create-subnet --vpc-id vpc-xxx --cidr-block 10.0.1.0/24

# Create private subnet (for backend/database)
aws ec2 create-subnet --vpc-id vpc-xxx --cidr-block 10.0.2.0/24
```

### Security Groups

**Backend Security Group:**
```bash
aws ec2 create-security-group \
    --group-name cybersam-backend-sg \
    --description "Backend security group" \
    --vpc-id vpc-xxx

# Allow from ALB
aws ec2 authorize-security-group-ingress \
    --group-id sg-backend-xxx \
    --protocol tcp \
    --port 8080 \
    --source-security-group-id sg-alb-xxx
```

**Database Security Group:**
```bash
aws ec2 create-security-group \
    --group-name cybersam-database-sg \
    --description "Database security group"

# Allow from backend
aws ec2 authorize-security-group-ingress \
    --group-id sg-database-xxx \
    --protocol tcp \
    --port 5432 \
    --source-security-group-id sg-backend-xxx
```

## Load Balancing & SSL/TLS

### Create Application Load Balancer

```bash
aws elbv2 create-load-balancer \
    --name cybersam-alb \
    --subnets subnet-xxx subnet-yyy \
    --security-groups sg-alb-xxx \
    --scheme internet-facing \
    --type application

# Create target group
aws elbv2 create-target-group \
    --name cybersam-backend-tg \
    --protocol HTTP \
    --port 8080 \
    --vpc-id vpc-xxx
```

### SSL/TLS Certificate

```bash
# Import certificate from ACM
aws acm request-certificate \
    --domain-name cybersam.defense \
    --validation-method DNS

# Use in listener
aws elbv2 create-listener \
    --load-balancer-arn arn:aws:elasticloadbalancing:... \
    --protocol HTTPS \
    --port 443 \
    --certificates CertificateArn=arn:aws:acm:...
```

## CI/CD Pipeline (GitHub Actions)

### Deploy on Push to Main

```yaml
name: Deploy CyberSAM

on:
  push:
    branches: [main]

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v2
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: us-east-1
      
      - name: Build and push backend Docker image
        run: |
          aws ecr get-login-password | docker login --username AWS --password-stdin $ECR_REGISTRY
          docker build -t $ECR_REGISTRY/cybersam-backend:latest ./backend
          docker push $ECR_REGISTRY/cybersam-backend:latest
      
      - name: Deploy backend to ECS
        run: |
          aws ecs update-service \
            --cluster cybersam-prod \
            --service cybersam-backend \
            --force-new-deployment
      
      - name: Build and deploy frontend to S3
        run: |
          cd frontend && npm install && npm run build:s3
          ./scripts/deploy-s3.sh
```

## Monitoring & Logging

### CloudWatch Logs

```bash
# Create log group
aws logs create-log-group --log-group-name /cybersam/backend

# Create log stream
aws logs create-log-stream \
    --log-group-name /cybersam/backend \
    --log-stream-name application
```

### Application Performance Monitoring

```bash
# Enable X-Ray for tracing
aws xray create-group --group-name CyberSAM

# Backend: Add Maven dependency
# <dependency>
#     <groupId>com.amazonaws</groupId>
#     <artifactId>aws-xray-recorder-sdk-spring</artifactId>
# </dependency>
```

## Backup & Disaster Recovery

### RDS Backups

```bash
# Automated backups (enabled by default)
# Manual snapshot
aws rds create-db-snapshot \
    --db-instance-identifier cybersam-db \
    --db-snapshot-identifier cybersam-db-snapshot-$(date +%Y%m%d)
```

### S3 Versioning & Backup

```bash
# Enable versioning
aws s3api put-bucket-versioning \
    --bucket cybersam-frontend \
    --versioning-configuration Status=Enabled

# Cross-region replication
aws s3api put-bucket-replication \
    --bucket cybersam-frontend \
    --replication-configuration file://replication.json
```

## Scaling

### Auto-Scaling Group (ECS)

```bash
aws autoscaling create-auto-scaling-group \
    --auto-scaling-group-name cybersam-backend-asg \
    --launch-template LaunchTemplateName=cybersam-backend \
    --min-size 2 \
    --max-size 6 \
    --desired-capacity 2 \
    --health-check-type ELB

# Scaling policy
aws autoscaling put-scaling-policy \
    --auto-scaling-group-name cybersam-backend-asg \
    --policy-name scale-up \
    --policy-type TargetTrackingScaling \
    --target-tracking-configuration file://target-tracking-config.json
```

## Post-Deployment Checklist

- [ ] Database connectivity verified
- [ ] API endpoints responding (Swagger UI accessible)
- [ ] Frontend loading correctly
- [ ] SSL/TLS certificates valid
- [ ] Backups configured and tested
- [ ] Monitoring enabled
- [ ] Logging configured
- [ ] Auto-scaling policies active
- [ ] Health checks passing
- [ ] Load testing completed
- [ ] Security scan completed
- [ ] Documentation updated

---

For AWS-specific questions, see [AWS_DEPLOYMENT.md](AWS_DEPLOYMENT.md)
