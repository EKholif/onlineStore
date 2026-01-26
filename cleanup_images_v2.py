import os
import shutil
import uuid

# Configuration
ROOT_DIR = r"e:\onlineStore_Active"
TARGET_TENANTS_DIR = os.path.join(ROOT_DIR, "webParentVer2", "WebBackEndVer2", "tenants")
DEFAULT_TENANT_ID = "0"

# Directories to clean up (move content from and then delete)
SOURCE_DIRS_TO_MERGE = [
    os.path.join(ROOT_DIR, "tenants"),   # The one I created or existed in root
    os.path.join(ROOT_DIR, "tenants33"), # The mysterious backup
    # Legacy photo roots
    os.path.join(ROOT_DIR, "customers-photos"),
    os.path.join(ROOT_DIR, "user-photos"),
    os.path.join(ROOT_DIR, "products-photos"),
    os.path.join(ROOT_DIR, "categories-photos"),
    os.path.join(ROOT_DIR, "brands-photos"),
    os.path.join(ROOT_DIR, "services-photos"),
    os.path.join(ROOT_DIR, "site-logo"),
     # Also check inside WebFrontEndVer2 (as seen earlier)
    os.path.join(ROOT_DIR, "webParentVer2", "WebFrontEndVer2", "customers-photos"),
]

# Legacy Entity keys to standardized names
ENTITY_MAP = {
    "customers-photos": "customers",
    "user-photos": "users",
    "products-photos": "products",
    "categories-photos": "categories",
    "brands-photos": "brands",
    "services-photos": "services",
    "site-logo": "site-logo" 
}

def scan_and_merge():
    if not os.path.exists(TARGET_TENANTS_DIR):
        os.makedirs(TARGET_TENANTS_DIR)
        
    print(f"TARGET: {TARGET_TENANTS_DIR}")

    # 1. Process explicit tenant dirs (tenants, tenants33)
    # They should have structure: {tid}/{assets?}/... or {tid}/{entity}-photos/...
    explicit_tenant_dirs = [os.path.join(ROOT_DIR, "tenants"), os.path.join(ROOT_DIR, "tenants33")]
    
    for src_tenants_root in explicit_tenant_dirs:
        if not os.path.exists(src_tenants_root): continue
        if os.path.normpath(src_tenants_root) == os.path.normpath(TARGET_TENANTS_DIR): continue # Don't process self

        print(f"Processing Source Tenants Root: {src_tenants_root}")
        
        for tenant_id in os.listdir(src_tenants_root):
            src_tenant_path = os.path.join(src_tenants_root, tenant_id)
            if not os.path.isdir(src_tenant_path): continue
            
            # Destination tenant path
            dest_tenant_path = os.path.join(TARGET_TENANTS_DIR, tenant_id)
            
            # Walk the source tenant dir
            for root, dirs, files in os.walk(src_tenant_path):
                for file in files:
                    # Logic to determine where this file goes in DEST
                    # Check relative path from src_tenant_path
                    rel_path = os.path.relpath(root, src_tenant_path)
                    
                    # If file is in assets/... -> preserve structure
                    # If file is in {entity}-photos/... -> map to assets/{entity}/...
                    
                    parts = rel_path.split(os.sep)
                    
                    new_rel_path = rel_path
                    
                    if parts[0] == "assets":
                        # Already in new structure
                        new_rel_path = rel_path
                    elif parts[0] in ENTITY_MAP:
                        # Legacy inside tenant folder: e.g. 7/user-photos/1/...
                        entity = ENTITY_MAP[parts[0]]
                        # Convert user-photos -> assets/users
                        new_rel_path = os.path.join("assets", entity, *parts[1:])
                    else:
                        # Unknown structure inside tenant? (e.g. root files)
                        # Keep as is or move to 'misc'?
                        # If it's just files at root of tenant, maybe default to assets/misc or leave at root
                        new_rel_path = rel_path

                    # Move
                    src_file = os.path.join(root, file)
                    dest_file = os.path.join(dest_tenant_path, new_rel_path, file)
                    
                    move_file(src_file, dest_file)
        
        # Cleanup
        shutil.rmtree(src_tenants_root)
        print(f"REMOVED: {src_tenants_root}")

    # 2. Process properties/root legacy folders
    # These lack Tenant ID in path -> Default to 0
    legacy_roots = [d for d in SOURCE_DIRS_TO_MERGE if "tenants" not in os.path.basename(d)]
    
    for src_dir in legacy_roots:
        if not os.path.exists(src_dir): continue
        
        dir_name = os.path.basename(src_dir)
        if dir_name not in ENTITY_MAP: continue # Should be in map
        
        entity = ENTITY_MAP[dir_name]
        
        print(f"Processing Legacy Root: {src_dir}")
        for root, dirs, files in os.walk(src_dir):
            for file in files:
                rel_path = os.path.relpath(root, src_dir)
                
                # Assume structure {id}/{file} or just {file}
                # Target: tenants/0/assets/{entity}/{id}/{file}
                
                parts = rel_path.split(os.sep)
                id_part = "0"
                remaining = ""
                
                if rel_path != "." and parts[0].isdigit():
                    id_part = parts[0]
                    if len(parts) > 1:
                        remaining = os.path.join(*parts[1:])
                elif rel_path != ".":
                    remaining = rel_path
                    
                dest_file = os.path.join(TARGET_TENANTS_DIR, DEFAULT_TENANT_ID, "assets", entity, id_part, remaining, file)
                move_file(os.path.join(root, file), dest_file)
                
        # Cleanup
        try:
            shutil.rmtree(src_dir)
            print(f"REMOVED: {src_dir}")
        except:
            pass

def move_file(src, dest):
    dest_dir = os.path.dirname(dest)
    if not os.path.exists(dest_dir):
        os.makedirs(dest_dir)
        
    if os.path.exists(dest):
        # Collision
        base, ext = os.path.splitext(os.path.basename(dest))
        dest = os.path.join(dest_dir, f"{base}_{uuid.uuid4().hex[:6]}{ext}")
        
    shutil.move(src, dest)
    # print(f"MOVED: {src} -> {dest}")

if __name__ == "__main__":
    scan_and_merge()
