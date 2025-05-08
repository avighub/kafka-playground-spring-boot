### How to start application

1. Clone the repository
   ```bash
   git clone
2. Install dependencies
   ```bash
   cd <project-directory>
   maven install
   ```
3. Setup services via docker
   ```bash
   docker compose up -d # Start docker and services
   docker compose down && docker compose up -d # Restarts Docker and Services
    ```
4. Access the application
    1. Access Confluent Control Center UI at: http://localhost:9021
    2. You might not see any topics created yet in the UI. This is because the topics are created dynamically when the
       application starts.
5. Run Spring boot app
   ```bash
   ./mvnw spring-boot:run
   ```
6. Access the application API
    1. POST /api/v1/messages/send - Send to any topic
   ``` bash 
   curl --location 'http://localhost:8080/api/v1/messages/send' \
   --header 'Content-Type: application/json' \
   --data '{
     "key": "sample-key-1",
     "value": "This is a test message",
     "topic": "messages"
   }'
   ```
    2. POST /api/v1/messages/send/message-topic - Send to message topic
   ``` bash 
   curl -X POST http://localhost:8080/api/v1/messages/send/message-topic \
   -H "Content-Type: text/plain" \
   -d "Hello Kafka Messages Topic!"
   ```
    3. POST /api/v1/messages/send/notification-topic - Send to notification topic
    ``` bash
   curl -X POST http://localhost:8080/api/v1/messages/send/notification-topic \
   -H "Content-Type: application/json" \
   -d '{
   "type": "alert",
   "message": "Server CPU usage high",
   "priority": "critical"
   }'
   ```
    4. POST /api/v1/messages/send/audit-topic - Send to audit topic
   ``` bash
    curl -X POST http://localhost:8080/api/v1/messages/send/audit-topic \
   -H "Content-Type: application/json" \
   -d '{
   "timestamp": "2023-11-15T14:30:00Z",
   "userId": "user-123",
   "action": "DELETE",
   "entity": "order",
   "entityId": "order-456"
   }'
   ```
