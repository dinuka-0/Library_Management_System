# AI Prompts Used During Development (Version B)

The following prompts were used with an AI coding assistant (ChatGPT) while
building Version B of the Library Management System. Each AI suggestion was
reviewed, tested, and adapted manually before being included in the final
code.

1. **Prompt:** "Suggest a class structure using Python dataclasses for a
   library system with Book, Member and Loan entities, including due dates."
   **Use:** Formed the basis of the `Book`, `Member`, and `Loan` dataclasses.

2. **Prompt:** "How can I add JSON-based persistence to a Python class so
   that data survives between program runs, with proper error handling for
   missing or corrupted files?"
   **Use:** Guided the `load()` and `save()` methods in the `Library` class.

3. **Prompt:** "Suggest a custom exception hierarchy for a library
   management system to represent errors such as book not found, member not
   found, and no copies available."
   **Use:** Basis for `LibraryError` and its subclasses.

4. **Prompt:** "Write a function to calculate an overdue fine given a due
   date stored as an ISO date string, charging a fixed amount per day late."
   **Use:** Adapted into `_calculate_fine()`.

5. **Prompt:** "Review this borrow_book function for edge cases I might be
   missing." (pasted an early draft)
   **Use:** AI flagged the missing check for members/books that do not exist
   and the case of zero available copies, which were then added manually.

All generated snippets were manually tested (see `screenshots/` in the
report) and adjusted for naming conventions and consistency with the rest of
the codebase. No code was copied into the final submission without being
read, understood, and verified by the student.
