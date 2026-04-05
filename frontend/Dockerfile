# Multi-Stage Build for CyberSAM Frontend
# Stage 1: Build Stage
FROM node:20-alpine AS builder

WORKDIR /app

# Build-time arguments for environment configuration
ARG VITE_API_URL=http://backend:8080/api/v1

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci

# Copy source code with environment for production build
COPY . .

# Build application with environment variables
RUN VITE_API_URL=${VITE_API_URL} npm run build

# Stage 2: Runtime Stage - Nginx for serving static files
FROM nginx:alpine

# Copy nginx configuration for SPA routing
COPY --chown=nginx:nginx <<EOF /etc/nginx/conf.d/app.conf
server {
    listen 80;
    server_name _;
    root /usr/share/nginx/html;
    index index.html;

    # Enable gzip compression
    gzip on;
    gzip_types text/plain text/css text/js text/xml text/javascript 
               application/javascript application/xml+rss application/rss+xml font/truetype;

    # SPA Routing
    location / {
        try_files \$uri \$uri/ /index.html;
        expires -1;
        add_header Cache-Control "public, max-age=0";
    }

    # Cache static assets with versioning
    location ~* \.(js|css|png|jpg|jpeg|gif|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "no-referrer-when-downgrade" always;
    add_header Permissions-Policy "geolocation=(), microphone=(), camera=()" always;

    # Proxy API requests to backend
    location /api/ {
        proxy_pass http://backend:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade \$http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host \$host;
        proxy_cache_bypass \$http_upgrade;
    }
}
EOF

# Copy built application from builder stage
COPY --from=builder --chown=nginx:nginx /app/dist /usr/share/nginx/html

# Remove default nginx config
RUN rm /etc/nginx/conf.d/default.conf

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:80/ || exit 1

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
