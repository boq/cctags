import sys
import os
import os.path as path
import re
import subprocess
import ConfigParser
from datetime import datetime

icon_dir = path.abspath(sys.argv[1])
print("Icon dir: " + icon_dir)

lua_dir = path.abspath(sys.argv[2])
print("Lua dir: " + lua_dir)

EXCLUDE = { "icon-marker.png",
            "icon-background-glass.png",
            "icon-background-paper.png" }

def update_icon_list():
    icon_file = path.join(icon_dir, "icons.properties")
    help_file = path.join(lua_dir, "icons")
    icon_regex = re.compile("^icon-([-_a-zA-Z0-9]+)\.png$");
    icons = list()
    
    for entry in os.listdir(icon_dir):
            if path.isdir(entry) or entry in EXCLUDE:
                continue
                
            match = icon_regex.match(entry)
            
            if match: 
                icons.append(match.group(1))
    
    icons.sort()
    
    print("Rewriting icon list %s " % icon_file)
    with open(icon_file, 'wb') as output:
        output.write("\n".join(icons))
        
    print("Rewriting 'help/icons' list %s " % icon_file)
    with open(help_file, 'wb') as output:
        output.write("Predefined tag icons:\n")
        output.write(",".join(icons))
      
def main():
    update_icon_list()
    
if __name__ == "__main__":
    main()