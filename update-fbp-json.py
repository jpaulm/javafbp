import sys
import os
import json

"""Generate a fbp.json file with component declarations, so that we can list them."""

# fbp.json is proposed work-in-process to replace custom package/component/microflo.json files
# https://github.com/noflo/noflo/issues/247
def lib(component_dir, classpath):
    java_files = [n for n in os.listdir(component_dir) if n.endswith('.java')]
    components = {}
    for filename in java_files:
        component_name = os.path.splitext(filename)[0]
        components[component_name] = filename
    lib = {
        '_classpath': classpath,
        'basedir': component_dir,
        'components': components
    }
    return lib

if __name__ == '__main__':
    fbp = 'src/main/java/com/jpmorrsn/fbp'
    manifest = {
        'javafbp': {
            'libraries': {
                'core': lib(fbp+'/components', 'com.jpmorrsn.fbp.components'),
                'examples': lib(fbp+'/examples/components', 'com.jpmorrsn.fbp.examples.components'),
            }
        }
    }
    f = open('fbp.json', 'w')
    f.write(json.dumps(manifest, indent=4, sort_keys=True))
    f.close()
