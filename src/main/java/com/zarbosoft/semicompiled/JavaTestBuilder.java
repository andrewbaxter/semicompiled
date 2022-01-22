package com.zarbosoft.semicompiled;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

import javax.lang.model.element.Modifier;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;

public class JavaTestBuilder {
  private static final String PACKAGE = "com.zarbosoft.semicompiled";
  private static final ClassName MAIN_NAME = ClassName.get(PACKAGE, "Main");
  public final String title;
  public final String id;
  private final List<ROPair<ClassName, TypeSpec>> classes = new ArrayList<>();
  private TypeSpec.Builder mainSpec = null;

  public JavaTestBuilder(String id, String title) {
    this.id = id;
    this.title = title;
  }

  private static Path pathSuffix(Path path, String s) {
    return path.getParent().resolve(path.getFileName().toString().replaceFirst("\\.[^/]+$", s));
  }

  public JavaTestBuilder edit(Consumer<JavaTestBuilder> inner) {
    inner.accept(this);
    return this;
  }

  public ClassName nestedName(String suffix) {
    return MAIN_NAME.nestedClass(suffix);
  }

  public ClassName name(String suffix) {
    return ClassName.get(PACKAGE, suffix);
  }

  public JavaTestBuilder class_(ClassName name, Consumer<TypeSpec.Builder> inner) {
    final TypeSpec.Builder builder = TypeSpec.classBuilder(name);
    inner.accept(builder);
    classes.add(new ROPair<>(name, builder.build()));
    return this;
  }

  public JavaTestBuilder interface_(ClassName name, Consumer<TypeSpec.Builder> inner) {
    final TypeSpec.Builder builder = TypeSpec.interfaceBuilder(name);
    inner.accept(builder);
    classes.add(new ROPair<>(name, builder.build()));
    return this;
  }

  public JavaTestBuilder method(MethodSpec spec) {
    if (mainSpec == null) mainSpec = TypeSpec.classBuilder(MAIN_NAME);
    mainSpec.addMethod(spec);
    return this;
  }

  public JavaTestBuilder static0Args(CodeBlock code) {
    if (mainSpec == null) mainSpec = TypeSpec.classBuilder(MAIN_NAME);
    mainSpec.addMethod(
        MethodSpec.methodBuilder("myMain")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(TypeName.VOID)
            .addCode(code)
            .build());
    return this;
  }

  public JavaTestBuilder static1Args(Type type, CodeBlock code) {
    if (mainSpec == null) mainSpec = TypeSpec.classBuilder(MAIN_NAME);
    mainSpec.addMethod(
        MethodSpec.methodBuilder("myMain")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(TypeName.VOID)
            .addParameter(type, "x")
            .addCode(code)
            .build());
    return this;
  }

  public JavaTestBuilder main(CodeBlock main) {
    if (mainSpec == null) mainSpec = TypeSpec.classBuilder(MAIN_NAME);
    mainSpec.addMethod(
        MethodSpec.methodBuilder("main")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(TypeName.VOID)
            .addParameter(String[].class, "args")
            .addCode(main)
            .build());
    return this;
  }

  public JavaTestBuilder editMain(Consumer<TypeSpec.Builder> inner) {
    if (mainSpec == null) mainSpec = TypeSpec.classBuilder(MAIN_NAME);
    inner.accept(mainSpec);
    return this;
  }

  public Threads.Future<Test> build() {
    return Threads.launch(
        () -> {
          try (Tempdir dir = new Tempdir(String.format("semicompiled-%s-", id))) {

            // Finish main class
            if (mainSpec != null) classes.add(0, new ROPair<>(MAIN_NAME, mainSpec.build()));

            // Prep
            Files.createDirectories(dir.path);

            Test out = new Test(id, title);

            List<Path> sources = new ArrayList<>();

            // Write source
            for (ROPair<ClassName, TypeSpec> klass : classes) {
              Path classPath =
                  JavaFile.builder(klass.first.packageName(), klass.second)
                      .build()
                      .writeToPath(dir.path);
              sources.add(classPath);
              out.files
                  .computeIfAbsent(classPath.getFileName().toString(), k -> new LinkedHashMap<>())
                  .put("Source", new ROPair<>("java", formatJava(Files.readString(classPath))));
            }

            for (boolean debug : new boolean[] {false, true}) {
              // Compile
              {
                List<String> command = new ArrayList<>();
                command.add("javac");
                command.add("-source");
                command.add("1.8");
                command.add("-target");
                command.add("1.8");
                command.add("-parameters");
                if (debug) {
                  command.add("-g");
                } else {
                  command.add("-g:none");
                }
                for (Path source : sources) {
                  command.add(source.toString());
                }
                final Process compileProc = new ProcessBuilder(command).inheritIO().start();
                if (compileProc.waitFor() != 0) throw new RuntimeException(id);
              }

              for (Path source : sources) {
                final String classPath = pathSuffix(source, ".class").toString();

                // Decompile
                {
                  List<String> command = new ArrayList<>();
                  command.add("javap");
                  command.add("-c");
                  command.add("-s");
                  command.add("-p");
                  command.add("-l");
                  command.add("-constants");
                  command.add("-v");
                  command.add(classPath);
                  final ProcessBuilder decompileProcBuilder = new ProcessBuilder(command);
                  decompileProcBuilder.inheritIO();
                  decompileProcBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);
                  final Process decompileProc = decompileProcBuilder.start();
                  try (InputStream s = decompileProc.getInputStream()) {
                    final String bytecode = new String(s.readAllBytes(), StandardCharsets.UTF_8);
                    final LinkedHashMap<String, ROPair<String, String>> outFile =
                        out.files.get(source.getFileName().toString());
                    if (debug) {
                      outFile.put("Bytecode (debug)", new ROPair<>("plaintext", bytecode));
                    } else {
                      outFile.put("Bytecode", new ROPair<>("plaintext", bytecode));
                    }
                  }
                  if (decompileProc.waitFor() != 0) throw new RuntimeException(id);
                }

                // ASMify
                {
                  final ASMifier asmifier = new ASMifier();
                  final ByteArrayOutputStream asmBuffer = new ByteArrayOutputStream();
                  new ClassReader(Files.readAllBytes(Paths.get(classPath)))
                      .accept(new TraceClassVisitor(null, asmifier, new PrintWriter(asmBuffer)), 0);
                  final String asm = new String(asmBuffer.toByteArray(), StandardCharsets.UTF_8);
                  final LinkedHashMap<String, ROPair<String, String>> outFile =
                      out.files.get(source.getFileName().toString());
                  if (debug) {
                    outFile.put("ASMifier (debug)", new ROPair<>("java", formatJava(asm)));
                  } else {
                    outFile.put("ASMifier", new ROPair<>("java", formatJava(asm)));
                  }
                }
              }
            }

            return out;
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });
  }

  private String formatJava(String source) {
    try {
      return new Formatter().formatSource(source);
    } catch (FormatterException e) {
      throw new RuntimeException("Source:\n" + source, e);
    }
  }
}
