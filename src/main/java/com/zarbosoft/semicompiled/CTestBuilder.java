package com.zarbosoft.semicompiled;

import com.zarbosoft.semicompiled.html.Piece;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CTestBuilder {
  private final String id;
  private final String title;
  private final String body;
  private List<Piece> notes = new ArrayList<>();
  private boolean noWasm = false;
  private boolean noEbpf = false;

  public CTestBuilder(String id, String title, String body) {
    this.id = id;
    this.title = title;
    this.body = body;
  }

  public CTestBuilder noEbpf(boolean condition, Piece note) {
    if (condition) {
      noEbpf = true;
      notes.add(note);
    }
    return this;
  }

  public CTestBuilder noWasm(boolean condition, Piece note) {
    if (condition) {
      noWasm = true;
      notes.add(note);
    }
    return this;
  }

  public CTestBuilder notes(Stream<Piece> notes) {
    this.notes.addAll(notes.collect(Collectors.toList()));
    return this;
  }

  public Threads.Future<Test> build() {
    return Threads.launch(
        () -> {
          try (Tempdir dir = new Tempdir(String.format("semicompiled-%s-", id))) {
            Test out = new Test(id, title, notes);
            Path source = dir.path.resolve("source.c");
            Files.write(source, body.getBytes(StandardCharsets.UTF_8));
            Utils.run("clang-format", "--style", "microsoft", "-i", source.toString());
            final LinkedHashMap<String, ROPair<String, String>> outFile =
                out.files.computeIfAbsent(
                    source.getFileName().toString(), k -> new LinkedHashMap<>());
            outFile.put("Source", new ROPair<>("c", Files.readString(source)));
            for (boolean debug : new boolean[] {false, true}) {
              String ir =
                  Utils.runOutput(
                      ListBuilder.of("clang")
                          .addIf(debug, "-ggdb")
                          .add(
                              "-c",
                              "-std=c99",
                              "-O0",
                              "-S",
                              "-emit-llvm",
                              "-o",
                              "-",
                              source.toString()));
              if (debug) {
                outFile.put("LLVM IR (debug)", new ROPair<>("plaintext", ir));
              } else {
                outFile.put("LLVM IR", new ROPair<>("plaintext", ir));
              }
            }
            if (!noWasm) {
              final Path wasmPath = source.getParent().resolve("source.wasm");
              Utils.run(
                  "emcc",
                  "-std=c99",
                  "-O0",
                  "-s",
                  "WASM=1",
                  "-s",
                  "ERROR_ON_UNDEFINED_SYMBOLS=0",
                  "-s",
                  "SIDE_MODULE=1",
                  source.toString(),
                  "-o",
                  wasmPath.toString());
              outFile.put(
                  "WASM (WAT)",
                  new ROPair<>("plaintext", Utils.runOutput("wasm2wat", wasmPath.toString())));
            }
            if (!noEbpf) {
              Path bpfPath = source.getParent().resolve("source.o");
              Utils.run(
                  "clang",
                  "-std=c99",
                  "-ggdb",
                  "-c",
                  "-O0",
                  "--target=bpf",
                  "-o",
                  bpfPath.toString(),
                  source.toString());
              final String[] preSections =
                  Utils.runOutput("llvm-objdump", "-h", bpfPath.toString()).split("\\r?\\n");
              int i = 0;
              for (; i < preSections.length; i += 1) {
                if (preSections[i].startsWith("Idx ")) {
                  break;
                }
              }
              List<String> sections = new ArrayList<>();
              for (i += 1; i < preSections.length; i += 1) {
                String line = preSections[i].strip();
                if (line.isEmpty()) continue;
                final String[] parts = line.split("\\s+");
                String section = parts[1];
                String[] sectionParts = section.split("\\.");
                if (sectionParts.length < 2) continue;
                String mainSection = sectionParts[1];
                switch (mainSection) {
                  case "text":
                  case "rodata":
                    sections.add(section);
                    break;
                }
              }
              outFile.put(
                  "eBPF (objdump)",
                  new ROPair<>(
                      "plaintext",
                      Utils.runOutput(
                          ListBuilder.of("llvm-objdump", "-l", "-D", "-S", "--no-show-raw-insn")
                              .addStream(sections.stream().flatMap(s -> Stream.of("-j", s)))
                              .add(bpfPath.toString()))));
            }

            dir.manualClean();

            return out;
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });
  }
}
