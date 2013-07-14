import json
import sys
import os
import os.path as path

INPUT_FILE = "../lua/boq/cctags/lua/library.json"
OUTPUT_FOLDER = "../target/doc/"
OUTPUT_FILE = path.join(OUTPUT_FOLDER, "library.html")

HTML_HEADER = """<!doctype html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"><title>CCTag library</title>
    <style type="text/css">
        body {
            background-color: #C6C6C6;
            font-family: 'Courier New', Courier, monospace;
        }
        table {
            border-collapse:collapse;
        }
        table,th, td {
            border: 1px solid black;
        }
        .name {
            font-weight:bold;
        }
        .note {
            font-weight:bold;
        }
        .desc {
            font-style:italic;
        }
    </style>
</head>
<body>
"""

CATEGORY_HEADER = "<h1>{category}</h1>\n"

CATEGORY_NOTE = '<p class="note">{note}</p>\n'

HTML_FOOTER = "</body></html>\n"

ICON_TABLE_HEADER = """<table><thead>
<tr><th>Name</th><th>Icon & color</th><th>Label</th><th>Comment</th></tr>
</thead><tbody>
"""

ICON_TABLE_ENTRY = """<tr><td class="name">{name}</td><td style="color: #{color};">{icon}</td><td>{label}</td><td class="desc">{comment}</td></tr>
"""

ICON_TABLE_ENTRY_CONTENTS = """<tr><td colspan="4">{contents}</td></tr>
"""

ICON_TABLE_ENTRY_DESCRIPTION = """<tr><td colspan="4" class="desc">{description}</td></tr>
"""

ICON_TABLE_FOOTER = "</tbody></table>\n"

def do_work(input_path, output_path):
    with open(input_path, "r") as input, open(output_path, "wb") as output:
        output.write(HTML_HEADER)
        data = json.load(input)
        for category_name, category_data in data.iteritems():
            output.write(CATEGORY_HEADER.format(category = category_name))
            
            note = category_data.get("notes")
            if note:
                output.write(CATEGORY_NOTE.format(note=note.replace('\n', '<br />\n')))
            
            
            output.write(ICON_TABLE_HEADER)
            
            default_color = category_data.get('color', "000000")
            default_icon = category_data.get('icon', '<whoops>')
            for tag_name, tag_data in sorted(category_data['tags'].items(), key=lambda (k,v) : v):
                tmp = dict()
                tmp['name'] = tag_name
                tmp['color'] = tag_data.get('color', default_color)
                tmp['icon'] = tag_data.get('icon', default_icon)
                tmp['label'] = tag_data['label']
                tmp['comment'] = tag_data.get('comment', "")
                
                output.write(ICON_TABLE_ENTRY.format(**tmp))
                output.write(ICON_TABLE_ENTRY_CONTENTS.format(contents = tag_data['contents']))
                
                description = tag_data.get('description')
                if description:
                    output.write(ICON_TABLE_ENTRY_DESCRIPTION.format(description = description))
            
            output.write(ICON_TABLE_FOOTER)
            
        output.write(HTML_FOOTER)
if __name__ == "__main__":
    if not path.isdir(OUTPUT_FOLDER):
        os.mkdir(OUTPUT_FOLDER)

    do_work(INPUT_FILE, OUTPUT_FILE)