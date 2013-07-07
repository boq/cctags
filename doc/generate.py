import Image
import re
import sys
import os
import os.path as path

BORDER = 'border.png'
BACKGROUND = 'icon-background-paper.png'
MARKER = 'icon-marker-lime.png'

INPUT_FOLDER = "../resources/mods/cctags/textures/items/"
OUTPUT_FOLDER = "../target/doc/"

border_img = Image.open(BORDER)
background_img = Image.open(BACKGROUND).resize((32,32))
marker_img = Image.open(MARKER).resize((32,32))

def iterate_property_keys(filename):
    r = re.compile("^([^#=][^=]*)")
    with open(filename, "r") as f:
        for line in f:
            match = r.match(line)
            if match:
                yield match.group(1).strip()

def merge_image(input_file, output_file):
    print("Image: input = '%s', output = '%s'" % (input_file, output_file))
    icon_img = Image.open(input_file).resize((32,32))
    target = Image.new('RGB', (52, 52))
    target.paste(border_img, (0,0,52,52))
    
    target.paste(background_img, (10,10))
    target.paste(icon_img, (10,10), mask=icon_img)
    target.paste(marker_img, (10,10), mask=marker_img)
    
    target.save(output_file, "PNG")


HTML_HEADER = """<!doctype html>
<html>
<head><meta http-equiv="Content-Type" content="text/html; charset=utf-8"><title>CCTag Icons</title></head>
<body style="background-color: #C6C6C6; font-family: 'Courier New', Courier, monospace;">
"""

HTML_FOOTER = "</body></html>\n"

ICON_TABLE_HEADER = """<table><thead>
<tr><th>Icon</th><th>Name</th></tr>
</thead><tbody>
"""

ICON_TABLE_ENTRY = """<tr><td><img src="{img}" alt="{name}" /></td><td>{name}</td></tr>
"""

ICON_TABLE_FOOTER = "</tbody></table>\n"

def do_images(input_folder, output_folder):
    html_file_path = path.join(output_folder, "cctag-icons.html")
    with open(html_file_path, "wb") as html_file:
        html_file.write(HTML_HEADER)
        html_file.write(ICON_TABLE_HEADER)
        
        prop_file = path.join(input_folder, "icons.properties")
        for entry in iterate_property_keys(prop_file):
            file_name = "icon-" + entry + ".png"
            input_file = path.join(input_folder, file_name)
            output_file = path.join(output_folder, file_name)
            merge_image(input_file, output_file)
            html_file.write(ICON_TABLE_ENTRY.format(img = file_name, name = entry))
        
        html_file.write(ICON_TABLE_FOOTER)
        html_file.write(HTML_FOOTER)

    
if __name__ == "__main__":
    if not path.isdir(OUTPUT_FOLDER):
        os.mkdir(OUTPUT_FOLDER)

    do_images(INPUT_FOLDER, OUTPUT_FOLDER)