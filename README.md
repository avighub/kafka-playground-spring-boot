### How to start application

1. Clone the repository
   ```bash
   git clone https://github.com/avighub/kafka-playground-spring-boot
2. Install dependencies
   ```bash
   cd <project-directory>
   mvn clean package -DskipTests
   ```
3. Setup services via docker
   ```bash
   docker compose up --build
    ```
4. Access the application
    1. Access Confluent Control Center UI at: http://localhost:9021
    2. You might not see any topics created yet in the UI. This is because the topics are created dynamically when the
       application starts.
5. Access the application API
    1. Create Order(s)
   ``` bash 
    curl --request POST \
   --url http://localhost:8080/api/v1/order/create \
   --header 'content-type: application/json' \
   --data '[
   {
   "orderId": "bfc5b215-29b6-4988-8a1f-08423f0acdf1",
   "transactionId": "8e7ed790-08f8-4e7c-bdbe-f87a9c4a2bbc",
   "customerEmail": "",
   "shippingAddress": "123 Main St, Springfield, USA",
   "totalAmount": 99.99,
   "paymentMethod": "CREDIT_CARD"
   },
   {
   "orderId": "bfc5b215-29b6-4988-8a1f-08423f0acdf2",
   "transactionId": "8e7ed790-08f8-4e7c-bdbe-f87a9c4a2bbb",
   "customerEmail": "",
   "shippingAddress": "123 Main St, Springfield, USA",
   "totalAmount": 100,
   "paymentMethod": "CASH"
   }
   ]'
   ```
    2. Send message to notifications topic
    ``` bash
   curl --request POST \
   --url http://localhost:8080/api/v1/order/send/notification-topic \
   --header 'content-type: application/json' \
   --data '[
   {
   "orderId": "bfc5b215-29b6-4988-8a1f-08423f0acdf1",
   "transactionId": "8e7ed790-08f8-4e7c-bdbe-f87a9c4a2bbc",
   "customerEmail": "",
   "shippingAddress": "123 Main St, Springfield, USA",
   "totalAmount": 99.99,
   "paymentMethod": "CREDIT_CARD"
   },
   {
   "orderId": "bfc5b215-29b6-4988-8a1f-08423f0acdf2",
   "transactionId": "8e7ed790-08f8-4e7c-bdbe-f87a9c4a2bbb",
   "customerEmail": "",
   "shippingAddress": "123 Main St, Springfield, USA",
   "totalAmount": 100,
   "paymentMethod": "CASH"
   }
   ]'
   ```
    3. Send message to payments topic
   ``` bash
    curl --request POST \
   --url http://localhost:8080/api/v1/order/send/payment-topic \
   --header 'content-type: application/json' \
   --data '[
   {
   "orderId": "bfc5b215-29b6-4988-8a1f-08423f0acdf1",
   "transactionId": "8e7ed790-08f8-4e7c-bdbe-f87a9c4a2bbc",
   "customerEmail": "",
   "shippingAddress": "123 Main St, Springfield, USA",
   "totalAmount": 99.99,
   "paymentMethod": "CREDIT_CARD"
   },
   {
   "orderId": "bfc5b215-29b6-4988-8a1f-08423f0acdf2",
   "transactionId": "8e7ed790-08f8-4e7c-bdbe-f87a9c4a2bbb",
   "customerEmail": "",
   "shippingAddress": "123 Main St, Springfield, USA",
   "totalAmount": 100,
   "paymentMethod": "CASH"
   }
   ]'
   ```

### Running Tests

``` bash
# Run only notifications topic tests
mvn test -Dgroups="unit-test,notifications-topic"
mvn test -Dgroups="integration-test,notifications-topic"

# Run only payments topic tests
mvn test -Dgroups="unit-test,payments-topic"
mvn test -Dgroups="integration-test,payments-topic"

# Run only create-order tests
mvn test -Dgroups="unit-test,create-order"
mvn test -Dgroups="integration-test,create-order"

# Run all tests
mvn test
```

### Log File Information

The application generates logs to help monitor and debug its behavior. <br>
Logs are written to a file located at `logs/app.log`. The log file is configured to reset daily, ensuring it does not
grow indefinitely. This is achieved through log rotation, which overwrites the log file every day. Additionally, the log
file size can be limited to prevent excessive disk usage.
