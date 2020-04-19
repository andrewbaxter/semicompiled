from pathlib import Path
from collections import defaultdict
import subprocess
import re
import tempfile
import shutil
import html
from abc import ABCMeta, abstractmethod
from copy import deepcopy


here = Path()

cache = here / 'semicache'
cache.mkdir(exist_ok=True)

out_root = Path() / 'public'
out_root.mkdir(exist_ok=True)

java_filename = 'index.html'
ccxx_filename = 'ccxx.html'


def get_regex(pattern, text, then=None):
    out = re.search(pattern, text, flags=re.M)
    if out:
        out = out.group(1)
        if then:
            out = then(out)
    return out


class Example:
    def __init__(self, **kwargs):
        for k, v in kwargs.items():
            setattr(self, k, v)


class Bytecode:
    def __init__(self, **kwargs):
        for k, v in kwargs.items():
            setattr(self, k, v)


class BuildBase(metaclass=ABCMeta):
    @abstractmethod
    def filename(self):
        pass

    @abstractmethod
    def category_order(self):
        pass

    @abstractmethod
    def categories(self):
        pass

    @abstractmethod
    def title(self):
        pass


class BuildCCXX(BuildBase):
    def title(self):
        return 'C/C++ IR'

    def filename(self):
        return 'ccxx.html'

    def categories(self):
        return self._categories

    def category_order(self):
        return self._category_order

    def __init__(self):
        root = Path() / 'ccxx'

        def decompile_normal(dest):
            return subprocess.check_output([
                'clang', '-O0', '-ggdb', '-S', dest, '-o', '-'
            ]).decode('utf-8')

        def decompile_asm(dest):
            return subprocess.check_output([
                'clang', '-O0', '-ggdb', '-S', '-emit-llvm', dest, '-o', '-'
            ]).decode('utf-8')

        categories = defaultdict(lambda: [])
        for example in root.iterdir():
            category, order, title = example.name.rsplit('.', 1)[0].split('_')
            source = example.read_text()
            bytecodes = dict()
            bytecodes['asm'] = Bytecode(
                title='ASM',
                text=decompile_normal(example),
                language='plaintext',
            )
            bytecodes['llvmir'] = Bytecode(
                title='LLVM IR',
                text=decompile_asm(example),
                language='plaintext',
            )
            categories[category].append(Example(
                id=example.stem,
                title=title,
                source=source,
                bytecodes=bytecodes,
                order=order,
            ))

        self._category_order = [
            'Basic',
            'Flow',
        ]

        for category in self._category_order:
            categories[category] = sorted(categories[category], key=lambda e: e.order)

        self._categories = categories


class BuildJava(BuildBase):
    def title(self):
        return 'Java Bytecode'

    def filename(self):
        return 'index.html'

    def categories(self):
        return self._categories

    def category_order(self):
        return self._category_order

    def __init__(self):
        asm_jar = 'asm-all-5.2.jar'
        format_jar = 'google-java-format-1.7-all-deps.jar'
        for parent, filename in [
            ('https://repository.ow2.org/nexus/content/repositories/releases/org/ow2/asm/asm-all/5.2/', asm_jar),
            ('https://github.com/google/google-java-format/releases/download/google-java-format-1.7/', format_jar),
        ]:
            if (cache / filename).exists():
                continue
            subprocess.check_call(['wget', parent + filename], cwd=cache)

        root = Path() / 'java'

        tmp_ctx = tempfile.TemporaryDirectory()
        tmp = Path(tmp_ctx.name)

        targets = ['8']

        class Example:
            def __init__(self, **kwargs):
                for k, v in kwargs.items():
                    setattr(self, k, v)

        class Bytecode:
            def __init__(self, **kwargs):
                for k, v in kwargs.items():
                    setattr(self, k, v)

        def decompile_normal(dest):
            return subprocess.check_output([
                'javap', '-c', '-s', '-p', '-l', '-constants', '-v', compiled
            ]).decode('utf-8')

        def decompile_asm(dest):
            asm = subprocess.check_output([
                'java', '-classpath', ':'.join(map(str, [cache / asm_jar, dest.parent])), 'org.objectweb.asm.util.ASMifier', '-debug', dest.stem
            ]).decode('utf-8')
            return subprocess.check_output([
                'java', '-jar', cache / format_jar, '-'
            ], input=asm.encode('utf-8')).decode('utf-8')

        categories = defaultdict(lambda: [])
        for version in root.iterdir():
            for cat_order in version.iterdir():
                category, order = cat_order.name.split('_')
                order = int(order)
                for example in cat_order.iterdir():
                    source = example.read_text()
                    dest = tmp / example.name
                    bytecodes = dict()
                    start_target = targets.index(version.name)
                    for target in targets[start_target:]:
                        compiled = tmp / (example.stem + '.class')
                        shutil.copy(example, dest)
                        subprocess.check_call([
                            'javac', '-source', version.name, '-target', target, dest
                        ])
                        bytecodes[target] = Bytecode(
                            title='Java {}'.format(target),
                            text=decompile_normal(dest),
                            language='plaintext',
                        )
                        if target == targets[-1]:
                            bytecodes['asm_{}'.format(target)] = Bytecode(
                                title='Java {}/ASM'.format(target),
                                text=decompile_asm(dest),
                                language='language-java',
                            )
                    categories[category].append(Example(
                        id=example.stem,
                        title=get_regex('^// Title: (.*)$', source) or example.stem,
                        source=source,
                        bytecodes=bytecodes,
                        order=order,
                    ))

        self._category_order = [
            'Basic',
            'Static',
            'Instance',
            'Flow',
            'Operators',
            'Other',
        ]

        for category in self._category_order:
            categories[category] = sorted(categories[category], key=lambda e: e.order)

        self._categories = categories


