package utils;

import models.Book;
import models.Loan;
import models.Member;
import models.User;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utility class for formatting data as tables for JOptionPane display
 */
public class TableFormatter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Format list of books as a table string
     */
    public static String formatBooksTable(List<Book> books) {
        if (books == null || books.isEmpty()) {
            return "No books found.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════════════════╗\n");
        sb.append("║                           BOOKS CATALOG                                ║\n");
        sb.append("╠════════════════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ %-18s %-25s %-20s %-6s ║\n", "ISBN", "Title", "Author", "Stock"));
        sb.append("╠════════════════════════════════════════════════════════════════════════╣\n");

        for (Book book : books) {
            String status = book.getIsActive() ? "✓" : "✗";
            String title = truncate(book.getTitle(), 25);
            String author = truncate(book.getAuthor(), 20);
            String stock = String.format("%d/%d", book.getAvailableCopies(), book.getTotalCopies());

            sb.append(String.format("║ %s %-18s %-25s %-20s %-6s ║\n",
                    status, truncate(book.getIsbn(), 18), title, author, stock));
        }

        sb.append("╚════════════════════════════════════════════════════════════════════════╝\n");
        sb.append(String.format("Total: %d books", books.size()));

        return sb.toString();
    }

    /**
     * Format list of members as a table string
     */
    public static String formatMembersTable(List<Member> members) {
        if (members == null || members.isEmpty()) {
            return "No members found.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════════════════╗\n");
        sb.append("║                            MEMBERS LIST                                ║\n");
        sb.append("╠════════════════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ %-4s %-25s %-30s %-8s ║\n", "ID", "Name", "Email", "Status"));
        sb.append("╠════════════════════════════════════════════════════════════════════════╣\n");

        for (Member member : members) {
            String status = member.getIsActive() ? "[ACTIVE]" : "[INACTIVE]";
            String name = truncate(member.getName(), 25);
            String email = truncate(member.getEmail(), 30);

            sb.append(String.format("║ %-4d %-25s %-30s %-8s ║\n",
                    member.getIdMember(), name, email, status));
        }

        sb.append("╚════════════════════════════════════════════════════════════════════════╝\n");
        sb.append(String.format("Total: %d members", members.size()));

        return sb.toString();
    }

    /**
     * Format list of users as a table string
     */
    public static String formatUsersTable(List<User> users) {
        if (users == null || users.isEmpty()) {
            return "No users found.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════════╗\n");
        sb.append("║                        USERS LIST                              ║\n");
        sb.append("╠════════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ %-4s %-20s %-12s %-10s %-10s ║\n", "ID", "Username", "Role", "Status", "Created"));
        sb.append("╠════════════════════════════════════════════════════════════════╣\n");

        for (User user : users) {
            String createdAt = user.getCreatedAt().format(DATETIME_FORMATTER);

            sb.append(String.format("║ %-4d %-20s %-12s %-10s %-10s ║\n",
                    user.getIdUser(),
                    truncate(user.getUsername(), 20),
                    user.getRole().name(),
                    user.getStatus().name(),
                    createdAt));
        }

        sb.append("╚════════════════════════════════════════════════════════════════╝\n");
        sb.append(String.format("Total: %d users", users.size()));

        return sb.toString();
    }

    /**
     * Format list of loans as a table string
     */
    public static String formatLoansTable(List<Loan> loans) {
        if (loans == null || loans.isEmpty()) {
            return "No loans found.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("╔═══════════════════════════════════════════════════════════════════════════╗\n");
        sb.append("║                              LOANS LIST                                   ║\n");
        sb.append("╠═══════════════════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ %-4s %-18s %-6s %-12s %-12s %-10s %-8s ║\n",
                "ID", "ISBN", "Member", "Loan Date", "Due Date", "Status", "Fine"));
        sb.append("╠═══════════════════════════════════════════════════════════════════════════╣\n");

        for (Loan loan : loans) {
            String loanDate = loan.getLoanDate().format(DATE_FORMATTER);
            String dueDate = loan.getDueDate().format(DATE_FORMATTER);
            String fine = String.format("$%.2f", loan.getFineAmount());
            String status = loan.getStatus().name();

            // Mark overdue loans
            if (loan.isOverdue()) {
                status += " ⚠";
            }

            sb.append(String.format("║ %-4d %-18s %-6d %-12s %-12s %-10s %-8s ║\n",
                    loan.getIdLoan(),
                    truncate(loan.getIsbn(), 18),
                    loan.getIdMember(),
                    loanDate,
                    dueDate,
                    status,
                    fine));
        }

        sb.append("╚═══════════════════════════════════════════════════════════════════════════╝\n");
        sb.append(String.format("Total: %d loans", loans.size()));

        return sb.toString();
    }

    /**
     * Format single book details
     */
    public static String formatBookDetails(Book book) {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║                    BOOK DETAILS                            ║\n");
        sb.append("╠════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ ISBN:             %-40s ║\n", book.getIsbn()));
        sb.append(String.format("║ Title:            %-40s ║\n", truncate(book.getTitle(), 40)));
        sb.append(String.format("║ Author:           %-40s ║\n", truncate(book.getAuthor(), 40)));
        sb.append(String.format("║ Category:         %-40s ║\n", truncate(book.getCategory(), 40)));
        sb.append(String.format("║ Total Copies:     %-40d ║\n", book.getTotalCopies()));
        sb.append(String.format("║ Available:        %-40d ║\n", book.getAvailableCopies()));
        sb.append(String.format("║ Price:            $%-39.2f ║\n", book.getReferencePrice()));
        sb.append(String.format("║ Status:           %-40s ║\n", book.getIsActive() ? "ACTIVE" : "INACTIVE"));
        sb.append(String.format("║ Created:          %-40s ║\n", book.getCreatedAt().format(DATETIME_FORMATTER)));
        sb.append("╚════════════════════════════════════════════════════════════╝\n");

        return sb.toString();
    }

    /**
     * Format single member details
     */
    public static String formatMemberDetails(Member member) {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║                   MEMBER DETAILS                           ║\n");
        sb.append("╠════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ ID:               %-40d ║\n", member.getIdMember()));
        sb.append(String.format("║ Name:             %-40s ║\n", truncate(member.getName(), 40)));
        sb.append(String.format("║ Email:            %-40s ║\n", truncate(member.getEmail(), 40)));
        sb.append(String.format("║ Phone:            %-40s ║\n", truncate(member.getPhone(), 40)));
        sb.append(String.format("║ Address:          %-40s ║\n", truncate(member.getAddress(), 40)));
        sb.append(String.format("║ Status:           %-40s ║\n", member.getIsActive() ? "ACTIVE" : "INACTIVE"));
        sb.append(String.format("║ Created:          %-40s ║\n", member.getCreatedAt().format(DATETIME_FORMATTER)));
        sb.append("╚════════════════════════════════════════════════════════════╝\n");

        return sb.toString();
    }

    /**
     * Format single loan details
     */
    public static String formatLoanDetails(Loan loan) {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════╗\n");
        sb.append("║                    LOAN DETAILS                            ║\n");
        sb.append("╠════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ Loan ID:          %-40d ║\n", loan.getIdLoan()));
        sb.append(String.format("║ ISBN:             %-40s ║\n", loan.getIsbn()));
        sb.append(String.format("║ Member ID:        %-40d ║\n", loan.getIdMember()));
        sb.append(String.format("║ Loan Date:        %-40s ║\n", loan.getLoanDate().format(DATE_FORMATTER)));
        sb.append(String.format("║ Due Date:         %-40s ║\n", loan.getDueDate().format(DATE_FORMATTER)));

        if (loan.getReturnDate() != null) {
            sb.append(String.format("║ Return Date:      %-40s ║\n", loan.getReturnDate().format(DATE_FORMATTER)));
        } else {
            sb.append(String.format("║ Return Date:      %-40s ║\n", "Not returned yet"));
        }

        sb.append(String.format("║ Fine Amount:      $%-39.2f ║\n", loan.getFineAmount()));
        sb.append(String.format("║ Status:           %-40s ║\n", loan.getStatus().name()));

        if (loan.isOverdue()) {
            sb.append(String.format("║ Days Overdue:     %-40d ║\n", loan.getDaysOverdue()));
            sb.append("║ ⚠ WARNING: This loan is overdue!                         ║\n");
        }

        sb.append("╚════════════════════════════════════════════════════════════╝\n");

        return sb.toString();
    }

    /**
     * Truncate string to specified length
     */
    private static String truncate(String str, int maxLength) {
        if (str == null) {
            return "";
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }
}