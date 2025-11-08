# MessengerMesh

MessengerMesh is a scalable, real-time messaging platform built with a microservices architecture. It provides secure authentication, encrypted messaging, channel-based communication, file sharing, and presence notifications. The platform consists of a robust backend API, a modern React-based frontend, and supporting services for real-time features.

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Services](#services)
  - [Backend (Core Auth Messaging Service)](#backend-core-auth-messaging-service)
  - [Frontend](#frontend)
  - [Notification Presence Service](#notification-presence-service)
- [Technologies](#technologies)
- [Prerequisites](#prerequisites)
- [Setup and Installation](#setup-and-installation)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Deployment](#deployment)
- [Contributing](#contributing)
- [License](#license)

## Features

- **Secure Authentication**: JWT-based authentication with refresh tokens
- **Real-time Messaging**: WebSocket support for instant messaging
- **Channel-based Communication**: Create and manage channels for group conversations
- **End-to-End Encryption**: Client-side encryption for message security
- **File Sharing**: Upload and share files via MinIO integration
- **Presence Indicators**: Real-time online/offline status updates
- **Rate Limiting**: Protection against abuse with configurable rate limits
- **Scalable Architecture**: Microservices design for horizontal scaling
- **Modern UI**: Responsive React frontend with Tailwind CSS

## Architecture

MessengerMesh follows a microservices architecture with the following components:

- **Core Auth Messaging Service**: Main backend service handling authentication, messaging, and data persistence
- **Frontend**: React-based user interface
- **Notification Presence Service**: Handles real-time notifications and presence updates
- **Databases**: MongoDB for main data, Redis for caching and pub/sub, MinIO for file storage

Services communicate via REST APIs and WebSockets, with Redis providing pub/sub functionality for real-time features.

## Services

### Backend (Core Auth Messaging Service)

The core backend is built with Spring Boot and provides:

- User authentication and authorization
- Message and channel management
- File upload/download
- WebSocket endpoints for real-time communication
- RESTful API endpoints
- Security features including JWT tokens and rate limiting

**Key Technologies:**
- Java 17
- Spring Boot 3.2.0
- Spring Security
- Spring Data MongoDB
- Spring WebSocket
- JWT (JJWT)
- MinIO for file storage
- Redis for caching

### Frontend

A modern, responsive web application built with React and TypeScript.

**Key Features:**
- User registration and login
- Real-time chat interface
- Channel management
- File sharing
- Presence indicators
- Responsive design

**Key Technologies:**
- React 18
- TypeScript
- Vite for build tooling
- Tailwind CSS for styling
- Axios for API calls
- SockJS + STOMP for WebSocket communication
- Zustand for state management
- React Hook Form + Zod for form validation

### Notification Presence Service

A Node.js service handling real-time notifications and user presence.

**Key Features:**
- Real-time presence updates
- Notification broadcasting
- Socket.IO integration

**Key Technologies:**
- Node.js
- Socket.IO
- Redis for pub/sub
- Express.js

## Technologies

- **Backend**: Java, Spring Boot, MongoDB, Redis, MinIO
- **Frontend**: React, TypeScript, Vite, Tailwind CSS
- **Real-time**: WebSocket, Socket.IO, STOMP
- **Infrastructure**: Docker, Docker Compose
- **Testing**: JUnit, Testcontainers, Vitest, React Testing Library
- **CI/CD**: GitHub Actions

## Prerequisites

- Docker and Docker Compose
- Node.js 20+ (for frontend development)
- Java 17+ (for backend development)
- Maven 3.8+ (for backend builds)

## Setup and Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/messengermesh.git
   cd messengermesh
   ```

2. **Set up environment variables:**
   Create a `.env` file in the root directory:
   ```
   APP_JWT_SECRET=your-super-secret-jwt-key-here
   ```

3. **Start the development environment:**
   ```bash
   docker-compose -f docker-compose.dev.yml up -d
   ```

   This will start all services including databases, backend, frontend, and notification service.

4. **For frontend development (optional):**
   If you want to run the frontend in development mode with hot reloading:
   ```bash
   cd services/frontend
   npm install
   npm run dev
   ```

5. **Access the application:**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - Notification Service: http://localhost:4000
   - MinIO Console: http://localhost:9000 (admin/admin)

## Usage

1. **Register a new account** or **login** with existing credentials
2. **Create or join channels** for group conversations
3. **Send messages** in real-time
4. **Share files** by uploading them to channels
5. **View presence indicators** to see who's online

### API Usage

The backend provides a RESTful API. Here are some key endpoints:

- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/register` - User registration
- `GET /api/v1/channels` - Get user's channels
- `POST /api/v1/channels` - Create a new channel
- `GET /api/v1/messages/{channelId}` - Get messages for a channel
- `POST /api/v1/messages` - Send a message

For WebSocket communication, connect to `/ws` endpoint using STOMP protocol.

## API Documentation

API documentation is available via Swagger UI at `http://localhost:8080/swagger-ui.html` when the backend is running.

An OpenAPI specification is also generated at `openapi.yaml` in the project root.

## Testing

### Backend Tests
```bash
cd services/core-auth-messaging-service
mvn test
```

### Frontend Tests
```bash
cd services/frontend
npm test
```

### End-to-End Tests
End-to-end tests are available in the backend service using Testcontainers.

## Deployment

### Production Deployment

1. **Build the services:**
   ```bash
   docker-compose build
   ```

2. **Deploy:**
   ```bash
   docker-compose up -d
   ```

### Environment Configuration

For production, configure the following environment variables:

- Database URIs
- JWT secrets
- MinIO credentials
- Redis connection details

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

Please ensure all tests pass and code follows the project's style guidelines.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

Built with ❤️ using microservices architecture for scalability and maintainability.
