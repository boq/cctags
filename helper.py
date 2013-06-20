import sys
import os
import os.path as path
import re
import subprocess
import ConfigParser
from datetime import datetime

mcp_dir = path.abspath(sys.argv[1])
print(mcp_dir)

tmp_dir = path.abspath(sys.argv[2])
print(tmp_dir)
sys.path.append(mcp_dir)

mod_name = sys.argv[3]

import runtime.commands as commands 
from runtime.commands import Commands

def iterate_property_keys(filename):
    r = re.compile("^([^#=][^=]*)")
    with open(filename, "r") as f:
        for line in f:
            match = r.match(line)
            if match:
                yield match.group(1).strip()

def update_lua_list():
    lua_dir = path.abspath("./lua/boq/cctags/lua")
    input_file = path.join(lua_dir, "files.properties")
    output_file = path.join(tmp_dir, "files.properties")
    print("Rewriting %s to %s" % (input_file, output_file))
    with open(output_file, "wb") as output:
        for key in iterate_property_keys(input_file):
            lua_file = path.join(lua_dir, key)
            try:
                stat = os.stat(lua_file)
                output.write("%s=%d\n" % (key, stat.st_mtime))
            except os.error, desc:
                print("Can't stat lua file %s: %s" % (lua_file, desc))
    

def update_icon_list():
    icon_dir = path.abspath("./resources/mods/cctags/textures/items")
    output_file = path.join(tmp_dir, "icons.properties")
    
    icon_regex = re.compile("^icon-([-_a-zA-Z0-9]+)\.png$");
    
    with open(output_file, 'wb') as output:
        for entry in os.listdir(icon_dir):
            if path.isdir(entry):
                continue
                
            match = icon_regex.match(entry)
            
            if match: 
                name = match.group(1)
                output.write(name + "\n")
                
def get_mcp_versions():
    config = ConfigParser.SafeConfigParser()
    config_path = path.normpath(path.join(mcp_dir,Commands._version_config))
 
    mcp_version = Commands.MCPVersion
    with open(config_path) as fh:
        config.readfp(fh)
        
        data_version = config.get('VERSION', 'MCPVersion')
        client_version = config.get('VERSION', 'ClientVersion')
        server_version = config.get('VERSION', 'ServerVersion')
        
        return (mcp_version, data_version, client_version, server_version)
    
def run_command(command):
    command = commands.cmdsplit(command)
    process = subprocess.Popen(command, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, bufsize=-1)
    stdout, _ = process.communicate()
    return stdout
    
def get_mod_version():
    tag_part = run_command('git describe') or 'x.x'
    hash_part = run_command('git show-ref --heads --hash=8 master')  or "sadperson"
    
    return (tag_part.strip(), hash_part.strip())

def create_version_properties():
    (mcp_version, data_version, client_version, server_version) = get_mcp_versions();
    (tag_part, hash_part) = get_mod_version();
    
    mod_version = tag_part + "-" + hash_part;
    line_format = mod_name + ".%s=%s\n"
    with open("version.properties","w") as f:
      f.write("#%s\n" % datetime.utcnow().isoformat())
      f.write(line_format % ("version", mod_version))
      f.write(line_format % ("version.tag", tag_part))
      f.write(line_format % ("version.hash", hash_part))
      f.write(line_format % ("version.mcp", mcp_version))
      f.write(line_format % ("version.mcp.data", data_version))
      f.write(line_format % ("version.mc.client", client_version))
      f.write(line_format % ("version.mc.server", server_version))
      
def main():
    create_version_properties()
    update_lua_list();
    update_icon_list()
    
if __name__ == "__main__":
    main()