package com.zarbosoft.semicompiled;

import com.zarbosoft.semicompiled.html.ContainerElement;
import com.zarbosoft.semicompiled.html.Element;
import com.zarbosoft.semicompiled.html.Piece;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Generator {
  public List<ROPair<TestGroup, List<Piece>>> topGroups = new ArrayList<>();

  public static void writePage(Path path, String title, Stream<Piece> body) {
    try {
      if (Files.exists(path)) throw new RuntimeException("Path already exists: " + path);
      try (OutputStream os = Files.newOutputStream(path)) {
        Stream.of(
                Stream.of("<!DOCTYPE html>"),
                Element.container("html")
                    .child(
                        Element.container("head")
                            .child(Element.text("meta").att("charset", "utf-8"))
                            .child(
                                Element.text("meta")
                                    .att("name", "viewport")
                                    .att("content", "width=device-width, initial-scale=1"))
                            .child(Element.text("title").tf("%s - Semicompiled", title))
                            .child(
                                Element.text("link")
                                    .att("rel", "stylesheet")
                                    .att(
                                        "href",
                                        "https://cdn.jsdelivr.net/gh/highlightjs/cdn-release@9.17.1/build/styles/default.min.css"))
                            .child(
                                Element.text("script")
                                    .att(
                                        "src",
                                        "https://cdn.jsdelivr.net/gh/highlightjs/cdn-release@9.17.1/build/highlight.min.js"))
                            .child(
                                Element.text("script")
                                    .att(
                                        "src",
                                        "https://cdn.jsdelivr.net/npm/highlightjs-line-numbers.js@2.7.0/dist/highlightjs-line-numbers.min.js"))
                            .child(
                                Element.text("script")
                                    .t("hljs.initHighlightingOnLoad();\n")
                                    .t("hljs.initLineNumbersOnLoad();\n"))
                            .child(
                                Element.simple("link")
                                    .att("rel", "preconnect")
                                    .att("href", "https://fonts.googleapis.com"))
                            .child(
                                Element.simple("link")
                                    .att("rel", "preconnect")
                                    .att("href", "https://fonts.gstatic.com")
                                    .att("crossorigin"))
                            .child(
                                Element.simple("link")
                                    .att("rel", "stylesheet")
                                    .att(
                                        "href",
                                        "https://fonts.googleapis.com/css2?family=Roboto:wght@100;300;400;500&display=swap"))
                            .child(
                                Element.text("link")
                                    .att("rel", "stylesheet")
                                    .att("href", "style.css"))
                            .child(Element.text("script").att("src", "util.js")))
                    .child(
                        Element.container("body")
                            .childN(body)
                            .child(
                                Element.div()
                                    .att("class", "footer")
                                    .child(
                                        Element.p()
                                            .t("Enjoy a thriving social scene at ")
                                            .child(
                                                Element.text("a")
                                                    .att(
                                                        "href",
                                                        "https://github.com/andrewbaxter/semicompiled")
                                                    .t("github.")))))
                    .build())
            .flatMap(s -> s)
            .forEachOrdered(
                l -> {
                  try {
                    os.write(l.getBytes(StandardCharsets.UTF_8));
                  } catch (IOException e) {
                    throw new RuntimeException(e);
                  }
                });
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static ContainerElement tocGroup(String title, Stream<Piece> children) {
    final ContainerElement group = Element.container("div").att("class", "group");
    group.child(Element.text("span").t(title));
    group.child(Element.container("div").childNSep(children));
    return group;
  }

  public static ContainerElement pageSection(String title) {
    final ContainerElement group = Element.container("div").att("class", "section");
    group.child(Element.text("span").att("class", "section-title").t(title));
    return group;
  }

  public static ContainerElement twoCols() {
    return Element.div().att("class", "twocols");
  }

  public static String leafPath(Test leaf) {
    return String.format("%s.html", leaf.id);
  }

  public static <T> T walkGroup(TestGroup group, BiFunction<TestGroup, List<T>, T> inner) {
    List<T> res = new ArrayList<>();
    for (TestGroup subsection : group.subsections) {
      res.add(walkGroup(subsection, inner));
    }
    return inner.apply(group, res);
  }

  public static ContainerElement body() {
    return Element.div().att("class", "body");
  }

  public void section(TestGroup group, Stream<Piece> details) {
    topGroups.add(new ROPair<>(group, details.collect(Collectors.toList())));
  }

  public void build() {
    Path root = Paths.get("public").toAbsolutePath();
    if (Files.exists(root)) Utils.recursiveDelete(root);
    try {
      Files.createDirectories(root);
      final Path utilsPath = root.resolve("util.js");
      if (!Files.exists(utilsPath)) Files.copy(root.getParent().resolve("util.js"), utilsPath);
      final Path stylePath = root.resolve("style.css");
      if (!Files.exists(stylePath)) Files.copy(root.getParent().resolve("style.css"), stylePath);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // Make index
    writePage(
        root.resolve("index.html"),
        "Semicompiled",
        Stream.of(
            body()
                .childN(
                    Stream.of(
                        Element.h1().t("Semicompiled"),
                        Element.h2()
                            .t(
                                "A spectrum of intermediate representation examples for compiler writers, with corresponding source code."),
                        twoCols()
                            .childN(topGroups.stream().map(group -> buildIndexSection(group)))))));

    // Make leaf pages
    for (ROPair<TestGroup, List<Piece>> topSection : topGroups) {
      walkGroup(
          topSection.first,
          (group, ignored) -> {
            for (Threads.Future<Test> test : group.leaves) {
              writeTestPage(root, topSection, test.get());
            }
            return null;
          });
    }
  }

  private void writeTestPage(Path root, ROPair<TestGroup, List<Piece>> topSection, Test test) {
    writePage(
        root.resolve(leafPath(test)),
        String.format("%s - Semicompiled", test.title),
        Stream.of(
            body()
                .childN(Stream.of(Element.h1().t("Semicompiled"), Element.h2().t(test.title)))
                .childNSep(
                    Utils.streamCat(
                        test.files.entrySet().stream()
                            .map(
                                f ->
                                    pageSection(f.getKey())
                                        .child(
                                            Element.div()
                                                .att("class", "tabs")
                                                .childNSep(
                                                    test.files.entrySet().stream()
                                                        .flatMap(
                                                            e -> e.getValue().keySet().stream())
                                                        .distinct()
                                                        .map(
                                                            e ->
                                                                Element.text("a")
                                                                    .att(
                                                                        "class", "sc-output-button")
                                                                    .att("href", "#" + e)
                                                                    .att("data-output", e)
                                                                    .t(e))))
                                        .child(sectionPostGap())
                                        .childN(
                                            f.getValue().entrySet().stream()
                                                .map(
                                                    c ->
                                                        Element.container("div")
                                                            .att("class", "pre-outer sc-output")
                                                            .att("data-output", c.getKey())
                                                            .att("style", "display: none;")
                                                            .child(
                                                                Element.container("pre")
                                                                    .child(
                                                                        Element.text("code")
                                                                            .att(
                                                                                "class",
                                                                                "language-"
                                                                                    + c.getValue()
                                                                                        .first)
                                                                            .t(
                                                                                c.getValue()
                                                                                    .second)))))),
                        test.notes.stream())),
            Element.div()
                .att("class", "sidebar")
                .child(
                    tocTop()
                        .child(
                            Element.text("a")
                                .att("href", "index.html")
                                .t("\uD83E\uDC68 " + topSection.first.title))
                        .childN(buildTestPageTocLeaves(test, topSection.first))
                        .childN(
                            topSection.first.subsections.stream()
                                .map(
                                    subTopSection ->
                                        walkGroup(
                                            subTopSection,
                                            (sec, children) ->
                                                tocGroup(
                                                    sec.title,
                                                    Utils.streamCat(
                                                        buildTestPageTocLeaves(test, sec),
                                                        children.stream()))))))));
  }

  private Stream<Piece> buildTestPageTocLeaves(Test test, TestGroup sec) {
    return sec.leaves.stream()
        .map(
            leaf ->
                Element.text("a")
                    .att("href", leafPath(leaf.get()))
                    .att("data-selected", leaf.get().id.equals(test.id) ? "true" : "false")
                    .t(leaf.get().title));
  }

  private ContainerElement buildIndexSection(ROPair<TestGroup, List<Piece>> group0) {
    final List<Piece> inject = group0.second;
    final TestGroup group = group0.first;
    return pageSection(group.title)
        .child(sectionPostGap())
        .childN(inject.stream())
        .child(
            tocTop()
                .childN(buildIndexSectionLeaves(group.leaves))
                .childN(
                    group.subsections.stream()
                        .map(
                            ts ->
                                walkGroup(
                                    ts,
                                    (g, children) ->
                                        tocGroup(
                                            g.title,
                                            Utils.streamCat(
                                                buildIndexSectionLeaves(g.leaves),
                                                children.stream()))))));
  }

  private Stream<Piece> buildIndexSectionLeaves(List<Threads.Future<Test>> tests) {
    return tests.stream()
        .map(leaf -> Element.text("a").att("href", leafPath(leaf.get())).t(leaf.get().title));
  }

  private ContainerElement tocTop() {
    return Element.div().att("class", "toc");
  }

  private Piece sectionPostGap() {
    return Element.container("div").att("class", "section-gap");
  }
}
