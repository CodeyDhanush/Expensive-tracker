#  Expense Tracker

A **JavaFX desktop application** for managing personal expenses with charts and SQLite database integration.  
This project is designed to provide an intuitive way to track income and expenses, analyze spending patterns, and visualize financial data.

---

##  Features

-  **Expense Management**  
  - Add, edit, and delete expenses by category.  
  - Store data locally in an SQLite database.  

-  **Data Visualization**  
  - Interactive charts and graphs using **JFreeChart**.  
  - Monthly summaries with category-wise breakdown.  

-  **User Interface**  
  - Built with **JavaFX + FXML**.  
  - Responsive design with clean layout.  

-  **Database**  
  - SQLite integration for persistence.  
  - Automatic table creation on first run.  

---

##  Tech Stack

- **Language**: Java 21  
- **UI Framework**: JavaFX 21.0.3  
- **Database**: SQLite (JDBC Driver)  
- **Charts**: JFreeChart 1.5.4  
- **Build Tool**: Maven  

---

## 📂 Project Structure

expense-tracker/
├── src/main/java/com/example/expensetracker/
│ ├── App.java # Main entry point
│ ├── controllers/ # JavaFX controllers
│ ├── models/ # Data models
│ ├── database/ # SQLite helper classes
│ └── utils/ # Utility classes
├── src/main/resources/com/example/expensetracker/
│ ├── MainView.fxml # UI layout
│ ├── styles.css # Stylesheet
│ └── icons/ # App icons
├── pom.xml # Maven configuration
└── README.md # Project documentation

