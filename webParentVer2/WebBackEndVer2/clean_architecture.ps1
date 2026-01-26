# Anti-Gravity Architecture Cleanup Script
$webBackEnd = "c:\Users\enoam\OneDrive\onlineStore\onlineStore\webParent\WebBackEnd"
$backupRoot = "$webBackEnd\_legacy_backup"
$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$logFile = "$webBackEnd\logs\cleanup-$timestamp.log"

function Log-Message {
    param([string]$msg)
    $line = "[$(Get-Date -Format 'HH:mm:ss')] $msg"
    Write-Host $line
    Add-Content -Path $logFile -Value $line
}

New-Item -ItemType Directory -Force -Path $backupRoot | Out-Null
New-Item -ItemType Directory -Force -Path "$webBackEnd\logs" | Out-Null
Log-Message "Starting Cleanup. Backup location: $backupRoot"

# 1. Clean Root Legacy Folders
$legacyTypes = @("brands-photos", "categories-photos", "customer-photos", "customers-photos", "products-photos", "services-photos", "site-logo", "user-photos")

foreach ($type in $legacyTypes) {
    $path = "$webBackEnd\$type"
    if (Test-Path $path) {
        $dest = "$backupRoot\root_backup\$type"
        New-Item -ItemType Directory -Force -Path "$backupRoot\root_backup" | Out-Null
        Log-Message "Moving Root Legacy: $type -> $dest"
        Move-Item -Path $path -Destination $dest -Force
    }
}

# 2. Clean Tenant Legacy Folders & Artifacts
$tenantsPath = "$webBackEnd\tenants"
if (Test-Path $tenantsPath) {
    $items = Get-ChildItem -Path $tenantsPath
    foreach ($item in $items) {
        if ($item.PSIsContainer) {
            # Check for Artifact Directories (Bug fixes)
            if ($item.Name -match "\.(png|jpg|jpeg)$") {
                $dest = "$backupRoot\artifacts\$($item.Name)"
                New-Item -ItemType Directory -Force -Path "$backupRoot\artifacts" | Out-Null
                Log-Message "Found Artifact Dir: $($item.Name). Moving to backup."
                Move-Item -Path $item.FullName -Destination $dest -Force
                continue;
            }

            # Check for inner legacy folders
            # Define the mapping for legacy folders to their new 'assets' subdirectories
            $legacyMap = @{
                "brands-photos"     = "brands"
                "categories-photos" = "categories"
                "customer-photos"   = "customers" # Assuming customer-photos maps to 'customers'
                "customers-photos"  = "customers"
                "products-photos"   = "products"
                "services-photos"   = "services"
                "site-logo"         = "site-logo" # This might be a direct move, or to a 'general' assets folder
                "user-photos"       = "users"
            }

            $tenantPath = $item.FullName
            $assetsPath = Join-Path $tenantPath "assets"

            # Ensure 'assets' directory exists for the tenant
            if (-not (Test-Path $assetsPath)) {
                New-Item -ItemType Directory -Force -Path $assetsPath | Out-Null
                Write-Host "Created missing 'assets' directory for tenant: $($item.Name)" -ForegroundColor Yellow
            }

            foreach ($pair in $legacyMap.GetEnumerator()) {
                $legacyName = $pair.Key
                $targetName = $pair.Value
                $legacyPath = Join-Path $tenantPath $legacyName

                if (Test-Path $legacyPath) {
                    $targetDir = Join-Path $assetsPath $targetName
                    
                    # Force creation of target directory
                    if (-not (Test-Path $targetDir)) {
                        New-Item -ItemType Directory -Force -Path $targetDir | Out-Null
                        Write-Host "   + Created missing target: $targetDir" -ForegroundColor Cyan
                    }

                    # Move content
                    Get-ChildItem -Path $legacyPath | Move-Item -Destination $targetDir -Force
                    Remove-Item -Path $legacyPath -Force -Recurse
                    Write-Host "   -> Moved $legacyName to assets/$targetName" -ForegroundColor Green
                }
            }
        }
    }
}

Log-Message "Cleanup Complete."
