# ToDo List Application

A modern and feature-rich task management application built with JavaFX. This application helps users organize their daily tasks, set priorities, and manage categories efficiently.

## Features

- **User Authentication**: Secure login and user management system
- **Task Management**: Create, edit, and delete tasks
- **Task Organization**:
  - My Day view for daily tasks
  - Important tasks section
  - Category-based organization
- **Task Details**:
  - Task name and description
  - Due dates
  - Priority levels
  - Status tracking
  - Comments system
- **Search Functionality**: Quick task search across all views
- **Modern UI**: Clean and intuitive interface with dark theme

## Technologies Used

- Java 21
- JavaFX
- Spring Security (for password encryption)
- JFoenix (for modern UI components)
- Maven (for dependency management)
- MySQL (for data persistence)

## Prerequisites

- Java Development Kit (JDK) 21 or later
- Maven 3.8.5 or later
- An IDE that supports Java and Maven (e.g., IntelliJ IDEA, Eclipse)

## Installation

1. Clone the repository:
   git clone https://github.com/Mohamed-Oday/miniprojet-todo.git

2. Navigate to the project directory:
   cd miniprojet-todo

3. Build the project using Maven:
   mvn clean install

4. Run the application:
   mvn javafx:run

## Project Structure

src/main/
├── java/
│   └── org/openjfx/miniprojet/
│       ├── controller/ # JavaFX controllers
│       ├── model/ # Data models
│       ├── dao/ # Data Access Objects
│       └── util/ # Database connection and updates
└── resources/
└── org/openjfx/miniprojet/
├── assets/
│   ├── fxml/ # FXML layout files
│   ├── images/ # Application images
│   └── styles/ # CSS style sheets
└── database/ # MySQL database

## Usage

1. **First Launch**:

   - Launch the application
   - Create a new account or login with existing credentials
   - Your session will be remembered for future launches

2. **Managing Tasks**:

   - Click the '+' button to add a new task
   - Click on any task to view or edit its details
   - Use checkboxes to mark tasks as complete
   - Add comments to tasks for additional context

3. **Organization**:
   - Use the left sidebar to navigate between different views
   - "My Day" section for daily planning
   - Mark important tasks with a star
   - Use the search feature to find specific tasks

## Languages

- Java (90.4%)
- CSS (9.6%)

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/NewFeature`)
3. Commit your changes (`git commit -m 'Add some NewFeature'`)
4. Push to the branch (`git push origin feature/NewFeature`)
5. Open a Pull Request

## Future Enhancements

- Task categories/labels
- Dark/Light theme toggle
- Export tasks to PDF/Excel
- Mobile companion app
- Cloud synchronization

## Author

- **Mohamed Oday**
  - GitHub: [Mohamed-Oday](https://github.com/Mohamed-Oday)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

Made with ❤️ by Mohamed Oday
