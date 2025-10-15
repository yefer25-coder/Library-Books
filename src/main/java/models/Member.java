package models;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Member entity representing library members/partners
 */
public class Member {
    private Integer idMember;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Boolean isActive;
    private LocalDateTime createdAt;

    // Constructors
    public Member() {
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    public Member(String name, String email, String phone, String address) {
        this();
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public Member(Integer idMember, String name, String email, String phone,
                  String address, Boolean isActive, LocalDateTime createdAt) {
        this.idMember = idMember;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Integer getIdMember() {
        return idMember;
    }

    public void setIdMember(Integer idMember) {
        this.idMember = idMember;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
    public boolean isActive() {
        return isActive != null && isActive;
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
        Member member = (Member) o;
        return Objects.equals(idMember, member.idMember) &&
                Objects.equals(email, member.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMember, email);
    }

    @Override
    public String toString() {
        return String.format("Member{id=%d, name='%s', email='%s', active=%s}",
                idMember, name, email, isActive);
    }
}