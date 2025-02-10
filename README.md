# TravelShare API

## Overview
TravelShare is a RESTful API built with Java and Maven that facilitates travel-related services, including user management, reservations, reporting, and posting. This API enables users to share travel experiences, book trips, and generate insights from their travel activities.

## Features
- **User Management**: Registration, authentication, profile updates
- **Reservations**: Booking, modifying, and canceling reservations
- **Reporting**: Generating reports on user activity and travel trends
- **Posting**: Users can share travel experiences with posts and media
- **Admin Controls**: Manage users, reservations, and system settings

## Technology Stack
- **Java 17+**
- **Maven**
- **MySQL** (as the primary database)
- **JWT Authentication**

## Getting Started

### Prerequisites
Ensure you have the following installed:
- Java 17+
- Maven 3.8+
- MySQL

### Installation & Setup
1. Clone the repository:
   ```sh
   git clone https://github.com/mAmineChniti/TravelShare-API.git
   cd TravelShare-API
   ```
2. Configure the application properties:
   ```properties
   # .env
   DB_USER=your_database_user
   DB_PASS=your_database_password
   JWT_SECRET=your_secret_key
   ```
3. Build and run the application:
   ```sh
   mvn clean package
   java -jar target/travelshare-api-0.0.1-SNAPSHOT.jar
   ```
4. Access the API at `http://localhost:8080`

## API Development
API endpoints are under development and will be documented in future updates.

## Contributing
1. Fork the repository
2. Create a new branch (`feature/your-feature`)
3. Commit your changes
4. Push to your branch
5. Open a Pull Request

## License
This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.
