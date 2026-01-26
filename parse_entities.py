import os
import re

ENTITY_DIR = r"e:\onlineStore_Active\comm\src\main\java\com\onlineStoreCom\entity"
OUTPUT_FILE = "entity_details_dump.md"

def parse_java_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    class_name_match = re.search(r'public class (\w+)', content)
    if not class_name_match:
        return ""
    
    class_name = class_name_match.group(1)
    
    # Extract Fields with simple regex approximation
    fields = []
    # simplistic pattern for fields: private Type name; or private Type name = val;
    # Captures: 1: Type, 2: Name
    field_pattern = re.compile(r'private\s+([\w<>?]+)\s+(\w+)(?:[^;]*);')
    
    # Extract methods
    # public Type name(Args)
    method_pattern = re.compile(r'public\s+(?:static\s+)?([\w<>?]+)\s+(\w+)\s*\(([^)]*)\)')

    output = []
    output.append(f"## Entity: {class_name}")
    output.append(f"**File:** `{filepath}`\n")
    
    output.append("### Data Fields")
    output.append("| Type | Name | Details/Constraints |")
    output.append("|---|---|---|")
    
    # We'll split by lines to contextually find annotations for fields
    lines = content.split('\n')
    for i, line in enumerate(lines):
        line = line.strip()
        match = field_pattern.search(line)
        if match:
            f_type = match.group(1)
            f_name = match.group(2)
            
            # Look backwards for annotations
            annotations = []
            j = i - 1
            while j >= 0:
                prev_line = lines[j].strip()
                if prev_line.startswith('@') and not prev_line.startswith('public'): # crude check
                    annotations.append(prev_line)
                elif prev_line.endswith(';') or prev_line.endswith('{') or prev_line == '':
                     # Stop if we hit end of previous statement or block start
                     if prev_line != '' and not prev_line.startswith('@'):
                         break
                j -= 1
            
            ann_str = "<br>".join(annotations) if annotations else "-"
            output.append(f"| `{f_type}` | `{f_name}` | {ann_str} |")

    output.append("\n### Methods")
    # Scan for methods
    for match in method_pattern.finditer(content):
        ret_type = match.group(1)
        m_name = match.group(2)
        params = match.group(3).strip()
        output.append(f"- **{m_name}**(`{params}`) -> `{ret_type}`")

    output.append("\n---\n")
    return "\n".join(output)

def main():
    final_output = ["# All Entities Detail Report\n"]
    
    for root, dirs, files in os.walk(ENTITY_DIR):
        for file in files:
            if file.endswith(".java"):
                full_path = os.path.join(root, file)
                result = parse_java_file(full_path)
                if result:
                    final_output.append(result)
    
    with open(OUTPUT_FILE, 'w', encoding='utf-8') as f:
        f.write("\n".join(final_output))
    
    print(f"Dumped to {OUTPUT_FILE}")

if __name__ == "__main__":
    main()
