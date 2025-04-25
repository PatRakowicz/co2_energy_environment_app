# CO₂ Emissions & Utility Data Management System

This project is a desktop JavaFX application designed to assist organizations in tracking and managing building-level utility 
data; electricity, water, sewage, and miscellaneous costs alongside optional gas usage. The goal is to centralize 
emissions-related data, simplify bulk uploads via CSV, and offer useful data visualization and report generation features.

## Key Features

- **Add Utility Records**: Users can add new entries for electricity, water, sewage, and other utilities.
- **CSV Import/Export**: Supports uploading utility data in bulk via CSV files and exporting templates.
- **Database Integration**: MySQL-based storage with auto-connect attempts via `.env`-like config file.
- **View Utility Trends**: Users can filter and visualize trends over time using line charts.
- **Update Records**: Modify utility data by month and building, with dynamic field disabling based on building sharing dates.
- **Modular Architecture**: Includes DAO pattern, MVC structure, and reusable query interfaces.

## Technologies Used

- Java 21
- JavaFX (UI framework) 17
- MySQL (Database)
- JDBC (Database connectivity)
- FXML (for layout design)
- Gradle (build tool)

## Project Structure Overview

- `model/` – JavaBeans like `Building`, `Utility`, `Gas`
- `controllers/` – JavaFX controllers for UI interaction
- `dao/` – Handles database logic and CSV parsing
- `fxml/` – UI layout files (not included in this repo version)
- `emissionsApp.java` – Main entry point for launching the application

## Requirements

- Java 21+
- MySQL Server running on `localhost:3306` (or login to a different server using the DataBase Button on app)
- Database: `wcuemissions` with relevant tables (Building, Utility, Gas)