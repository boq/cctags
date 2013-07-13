import sys
import os
import os.path as path
import re
import subprocess
import ConfigParser
from datetime import datetime

mcp_dir = path.abspath(sys.argv[1])
print("MCP dir: " + mcp_dir)

tmp_dir = path.abspath(sys.argv[2])
print("Output dir: " + tmp_dir)
sys.path.append(mcp_dir)

mod_name = sys.argv[3]
print("Mod: " + mod_name) 

import runtime.commands as commands 
from runtime.commands import Commands

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
    tag_part = run_command('git describe --abbrev=0 --tags') or 'x.x'
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
    
if __name__ == "__main__":
    main()