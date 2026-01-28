# Deployment Guide

## Prerequisites
- Docker and Docker Compose installed
- PostgreSQL credentials configured
- JWT secret key configured
- AWS/Azure account (optional for cloud deployment)

## Local Development Deployment

### Using Docker Compose
```bash
# Start the entire stack
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down
```

## Docker Deployment

### Build Docker Image
```bash
docker build -f docker/Dockerfile -t ecommerce-backend:latest .
```

### Run Docker Container
```bash
docker run -d \
  --name ecommerce-app \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/ecommerce_db \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  -e SPRING_PROFILES_ACTIVE=dev \
  ecommerce-backend:latest
```

## Kubernetes Deployment

### Create Deployment YAML
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ecommerce-backend
spec:
  replicas: 3
  selector:
    matchLabels:
      app: ecommerce-backend
  template:
    metadata:
      labels:
        app: ecommerce-backend
    spec:
      containers:
      - name: ecommerce-backend
        image: ecommerce-backend:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: SPRING_DATASOURCE_URL
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: url
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
```

### Deploy to Kubernetes
```bash
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/configmap.yaml
```

## AWS Deployment

### Deploy to Elastic Beanstalk
```bash
# Install EB CLI
pip install awsebcli

# Initialize EB application
eb init -p java-17 ecommerce-backend

# Create environment
eb create ecommerce-prod

# Deploy
eb deploy

# View logs
eb logs
```

### Deploy to ECS
1. Create ECR repository
2. Push Docker image
3. Create ECS cluster
4. Create task definition
5. Create service

## Azure Deployment

### Deploy to App Service
```bash
# Create resource group
az group create --name ecommerce-rg --location eastus

# Create App Service Plan
az appservice plan create \
  --name ecommerce-plan \
  --resource-group ecommerce-rg \
  --sku B2 \
  --is-linux

# Create Web App
az webapp create \
  --resource-group ecommerce-rg \
  --plan ecommerce-plan \
  --name ecommerce-backend \
  --runtime "java|17-java17"

# Deploy
az webapp deployment source config-zip \
  --resource-group ecommerce-rg \
  --name ecommerce-backend \
  --src target/ecommerce-backend.jar
```

## Environment Configuration

### Production Environment Variables
```bash
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://db-prod.example.com:5432/ecommerce_db
SPRING_DATASOURCE_USERNAME=prod_user
SPRING_DATASOURCE_PASSWORD=secure_password_here
APP_JWT_SECRET=your-production-secret-key-min-32-chars
LOGGING_LEVEL_ROOT=WARN
LOGGING_LEVEL_COM_ECOMMERCE=INFO
```

## Database Backup and Restore

### Backup PostgreSQL Database
```bash
docker exec ecommerce_postgres pg_dump -U postgres ecommerce_db > backup.sql
```

### Restore PostgreSQL Database
```bash
docker exec -i ecommerce_postgres psql -U postgres ecommerce_db < backup.sql
```

## Health Checks and Monitoring

### Health Check Endpoint
```bash
curl http://localhost:8080/api/actuator/health
```

### Metrics
```bash
curl http://localhost:8080/api/actuator/metrics
```

### Application Info
```bash
curl http://localhost:8080/api/actuator/info
```

## CI/CD Integration

### GitHub Actions
See `.github/workflows/ci-cd.yml`

### GitLab CI
Create `.gitlab-ci.yml` in repository root

### Jenkins
Create `Jenkinsfile` for pipeline configuration

## Scaling Considerations

- Horizontal scaling: Use load balancers with multiple instances
- Vertical scaling: Increase container memory and CPU
- Database scaling: Use connection pooling and read replicas
- Caching: Implement Redis for session and data caching

## Security Checklist

- [ ] Change default JWT secret
- [ ] Use HTTPS in production
- [ ] Enable CORS only for trusted domains
- [ ] Set up firewall rules
- [ ] Enable database encryption
- [ ] Regular security updates
- [ ] Monitor access logs
- [ ] Configure SSL/TLS certificates

## Rollback Procedures

### Docker Rollback
```bash
docker pull ecommerce-backend:previous-version
docker stop ecommerce-app
docker run -d --name ecommerce-app ecommerce-backend:previous-version
```

### Kubernetes Rollback
```bash
kubectl rollout undo deployment/ecommerce-backend
```

---

**Last Updated:** January 2026