generators = [
    BuildJava(),
    BuildCCXX(),
]

for build in generators:
    with (out_root / build.filename()).open('wt') as out:
        out.write('<!DOCTYPE html>\n')
        out.write('<html>\n')
        out.write('  <head>\n')
        out.write('    <meta charset="utf-8">\n')
        out.write('    <meta name="viewport" content="width=device-width, initial-scale=1">\n')
        out.write('    <title>Semicompiled - {}</title>\n'.format(build.title()))
        out.write('    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bulma@0.8.0/css/bulma.min.css">\n')
        out.write('    <script defer src="https://use.fontawesome.com/releases/v5.3.1/js/all.js"></script>\n')
        out.write('    <link rel="stylesheet" href="https://cdn.jsdelivr.net/gh/highlightjs/cdn-release@9.17.1/build/styles/default.min.css">\n')
        out.write('    <script src="https://cdn.jsdelivr.net/gh/highlightjs/cdn-release@9.17.1/build/highlight.min.js"></script>\n')
        out.write('    <script src="https://cdn.jsdelivr.net/npm/highlightjs-line-numbers.js@2.7.0/dist/highlightjs-line-numbers.min.js"></script>\n')
        out.write('    <script>\n')
        out.write('      hljs.initHighlightingOnLoad();\n')
        out.write('      hljs.initLineNumbersOnLoad();\n')
        out.write('    </script>\n')
        out.write('''    <style>
.bd-anchor-title {
  padding-top: 1.5rem;
  position: relative;
}

.bd-anchor-link {
  position: absolute;
  right: calc(100% + 1rem);
  top: 1.5rem;
}

.hljs-ln-n {
  margin-right: 1em;
  color: steelblue;
  opacity: 70%;
}

.hljs {
  background: none;
  max-height: 90vh;
}
    </style>
''')
        out.write('    </style>\n')
        out.write('''    <script type="text/javascript">
window.addEventListener('DOMContentLoaded', (e_) => {
  for (let l of document.getElementsByClassName('sc-output-link')) (() => {
      const target = l.getAttribute('data')
      l.addEventListener('click', (e_) => {
        for (let l2 of document.getElementsByClassName('sc-output-link')) {
          if (l2.getAttribute('data') == target) {
            l2.classList.add('is-active');
          } else {
            l2.classList.remove('is-active');
          }
        }
        for (let t of document.getElementsByClassName('sc-output')) {
          if (t.getAttribute('data') == target) {
            t.style.display = '';
          } else {
            t.style.display = 'none';
          }
        }
      })
  })();
});
    </script>
''')
        out.write('  </head>\n')
        out.write('  <body>\n')
        out.write('\n')

        out.write('  <nav class="navbar" role="navigation">\n')
        out.write('  </nav>\n')

        out.write('<div class="columns">\n')

        # MENU
        out.write('<div class="column is-2 is-offset-1">\n')
        out.write('<div class="container">\n')
        out.write('<aside class="menu">\n')
        out.write('<p class="menu-label">Semicompiled</p>\n')
        out.write('<ul class="menu-list">\n')
        for otherbuild in generators:
            out.write('<li>\n')
            out.write('<a href="{}" class="{}">{}</a>\n'.format(
                otherbuild.filename(),
                'is-active' if build == otherbuild else '',
                otherbuild.title(),
            ))
            if build == otherbuild:
                out.write('<ul>\n')
                for child_name, child_link, grandchildren in [
                    (
                        category,
                        '{}#{}'.format('' if build == otherbuild else otherbuild.filename(), category),
                        [
                            (
                                example.title,
                                '{}#{}'.format('' if build == otherbuild else otherbuild.filename(), example.id)
                            )
                            for example in otherbuild.categories()[category]
                        ],
                    )
                    for category in otherbuild.category_order()
                ]:
                    out.write('<li>\n')
                    out.write('<a href="{}">{}</a>\n'.format(
                        child_link, child_name))
                    if grandchildren:
                        out.write('<ul>\n')
                        for grandchild_name, grandchild_link in grandchildren:
                            out.write('<li>\n')
                            out.write('<a href="{}">{}</a>\n'.format(
                                grandchild_link, grandchild_name))
                            out.write('</li>\n')
                        out.write('</ul>\n')
                    out.write('</li>\n')
                out.write('</ul>\n')

            out.write('</li>\n')
        out.write('<li><a href="https://gitlab.com/rendaw/semicompiled">(gitlab)</a></li>\n')
        out.write('</ul>\n')
        out.write('</aside>\n')
        out.write('</div>\n')  # container
        out.write('</div>\n')  # column
        # /MENU

        # BODY
        out.write('<div class="column is-8">\n')
        out.write('<section class="section">\n')
        out.write('<div class="container">\n')
        out.write('<h1 class="title is-2">{} Examples</h1>\n'.format(build.title()))
        out.write('<h1 class="subtitle is-3">Know these, know {} (maybe)</h1>\n'.format(build.title()))
        out.write('</div>\n')
        out.write('</section>\n')
        categories = deepcopy(build.categories())
        for category in build.category_order():
            examples = categories.pop(category)
            out.write('<section class="section">\n')
            out.write('<div class="container">\n')
            out.write('<h1 id="{link}" class="title bd-anchor-title"><span class="bd-anchor-name">{title}</span><a class="bd-anchor-link" href="#{link}">#</a></h1>\n'.format(
                link=category,
                title=category,
            ))
            for example in examples:
                source = example.source
                bytecodes = example.bytecodes
                out.write('<h2 id="{link}" class="title is-4 bd-anchor-title"><span class="bd-anchor-name">{title}</span><a class="bd-anchor-link" href="#{link}">#</a></h2>\n'.format(
                    link=example.id,
                    title=example.title,
                ))
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
                first = True
                for key in bc_keys:
                    klass = ''
                    if first:
                        klass = 'is-active'
                        first = False
                    out.write('<li class="sc-output-link {klass}" data="{target}"><a>{title}</a></li>\n'.format(
                        target=key,
                        klass=klass,
                        title=bytecodes[key].title,
                    ))
                out.write('</ul>\n')
                out.write('</div>\n')  # tabs
                first = True
                for key in bc_keys:
                    style = 'display: none;'
                    if first:
                        style = ''
                        first = False
                    bytecode = bytecodes[key]
                    out.write('<figure class="sc-output" data="{target}" style="{style} height: 90vh;"><pre><code class="{language}">{text}</code></pre></figure>\n'.format(
                        target=key,
                        style=style,
                        language=bytecode.language,
                        text=html.escape(bytecode.text),
                    ))
                out.write('</div>\n')  # column

                out.write('</div>\n')  # columns
            out.write('</div>\n')  # container
            out.write('</section>\n')
        if categories:
            raise RuntimeError('Missing category order for {}'.format(list(categories.keys())))
        out.write('</div>\n')  # column
        # /BODY

        out.write('</div>\n')  # columns

        out.write('\n')
        out.write('  </body>\n')
        out.write('</html>\n')
