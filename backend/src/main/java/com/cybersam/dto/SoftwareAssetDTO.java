package com.cybersam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * DTO for SoftwareAsset - Used for API request/response transfers
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoftwareAssetDTO {
    private String id;
    private String name;
    private String version;
    private String vendor;
    private LocalDate expiryDate;
    private Boolean isVulnerable;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private String cveNotes;
    private String description;

    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }

    public boolean isCritical() {
        return isVulnerable || isExpired();
    }
}