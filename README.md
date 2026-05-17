# Inventory Management System

RetailNet Inventory Management System is a full-stack application for managing inventory, procurement, and sales workflows. The backend is built with Spring Boot and Maven, while the frontend is an Angular application organized in an Nx workspace.

## Overview

The system supports a typical inventory lifecycle: products are tracked in stock, low inventory can trigger procurement activity, and sales automatically reduce available quantities. Authentication is designed around OAuth2 / OpenID Connect with Keycloak, and the backend persists data in MySQL.

## Features

- Inventory tracking with stock visibility
- Procurement and supplier workflow support
- Sales operations tied to inventory updates
- REST API backend with layered service architecture
- Angular UI built in a modular Nx monorepo
- OAuth2 / OIDC integration via Keycloak

## Technology Stack

- Backend: Java 21, Spring Boot 3.2, Maven, Spring Data JPA, Hibernate
- Database: MySQL
- Authentication: Keycloak, OAuth2 Resource Server
- Frontend: Angular 21, TypeScript, RxJS, Nx
- Testing: JUnit, Jest, Cypress

## Repository Structure

```
Backend/
   inventory-api/        REST API contracts and request/response models
   inventory-service/    Business logic and service layer
   inventory-app/        Spring Boot application entry point

ui/
   retailnet-workspace/  Nx frontend workspace
      retailnet-ui/       Angular application
      feature-inventory/  Inventory feature module
      feature-procurement/ Procurement feature module
      feature-sales/      Sales feature module
      data-access/        Shared data-access layer
      ui-shared/          Shared UI components and utilities
```

## Prerequisites

- Java 21
- Maven 3.9 or newer
- Node.js 20 or newer
- npm
- MySQL 8+
- Keycloak available locally or remotely

## Configuration

The backend reads database and auth settings from `Backend/inventory-app/src/main/resources/application.properties`.

Default local settings:

- Backend port: `8081`
- MySQL: `jdbc:mysql://localhost:3306/RETAILNET_INVENTORY_DB`
- Keycloak realm issuer: `http://localhost:8080/realms/retailnet`

Environment variables used by the backend:

- `MYSQL_USER` defaults to `root`
- `MYSQL_PASSWORD` defaults to `admin123`

If you use different local credentials, set those environment variables before starting the backend.

## Quick Start

### 1. Start the backend

From the `Backend/` directory:

```sh
mvn clean install
cd inventory-app
mvn spring-boot:run
```

The backend starts on port `8081`.

### 2. Start the frontend

From the `ui/retailnet-workspace/` directory:

```sh
npm install
npx nx serve retailnet-ui
```

The Angular app runs on port `4200`.

## Common Commands

### Backend

```sh
cd Backend
mvn test
mvn clean install
cd inventory-app
mvn spring-boot:run
```

### Frontend

```sh
cd ui/retailnet-workspace
npx nx build retailnet-ui
npx nx lint retailnet-ui
npx nx test retailnet-ui
npx nx serve retailnet-ui
```

## Project Notes

- The backend is split into API, service, and application modules to keep the domain logic separate from transport concerns.
- The frontend is organized as an Nx workspace so feature modules can grow independently while sharing common UI and data-access code.
- Authentication and authorization are expected to flow through Keycloak before protected API calls succeed.

## Troubleshooting

- If the backend fails to connect to MySQL, confirm the database exists and the credentials in your environment match the values used by the application.
- If login or protected API requests fail, verify that Keycloak is running on port `8080` and that the `retailnet` realm is available.
- If the frontend build fails after pulling changes, reinstall dependencies from `ui/retailnet-workspace/` with `npm install`.

## Contributing

1. Create a feature branch.
2. Make your changes.
3. Run the relevant backend and frontend checks.
4. Open a pull request with a clear summary of the change.

