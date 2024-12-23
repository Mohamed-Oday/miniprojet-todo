# ToDo List Application

A modern and feature-rich task management application built with JavaFX. This application helps users organize their daily tasks, set priorities, and collaborate with other users.

## ✨ Features

- **User Authentication**
    - Secure login and user management system
    - Session persistence
    - Password encryption with Spring Security
    - Automatic login for returning users

- **Task Management**
    - Create, edit, and delete tasks
    - Comments system
    - Task status tracking (Started, Pending, Completed, Abandoned)
    - Automatic status updates based on dates
    - Task override protection

- **Task Organization**
    - My Day view for daily tasks
    - Important tasks section
    - Category-based organization
    - Custom labels and tags
    - Start date and due date scheduling
    - Task completion tracking

- **Collaboration Features**
    - Share categories with other users
    - Real-time task synchronization
    - Permission-based access control (Read/Write)
    - Collaborative task editing
    - Comment sharing across users
    - Task status synchronization

- **Smart Features**
    - Automatic task status updates
    - Dynamic task sorting and filtering
    - Multi-criteria search
    - Task activity tracking
    - Weekly completion charts
    - User statistics and analytics
    - Task completion rate tracking

- **Task Details**
    - Rich task information display
    - Priority levels (High, Medium, Low)
    - Status tracking with visual indicators
    - Comment threading
    - Task history
    - Due date monitoring
    - Category assignment

- **Notification System**
    - Task completion notifications
    - Status change alerts
    - Collaboration invites
    - Task updates notifications
    - Pending invites alerts

- **User Interface**
    - Modern dark theme
    - Intuitive navigation
    - Advanced filtering system
    - Task details panel
    - Category management
    - Visual statistics
    - Weekly activity charts

## 🛠️ Technologies Used

- **Backend**
    - Java 21
    - Spring Security
    - MySQL Database

- **Frontend**
    - JavaFX
    - JFoenix UI Components
    - CSS3
    - FXML for layout design

- **Build & Development**
    - Maven
    - Git
    - Scene Builder for UI design

## ⚙️ Prerequisites

Before you begin, ensure you have the following installed:

- Java Development Kit (JDK) 21 or later
- Maven 3.8.5 or later
- MySQL Server
- An IDE that supports Java and Maven (e.g., IntelliJ IDEA, Eclipse)

## 🚀 Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/Mohamed-Oday/miniprojet-todo.git
   ```

2. Navigate to the project directory:

   ```bash
   cd miniprojet-todo
   ```

3. Build the project using Maven:

   ```bash
   mvn clean install
   ```

4. Run the application:
   ```bash
   mvn javafx:run
   ```

## 📁 Project Structure

```bash
miniprojet-todo/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/openjfx/miniprojet/
│   │   │       ├── controller/
│   │   │       │   ├── AddCategoryController.java          # Category management
│   │   │       │   ├── AddCollaborationController.java     # Collaboration handling
│   │   │       │   ├── AddCommentController.java           # Comment handling
│   │   │       │   ├── AddTaskController.java              # Task handling
│   │   │       │   ├── AppController.java                  # Main application controller
│   │   │       │   ├── LandingPageController.java          # Landing interface controller
│   │   │       │   ├── LoginFormController.java            # Login handling
│   │   │       │   ├── NotificationSoundController.java    # Notification sound handling
│   │   │       │   └── RegisterFormController.java         # Sign up handling
│   │   │       ├── dao/
│   │   │       │   ├── CategoryDAO.java                    # Category database operations
│   │   │       │   ├── CollaborationDAO.java               # Collaboration database operations
│   │   │       │   ├── DataAccessException.java            # Database exception handling
│   │   │       │   ├── NotificationDAO.java                # Notification database operations
│   │   │       │   ├── TaskDAO.java                        # Task database operations
│   │   │       │   └── UserDAO.java                        # User database operations
│   │   │       ├── model/
│   │   │       │   ├── App.java                            # Main application class
│   │   │       │   ├── Notification.java                   # Notification entity
│   │   │       │   ├── Permission.java                     # User permission enum
│   │   │       │   ├── Status.java                         # Task status enum
│   │   │       │   ├── Task.java                           # Task interface
│   │   │       │   ├── TaskImpl.java                       # Task implementation
│   │   │       │   ├── TaskList.java                       # Abstract task list
│   │   │       │   └── TaskListImpl.java                   # Task list implementation
│   │   │       ├── util/
│   │   │       │   └── Database.java                       # Database utilities
│   │   │       └── module-info.java                        # Java module configuration
│   │   └── resources/
│   │       └── org/openjfx/miniprojet/
│   │           ├── assets/
│   │           │   ├── sounds/                             # Notification sounds
│   │           │   ├── fxml/
│   │           │   │   ├── AddCategoryForm.fxml            # Add category form
│   │           │   │   ├── AddCollaborationForm.fxml       # Add collaboration form
│   │           │   │   ├── AddCommentForm.fxml             # Add comment form
│   │           │   │   ├── AddTaskForm.fxml                # Add task form
│   │           │   │   ├── AppPage.fxml                    # Main application page
│   │           │   │   ├── LandingPage.fxml                # Landing page interface
│   │           │   │   ├── LoginForm.fxml                  # Login form
│   │           │   │   └── RegisterForm.fxml               # Sign up form
│   │           │   ├── images/                      # Application icons/images
│   │           │   └── styles/                      # CSS stylesheets
│   │           └── database/
│   │               └── database_queries.sql         # Database schema
│   └── test/                                        # Test files
├── pom.xml                                          # Maven configuration
└── README.md                                        # Project documentation
```

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

- Java (84.7%)
- CSS (15.3%)

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/NewFeature`)
3. Commit your changes (`git commit -m 'Add some NewFeature'`)
4. Push to the branch (`git push origin feature/NewFeature`)
5. Open a Pull Request

## Future Enhancements

- Task categories/labels (Done)
- Dark/Light theme toggle
- Export tasks to PDF/Excel
- Mobile companion app
- Cloud synchronization

## Author

- **Mohamed Oday**
    - GitHub: [Mohamed-Oday](https://github.com/Mohamed-Oday)

---

Made with ❤️ by Mohamed Oday
