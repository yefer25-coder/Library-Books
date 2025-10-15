package models;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Book entity representing library catalog items
 */
public class Book {
    private String isbn;
    private String title;
    private String author;
    private String category;
    private Integer totalCopies;
    private Integer availableCopies;
    private Double referencePrice;
    private Boolean isActive;
    private LocalDateTime createdAt;

    // Constructors
    public Book() {
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    public Book(String isbn, String title, String author, String category,
                Integer totalCopies, Double referencePrice) {
        this();
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.category = category;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
        this.referencePrice = referencePrice;
    }

    public Book(String isbn, String title, String author, String category,
                Integer totalCopies, Integer availableCopies, Double referencePrice,
                Boolean isActive, LocalDateTime createdAt) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.category = category;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.referencePrice = referencePrice;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(Integer totalCopies) {
        this.totalCopies = totalCopies;
    }

    public Integer getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(Integer availableCopies) {
        this.availableCopies = availableCopies;
    }

    public Double getReferencePrice() {
        return referencePrice;
    }

    public void setReferencePrice(Double referencePrice) {
        this.referencePrice = referencePrice;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Business methods
    public boolean hasAvailableCopies() {
        return availableCopies != null && availableCopies > 0;
    }

    public boolean isActive() {
        return isActive != null && isActive;
    }

    public void decrementStock() {
        if (availableCopies > 0) {
            availableCopies--;
        }
    }

    public void incrementStock() {
        if (availableCopies < totalCopies) {
            availableCopies++;
        }
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return String.format("Book{isbn='%s', title='%s', author='%s', available=%d/%d, active=%s}",
                isbn, title, author, availableCopies, totalCopies, isActive);
    }
}