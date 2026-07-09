# Library Management System — AI-Assisted Programming Assignment (Java Version)

This repository contains two Java implementations of a Library Management
System, converted from the original Python versions for the "Analyzing the
Role of AI-Assisted Programming in Modern Software Development" assignment.
The concepts, structure, and console output of each version are unchanged
from the Python originals — only the language is different.

## Structure

```
repo/
├── version_a_traditional/
│   └── src/
│       └── LibraryManagementSystem.java        # Written manually, no AI assistance
├── version_b_ai_assisted/
│   ├── src/
│   │   └── LibraryManagementSystemAIAssisted.java  # AI-assisted (see ai_prompts.md)
│   └── library_data.json                        # Sample persisted data
├── ai_prompts.md               # Prompts used with the AI tool (original Python build)
└── README.md
```

## Requirements

Java 17 or later (JDK, not just JRE, since you need `javac` to compile).

## Running the programs

```bash
# Version A
cd version_a_traditional/src
javac LibraryManagementSystem.java
java LibraryManagementSystem

# Version B
cd version_b_ai_assisted/src
javac LibraryManagementSystemAIAssisted.java
java LibraryManagementSystemAIAssisted
```

Version B reads/writes `library_data.json` in the current working directory,
so run it from `version_b_ai_assisted/src` (or copy `library_data.json` next
to wherever you run it from) if you want to keep using the sample data.

## Features

**Version A (Traditional):**
- Add books and members
- Borrow / return books
- List books and members
- In-memory storage only (data is lost on exit)

**Version B (AI-Assisted):**
- All Version A features, plus:
- JSON persistence between runs (hand-written JSON parser/writer, since no
  external libraries are used — Java has no built-in JSON support the way
  Python's `json` module provides)
- Search books by title/author
- Due dates and overdue fine calculation
- Custom exception hierarchy (`LibraryError` and subclasses) and structured
  logging (mirrors Python's `logging` module output format)
- Plain classes with typed fields in place of Python's `@dataclass`

## Notes on the Python → Java conversion

- Python's `dict` keyed by ID → Java `LinkedHashMap<String, ...>` (keeps
  insertion order, same as Python 3.7+ dicts).
- Python's `dataclasses` (`Book`, `Loan`, `Member`) → plain Java classes with
  public fields and a constructor.
- Python's custom exception hierarchy → Java checked exceptions extending a
  common `LibraryError` base class.
- Python's `logging` module → a small `Logger` helper class that prints
  timestamped `[LEVEL] message` lines in the same format.
- Python's `json.load` / `json.dump` → a minimal hand-rolled JSON
  parser/writer scoped to this program's fixed schema (no external
  dependency is available in this environment).
- `date.today() + timedelta(days=14)` → `LocalDate.now().plusDays(14)`.

See the accompanying report for a full comparison and critical analysis.
