import os
import shutil
import re
from pathlib import Path

# Configuration
ROOT_DIR = r"e:\onlineStore_Active"
TENANTS_DIR = os.path.join(ROOT_DIR, "tenants")
DEFAULT_TENANT_ID = "0" # Default legacy/global tenant

# Entity Mapping (Dir Name -> Entity Name in target path)
# Target path: tenants/{tid}/assets/{entity_path}/{id}/
ENTITY_MAP = {
    "customers-photos": "customers",
    "user-photos": "users",
    "products-photos": "products",
    "categories-photos": "categories",
    "brands-photos": "brands",
    "services-photos": "services",
    "site-logo": "site-logo" # Special case, might not have ID
}

def scan_and_move():
    if not os.path.exists(TENANTS_DIR):
        os.makedirs(TENANTS_DIR)

    report = []
    
    # 1. Scan for Legacy Directories in likely locations
    search_roots = [
        ROOT_DIR,
        os.path.join(ROOT_DIR, "webParentVer2", "WebFrontEndVer2"),
        os.path.join(ROOT_DIR, "webParentVer2", "WebBackEndVer2"),
        # Add legacy webParent if needed, though we focus on active
        ]
    
    for search_root in search_roots:
        if not os.path.exists(search_root):
            continue
            
        for item in os.listdir(search_root):
            if item in ENTITY_MAP:
                source_dir = os.path.join(search_root, item)
                entity_type_path = ENTITY_MAP[item]
                process_directory(source_dir, entity_type_path, DEFAULT_TENANT_ID)

    # 2. Scan for existing tenants/ structure that might be in wrong format 
    # (e.g. tenants/7/products-photos/ instead of assets/products)
    if os.path.exists(TENANTS_DIR):
        for tenant_id in os.listdir(TENANTS_DIR):
            tenant_path = os.path.join(TENANTS_DIR, tenant_id)
            if not os.path.isdir(tenant_path): continue
            
            # Check for legacy folders inside tenant
            for item in os.listdir(tenant_path):
                if item in ENTITY_MAP:
                    process_directory(os.path.join(tenant_path, item), ENTITY_MAP[item], tenant_id)

def process_directory(source_dir, entity_type_path, tenant_id):
    # Walk through the directory
    for root, dirs, files in os.walk(source_dir):
        for file in files:
            if not is_image(file): continue
            
            # Determine Entity ID from path
            # Structure usually: .../{entity}-photos/{id}/file.png
            # rel_path matches {id}/file.png or just file.png (if no ID, e.g. site-logo)
            rel_path = os.path.relpath(root, source_dir)
            
            # Try to extract ID
            entity_id = "0"
            parts = rel_path.split(os.sep)
            
            if rel_path == ".":
                # File is directly in photo dir (e.g. site-logo/logo.png)
                entity_id = "0"
            elif len(parts) >= 1:
                # Assuming first folder is ID
                # check if parts[0] is numeric
                if parts[0].isdigit():
                    entity_id = parts[0]
                else:
                    # Maybe it's extras? "extras/..."
                    # If assume structure is strict, we set ID=0 or keep path
                    pass

            source_file = os.path.join(root, file)
            
            # Construct Target Path
            # tenants/{tenantId}/assets/{entity_type_path}/{entity_id}/{filename} (or subdirs)
            
            # Use original relative structure for subdirs (like 'extras')
            # If parts[0] was ID, removal of ID from path usage
            remaining_path = ""
            if rel_path != "." and parts[0].isdigit():
                if len(parts) > 1:
                    remaining_path = os.path.join(*parts[1:])
            elif rel_path != ".":
                remaining_path = rel_path
                
            
            target_dir = os.path.join(TENANTS_DIR, tenant_id, "assets", entity_type_path, entity_id, remaining_path)
            target_file = os.path.join(target_dir, file)
            
            # Info for report
            print(f"FOUND: File={file} | Entity={entity_type_path} | Tenant={tenant_id} | ID={entity_id}")
            
            # Move
            if not os.path.exists(target_dir):
                os.makedirs(target_dir)
            
            # Handle collisions (rename if needed)
            if os.path.exists(target_file):
                base, ext = os.path.splitext(file)
                import uuid
                target_file = os.path.join(target_dir, f"{base}_{uuid.uuid4().hex[:6]}{ext}")
            
            shutil.move(source_file, target_file)
            print(f"MOVED: {source_file} -> {target_file}")

    # Cleanup empty dirs
    try:
        shutil.rmtree(source_dir)
        print(f"CLEANED: {source_dir}")
    except Exception as e:
        print(f"ERROR cleaning {source_dir}: {e}")

def is_image(filename):
    return filename.lower().endswith(('.png', '.jpg', '.jpeg', '.gif', '.webp', '.svg'))

if __name__ == "__main__":
    scan_and_move()
