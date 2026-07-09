/*
 * Library Management System - Version A (Traditional Approach)
 * Java version - converted from Python, same logic and same console output.
 */

import java.util.ArrayList;
import java.util.Scanner;

public class LibraryManagementSystem {

    // Global-style lists, same role as the Python "books" and "members" lists
    static ArrayList<Book> books = new ArrayList<>();
    static ArrayList<Member> members = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);

    // ---------- Book class ----------
    static class Book {
        String bookId;
        String title;
        String author;
        int copies;
        int borrowed;

        Book(String bookId, String title, String author, int copies) {
            this.bookId = bookId;
            this.title = title;
            this.author = author;
            this.copies = copies;
            this.borrowed = 0;
        }
    }

    // ---------- Member class ----------
    static class Member {
        String memberId;
        String name;
        ArrayList<String> borrowedBooks;

        Member(String memberId, String name) {
            this.memberId = memberId;
            this.name = name;
            this.borrowedBooks = new ArrayList<>();
        }
    }

    // ---------- Core functions ----------

    static void addBook(String bookId, String title, String author, int copies) {
        for (Book b : books) {
            if (b.bookId.equals(bookId)) {
                System.out.println("Book ID already exists!");
                return;
            }
        }
        Book book = new Book(bookId, title, author, copies);
        books.add(book);
        System.out.println("Book added: " + title);
    }

    static void addMember(String memberId, String name) {
        for (Member m : members) {
            if (m.memberId.equals(memberId)) {
                System.out.println("Member ID already exists!");
                return;
            }
        }
        Member member = new Member(memberId, name);
        members.add(member);
        System.out.println("Member added: " + name);
    }

    static Book findBook(String bookId) {
        for (Book b : books) {
            if (b.bookId.equals(bookId)) {
                return b;
            }
        }
        return null;
    }

    static Member findMember(String memberId) {
        for (Member m : members) {
            if (m.memberId.equals(memberId)) {
                return m;
            }
        }
        return null;
    }

    static void borrowBook(String memberId, String bookId) {
        Member member = findMember(memberId);
        Book book = findBook(bookId);

        if (member == null) {
            System.out.println("Member not found");
            return;
        }
        if (book == null) {
            System.out.println("Book not found");
            return;
        }
        if (book.borrowed >= book.copies) {
            System.out.println("No copies available");
            return;
        }

        book.borrowed = book.borrowed + 1;
        member.borrowedBooks.add(bookId);
        System.out.println(member.name + " borrowed " + book.title);
    }

    static void returnBook(String memberId, String bookId) {
        Member member = findMember(memberId);
        Book book = findBook(bookId);

        if (member == null || book == null) {
            System.out.println("Invalid member or book");
            return;
        }

        if (member.borrowedBooks.contains(bookId)) {
            member.borrowedBooks.remove(bookId);
            book.borrowed = book.borrowed - 1;
            System.out.println(member.name + " returned " + book.title);
        } else {
            System.out.println("This member did not borrow this book");
        }
    }

    static void listBooks() {
        System.out.println("\n--- Book List ---");
        for (Book b : books) {
            int available = b.copies - b.borrowed;
            System.out.println(b.bookId + " | " + b.title + " | " + b.author
                    + " | Available: " + available + " / " + b.copies);
        }
    }

    static void listMembers() {
        System.out.println("\n--- Member List ---");
        for (Member m : members) {
            System.out.println(m.memberId + " | " + m.name + " | Borrowed: " + m.borrowedBooks);
        }
    }

    // ---------- Menu ----------

    static void menu() {
        while (true) {
            System.out.println("\n===== Library Management System (Traditional) =====");
            System.out.println("1. Add Book");
            System.out.println("2. Add Member");
            System.out.println("3. Borrow Book");
            System.out.println("4. Return Book");
            System.out.println("5. List Books");
            System.out.println("6. List Members");
            System.out.println("7. Exit");
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Book ID: ");
                    String bid1 = scanner.nextLine();
                    System.out.print("Title: ");
                    String title = scanner.nextLine();
                    System.out.print("Author: ");
                    String author = scanner.nextLine();
                    System.out.print("Copies: ");
                    int copies = Integer.parseInt(scanner.nextLine());
                    addBook(bid1, title, author, copies);
                    break;

                case "2":
                    System.out.print("Member ID: ");
                    String mid1 = scanner.nextLine();
                    System.out.print("Name: ");
                    String name = scanner.nextLine();
                    addMember(mid1, name);
                    break;

                case "3":
                    System.out.print("Member ID: ");
                    String mid2 = scanner.nextLine();
                    System.out.print("Book ID: ");
                    String bid2 = scanner.nextLine();
                    borrowBook(mid2, bid2);
                    break;

                case "4":
                    System.out.print("Member ID: ");
                    String mid3 = scanner.nextLine();
                    System.out.print("Book ID: ");
                    String bid3 = scanner.nextLine();
                    returnBook(mid3, bid3);
                    break;

                case "5":
                    listBooks();
                    break;

                case "6":
                    listMembers();
                    break;

                case "7":
                    System.out.println("Goodbye!");
                    return;

                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    public static void main(String[] args) {
        menu();
    }
}
