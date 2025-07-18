# Order Batch Job Service

A Node.js service that automatically creates orders every 10 seconds by making HTTP requests to the Order Management Service API.

## Features

- **Automated Order Creation**: Creates new orders every 10 seconds
- **Random Data Generation**: Generates orders with random users, products, and quantities
- **Graceful Shutdown**: Handles SIGINT and SIGTERM signals properly
- **Error Handling**: Logs errors and continues running even if individual requests fail
- **Docker Support**: Fully containerized and integrated with docker-compose

## How It Works

The batch job service:

1. Generates random order data using:
   - Random user ID (1-5)
   - Random products (1-3 items per order from product IDs 1-25)
   - Random quantities (1-5 per product)

2. Makes a POST request to `/orders` endpoint with the generated data

3. Logs the creation process and results

4. Repeats every 10 seconds

## Configuration

The service can be configured using environment variables:

- `BACKEND_URL`: URL of the Order Management Service backend (default: `http://backend:8080`)

## Sample Order Structure

The service generates orders in the following format:

```json
{
  "userId": 2,
  "orderItems": [
    {
      "productId": 15,
      "quantity": 3
    },
    {
      "productId": 7,
      "quantity": 1
    }
  ]
}
```

## Running with Docker Compose

The batch job service is included in the main docker-compose.yml file and will start automatically when you run:

```bash
docker-compose up
```

## Logs

You can view the batch job logs using:

```bash
docker-compose logs batch-job
```

To follow the logs in real-time:

```bash
docker-compose logs -f batch-job
```

## Stopping the Service

To stop just the batch job service:

```bash
docker-compose stop batch-job
```

To stop all services:

```bash
docker-compose down
```

## Development

To run locally for development:

1. Install dependencies:
```bash
npm install
```

2. Set environment variables:
```bash
export BACKEND_URL=http://localhost:8080
```

3. Run the service:
```bash
npm start
``` 