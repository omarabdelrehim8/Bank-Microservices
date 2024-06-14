# Bank-Microservices

Welcome to the **Bank-Microservices** project! This project showcases the implementation of a microservices architecture using Java and Spring Boot. It demonstrates how to build, deploy, and manage a set of loosely coupled, independently deployable services that work together to simulate a simple banking system.

## Table of Contents
- [Architecture](#architecture)
- [Services](#services)
- [Technologies](#technologies)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Running the Services](#running-the-services)
- [API Documentation](#api-documentation)
- [Postman Collection](#postman-collection)


## Architecture

The architecture of this project consists of multiple microservices, each responsible for a specific aspect inside the architecture. The services communicate with each other using REST APIs and Kafka for messaging, and are managed through a central configuration server and a service discovery mechanism.


## Services

1. **Accounts Service**: Manages bank accounts and related operations.
2. **Cards Service**: Handles credit/debit card information and related operations.
3. **Gateway Server**: Acts as a gateway for routing requests to the appropriate microservices.
4. **Eureka Server**: Service discovery server for registering and discovering microservices.
5. **Config Server**: Centralized configuration server for managing service configurations.
6. **Message Service**: Communicates with the Accounts Service via Kafka for message processing.

## Technologies

- **Java**: Programming language used for developing the microservices.
- **Spring Boot**: Framework for building microservices with Java.
- **Spring Cloud**: Provides tools for service discovery, configuration management, and load balancing.
- **Eureka**: Service discovery server.
- **Spring Cloud Config**: Centralized configuration management.
- **Spring Cloud Stream**: Framework for building highly scalable event-driven microservices connected with shared messaging systems.
- **Spring Cloud Function**: Promotes the use of functions as the unit of deployment and scaling for Spring applications.
- **Spring Data JPA**: Persistence layer for data access.
- **MySQL**: Database for persisting account and card information.
- **Kafka**: Messaging platform for communication between services.
- **Redis**: In-memory data structure store, used as a cache.
- **Docker**: Containerization platform for deploying services.
- **OpenAPI/Swagger**: API documentation tool.
- **Git**: Version control system for managing the project repository.
- **JUnit**: Testing framework for unit testing.
- **Mockito**: Testing framework for mocking dependencies in unit tests.

## Getting Started

### Prerequisites

- Docker installed inside local system to run the services

### Installation

1. **Clone the repository:**
    ```sh
    git clone https://github.com/omarabdelrehim8/Bank-Microservices.git
    cd Bank-Microservices/docker-compose
    ```

2. **Navigate to the desired environment folder:**
    ```sh
    cd default  # or cd prod for production environment
    ```

### Running the Services

1. **Once inside the desired environment folder, Start all services using Docker Compose:**
    ```sh
    docker compose up -d
    ```

This will start all the microservices along with MySQL, Kafka, and Redis containers.

## API Documentation

API documentation is available for the Accounts and Cards microservices using Swagger UI.

- **Accounts Service Documentation**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **Cards Service Documentation**: [http://localhost:9000/swagger-ui/index.html](http://localhost:9000/swagger-ui/index.html)

You can use the "Try it out" feature in Swagger UI to interact with the APIs directly from the documentation.

## Postman Collection

A Postman collection is available to help you interact with the application. You can find the collection file in the `postman` folder of the repository.

1. **Download the Postman collection file:**
    - Navigate to the `postman` folder in the repository and download the collection file.

2. **Import the collection into Postman:**
    - Open Postman.
    - Click on the `Import` button.
    - Select the downloaded collection file and import it.

This collection includes predefined requests for the various endpoints, making it easy to test and interact with the microservices.

---

Thank you for checking out the **Bank-Microservices** project! I hope this example helps you understand and implement microservices in your own projects. Happy coding!
