# coupon-flash

`coupon-flash` is a full-stack coupon flash-sale project built around a Spring Boot 3 backend and a Vue 3 frontend.

It focuses on high-concurrency voucher ordering, cache protection, rate limiting, reliable messaging, delayed notifications, and sharded MySQL storage.

## Highlights

- SMS-code login with Redis-based session storage
- Shop and voucher query optimization with Redis, local cache, and Bloom filters
- Flash-sale voucher ordering with Lua, Kafka, and reconciliation logs
- Subscription and delayed reminder workflow for voucher events
- Redis rate limiting and token-based access control
- Redisson-based distributed lock, repeat-submit protection, and delay queue support
- MySQL sharding through ShardingSphere

## Tech Stack

- Backend: Java 17+, Spring Boot 3.5, MyBatis-Plus, ShardingSphere
- Cache and middleware: Redis, Redisson, Kafka, ZooKeeper
- Frontend: Vue 3, Vite, Pinia, Element Plus
- Database: MySQL 8

## Repository Layout

- `hmdp-core-service`: main backend application
- `hmdp-common`: shared constants, enums, exceptions, helpers
- `hmdp-parameter`: DTOs and VOs shared across modules
- `hmdp-id-generator-framework`: Snowflake-style ID generation
- `hmdp-redis-tool-framework`: Redis cache, key management, rate limiting
- `hmdp-redisson-framework`: Redisson lock, Bloom filter, delay queue support
- `hmdp-mq-framework`: Kafka producer and consumer abstractions
- `hmdp-sharding`: Sharding support layer
- `hmdp-vue3`: frontend application
- `sql`: database bootstrap and shard data scripts

## Requirements

- JDK 17 or newer
- Maven 3.9+
- MySQL 8
- Redis
- Kafka
- ZooKeeper
- Node.js 18+ and npm

## Quick Start

### 1. Prepare MySQL

Create and import the two shard schemas:

```bash
mysql -u root -p < sql/1_create_database.sql
mysql -u root -p < sql/hmdp_0.sql
mysql -u root -p < sql/hmdp_1.sql
```

The backend expects:

- `hmdp_0`
- `hmdp_1`

### 2. Configure the backend datasource

The backend uses ShardingSphere through:

- `hmdp-core-service/src/main/resources/shardingsphere.yaml`

Update the MySQL host, port, username, and password in that file for your environment before starting the app.

### 3. Start middleware

Make sure these services are running locally or reachable from the backend:

- MySQL
- Redis on `127.0.0.1:6379`
- Kafka on `localhost:9092`
- ZooKeeper on `localhost:2181`

### 4. Build the backend

From the repository root:

```bash
mvn clean install -DskipTests
```

### 5. Run the backend

From the repository root:

```bash
mvn -pl hmdp-core-service spring-boot:run
```

Default backend port:

- `8085`

Health endpoint:

```bash
curl http://127.0.0.1:8085/actuator/health
```

### 6. Run the frontend

```bash
cd hmdp-vue3
npm install
npm run dev
```

## Development Notes

- The backend login flow is code-based, not password-based.
- The root project is a Maven multi-module build.
- Frontend and backend are developed separately.
- `README.md` and source comments have been customized for this repository.

## Useful Commands

Build all modules:

```bash
mvn clean install
```

Run frontend production build:

```bash
cd hmdp-vue3
npm run build
```

Check backend health:

```bash
curl http://127.0.0.1:8085/actuator/health
```

## License

This repository includes the Apache License 2.0 text in [`LICENSE`](./LICENSE).
