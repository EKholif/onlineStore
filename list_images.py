import os

TARGET_DIR = r"e:\onlineStore_Active\webParentVer2\WebBackEndVer2\tenants"

print(f"{'IMAGE NAME':<50} | {'ENTITY':<15} | {'TENANT':<10} | {'ID':<10}")
print("-" * 100)

if os.path.exists(TARGET_DIR):
    for root, dirs, files in os.walk(TARGET_DIR):
        for file in files:
            # Path structure: .../tenants/{tid}/assets/{entity}/{id}/filename
            rel_path = os.path.relpath(root, TARGET_DIR)
            parts = rel_path.split(os.sep)
            
            tenant_id = parts[0]
            entity = "Unknown"
            entity_id = "N/A"
            
            if len(parts) > 2 and parts[1] == "assets":
                entity = parts[2]
                if len(parts) > 3:
                    entity_id = parts[3]
            
            print(f"{file:<50} | {entity:<15} | {tenant_id:<10} | {entity_id:<10}")
