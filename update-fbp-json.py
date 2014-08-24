import sys
import os
import json

"""Generate a fbp.json file with component declarations, so that we can list them."""

# fbp.json is proposed work-in-process to replace custom package/component/microflo.json files
# https://github.com/noflo/noflo/issues/247

if __name__ == '__main__':
    component_dir = 'src/main/java/com/jpmorrsn/fbp/components'
    java_files = [n for n in os.listdir(component_dir) if n.endswith('.java')]
    components = {}
    for filename in java_files:
        component_name = filename.rstrip('.java')
        path = os.path.join(component_dir, filename)
        components[component_name] = path

    f = open('fbp.json', 'w')
    f.write(json.dumps({'components': components}, indent=4, sort_keys=True))
    f.close()
