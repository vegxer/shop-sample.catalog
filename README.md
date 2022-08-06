# Catalog

## Local run in development mode
1) Create postgresql database
2) Set following settings in application.yml file:
   ```
   spring.datasource.url
   spring.datasource.username
   spring.datasource.password
   ```
3) Build with Maven using command
    ```
    mvn clean package
    ```
4) Run with Maven using command
    ```
    mvn spring-boot:run
    ```
   
## Run with Docker
1) Build Docker image using command
   ```
   docker build -t vegxer/shop-sample/catalog:${CATALOG_VERSION from docker/.env} -f ./docker/Dockerfile .
   ```
2) Go to directory 'docker'
3) Set environment variables in file .env
4) Run service using command
   ```
   docker-compose up -d
   ```
   
While server is running you can see its endpoints and make requests at http://localhost:${PORT}/swagger-ui.html