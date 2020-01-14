from pathlib import Path
from collections import defaultdict
import subprocess
import re
import tempfile
import shutil
import html


out_root = Path() / 'public'
out_root.mkdir(exist_ok=True)


def get_regex(pattern, text, then=None):
    out = re.search(pattern, text, flags=re.M)
    if out:
        out = out.group(1)
        if then:
            out = then(out)
    return out


def write_menu(out, name, children):
    out.write('<div class="column is-2 is-offset-1">\n')
    out.write('<div class="container">\n')
    out.write('<aside class="menu">\n')
    out.write('<p class="menu-label">Semicompiled</p>\n')
    out.write('<ul class="menu-list">\n')
    for othertitle, othername, _ in generators:
        out.write('<li>\n')
        out.write('<a href="{}" class="{}">{}</a>\n'.format(
            othername,
            'is-active' if name == othername else '',
            othertitle,
        ))
        if children:
            out.write('<ul>\n')
            for child_name, child_link in children:
                out.write('<li><a href="{}">{}</a></li>\n'.format(
                    child_link, child_name))
            out.write('</ul>\n')
        out.write('</li>\n')
    out.write('<li><a href="https://gitlab.com/rendaw/semicompiled">(gitlab)</a></li>\n')
    out.write('</ul>\n')
    out.write('</aside>\n')
    out.write('</div>\n')  # container
    out.write('</div>\n')  # column


def do_java(out):
    root = Path() / 'java'

    tmp_ctx = tempfile.TemporaryDirectory()
    tmp = Path(tmp_ctx.name)

    targets = ['8']

    class Example:
        def __init__(self, **kwargs):
            for k, v in kwargs.items():
                setattr(self, k, v)

    categories = defaultdict(lambda: [])
    for version in root.iterdir():
        for example in version.iterdir():
            source = example.read_text()
            category = get_regex('^// Category: (.*)$', source) or 'General'
            dest = tmp / example.name
            bytecodes = dict()
            start_target = targets.index(version.name)
            for target in targets[start_target:]:
                compiled = tmp / (example.stem + '.class')
                shutil.copy(example, dest)
                subprocess.check_call([
                    'javac', '-source', version.name, '-target', target, dest
                ])
                bytecode = subprocess.check_output([
                    'javap', '-c', '-s', '-p', '-l', '-constants', '-v', compiled
                ]).decode('utf-8')
                bytecodes[target] = bytecode
            categories[category].append(Example(
                title=get_regex('^// Title: (.*)$', source) or example.stem,
                source=source,
                bytecodes=bytecodes,
                order=get_regex('^// Order: (.*)$', source, lambda m: int(m)) or 0,
            ))

    category_order = [
        'Basic',
        'Static',
        'Flow',
        'Math',
    ]
    write_menu(
        out,
        'java.html',
        [[category, '#' + category] for category in category_order])

    out.write('<div class="column is-8">\n')
    out.write('<section class="section">\n')
    out.write('<div class="container">\n')
    out.write('<h1 class="title is-2">Java Bytecode Examples</h1>\n')
    out.write('<h1 class="subtitle is-3">Know these, know Java bytecode (maybe)</h1>\n')
    out.write('</div>\n')
    out.write('</section>\n')
    for category in category_order:
        examples = categories.pop(category)
        out.write('<section class="section">\n')
        out.write('<div class="container">\n')
        out.write('<a name="{}" href="#{}"><h1 class="title">{}</h1></a>\n'.format(
            category,
            category,
            category,
        ))
        for example in sorted(examples, key=lambda e: e.order):
            source = example.source
            bytecodes = example.bytecodes
            out.write('<h2 class="title is-4">{}</h2>\n'.format(example.title))
            out.write('<div class="columns">\n')

            out.write('<div class="column is-half">\n')
            out.write('<figure><pre><code class="language-java">{}</code></pre></figure>'.format(
                html.escape(source),
            ))
            out.write('</div>\n')  # column
            out.write('<div class="column is-half">\n')

            bc_keys = list(bytecodes.keys())
            out.write('<div class="tabs">\n')
            out.write('<ul>\n')
            for key in bc_keys:
                out.write('<li data="tab-bytecode-{}">Java {}</li>\n'.format(
                    key, key))
            out.write('</ul>\n')
            out.write('</div>\n')  # tabs
            for key in bc_keys:
                out.write('<figure data="tab-bytecode-{}"><pre><code class="plaintext">{}</code></pre></figure>\n'.format(
                    key, html.escape(bytecodes[key]),
                ))
            out.write('</div>\n')  # column

            out.write('</div>\n')  # columns
        out.write('</div>\n')  # container
        out.write('</section>\n')
    if categories:
        raise RuntimeError('Missing category order for {}'.format(list(categories.keys())))
    out.write('</div>\n')  # column


generators = [
    ('Java', 'java.html', do_java)
]

for title, name, method in generators:
    with (out_root / name).open('wt') as out:
        out.write('<!DOCTYPE html>\n')
        out.write('<html>\n')
        out.write('  <head>\n')
        out.write('    <meta charset="utf-8">\n')
        out.write('    <meta name="viewport" content="width=device-width, initial-scale=1">\n')
        out.write('    <title>Semicompiled - {}</title>\n'.format(title))
        out.write('    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bulma@0.8.0/css/bulma.min.css">\n')
        out.write('    <script defer src="https://use.fontawesome.com/releases/v5.3.1/js/all.js"></script>\n')
        out.write('    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/highlightjs/cdn-release@9.17.1/build/styles/default.min.css">\n')
        out.write('    <script src="https://cdn.jsdelivr.net/gh/highlightjs/cdn-release@9.17.1/build/highlight.min.js"></script>\n')
        out.write('    <script src="https://cdn.jsdelivr.net/npm/highlightjs-line-numbers.js@2.7.0/dist/highlightjs-line-numbers.min.js"></script>\n')
        out.write('    <script>\n')
        out.write('      hljs.initHighlightingOnLoad();\n')
        out.write('      hljs.initLineNumbersOnLoad();\n')
        out.write('    </script>\n')
        out.write('    <style>\n')
        out.write('      .hljs-ln-n {\n')
        out.write('          margin-right: 1em;\n')
        out.write('      }\n')
        out.write('    </style>\n')
        out.write('  </head>\n')
        out.write('  <body>\n')
        out.write('\n')

        out.write('  <nav class="navbar" role="navigation">\n')
        out.write('  </nav>\n')

        out.write('<div class="columns">\n')

        do_java(out)

        out.write('</div>\n')  # columns

        out.write('\n')
        out.write('  </body>\n')
        out.write('</html>\n')
