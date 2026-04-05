package com.cybersam.service;

import com.cybersam.dto.SoftwareAssetDTO;
import com.cybersam.entity.SoftwareAsset;
import com.cybersam.repository.SoftwareAssetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for SoftwareAsset operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SoftwareAssetService {

    private final SoftwareAssetRepository repository;

    public SoftwareAssetDTO createAsset(SoftwareAssetDTO dto) {
        log.info("Creating new software asset: {}", dto.getName());
        SoftwareAsset asset = new SoftwareAsset();
        asset.setName(dto.getName());
        asset.setVersion(dto.getVersion());
        asset.setVendor(dto.getVendor());
        asset.setExpiryDate(dto.getExpiryDate());
        asset.setIsVulnerable(dto.getIsVulnerable());
        asset.setDescription(dto.getDescription());
        asset.setCveNotes(dto.getCveNotes());

        SoftwareAsset saved = repository.save(asset);
        return mapToDTO(saved);
    }

    public SoftwareAssetDTO updateAsset(String id, SoftwareAssetDTO dto) {
        log.info("Updating software asset: {}", id);
        SoftwareAsset asset = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found: " + id));

        asset.setName(dto.getName());
        asset.setVersion(dto.getVersion());
        asset.setVendor(dto.getVendor());
        asset.setExpiryDate(dto.getExpiryDate());
        asset.setIsVulnerable(dto.getIsVulnerable());
        asset.setDescription(dto.getDescription());
        asset.setCveNotes(dto.getCveNotes());

        SoftwareAsset updated = repository.save(asset);
        return mapToDTO(updated);
    }

    public SoftwareAssetDTO getAssetById(String id) {
        log.info("Fetching asset: {}", id);
        return repository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Asset not found: " + id));
    }

    public List<SoftwareAssetDTO> getAllAssets() {
        log.info("Fetching all assets");
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void deleteAsset(String id) {
        log.info("Deleting asset: {}", id);
        repository.deleteById(id);
    }

    public List<SoftwareAssetDTO> getExpiredLicenses() {
        log.info("Fetching expired licenses");
        LocalDate today = LocalDate.now();
        return repository.findExpiredLicenses(today).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<SoftwareAssetDTO> getLicensesExpiringWithin(int days) {
        log.info("Fetching licenses expiring within {} days", days);
        LocalDate targetDate = LocalDate.now().plusDays(days);
        return repository.findLicensesExpiringWithin(targetDate).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<SoftwareAssetDTO> getVulnerableAssets() {
        log.info("Fetching vulnerable assets");
        return repository.findVulnerableAssets(true).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<SoftwareAssetDTO> getCriticalAssets() {
        log.info("Fetching critical assets");
        LocalDate today = LocalDate.now();
        return repository.findCriticalAssets(true, today).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<SoftwareAssetDTO> getAssetsByVendor(String vendor) {
        log.info("Fetching assets for vendor: {}", vendor);
        return repository.findByVendorIgnoreCase(vendor).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<SoftwareAssetDTO> searchAssets(String namePattern) {
        log.info("Searching assets with pattern: {}", namePattern);
        return repository.findByNameContainingIgnoreCase(namePattern).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public SoftwareAssetDTO mapToDTO(SoftwareAsset asset) {
        return new SoftwareAssetDTO(
                asset.getId(),
                asset.getName(),
                asset.getVersion(),
                asset.getVendor(),
                asset.getExpiryDate(),
                asset.getIsVulnerable(),
                asset.getCreatedAt(),
                asset.getUpdatedAt(),
                asset.getCveNotes(),
                asset.getDescription()
        );
    }
}