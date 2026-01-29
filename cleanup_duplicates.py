import os
import shutil
import filecmp

TARGET_DIR = r"E:\onlineStore_Active\webParentVer2\WebBackEndVer2\tenants\4\assets"

def get_folders_with_variants(parent_dir):
    """
    Returns a dict { base_name: [list of full paths to variants including base] }
    logic: 
       folders: "10", "10_12345", "10_67890" -> base "10"
       folders: "11_12345", "11_67890" -> base "11" (even if "11" doesn't exist yet)
    """
    if not os.path.exists(parent_dir):
        return {}
        
    entries = os.listdir(parent_dir)
    dirs = [d for d in entries if os.path.isdir(os.path.join(parent_dir, d))]
    
    groups = {}
    
    for d in dirs:
        # Check if it matches pattern Number_Something or just Number
        parts = d.split('_')
        base = parts[0]
        
        # We assume base is numeric ID based on previous `ls` output, 
        # but let's be generic. If it starts with digit, good.
        if not base.isdigit():
            continue
            
        if base not in groups:
            groups[base] = []
        groups[base].append(d)
        
    return groups

def merge_folders(source, dest):
    """
    Moves files from source to dest.
    If file exists in dest:
       if identical -> delete source file
       if different -> keep source file (don't move), warn? or rename?
       Current decision: Rename source file to keep it, just in case.
    """
    if not os.path.exists(dest):
        os.makedirs(dest)
        
    for root, dirs, files in os.walk(source):
        # We only care about files at this level (assuming flat structure inside ID folders usually)
        # But os.walk handles recursive
        
        rel_path = os.path.relpath(root, source)
        dest_root = os.path.join(dest, rel_path)
        
        if not os.path.exists(dest_root):
            os.makedirs(dest_root)
            
        for file in files:
            src_file = os.path.join(root, file)
            dest_file = os.path.join(dest_root, file)
            
            if os.path.exists(dest_file):
                # Compare
                if filecmp.cmp(src_file, dest_file, shallow=False):
                    # Identical, safe to delete source
                    os.remove(src_file)
                    print(f"  [Duplicate] Removed {src_file}")
                else:
                    # Different content, rename and move
                    base, ext = os.path.splitext(file)
                    new_dest_name = f"{base}_merged_{ext}"
                    new_dest_file = os.path.join(dest_root, new_dest_name)
                    
                    # Ensure uniqueness
                    counter = 1
                    while os.path.exists(new_dest_file):
                        new_dest_name = f"{base}_merged_{counter}{ext}"
                        new_dest_file = os.path.join(dest_root, new_dest_name)
                        counter += 1
                        
                    shutil.move(src_file, new_dest_file)
                    print(f"  [Conflict] Moved {src_file} -> {new_dest_file}")
            else:
                # No conflict, just move
                shutil.move(src_file, dest_file)
                print(f"  [Moved] {src_file} -> {dest_file}")

    # Now source should be empty of files, try to remove dirs
    # os.walk is top-down, so we can't remove current dir while walking inside
    # Retry removal from bottom up
    for root, dirs, files in os.walk(source, topdown=False):
        for name in dirs:
            d_path = os.path.join(root, name)
            try:
                os.rmdir(d_path)
            except OSError:
                pass # not empty
                
    try:
        os.rmdir(source)
        print(f"  [Cleaned] Removed folder {source}")
    except OSError:
        print(f"  [Warning] Could not remove {source}, not empty?")

def clean_entity_dir(entity_path):
    print(f"Processing {entity_path}...")
    groups = get_folders_with_variants(entity_path)
    
    for base, variants in groups.items():
        if len(variants) < 2 and variants[0] == base:
            # Only the base folder exists, no duplicates
            continue
            
        print(f" -> Found duplicates for ID {base}: {variants}")
        
        # Determine primary
        primary_name = base
        primary_path = os.path.join(entity_path, primary_name)
        
        # If primary folder doesn't exist (e.g. only 10_xxxx exists), create/rename
        if primary_name not in variants:
            # Rename the first variant to be primary
            first_variant = variants[0]
            src = os.path.join(entity_path, first_variant)
            print(f"  [Fix] No base folder '{base}' found. Renaming '{first_variant}' to '{base}'")
            shutil.move(src, primary_path)
            variants.remove(first_variant)
            
            # If there are others left, they are now secondary
            
        # Now merge all remaining variants into primary
        for variant in variants:
            if variant == primary_name:
                continue
                
            variant_path = os.path.join(entity_path, variant)
            print(f"  Merging {variant} into {primary_name}...")
            merge_folders(variant_path, primary_path)

def main():
    if not os.path.exists(TARGET_DIR):
        print("Target dir not found")
        return

    # Scan for entity directories (products, brands, etc)
    for item in os.listdir(TARGET_DIR):
        item_path = os.path.join(TARGET_DIR, item)
        if os.path.isdir(item_path):
            clean_entity_dir(item_path)

if __name__ == "__main__":
    main()
