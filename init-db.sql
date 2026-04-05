-- Initialize CyberSAM Database
-- This script runs when PostgreSQL container starts

CREATE SCHEMA IF NOT EXISTS public;

-- Create software_assets table
CREATE TABLE IF NOT EXISTS public.software_assets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    version VARCHAR(50) NOT NULL,
    vendor VARCHAR(255) NOT NULL,
    expiry_date DATE NOT NULL,
    is_vulnerable BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATE NOT NULL DEFAULT CURRENT_DATE,
    updated_at DATE NOT NULL DEFAULT CURRENT_DATE,
    cve_notes VARCHAR(1000),
    description VARCHAR(1000),
    CONSTRAINT uk_asset_name_vendor UNIQUE(name, vendor)
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_expiry_date ON public.software_assets(expiry_date);
CREATE INDEX IF NOT EXISTS idx_vendor ON public.software_assets(vendor);
CREATE INDEX IF NOT EXISTS idx_is_vulnerable ON public.software_assets(is_vulnerable);
CREATE INDEX IF NOT EXISTS idx_created_at ON public.software_assets(created_at);

-- Sample data (optional - for testing)
INSERT INTO public.software_assets 
(name, version, vendor, expiry_date, is_vulnerable, cve_notes, description)
VALUES
('OpenSSL', '3.0.7', 'OpenSSL Software Foundation', '2025-12-31', true, 
 'CVE-2023-0464: Potential infinite loop in X.509 verification', 
 'Critical cryptography library for TLS/SSL protocols'),
('Log4j', '2.20.0', 'Apache Software Foundation', '2025-06-30', false,
 'Previous versions had critical RCE vulnerability (CVE-2021-44228)',
 'Widely used Java logging framework'),
('Java Runtime', '17.0.8', 'Oracle', '2026-09-30', false,
 'LTS version - regularly patched',
 'Java Development Kit and Runtime Environment'),
('PostgreSQL', '16.0', 'PostgreSQL Global Development Group', '2026-10-13', false,
 'Latest stable version with security patches',
 'Enterprise-grade open source database'),
('Spring Boot', '3.2.0', 'VMware Spring', '2027-12-31', false,
 'Latest LTS release',
 'Modern Java application framework')
ON CONFLICT (name, vendor) DO NOTHING;

-- Grant permissions
GRANT USAGE ON SCHEMA public TO cybersam_user;
GRANT CREATE ON SCHEMA public TO cybersam_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO cybersam_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO cybersam_user;
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA public TO cybersam_user;
