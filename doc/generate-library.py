import json
import cgi
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
            font-size: 20px;
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

TOC_HEADER = '<h1 id="toc">Categories</h1>\n'

CONTENTS_HEADER = '<h1 id="contents">Contents</h1>\n'

CATEGORY_LINK = '<a href="#{id}">{category}</a>\n'

CATEGORY_HEADER = '<h2 id="{id}">{category}</h2>\n'

CATEGORY_NOTE = '<p class="note">{note}</p>\n'

HTML_FOOTER = "</body></html>\n"

ICON_TABLE_HEADER = """<table><thead>
<tr><th>Name</th><th>Icon & color</th><th>Label</th><th>Comment</th></tr>
</thead><tbody>
"""

ICON_TABLE_ENTRY = """<tr id="{id}"><td class="name">{name}</td><td style="color: #{color};">{icon}</td><td>{label}</td><td class="desc">{comment}</td></tr>
"""

ICON_TABLE_ENTRY_CONTENTS = """<tr><th>Contents:</th><td colspan="3">{contents}</td></tr>
"""

ICON_TABLE_ENTRY_DESCRIPTION = """<tr><th>Description:</th><td colspan="3" class="desc">{description}</td></tr>
"""

ICON_TABLE_FOOTER = "</tbody></table>\n"

def do_work(input_path, output_path):
    with open(input_path, "r") as input, open(output_path, "wb") as output:
        output.write(HTML_HEADER)
        data = json.load(input)
        output.write(TOC_HEADER)
        for category in data:
             output.write(CATEGORY_LINK.format(category = category['name'], id = category['name']))
        
        output.write(CONTENTS_HEADER)
        for category in data:
            output.write(CATEGORY_HEADER.format(category = category['name'], id = category['name']))
            
            note = category.get("notes")
            if note:
                output.write(CATEGORY_NOTE.format(note=note.replace('\n', '<br />\n')))
            
            
            output.write(ICON_TABLE_HEADER)
            
            default_color = category.get('color', "000000")
            default_icon = category.get('icon', '<whoops>')
            for tag in category['tags']:
                tmp = dict()
                tmp['name'] = tag['name']
                tmp['id'] = tag['name']
                tmp['color'] = tag.get('color', default_color)
                tmp['icon'] = cgi.escape(tag.get('icon', default_icon))
                tmp['label'] = cgi.escape(tag['label'])
                tmp['comment'] = tag.get('comment', "")
                
                output.write(ICON_TABLE_ENTRY.format(**tmp))
                output.write(ICON_TABLE_ENTRY_CONTENTS.format(contents = cgi.escape(tag['contents'])))
                
                description = tag.get('description')
                if description:
                    output.write(ICON_TABLE_ENTRY_DESCRIPTION.format(description = description))
            
            output.write(ICON_TABLE_FOOTER)
            
        output.write(HTML_FOOTER)
if __name__ == "__main__":
    if not path.isdir(OUTPUT_FOLDER):
        os.mkdir(OUTPUT_FOLDER)

    do_work(INPUT_FILE, OUTPUT_FILE)