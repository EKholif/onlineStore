# Storage Path Migration Script
# AG-STORAGE-001: Move assets from incorrect location to correct location
# 
# FROM: E:\onlineStore_Active\tenants\
# TO:   E:\onlineStore_Active\webParentVer2\WebBackEndVer2\tenants\

Write-Host "=================================================="
Write-Host "AG-STORAGE-001: Storage Path Migration"
Write-Host "=================================================="
Write-Host ""

$sourceDir = "E:\onlineStore_Active\tenants"
$targetDir = "E:\onlineStore_Active\webParentVer2\WebBackEndVer2\tenants"

# Check if source exists
if (-not (Test-Path $sourceDir)) {
    Write-Host "❌ Source directory not found: $sourceDir"
    exit 1
}

# Create target directory if not exists
if (-not (Test-Path $targetDir)) {
    Write-Host "📁 Creating target directory: $targetDir"
    New-Item -ItemType Directory -Force -Path $targetDir | Out-Null
}

# Count files to migrate
$filesToMigrate = Get-ChildItem -Path $sourceDir -Recurse -File
$fileCount = $filesToMigrate.Count

Write-Host "📊 Found $fileCount files to migrate"
Write-Host ""
Write-Host "Source: $sourceDir"
Write-Host "Target: $targetDir"
Write-Host ""

# Ask for confirmation
$confirmation = Read-Host "Proceed with migration? (yes/no)"
if ($confirmation -ne "yes") {
    Write-Host "❌ Migration cancelled"
    exit 0
}

Write-Host ""
Write-Host "🚀 Starting migration..."
Write-Host ""

# Copy files preserving structure
$copiedCount = 0
foreach ($file in $filesToMigrate) {
    $relativePath = $file.FullName.Substring($sourceDir.Length).TrimStart('\')
    $targetPath = Join-Path $targetDir $relativePath
    $targetFolder = Split-Path $targetPath -Parent
    
    # Create target folder if not exists
    if (-not (Test-Path $targetFolder)) {
        New-Item -ItemType Directory -Force -Path $targetFolder | Out-Null
    }
    
    # Copy file
    Copy-Item -Path $file.FullName -Destination $targetPath -Force
    $copiedCount++
    
    if ($copiedCount % 10 -eq 0) {
        Write-Host "  Copied $copiedCount / $fileCount files..."
    }
}

Write-Host ""
Write-Host "✅ Migration complete: $copiedCount files copied"
Write-Host ""

# Ask if should delete source
$deleteConfirm = Read-Host "Delete source directory after migration? (yes/no)"
if ($deleteConfirm -eq "yes") {
    Write-Host "🗑️  Deleting source directory..."
    Remove-Item -Path $sourceDir -Recurse -Force
    Write-Host "✅ Source directory deleted"
} else {
    Write-Host "⚠️  Source directory kept at: $sourceDir"
    Write-Host "   You can manually delete it after verifying the migration"
}

Write-Host ""
Write-Host "=================================================="
Write-Host "NEXT STEPS:"
Write-Host "=================================================="
Write-Host "1. IntelliJ → Run → Edit Configurations"
Write-Host "2. Select 'WebBackEndApplication'"
Write-Host "3. Environment Variables → DELETE 'TENANTS_PATH'"
Write-Host "4. Click OK and restart Backend"
Write-Host ""
Write-Host "Expected log after restart:"
Write-Host "✅ [AG-STORAGE-001] Storage path validated: E:\onlineStore_Active\webParentVer2\WebBackEndVer2\tenants"
Write-Host "=================================================="
