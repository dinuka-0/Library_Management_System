# Library Management System — AI-Assisted Programming Assignment

This repository contains two Java implementations of a Library Management System developed for the assignment **"Analyzing the Role of AI-Assisted Programming in Modern Software Development."**

The project compares two software development approaches while implementing the same system requirements and functionality.

- **Version A – Traditional Development:** Developed using conventional Java programming practices.
- **Version B – AI-Assisted Development:** Developed with AI assistance to improve productivity while maintaining software quality.

Both implementations provide core library management features such as book and member management, borrowing and returning books, due date tracking, overdue fine calculation, exception handling, and data persistence.

---

## Project Structure

```text
Library_Management_System/
├── version_a_traditional/
│   └── src/
│       └── LibraryManagementSystem.java
├── version_b_ai_assisted/
│   ├── src/
│   │   └── LibraryManagementSystemAIAssisted.java
│   └── library_data.json
├── ai_prompts.md
└── README.md
```

---

## Requirements

- Java Development Kit (JDK) 17 or later
- Any Java IDE (IntelliJ IDEA, Eclipse, NetBeans, or VS Code with Java Extension Pack)

---

## Running the Programs

### Version A – Traditional

```bash
cd version_a_traditional/src
javac LibraryManagementSystem.java
java LibraryManagementSystem
```

### Version B – AI-Assisted

```bash
cd version_b_ai_assisted/src
javac LibraryManagementSystemAIAssisted.java
java LibraryManagementSystemAIAssisted
```

> **Note:** Version B uses the `library_data.json` file to store and retrieve library data between program executions.

---

## Features

### Version A – Traditional Development

- Register books and members
- Borrow and return books
- Display available books and registered members
- Menu-driven console interface
- In-memory data management

### Version B – AI-Assisted Development

Includes all Version A features, plus:

- Persistent data storage using JSON
- Book search by title or author
- Due date management
- Automatic overdue fine calculation
- Custom exception handling
- Improved validation and error handling
- Enhanced code organization and maintainability

---

## Technologies Used

- Java
- Object-Oriented Programming (OOP)
- File Handling
- JSON Data Storage
- Exception Handling
- Collections Framework

---

## Purpose

This project was developed to evaluate the impact of AI-assisted programming on software development by comparing a traditionally developed Java application with an AI-assisted implementation. The comparison focuses on software quality, development efficiency, maintainability, and overall functionality while satisfying the same system requirements.

---

## Author

**Dinuka Pabasara**

Academic Project – AI-Assisted Programming Assignment