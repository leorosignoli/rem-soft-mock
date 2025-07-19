## Getting Started

To run the entire application locally:

```bash
docker-compose up -d
```

This will start all services including the database, cache, backend API, frontend, and the batch job service that creates sample orders.

## Access Points _(using docker compose)_
- Frontend: http://localhost:80
- Backend API: http://localhost:8080
- Database: localhost:5432

## Features
- Real-time order notifications via SSE
- Notification queue system on product details page
- Redis caching (10min TTL)
- Random order creation (3-12s intervals)
- Sort by any collumn in the front-end app


# Architecture
Remsoft's test of a mock order management API 

![Architecture Diagram](images/architecture.png)

1. Frontend (Angular 20 with SSR)
2. Backend monolith (Spring Boot + Java 17)
3. Relational Database (PostgreSQL)
4. Redis cache (decreases database load)
5. Order simulation batch job (Node.js, 3-12s intervals) 


## Demo

![Order Management](images/order_mgmt.png)
![Order Details](images/order_details.png)


