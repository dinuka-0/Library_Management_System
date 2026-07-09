/*
 * Library Management System - Version B (AI-Assisted Approach)
 * Java version - converted from Python, same logic and same console output.
 *
 * Mirrors the original Python design:
 *  - Book / Loan / Member "data objects" (Python used @dataclass, Java uses
 *    plain classes with fields)
 *  - A Library class that encapsulates all operations and JSON persistence
 *  - A custom exception hierarchy (LibraryError and subclasses)
 *  - Simple structured logging similar to Python's logging module output
 *  - Due dates (14-day loan period) and overdue fine calculation
 */

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class LibraryManagementSystemAIAssisted {

    static final Path DATA_FILE = Paths.get("library_data.json");
    static final int LOAN_PERIOD_DAYS = 14;
    static final int FINE_PER_DAY = 10; // currency units

    static final Logger logger = new Logger("LibrarySystem");

    // ---------- Custom exception hierarchy ----------

    static class LibraryError extends Exception {
        LibraryError(String message) {
            super(message);
        }
    }

    static class BookNotFoundError extends LibraryError {
        BookNotFoundError(String message) {
            super(message);
        }
    }

    static class MemberNotFoundError extends LibraryError {
        MemberNotFoundError(String message) {
            super(message);
        }
    }

    static class NoCopiesAvailableError extends LibraryError {
        NoCopiesAvailableError(String message) {
            super(message);
        }
    }

    static class DuplicateIDError extends LibraryError {
        DuplicateIDError(String message) {
            super(message);
        }
    }

    // ---------- Data objects (Python used @dataclass) ----------

    static class Book {
        String bookId;
        String title;
        String author;
        int copies;
        int borrowed;

        Book(String bookId, String title, String author, int copies, int borrowed) {
            this.bookId = bookId;
            this.title = title;
            this.author = author;
            this.copies = copies;
            this.borrowed = borrowed;
        }

        int available() {
            return copies - borrowed;
        }
    }

    static class Loan {
        String bookId;
        String dueDate; // ISO format string (yyyy-MM-dd), same as Python

        Loan(String bookId, String dueDate) {
            this.bookId = bookId;
            this.dueDate = dueDate;
        }
    }

    static class Member {
        String memberId;
        String name;
        List<Loan> loans = new ArrayList<>();

        Member(String memberId, String name) {
            this.memberId = memberId;
            this.name = name;
        }
    }

    // ---------- Minimal logger, mimicking Python's default logging format ----------

    static class Logger {
        private final String name;
        private final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");

        Logger(String name) {
            this.name = name;
        }

        void info(String message) {
            log("INFO", message);
        }

        void warning(String message) {
            log("WARNING", message);
        }

        void error(String message) {
            log("ERROR", message);
        }

        private void log(String level, String message) {
            System.out.println(timeFormat.format(new Date()) + " [" + level + "] " + message);
        }
    }

    // ---------- Library: core operations + JSON persistence ----------

    static class Library {
        Map<String, Book> books = new LinkedHashMap<>();
        Map<String, Member> members = new LinkedHashMap<>();

        Library() {
            load();
        }

        // ---------- Persistence ----------

        @SuppressWarnings("unchecked")
        void load() {
            if (!Files.exists(DATA_FILE)) {
                logger.info("No existing data file found. Starting fresh.");
                return;
            }
            try {
                String content = new String(Files.readAllBytes(DATA_FILE), StandardCharsets.UTF_8);
                Object parsed = Json.parse(content);
                if (!(parsed instanceof Map)) {
                    throw new IllegalArgumentException("Root JSON element must be an object");
                }
                Map<String, Object> data = (Map<String, Object>) parsed;

                books = new LinkedHashMap<>();
                Object booksObj = data.get("books");
                if (booksObj instanceof Map) {
                    Map<String, Object> booksMap = (Map<String, Object>) booksObj;
                    for (Map.Entry<String, Object> entry : booksMap.entrySet()) {
                        Map<String, Object> b = (Map<String, Object>) entry.getValue();
                        Book book = new Book(
                                (String) b.get("book_id"),
                                (String) b.get("title"),
                                (String) b.get("author"),
                                ((Number) b.get("copies")).intValue(),
                                b.containsKey("borrowed") ? ((Number) b.get("borrowed")).intValue() : 0
                        );
                        books.put(entry.getKey(), book);
                    }
                }

                members = new LinkedHashMap<>();
                Object membersObj = data.get("members");
                if (membersObj instanceof Map) {
                    Map<String, Object> membersMap = (Map<String, Object>) membersObj;
                    for (Map.Entry<String, Object> entry : membersMap.entrySet()) {
                        Map<String, Object> m = (Map<String, Object>) entry.getValue();
                        Member member = new Member(entry.getKey(), (String) m.get("name"));
                        Object loansObj = m.get("loans");
                        if (loansObj instanceof List) {
                            for (Object lnObj : (List<Object>) loansObj) {
                                Map<String, Object> ln = (Map<String, Object>) lnObj;
                                member.loans.add(new Loan((String) ln.get("book_id"), (String) ln.get("due_date")));
                            }
                        }
                        members.put(entry.getKey(), member);
                    }
                }
                logger.info("Data loaded successfully.");
            } catch (IOException | RuntimeException exc) {
                logger.error("Failed to load data file: " + exc.getMessage());
            }
        }

        void save() {
            StringBuilder sb = new StringBuilder();
            sb.append("{\n");

            sb.append("  \"books\": {\n");
            int bi = 0;
            for (Map.Entry<String, Book> entry : books.entrySet()) {
                Book b = entry.getValue();
                sb.append("    ").append(Json.quote(entry.getKey())).append(": {\n");
                sb.append("      \"book_id\": ").append(Json.quote(b.bookId)).append(",\n");
                sb.append("      \"title\": ").append(Json.quote(b.title)).append(",\n");
                sb.append("      \"author\": ").append(Json.quote(b.author)).append(",\n");
                sb.append("      \"copies\": ").append(b.copies).append(",\n");
                sb.append("      \"borrowed\": ").append(b.borrowed).append("\n");
                sb.append("    }");
                sb.append(++bi < books.size() ? ",\n" : "\n");
            }
            sb.append("  },\n");

            sb.append("  \"members\": {\n");
            int mi = 0;
            for (Map.Entry<String, Member> entry : members.entrySet()) {
                Member m = entry.getValue();
                sb.append("    ").append(Json.quote(entry.getKey())).append(": {\n");
                sb.append("      \"name\": ").append(Json.quote(m.name)).append(",\n");
                sb.append("      \"loans\": [");
                if (m.loans.isEmpty()) {
                    sb.append("]\n");
                } else {
                    sb.append("\n");
                    for (int i = 0; i < m.loans.size(); i++) {
                        Loan ln = m.loans.get(i);
                        sb.append("        {\"book_id\": ").append(Json.quote(ln.bookId))
                                .append(", \"due_date\": ").append(Json.quote(ln.dueDate)).append("}");
                        sb.append(i < m.loans.size() - 1 ? ",\n" : "\n");
                    }
                    sb.append("      ]\n");
                }
                sb.append("    }");
                sb.append(++mi < members.size() ? ",\n" : "\n");
            }
            sb.append("  }\n");
            sb.append("}");

            try {
                Files.write(DATA_FILE, sb.toString().getBytes(StandardCharsets.UTF_8));
                logger.info("Data saved successfully.");
            } catch (IOException exc) {
                logger.error("Failed to save data: " + exc.getMessage());
            }
        }

        // ---------- Book / Member management ----------

        Book addBook(String bookId, String title, String author, int copies) throws DuplicateIDError {
            if (books.containsKey(bookId)) {
                throw new DuplicateIDError("Book ID '" + bookId + "' already exists.");
            }
            if (copies < 1) {
                throw new IllegalArgumentException("Copies must be at least 1.");
            }
            Book book = new Book(bookId, title, author, copies, 0);
            books.put(bookId, book);
            logger.info("Book added: " + title);
            save();
            return book;
        }

        Member addMember(String memberId, String name) throws DuplicateIDError {
            if (members.containsKey(memberId)) {
                throw new DuplicateIDError("Member ID '" + memberId + "' already exists.");
            }
            Member member = new Member(memberId, name);
            members.put(memberId, member);
            logger.info("Member added: " + name);
            save();
            return member;
        }

        // ---------- Borrowing / returning ----------

        Loan borrowBook(String memberId, String bookId) throws LibraryError {
            Member member = getMember(memberId);
            Book book = getBook(bookId);
            if (book.available() <= 0) {
                throw new NoCopiesAvailableError("No copies of '" + book.title + "' available.");
            }
            String due = LocalDate.now().plusDays(LOAN_PERIOD_DAYS).toString();
            Loan loan = new Loan(bookId, due);
            member.loans.add(loan);
            book.borrowed += 1;
            logger.info(member.name + " borrowed '" + book.title + "' (due " + due + ")");
            save();
            return loan;
        }

        /** Returns the fine amount owed (0 if returned on time). */
        int returnBook(String memberId, String bookId) throws LibraryError {
            Member member = getMember(memberId);
            Book book = getBook(bookId);
            Loan loan = null;
            for (Loan ln : member.loans) {
                if (ln.bookId.equals(bookId)) {
                    loan = ln;
                    break;
                }
            }
            if (loan == null) {
                throw new LibraryError(member.name + " has not borrowed '" + book.title + "'.");
            }
            member.loans.remove(loan);
            book.borrowed = Math.max(0, book.borrowed - 1);
            int fine = calculateFine(loan.dueDate);
            logger.info(member.name + " returned '" + book.title + "' (fine: " + fine + ")");
            save();
            return fine;
        }

        static int calculateFine(String dueDateIso) {
            try {
                LocalDate due = LocalDate.parse(dueDateIso);
                long overdueDays = LocalDate.now().toEpochDay() - due.toEpochDay();
                return (int) Math.max(0, overdueDays) * FINE_PER_DAY;
            } catch (DateTimeParseException exc) {
                return 0;
            }
        }

        // ---------- Search ----------

        List<Book> searchBooks(String keyword) {
            String k = keyword.toLowerCase();
            List<Book> results = new ArrayList<>();
            for (Book b : books.values()) {
                if (b.title.toLowerCase().contains(k) || b.author.toLowerCase().contains(k)) {
                    results.add(b);
                }
            }
            return results;
        }

        // ---------- Helpers ----------

        Book getBook(String bookId) throws BookNotFoundError {
            Book book = books.get(bookId);
            if (book == null) {
                throw new BookNotFoundError("Book ID '" + bookId + "' not found.");
            }
            return book;
        }

        Member getMember(String memberId) throws MemberNotFoundError {
            Member member = members.get(memberId);
            if (member == null) {
                throw new MemberNotFoundError("Member ID '" + memberId + "' not found.");
            }
            return member;
        }

        void listBooks() {
            System.out.println("\n--- Book List ---");
            if (books.isEmpty()) {
                System.out.println("(no books registered)");
            }
            for (Book b : books.values()) {
                System.out.println(b.bookId + " | " + b.title + " | " + b.author
                        + " | Available: " + b.available() + "/" + b.copies);
            }
        }

        void listMembers() {
            System.out.println("\n--- Member List ---");
            if (members.isEmpty()) {
                System.out.println("(no members registered)");
            }
            for (Member m : members.values()) {
                List<String> active = new ArrayList<>();
                for (Loan ln : m.loans) {
                    active.add(ln.bookId);
                }
                System.out.println(m.memberId + " | " + m.name + " | Active loans: " + active);
            }
        }
    }

    // ---------- Menu ----------

    static void menu() {
        Library library = new Library();
        Scanner scanner = new Scanner(System.in);

        String[][] actions = {
                {"1", "Add Book"},
                {"2", "Add Member"},
                {"3", "Borrow Book"},
                {"4", "Return Book"},
                {"5", "List Books"},
                {"6", "List Members"},
                {"7", "Search Books"},
                {"8", "Exit"},
        };

        while (true) {
            System.out.println("\n===== Library Management System (AI-Assisted) =====");
            for (String[] action : actions) {
                System.out.println(action[0] + ". " + action[1]);
            }
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1": {
                        System.out.print("Book ID: ");
                        String bid = scanner.nextLine().trim();
                        System.out.print("Title: ");
                        String title = scanner.nextLine().trim();
                        System.out.print("Author: ");
                        String author = scanner.nextLine().trim();
                        System.out.print("Copies: ");
                        int copies = Integer.parseInt(scanner.nextLine().trim());
                        library.addBook(bid, title, author, copies);
                        break;
                    }
                    case "2": {
                        System.out.print("Member ID: ");
                        String mid = scanner.nextLine().trim();
                        System.out.print("Name: ");
                        String name = scanner.nextLine().trim();
                        library.addMember(mid, name);
                        break;
                    }
                    case "3": {
                        System.out.print("Member ID: ");
                        String mid = scanner.nextLine().trim();
                        System.out.print("Book ID: ");
                        String bid = scanner.nextLine().trim();
                        Loan loan = library.borrowBook(mid, bid);
                        System.out.println("Borrowed. Due date: " + loan.dueDate);
                        break;
                    }
                    case "4": {
                        System.out.print("Member ID: ");
                        String mid = scanner.nextLine().trim();
                        System.out.print("Book ID: ");
                        String bid = scanner.nextLine().trim();
                        int fine = library.returnBook(mid, bid);
                        if (fine > 0) {
                            System.out.println("Returned late. Fine due: " + fine);
                        } else {
                            System.out.println("Returned on time. No fine.");
                        }
                        break;
                    }
                    case "5":
                        library.listBooks();
                        break;
                    case "6":
                        library.listMembers();
                        break;
                    case "7": {
                        System.out.print("Search keyword: ");
                        String keyword = scanner.nextLine().trim();
                        List<Book> results = library.searchBooks(keyword);
                        System.out.println("\n--- Search Results for '" + keyword + "' ---");
                        for (Book b : results) {
                            System.out.println(b.bookId + " | " + b.title + " | " + b.author);
                        }
                        if (results.isEmpty()) {
                            System.out.println("No matches found.");
                        }
                        break;
                    }
                    case "8":
                        System.out.println("Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid choice, please try again.");
                }
            } catch (LibraryError exc) {
                logger.warning("Operation failed: " + exc.getMessage());
                System.out.println("Error: " + exc.getMessage());
            } catch (NumberFormatException exc) {
                logger.warning("Invalid input: " + exc.getMessage());
                System.out.println("Invalid input: " + exc.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        menu();
    }

    // ---------- Minimal JSON parser/writer (no external dependencies) ----------
    // Handles the fixed schema used by this program: nested objects, arrays,
    // strings, and numbers - enough to read/write library_data.json.

    static class Json {

        static Object parse(String text) {
            Parser p = new Parser(text);
            p.skipWhitespace();
            Object value = p.parseValue();
            p.skipWhitespace();
            return value;
        }

        static String quote(String s) {
            StringBuilder sb = new StringBuilder();
            sb.append('"');
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                switch (c) {
                    case '"': sb.append("\\\""); break;
                    case '\\': sb.append("\\\\"); break;
                    case '\n': sb.append("\\n"); break;
                    case '\r': sb.append("\\r"); break;
                    case '\t': sb.append("\\t"); break;
                    default:
                        if (c < 0x20) {
                            sb.append(String.format("\\u%04x", (int) c));
                        } else {
                            sb.append(c);
                        }
                }
            }
            sb.append('"');
            return sb.toString();
        }

        private static class Parser {
            private final String text;
            private int pos;

            Parser(String text) {
                this.text = text;
                this.pos = 0;
            }

            void skipWhitespace() {
                while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
                    pos++;
                }
            }

            Object parseValue() {
                skipWhitespace();
                char c = text.charAt(pos);
                if (c == '{') return parseObject();
                if (c == '[') return parseArray();
                if (c == '"') return parseString();
                if (c == 't' || c == 'f') return parseBoolean();
                if (c == 'n') { pos += 4; return null; }
                return parseNumber();
            }

            Map<String, Object> parseObject() {
                Map<String, Object> map = new LinkedHashMap<>();
                pos++; // {
                skipWhitespace();
                if (text.charAt(pos) == '}') {
                    pos++;
                    return map;
                }
                while (true) {
                    skipWhitespace();
                    String key = parseString();
                    skipWhitespace();
                    pos++; // :
                    Object value = parseValue();
                    map.put(key, value);
                    skipWhitespace();
                    char c = text.charAt(pos);
                    if (c == ',') {
                        pos++;
                    } else if (c == '}') {
                        pos++;
                        break;
                    }
                }
                return map;
            }

            List<Object> parseArray() {
                List<Object> list = new ArrayList<>();
                pos++; // [
                skipWhitespace();
                if (text.charAt(pos) == ']') {
                    pos++;
                    return list;
                }
                while (true) {
                    Object value = parseValue();
                    list.add(value);
                    skipWhitespace();
                    char c = text.charAt(pos);
                    if (c == ',') {
                        pos++;
                    } else if (c == ']') {
                        pos++;
                        break;
                    }
                }
                return list;
            }

            String parseString() {
                pos++; // opening quote
                StringBuilder sb = new StringBuilder();
                while (text.charAt(pos) != '"') {
                    char c = text.charAt(pos);
                    if (c == '\\') {
                        pos++;
                        char esc = text.charAt(pos);
                        switch (esc) {
                            case 'n': sb.append('\n'); break;
                            case 'r': sb.append('\r'); break;
                            case 't': sb.append('\t'); break;
                            case '"': sb.append('"'); break;
                            case '\\': sb.append('\\'); break;
                            case '/': sb.append('/'); break;
                            case 'u':
                                String hex = text.substring(pos + 1, pos + 5);
                                sb.append((char) Integer.parseInt(hex, 16));
                                pos += 4;
                                break;
                            default: sb.append(esc);
                        }
                    } else {
                        sb.append(c);
                    }
                    pos++;
                }
                pos++; // closing quote
                return sb.toString();
            }

            Boolean parseBoolean() {
                if (text.startsWith("true", pos)) {
                    pos += 4;
                    return Boolean.TRUE;
                } else {
                    pos += 5;
                    return Boolean.FALSE;
                }
            }

            Number parseNumber() {
                int start = pos;
                while (pos < text.length() && "-+.eE0123456789".indexOf(text.charAt(pos)) >= 0) {
                    pos++;
                }
                String numStr = text.substring(start, pos);
                if (numStr.contains(".") || numStr.contains("e") || numStr.contains("E")) {
                    return Double.parseDouble(numStr);
                }
                return Long.parseLong(numStr);
            }
        }
    }
}
