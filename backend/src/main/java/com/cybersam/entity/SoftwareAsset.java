package com.cybersam.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * SoftwareAsset Entity - Represents a managed software component
 * with vulnerability tracking and license expiry monitoring.
 * Compliant with defense industry standards.
 */
@Entity
@Table(name = "software_assets", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoftwareAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 50)
    private String version;

    @Column(nullable = false, length = 255)
    private String vendor;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false)
    private Boolean isVulnerable;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @Column(length = 1000)
    private String cveNotes;

    @Column(length = 1000)
    private String description;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }

    public boolean isCritical() {
        return isVulnerable || isExpired();
    }
}