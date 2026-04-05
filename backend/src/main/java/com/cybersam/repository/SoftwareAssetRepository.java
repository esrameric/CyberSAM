package com.cybersam.repository;

import com.cybersam.entity.SoftwareAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for SoftwareAsset entity
 * Provides specialized query methods for license and vulnerability management
 */
@Repository
public interface SoftwareAssetRepository extends JpaRepository<SoftwareAsset, String> {

    /**
     * Find all software assets with expired licenses
     * @return List of expired software assets
     */
    @Query("SELECT sa FROM SoftwareAsset sa WHERE sa.expiryDate < :referenceDate ORDER BY sa.expiryDate ASC")
    List<SoftwareAsset> findExpiredLicenses(@Param("referenceDate") LocalDate referenceDate);

    /**
     * Find all software assets expiring within specified days
     * @param targetDate The target date to compare against (should be calculated as LocalDate.now().plusDays(days))
     * @return List of soon-to-expire assets
     */
    @Query("SELECT sa FROM SoftwareAsset sa WHERE sa.expiryDate <= :targetDate AND sa.expiryDate >= CURRENT_DATE ORDER BY sa.expiryDate ASC")
    List<SoftwareAsset> findLicensesExpiringWithin(@Param("targetDate") LocalDate targetDate);

    /**
     * Find all vulnerable software assets (CVE flags)
     * @return List of vulnerable assets
     */
    @Query("SELECT sa FROM SoftwareAsset sa WHERE sa.isVulnerable = :isVulnerable ORDER BY sa.vendor ASC, sa.name ASC")
    List<SoftwareAsset> findVulnerableAssets(@Param("isVulnerable") Boolean isVulnerable);

    /**
     * Find critical assets (vulnerable OR expired)
     * @return List of critical assets
     */
    @Query("SELECT sa FROM SoftwareAsset sa WHERE sa.isVulnerable = :isVulnerable OR sa.expiryDate < :referenceDate ORDER BY sa.name ASC")
    List<SoftwareAsset> findCriticalAssets(@Param("isVulnerable") Boolean isVulnerable, @Param("referenceDate") LocalDate referenceDate);

    /**
     * Find assets by vendor
     * @param vendor Vendor name
     * @return List of assets from specified vendor
     */
    List<SoftwareAsset> findByVendorIgnoreCase(String vendor);

    /**
     * Find assets by name (partial match)
     * @param namePattern Name pattern
     * @return List of matching assets
     */
    @Query("SELECT sa FROM SoftwareAsset sa WHERE LOWER(sa.name) LIKE LOWER(CONCAT('%', :namePattern, '%'))")
    List<SoftwareAsset> findByNameContainingIgnoreCase(@Param("namePattern") String namePattern);

    /**
     * Count total assets
     * @return Total count
     */
    long count();

    /**
     * Count vulnerable assets
     * @return Vulnerable assets count
     */
    @Query("SELECT COUNT(sa) FROM SoftwareAsset sa WHERE sa.isVulnerable = :isVulnerable")
    long countVulnerableAssets(@Param("isVulnerable") Boolean isVulnerable);

    /**
     * Count expired licenses
     * @return Expired count
     */
    @Query("SELECT COUNT(sa) FROM SoftwareAsset sa WHERE sa.expiryDate < :referenceDate")
    long countExpiredLicenses(@Param("referenceDate") LocalDate referenceDate);

    /**
     * Count critical assets
     * @return Critical count
     */
    @Query("SELECT COUNT(sa) FROM SoftwareAsset sa WHERE sa.isVulnerable = :isVulnerable OR sa.expiryDate < :referenceDate")
    long countCriticalAssets(@Param("isVulnerable") Boolean isVulnerable, @Param("referenceDate") LocalDate referenceDate);

    /**
     * Find asset by exact name and vendor
     * @param name Asset name
     * @param vendor Asset vendor
     * @return Optional containing the asset if found
     */
    Optional<SoftwareAsset> findByNameAndVendor(String name, String vendor);
}