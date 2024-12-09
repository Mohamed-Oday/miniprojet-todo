# ToDo List Application

A modern and feature-rich task management application built with JavaFX. This application helps users organize their daily tasks, set priorities, and manage categories efficiently.

## ✨ Features

- **User Authentication**

  - Secure login and user management system
  - Session persistence
  - Password encryption with Spring Security

- **Task Management**

  - Create, edit, and delete tasks
  - Rich text descriptions
  - Comments system

- **Task Organization**

  - My Day view for daily tasks
  - Important tasks section
  - Category-based organization
  - Custom labels and tags

- **Task Details**

  - Task name and description
  - Due dates
  - Priority levels (High, Medium, Low)
  - Status tracking (Started, Completed, Abandoned)
  - Comment System

- **User Interface**
  - Modern dark theme
  - Intuitive navigation
  - Quick search functionality
  - Task filtering and sorting

## 🛠️ Technologies Used

- **Backend**
  - Java 21
  - Spring Security
  - MySQL Database
- **Frontend**
  - JavaFX
  - JFoenix UI Components
  - CSS3
- **Build Tools**
  - Maven
  - Git

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

miniprojet-todo/
├── .mvn/
│ └── wrapper/
│ ├── maven-wrapper.jar
│ └── maven-wrapper.properties
├── .idea/
│ ├── inspectionProfiles/
│ │ └── Project_Default.xml
│ ├── .gitignore
│ ├── compiler.xml
│ ├── encodings.xml
│ ├── misc.xml
│ ├── uiDesigner.xml
│ └── vcs.xml
├── src/
│ ├── main/
│ │ ├── java/
│ │ │ ├── org/openjfx/miniprojet/
│ │ │ │ ├── controller/
│ │ │ │ │ ├── AddCategoryController.java
│ │ │ │ │ ├── addTaskController.java
│ │ │ │ │ ├── Controller.java
│ │ │ │ │ └── EntryPageController.java
│ │ │ │ ├── model/
│ │ │ │ │ └── App.java
│ │ │ │ └── util/
│ │ │ │ └── Database.java
│ │ │ └── module-info.java
│ │ └── resources/
│ │ └── org/openjfx/miniprojet/
│ │ ├── assets/
│ │ │ ├── fxml/
│ │ │ │ ├── Main.fxml
│ │ │ │ ├── EntryPage.fxml
│ │ │ │ ├── addTask.fxml
│ │ │ │ └── addCategory.fxml
│ │ │ ├── images/
│ │ │ │ ├── clipboard.png
│ │ │ │ ├── category.png
│ │ │ │ └── AddNotes-pana-2x.png
│ │ │ └── styles/
│ │ │ └── style.css
│ │ └── database/
│ │ └── database_queries.sql
│ └── test/
│ └── java/
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md

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

- Java (81.9%)
- CSS (18.1%)

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
