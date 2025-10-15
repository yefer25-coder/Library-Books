package models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Loan entity representing book loans to members
 */
public class Loan {
    private Integer idLoan;
    private String isbn;
    private Integer idMember;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private Double fineAmount;
    private LoanStatus status;
    private LocalDateTime createdAt;

    // Constructors
    public Loan() {
        this.loanDate = LocalDate.now();
        this.fineAmount = 0.0;
        this.status = LoanStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    public Loan(String isbn, Integer idMember, LocalDate dueDate) {
        this();
        this.isbn = isbn;
        this.idMember = idMember;
        this.dueDate = dueDate;
    }

    public Loan(Integer idLoan, String isbn, Integer idMember, LocalDate loanDate,
                LocalDate dueDate, LocalDate returnDate, Double fineAmount,
                LoanStatus status, LocalDateTime createdAt) {
        this.idLoan = idLoan;
        this.isbn = isbn;
        this.idMember = idMember;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fineAmount = fineAmount;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Integer getIdLoan() {
        return idLoan;
    }

    public void setIdLoan(Integer idLoan) {
        this.idLoan = idLoan;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getIdMember() {
        return idMember;
    }


    public LocalDate getLoanDate() {
        return loanDate;
    }


    public LocalDate getDueDate() {
        return dueDate;
    }


    public LocalDate getReturnDate() {
        return returnDate;
    }


    public Double getFineAmount() {
        return fineAmount;
    }


    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    // Business methods
    public boolean isOverdue() {
        return status == LoanStatus.ACTIVE && LocalDate.now().isAfter(dueDate);
    }

    public long getDaysOverdue() {
        if (!isOverdue()) {
            return 0;
        }
        return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }

    public double calculateFine(double finePerDay) {
        long daysOverdue = getDaysOverdue();
        return daysOverdue > 0 ? daysOverdue * finePerDay : 0.0;
    }

    public boolean isActive() {
        return status == LoanStatus.ACTIVE;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Loan loan = (Loan) o;
        return Objects.equals(idLoan, loan.idLoan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idLoan);
    }

    @Override
    public String toString() {
        return String.format("Loan{id=%d, isbn='%s', member=%d, dueDate=%s, status=%s, fine=%.2f}",
                idLoan, isbn, idMember, dueDate, status, fineAmount);
    }

    // Enum
    public enum LoanStatus {
        ACTIVE, RETURNED
    }
}
