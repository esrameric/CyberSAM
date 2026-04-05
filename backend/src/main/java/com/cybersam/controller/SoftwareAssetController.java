package com.cybersam.controller;

import com.cybersam.dto.SoftwareAssetDTO;
import com.cybersam.service.SoftwareAssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for Software Asset Management
 * Provides CRUD operations and specialized queries for defense industry compliance
 */
@RestController
@RequestMapping("/api/v1/software-assets")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Software Assets", description = "Software Asset Management APIs - Defense Industry Standard")
public class SoftwareAssetController {

    private final SoftwareAssetService service;

    @PostMapping
    @Operation(summary = "Create a new software asset", description = "Create and register a new software asset with license and vulnerability details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Asset created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<SoftwareAssetDTO> createAsset(@Valid @RequestBody SoftwareAssetDTO dto) {
        log.info("POST /software-assets - Creating new asset: {}", dto.getName());
        SoftwareAssetDTO created = service.createAsset(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "Get all software assets", description = "Retrieve list of all registered software assets")
    @ApiResponse(responseCode = "200", description = "Assets retrieved successfully")
    public ResponseEntity<List<SoftwareAssetDTO>> getAllAssets() {
        log.info("GET /software-assets - Fetching all assets");
        List<SoftwareAssetDTO> assets = service.getAllAssets();
        return ResponseEntity.ok(assets);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get asset by ID", description = "Retrieve a specific software asset by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asset found"),
            @ApiResponse(responseCode = "404", description = "Asset not found")
    })
    public ResponseEntity<SoftwareAssetDTO> getAssetById(
            @PathVariable @Parameter(description = "Asset unique identifier") String id) {
        log.info("GET /software-assets/{} - Fetching asset", id);
        SoftwareAssetDTO asset = service.getAssetById(id);
        return ResponseEntity.ok(asset);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update software asset", description = "Update an existing software asset's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asset updated successfully"),
            @ApiResponse(responseCode = "404", description = "Asset not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<SoftwareAssetDTO> updateAsset(
            @PathVariable @Parameter(description = "Asset unique identifier") String id,
            @Valid @RequestBody SoftwareAssetDTO dto) {
        log.info("PUT /software-assets/{} - Updating asset", id);
        SoftwareAssetDTO updated = service.updateAsset(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete software asset", description = "Remove a software asset from the registry")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Asset deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Asset not found")
    })
    public ResponseEntity<Void> deleteAsset(
            @PathVariable @Parameter(description = "Asset unique identifier") String id) {
        log.info("DELETE /software-assets/{} - Deleting asset", id);
        service.deleteAsset(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/expired/licenses")
    @Operation(summary = "Get expired licenses", description = "Retrieve all software assets with expired licenses")
    @ApiResponse(responseCode = "200", description = "Expired licenses retrieved")
    public ResponseEntity<List<SoftwareAssetDTO>> getExpiredLicenses() {
        log.info("GET /software-assets/expired/licenses - Fetching expired licenses");
        List<SoftwareAssetDTO> expired = service.getExpiredLicenses();
        return ResponseEntity.ok(expired);
    }

    @GetMapping("/expiring/within")
    @Operation(summary = "Get licenses expiring soon", description = "Retrieve licenses expiring within specified number of days")
    @ApiResponse(responseCode = "200", description = "Expiring licenses retrieved")
    public ResponseEntity<List<SoftwareAssetDTO>> getLicensesExpiringWithin(
            @RequestParam @Parameter(description = "Number of days from today") int days) {
        log.info("GET /software-assets/expiring/within?days={} - Fetching licenses expiring", days);
        List<SoftwareAssetDTO> expiring = service.getLicensesExpiringWithin(days);
        return ResponseEntity.ok(expiring);
    }

    @GetMapping("/vulnerable")
    @Operation(summary = "Get vulnerable assets", description = "Retrieve all software assets flagged with CVE vulnerabilities")
    @ApiResponse(responseCode = "200", description = "Vulnerable assets retrieved")
    public ResponseEntity<List<SoftwareAssetDTO>> getVulnerableAssets() {
        log.info("GET /software-assets/vulnerable - Fetching vulnerable assets");
        List<SoftwareAssetDTO> vulnerable = service.getVulnerableAssets();
        return ResponseEntity.ok(vulnerable);
    }

    @GetMapping("/critical")
    @Operation(summary = "Get critical assets", description = "Retrieve all critical assets (vulnerable OR expired)")
    @ApiResponse(responseCode = "200", description = "Critical assets retrieved")
    public ResponseEntity<List<SoftwareAssetDTO>> getCriticalAssets() {
        log.info("GET /software-assets/critical - Fetching critical assets");
        List<SoftwareAssetDTO> critical = service.getCriticalAssets();
        return ResponseEntity.ok(critical);
    }

    @GetMapping("/vendor/{vendor}")
    @Operation(summary = "Get assets by vendor", description = "Retrieve all software assets from a specific vendor")
    @ApiResponse(responseCode = "200", description = "Vendor assets retrieved")
    public ResponseEntity<List<SoftwareAssetDTO>> getAssetsByVendor(
            @PathVariable @Parameter(description = "Vendor name") String vendor) {
        log.info("GET /software-assets/vendor/{} - Fetching assets for vendor", vendor);
        List<SoftwareAssetDTO> assets = service.getAssetsByVendor(vendor);
        return ResponseEntity.ok(assets);
    }

    @GetMapping("/search")
    @Operation(summary = "Search assets by name", description = "Search software assets by name pattern")
    @ApiResponse(responseCode = "200", description = "Search results retrieved")
    public ResponseEntity<List<SoftwareAssetDTO>> searchAssets(
            @RequestParam @Parameter(description = "Search pattern") String q) {
        log.info("GET /software-assets/search?q={} - Searching assets", q);
        List<SoftwareAssetDTO> results = service.searchAssets(q);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/stats/dashboard")
    @Operation(summary = "Get dashboard statistics", description = "Retrieve aggregated statistics for dashboard display")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        log.info("GET /software-assets/stats/dashboard - Fetching dashboard statistics");
        Map<String, Object> stats = new HashMap<>();
        
        List<SoftwareAssetDTO> allAssets = service.getAllAssets();
        List<SoftwareAssetDTO> vulnerableAssets = service.getVulnerableAssets();
        List<SoftwareAssetDTO> expiredLicenses = service.getExpiredLicenses();
        List<SoftwareAssetDTO> criticalAssets = service.getCriticalAssets();

        stats.put("totalAssets", allAssets.size());
        stats.put("vulnerableCount", vulnerableAssets.size());
        stats.put("expiredLicensesCount", expiredLicenses.size());
        stats.put("criticalAssetsCount", criticalAssets.size());
        stats.put("healthPercentage", calculateHealthPercentage(allAssets.size(), criticalAssets.size()));

        return ResponseEntity.ok(stats);
    }

    private double calculateHealthPercentage(int total, int critical) {
        if (total == 0) return 100.0;
        return Math.round(((double) (total - critical) / total) * 10000.0) / 100.0;
    }
}