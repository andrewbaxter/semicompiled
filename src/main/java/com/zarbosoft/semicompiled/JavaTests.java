package com.zarbosoft.semicompiled;

import com.google.googlejavaformat.java.Formatter;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaTests {
  public static TestGroup build() {
    try {
      final Field lineLength = Formatter.class.getField("MAX_LINE_LENGTH");
      lineLength.setAccessible(true);
      Field modifiersField = Field.class.getDeclaredField("modifiers");
      modifiersField.setAccessible(true);
      modifiersField.setInt(
          lineLength, lineLength.getModifiers() & ~java.lang.reflect.Modifier.FINAL);
      lineLength.set(null, 75);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return new TestGroup(
        "Java",
        Stream.of(
            new JavaTestBuilder("5mKIlBrv", "Hello world")
                .method(
                    MethodSpec.methodBuilder("main")
                        .addModifiers(
                            javax.lang.model.element.Modifier.PUBLIC,
                            javax.lang.model.element.Modifier.STATIC)
                        .returns(TypeName.VOID)
                        .addParameter(String[].class, "args")
                        .addCode("$T.out.println(\"Hello world\");", System.class)
                        .build())
                .build()),
        Stream.of(
            new TestGroup(
                "Data types",
                Stream.of(),
                Stream.of(
                    javaNumericPrimitiveTestGroup("vQ2evTWW", int.class, Integer.class, true),
                    javaNumericPrimitiveTestGroup("LW2Uf666", long.class, Long.class, true),
                    javaNumericPrimitiveTestGroup("hi7uEmKj", short.class, Short.class, true),
                    javaNumericPrimitiveTestGroup("NKSMcYSr", byte.class, Byte.class, true),
                    javaNumericPrimitiveTestGroup("4IeGaY2q", float.class, Float.class, false),
                    javaNumericPrimitiveTestGroup("R8pc3gd1", double.class, Double.class, false),
                    new Supplier<TestGroup>() {
                      @Override
                      public TestGroup get() {
                        Class type = boolean.class;
                        return javaPrimitiveTestGroup(
                            "BRMXa3IX",
                            type,
                            Stream.of(false, true),
                            Stream.of(
                                new JavaTestBuilder("3zib7hCc", "Logical and")
                                    .static1Args(type, CodeBlock.of("boolean y = x && false;"))
                                    .build(),
                                new JavaTestBuilder("SJzTB2GS", "Logical or")
                                    .static1Args(type, CodeBlock.of("boolean y = x || false;"))
                                    .build(),
                                new JavaTestBuilder("RSHb74OE", "Logical not")
                                    .static1Args(type, CodeBlock.of("boolean y = !x;"))
                                    .build(),
                                new JavaTestBuilder("oVdbZjnx", "Equal")
                                    .static1Args(type, CodeBlock.of("boolean y = x == false;"))
                                    .build(),
                                new JavaTestBuilder("e8fyNApX", "Not equal")
                                    .static1Args(type, CodeBlock.of("boolean y = x != false;"))
                                    .build()));
                      }
                    }.get(),
                    new Supplier<TestGroup>() {
                      @Override
                      public TestGroup get() {
                        final Class<String> type = String.class;
                        return javaPrimitiveTestGroup(
                            "cPS2FjBM",
                            type,
                            Stream.of(null, CodeBlock.of("$S", "hi")),
                            Stream.of(
                                new JavaTestBuilder("fFeCmGOv", "Downcast")
                                    .static1Args(
                                        Object.class, CodeBlock.of("$T y = ($T) x;", type, type))
                                    .build(),
                                new JavaTestBuilder("ViHsOhZV", "Equal")
                                    .static1Args(
                                        type, CodeBlock.of("boolean y = x == $S;", "apple"))
                                    .build(),
                                new JavaTestBuilder("WZsqJkGM", "Not equal")
                                    .static1Args(type, CodeBlock.of("boolean y = x != $S;", "lift"))
                                    .build()));
                      }
                    }.get(),
                    new Supplier<TestGroup>() {
                      @Override
                      public TestGroup get() {
                        Class type = int[].class;
                        return new TestGroup(
                            "Array",
                            Stream.of(
                                java0ArgsTest(
                                    "UtByAtBM", "Declaration", CodeBlock.of("$T x;", type)),
                                java0ArgsTest(
                                    "Rmr6OoeA",
                                    "Initialization with size",
                                    CodeBlock.of("$T x = new $T[4];", type, int.class)),
                                java0ArgsTest(
                                    "vsCMaKat",
                                    "Initialization with elements",
                                    CodeBlock.of("$T x = new $T[]{3, 7};", type, int.class)),
                                java0ArgsTest(
                                    "3wgux0VJ",
                                    "Element assignment",
                                    CodeBlock.of("$T x = new $T[4];x[2] = 7;", type, int.class)),
                                java0ArgsTest(
                                    "o5eUZxzz",
                                    "Element read",
                                    CodeBlock.of(
                                        "$T x = new $T[]{3}; int y = x[0];", type, int.class))),
                            Stream.of());
                      }
                    }.get(),
                    new Supplier<TestGroup>() {
                      @Override
                      public TestGroup get() {
                        return new TestGroup(
                            "Generic",
                            Stream.of(
                                java0ArgsTest(
                                    "zCEP8I24",
                                    "Argument",
                                    CodeBlock.of(
                                        "$T x = new $T<>(); x.add($S);",
                                        List.class,
                                        ArrayList.class,
                                        "hello")),
                                java0ArgsTest(
                                    "zQSFZH7I",
                                    "Return",
                                    CodeBlock.of(
                                        "$T x = new $T<>(); $T y = x.get(0);",
                                        List.class,
                                        ArrayList.class,
                                        String.class))),
                            Stream.of());
                      }
                    }.get())),
            new TestGroup(
                "Control flow",
                Stream.of(),
                Stream.of(
                    new TestGroup(
                        "Branch",
                        Stream.of(
                            java0ArgsTest(
                                "tRjzSqTQ", "If", CodeBlock.of("int x = 3; if (true) { x = 4; }")),
                            java0ArgsTest(
                                "VBUdfIx7",
                                "If, else",
                                CodeBlock.of("int x = 3; if (x == 3) { x = 4; } else { x = 7; }")),
                            java0ArgsTest(
                                "oqzuW0cR",
                                "Branched initialization",
                                CodeBlock.of(
                                    "int x; if (4 == 2) { int y = 7; x = x + y; } else { x = 2; }")),
                            java0ArgsTest(
                                "8qc3juc9",
                                "If, else if",
                                CodeBlock.of(
                                    "int x = 3; if (x == 7) { x = 4; } else if (false) { x = 5; }")),
                            java0ArgsTest(
                                "67hsRfey",
                                "If, else if, else",
                                CodeBlock.of(
                                    "int x = 3; if (x == 9) { x = 4; } else if (x == 12) { x = 12; } else { x = 7; }"))),
                        Stream.of()),
                    new TestGroup(
                        "Loop",
                        Stream.of(
                            java0ArgsTest(
                                "LsmN0K43",
                                "While",
                                CodeBlock.of("int x = 0; while (true) { x += 1; }")),
                            java0ArgsTest(
                                "WyR4GyVs",
                                "Continue",
                                CodeBlock.of(
                                    "int x = 0; while (true) { x += 1; if (x == 3) {continue; }x += 4; }")),
                            java0ArgsTest(
                                "BiEoQy0X",
                                "Break",
                                CodeBlock.of(
                                    "int x = 0; while (true) { x += 1; if (x == 3) {break; } x += 2;}"))),
                        Stream.of()),
                    new TestGroup(
                        "Switch",
                        Stream.of(
                            java1ArgsTest(
                                "4Db5MVuN",
                                "Integer cases",
                                int.class,
                                CodeBlock.of(
                                    "int y; switch (x) { case 0: y = 0; break; case 1: y = 33; break; }")),
                            java1ArgsTest(
                                "tWLWsiZX",
                                "String cases",
                                String.class,
                                CodeBlock.of(
                                    "int y; switch (x) { case $S: y = 0; break; case $S: y = 33; break; }",
                                    "query",
                                    "midruffian")),
                            java1ArgsTest(
                                "Pt6uIbVR",
                                "Default case",
                                int.class,
                                CodeBlock.of(
                                    "int y; switch (x) { case 0: y = 0; break; case 1: y = 33; break; default: y = 72; break; }")),
                            java1ArgsTest(
                                "lNX69kKA",
                                "Fallthrough",
                                int.class,
                                CodeBlock.of(
                                    "int y; switch (x) { case 0: y = 0; case 1: y = 33; break; }")),
                            java1ArgsTest(
                                "gXQbcIfy",
                                "Return",
                                int.class,
                                CodeBlock.of(
                                    "int y; switch (x) { case 0: y = 0; break; case 1: y = 33; return; }"))),
                        Stream.of()),
                    new TestGroup(
                        "Exception",
                        Stream.of(
                            java0ArgsTest(
                                "ZmOCKIjw",
                                "Throw",
                                CodeBlock.of("throw new $T(\"Hello\");", RuntimeException.class)),
                            java0ArgsTest(
                                "2ciu6bbD",
                                "Try, finally",
                                CodeBlock.of(
                                    "try { $T.out.println(\"Okay\"); } finally { int x = 72; }",
                                    System.class)),
                            java0ArgsTest(
                                "z5IEY18T",
                                "Try, finally, break",
                                CodeBlock.of(
                                    "int z = 3; "
                                        + "for (int i = 0; i < 1; ++i) { "
                                        + "try { "
                                        + "if (z == 3) { "
                                        + "break; "
                                        + "} "
                                        + "$T.out.println(\"Okay\"); "
                                        + "} finally { int x = 72; } "
                                        + "}",
                                    System.class)),
                            java0ArgsTest(
                                "p56NWWXO",
                                "Try, catch, finally",
                                CodeBlock.of(
                                    "try { $T.out.println(\"Okay\"); } catch ($T e) { e.printStackTrace(); } finally { int w = 3131; }",
                                    System.class,
                                    RuntimeException.class)),
                            java0ArgsTest(
                                "J65TEQ3F",
                                "Try, multiple catch",
                                CodeBlock.of(
                                    "try { $T.out.println(\"Okay\"); } catch ($T e) { e.printStackTrace(); } catch ($T e) { int q = 1; }",
                                    System.class,
                                    RuntimeException.class,
                                    Throwable.class)),
                            java0ArgsTest(
                                "vgWtbZJp",
                                "Try, catch multiple types",
                                CodeBlock.of(
                                    "try { $T.out.println(\"Okay\"); } catch ($T | $T e) { e.printStackTrace(); }",
                                    System.class,
                                    NullPointerException.class,
                                    IllegalArgumentException.class)),
                            java0ArgsTest(
                                "7SQ338gD",
                                "Nested-try, catch",
                                CodeBlock.of(
                                    "try { try { $T.out.println(\"Okay\"); } catch ($T e) { e.printStackTrace(); } } catch ($T e) { e.printStackTrace(); }",
                                    System.class,
                                    NullPointerException.class,
                                    IllegalArgumentException.class)),
                            java0ArgsTest(
                                "d3kdjP66",
                                "Try, nested-catch",
                                CodeBlock.of(
                                    "try { $T.out.println(\"Okay\"); } catch ($T e) { try { e.printStackTrace(); } catch ($T e1) { e1.printStackTrace(); } }",
                                    System.class,
                                    NullPointerException.class,
                                    IllegalArgumentException.class))),
                        Stream.of()))),
            new TestGroup(
                "Static",
                Stream.of(),
                Stream.of(
                    new TestGroup(
                        "Methods",
                        Stream.of(
                            javaStaticMethodTest(
                                "89Sw7EYo",
                                "No arguments, no return",
                                MethodSpec.methodBuilder("myMethod")
                                    .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                                    .returns(TypeName.VOID)
                                    .addCode("double z = 13.7;")
                                    .build(),
                                CodeBlock.of("myMethod();")),
                            javaStaticMethodTest(
                                "MZ5dDaQh",
                                "Arguments, no return",
                                MethodSpec.methodBuilder("myMethod")
                                    .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                                    .returns(TypeName.VOID)
                                    .addParameter(int.class, "x")
                                    .addParameter(double.class, "y")
                                    .addCode("double z = x + y;")
                                    .build(),
                                CodeBlock.of("myMethod(4, 13.2);")),
                            javaStaticMethodTest(
                                "t9dx2v63",
                                "No arguments, return",
                                MethodSpec.methodBuilder("myMethod")
                                    .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                                    .returns(TypeName.INT)
                                    .addCode("return 70;")
                                    .build(),
                                CodeBlock.of("int x = myMethod();")),
                            javaStaticMethodTest(
                                "X1k3Fwfc",
                                "Arguments, return",
                                MethodSpec.methodBuilder("myMethod")
                                    .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                                    .returns(TypeName.DOUBLE)
                                    .addParameter(int.class, "x")
                                    .addParameter(double.class, "y")
                                    .addCode("return x + y;")
                                    .build(),
                                CodeBlock.of("double x = myMethod(4, 13.2);"))),
                        Stream.of()),
                    new TestGroup(
                        "Fields",
                        Stream.of(
                            new JavaTestBuilder("jWvEgSoB", "Static field")
                                .main(CodeBlock.of("x = x + 1;"))
                                .editMain(
                                    spec ->
                                        spec.addField(
                                            FieldSpec.builder(
                                                    int.class,
                                                    "x",
                                                    Modifier.STATIC,
                                                    Modifier.PUBLIC)
                                                .initializer(CodeBlock.of("4"))
                                                .build()))
                                .build(),
                            new JavaTestBuilder("Bp0rPHJh", "Static block")
                                .editMain(
                                    spec -> {
                                      spec.addField(
                                          FieldSpec.builder(
                                                  int.class, "x", Modifier.STATIC, Modifier.PUBLIC)
                                              .build());
                                      spec.addStaticBlock(CodeBlock.of("x = 13 * 23;"));
                                    })
                                .build()),
                        Stream.of()))),
            new TestGroup(
                "Class",
                Stream.of(),
                Stream.of(
                    new TestGroup(
                        "Declaration",
                        Stream.of(
                            new JavaTestBuilder("iGSOZbFe", "Empty class")
                                .edit(
                                    t -> {
                                      final ClassName name = t.name("MyClass");
                                      t.class_(name, c -> {}).main(CodeBlock.of("new $T();", name));
                                    })
                                .build(),
                            new JavaTestBuilder("7i18wrTj", "Class with constructor")
                                .edit(
                                    t -> {
                                      final ClassName name = t.name("MyClass");
                                      t.class_(
                                              name,
                                              c -> {
                                                c.addMethod(
                                                    MethodSpec.constructorBuilder()
                                                        .addCode(CodeBlock.of("int x = 4;"))
                                                        .build());
                                              })
                                          .main(CodeBlock.of("new $T();", name));
                                    })
                                .build(),
                            new JavaTestBuilder("Jur3Ngqh", "Class cross-constructor")
                                .edit(
                                    t -> {
                                      final ClassName name = t.name("MyClass");
                                      t.class_(
                                              name,
                                              c -> {
                                                c.addMethod(
                                                    MethodSpec.constructorBuilder()
                                                        .addParameter(int.class, "x")
                                                        .build());
                                                c.addMethod(
                                                    MethodSpec.constructorBuilder()
                                                        .addCode(CodeBlock.of("this(4);"))
                                                        .addCode(CodeBlock.of("int x = 4;"))
                                                        .build());
                                              })
                                          .main(CodeBlock.of("new $T();", name));
                                    })
                                .build(),
                            new JavaTestBuilder("1cKp0Aar", "Nested class")
                                .edit(
                                    t -> {
                                      final ClassName name = t.nestedName("MyClass");
                                      t.editMain(
                                          b -> {
                                            b.addType(TypeSpec.classBuilder(name).build());
                                            b.addMethod(
                                                MethodSpec.methodBuilder("otherMethod")
                                                    .addCode(CodeBlock.of("new $T();", name))
                                                    .build());
                                          });
                                    })
                                .build(),
                            java0ArgsTest(
                                "PRj4HXzX",
                                "Local class",
                                CodeBlock.of(
                                    "int y = 17; class MyClass { void myMethod() { int z = y; } } MyClass x = new MyClass();")),
                            java0ArgsTest(
                                "wworYyIB",
                                "Anonymous class",
                                CodeBlock.of(
                                    "int y = 32; new Object(){ void myMethod() { int q = y + 2; } };")),
                            new JavaTestBuilder("JDj7q8z0", "Inheritance")
                                .edit(
                                    t -> {
                                      final ClassName nameA = t.name("ClassA");
                                      final ClassName nameB = t.name("ClassB");
                                      t.class_(
                                              nameA,
                                              c -> {
                                                c.addMethod(
                                                    MethodSpec.constructorBuilder()
                                                        .addParameter(int.class, "x")
                                                        .build());
                                              })
                                          .class_(
                                              nameB,
                                              c -> {
                                                c.superclass(nameA);
                                                c.addMethod(
                                                    MethodSpec.constructorBuilder()
                                                        .addCode(CodeBlock.of("super(14);"))
                                                        .build());
                                              })
                                          .main(CodeBlock.of("new $T();", nameB));
                                    })
                                .build(),
                            new JavaTestBuilder("jf7PB19D", "Interface")
                                .edit(
                                    t -> {
                                      final ClassName nameA = t.name("InterfaceA");
                                      final ClassName nameB = t.name("ClassB");
                                      t.interface_(nameA, c -> {})
                                          .class_(
                                              nameB,
                                              c -> {
                                                c.addSuperinterface(nameA);
                                                c.addMethod(
                                                    MethodSpec.constructorBuilder().build());
                                              })
                                          .main(CodeBlock.of("new $T();", nameB));
                                    })
                                .build()),
                        Stream.of()),
                    new TestGroup(
                        "Methods",
                        Stream.of(
                            new JavaTestBuilder("v1jQFDPn", "Method")
                                .edit(
                                    c -> {
                                      final ClassName name = c.name("MyClass");
                                      c.class_(
                                              name,
                                              b -> {
                                                b.addMethod(
                                                    MethodSpec.methodBuilder("myMethod")
                                                        .addModifiers(Modifier.PUBLIC)
                                                        .build());
                                                b.addMethod(
                                                    MethodSpec.methodBuilder("myMethod2")
                                                        .addModifiers(Modifier.PUBLIC)
                                                        .addCode(CodeBlock.of("myMethod();"))
                                                        .build());
                                              })
                                          .main(CodeBlock.of("new $T().myMethod();", name));
                                    })
                                .build(),
                            new JavaTestBuilder("2rOLz2Xy", "Private method")
                                .edit(
                                    c -> {
                                      final ClassName name = c.name("MyClass");
                                      c.class_(
                                          name,
                                          b -> {
                                            b.addMethod(
                                                MethodSpec.methodBuilder("myMethod")
                                                    .addModifiers(Modifier.PRIVATE)
                                                    .build());
                                            b.addMethod(
                                                MethodSpec.methodBuilder("myMethod2")
                                                    .addModifiers(Modifier.PUBLIC)
                                                    .addCode(CodeBlock.of("myMethod();"))
                                                    .build());
                                          });
                                    })
                                .build(),
                            new JavaTestBuilder("ruxZBzaZ", "Final method")
                                .edit(
                                    c -> {
                                      final ClassName name = c.name("MyClass");
                                      c.class_(
                                              name,
                                              b -> {
                                                b.addMethod(
                                                    MethodSpec.methodBuilder("myMethod")
                                                        .addModifiers(
                                                            Modifier.PUBLIC, Modifier.FINAL)
                                                        .build());
                                                b.addMethod(
                                                    MethodSpec.methodBuilder("myMethod2")
                                                        .addModifiers(Modifier.PUBLIC)
                                                        .addCode(CodeBlock.of("myMethod();"))
                                                        .build());
                                              })
                                          .main(CodeBlock.of("new $T().myMethod();", name));
                                    })
                                .build(),
                            new JavaTestBuilder("ylGzWdic", "Inherit method")
                                .edit(
                                    c -> {
                                      final ClassName nameA = c.name("ClassA");
                                      final ClassName nameB = c.name("ClassB");
                                      c.class_(
                                              nameA,
                                              b -> {
                                                b.addMethod(
                                                    MethodSpec.methodBuilder("myMethod")
                                                        .addModifiers(Modifier.PUBLIC)
                                                        .build());
                                              })
                                          .class_(
                                              nameB,
                                              b -> {
                                                b.superclass(nameA);
                                                b.addMethod(
                                                    MethodSpec.methodBuilder("myMethod")
                                                        .addModifiers(Modifier.PUBLIC)
                                                        .build());
                                                b.addMethod(
                                                    MethodSpec.methodBuilder("myMethod2")
                                                        .addModifiers(Modifier.PUBLIC)
                                                        .addCode(CodeBlock.of("myMethod();"))
                                                        .build());
                                              })
                                          .main(CodeBlock.of("new $T().myMethod();", nameB));
                                    })
                                .build(),
                            new JavaTestBuilder("HLz95e09", "Final inherit method")
                                .edit(
                                    c -> {
                                      final ClassName nameA = c.name("ClassA");
                                      final ClassName nameB = c.name("ClassB");
                                      c.class_(
                                              nameA,
                                              b -> {
                                                b.addMethod(
                                                    MethodSpec.methodBuilder("myMethod")
                                                        .addModifiers(Modifier.PUBLIC)
                                                        .build());
                                              })
                                          .class_(
                                              nameB,
                                              b -> {
                                                b.superclass(nameA);
                                                b.addMethod(
                                                    MethodSpec.methodBuilder("myMethod")
                                                        .addModifiers(
                                                            Modifier.PUBLIC, Modifier.FINAL)
                                                        .build());
                                                b.addMethod(
                                                    MethodSpec.methodBuilder("myMethod2")
                                                        .addModifiers(Modifier.PUBLIC)
                                                        .addCode(CodeBlock.of("myMethod();"))
                                                        .build());
                                              })
                                          .main(CodeBlock.of("new $T().myMethod();", nameB));
                                    })
                                .build()),
                        Stream.of()),
                    new TestGroup(
                        "Fields",
                        Stream.of(
                            new JavaTestBuilder("kQCzcLZ6", "Declaration")
                                .edit(
                                    c -> {
                                      final ClassName name = c.name("MyClass");
                                      c.class_(
                                              name,
                                              b -> {
                                                b.addField(
                                                    FieldSpec.builder(int.class, "x")
                                                        .initializer(CodeBlock.of("14"))
                                                        .build());
                                                b.addMethod(
                                                    MethodSpec.methodBuilder("myMethod")
                                                        .addModifiers(Modifier.PUBLIC)
                                                        .addCode(
                                                            CodeBlock.of("int y = x; x = y + 12;"))
                                                        .build());
                                              })
                                          .main(
                                              CodeBlock.of(
                                                  "$T x = new $T(); x.x = x.x + 1;", name, name));
                                    })
                                .build(),
                            new JavaTestBuilder("OsECV9nG", "Constructor initialization")
                                .edit(
                                    c -> {
                                      c.class_(
                                          c.name("MyClass"),
                                          b -> {
                                            b.addField(
                                                FieldSpec.builder(int.class, "x")
                                                    .initializer(CodeBlock.of("14"))
                                                    .build());
                                            b.addMethod(
                                                MethodSpec.constructorBuilder()
                                                    .addModifiers(Modifier.PUBLIC)
                                                    .addCode("x = 99;")
                                                    .build());
                                          });
                                    })
                                .build(),
                            new JavaTestBuilder("WiQ6Uvv3", "Non-constructor initialization")
                                .edit(
                                    c -> {
                                      c.class_(
                                          c.name("MyClass"),
                                          b -> {
                                            b.addField(FieldSpec.builder(int.class, "x").build());
                                            b.addInitializerBlock(CodeBlock.of("x = 17;"));
                                          });
                                    })
                                .build()),
                        Stream.of()))),
            new TestGroup(
                "Other",
                Stream.of(
                    new JavaTestBuilder("HJ9n1mha", "Import")
                        .edit(
                            t -> {
                              final ClassName name = t.name("MyClass");
                              t.class_(
                                      name,
                                      c -> {
                                        c.addMethod(
                                            MethodSpec.methodBuilder("myMethod")
                                                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                                                .build());
                                      })
                                  .main(CodeBlock.of("$T.myMethod();", name));
                            })
                        .build(),
                    java0ArgsTest(
                        "SQmFqboA",
                        "Local reuse",
                        CodeBlock.of(
                            "int x; if (14 % 3 == 2) { int y = 17; x = y + 2; int z = 1; } else { x = 43; } int a = x * 9;")),
                    new JavaTestBuilder("c6kMuAp5", "First class function")
                        .edit(
                            t -> {
                              final ClassName nameA = t.name("Lambda");
                              final ClassName nameB = t.name("Other");
                              t.interface_(
                                      nameA,
                                      c -> {
                                        c.addAnnotation(FunctionalInterface.class);
                                        c.addMethod(
                                            MethodSpec.methodBuilder("interfaceMethod")
                                                .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
                                                .addParameter(int.class, "x")
                                                .build());
                                      })
                                  .class_(
                                      nameB,
                                      c -> {
                                        c.addMethod(
                                            MethodSpec.methodBuilder("matchingMethod")
                                                .addModifiers(Modifier.STATIC)
                                                .addParameter(int.class, "x")
                                                .build());
                                        c.addMethod(
                                            MethodSpec.methodBuilder("main")
                                                .addModifiers(Modifier.STATIC)
                                                .addCode(
                                                    CodeBlock.of(
                                                        "$T x = $T::matchingMethod;", nameA, nameB))
                                                .build());
                                      });
                            })
                        .build()),
                Stream.of())));
  }

  public static Threads.Future<Test> javaStaticMethodTest(
      String id, String title, MethodSpec method, CodeBlock main) {
    return new JavaTestBuilder(id, title).editMain(c -> c.addMethod(method)).main(main).build();
  }

  public static TestGroup javaNumericPrimitiveTestGroup(
      String id, Class type, Class boxType, boolean integer) {
    return new TestGroup(
        Main.capFirst(type.getSimpleName()),
        Utils.streamCat(
            javaPrimitiveTests(
                id,
                type,
                Stream.of(
                    0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, -1, -2, -3, -4, -5, -6, -7, -8, -9, -10)),
            Stream.of(
                java0ArgsTest(
                    id + "-QfXtniZ9",
                    "Unbox and unbox",
                    CodeBlock.of(
                        "$T x = $T.valueOf(($T)2).$LValue();",
                        type,
                        boxType,
                        type,
                        type.getSimpleName()))),
            Stream.of(int.class, short.class, long.class, byte.class, float.class, double.class)
                .filter(c -> c != type)
                .map(
                    c ->
                        new JavaTestBuilder(
                                id + "-26LNAx3H-" + c.getSimpleName(),
                                String.format("Cast from %s", c))
                            .static1Args(c, CodeBlock.of("$T y = ($T)x;", type, type))
                            .build()),
            Stream.of(
                new JavaTestBuilder(id + "-IUei5O2W", "Add")
                    .static1Args(type, CodeBlock.of("$T y = ($T)(x+($T)2);", type, type, type))
                    .build(),
                new JavaTestBuilder(id + "-gCXFOJUW", "Subtract")
                    .static1Args(type, CodeBlock.of("$T y = ($T)(x-($T)2);", type, type, type))
                    .build(),
                new JavaTestBuilder(id + "-IOSRxhGe", "Multiply")
                    .static1Args(type, CodeBlock.of("$T y = ($T)(x*($T)3);", type, type, type))
                    .build(),
                new JavaTestBuilder(id + "-yyNdXz5g", "Divide")
                    .static1Args(type, CodeBlock.of("$T y = ($T)(x/($T)7);", type, type, type))
                    .build(),
                new JavaTestBuilder(id + "-FN84Pimh", "Mod")
                    .static1Args(type, CodeBlock.of("$T y = ($T)(x%($T)7);", type, type, type))
                    .build()),
            Utils.streamOpt(
                integer,
                () ->
                    Stream.of(
                        new JavaTestBuilder(id + "-9mBJbleB", "Shift left")
                            .static1Args(type, CodeBlock.of("$T y = ($T)(x<<2);", type, type))
                            .build(),
                        new JavaTestBuilder(id + "-pgKyHDmG", "Shift right")
                            .static1Args(type, CodeBlock.of("$T y = ($T)(x>> 3);", type, type))
                            .build(),
                        new JavaTestBuilder(id + "-xxzHuyil", "Bitwise and")
                            .static1Args(
                                type, CodeBlock.of("$T y = ($T)(x&($T)3);", type, type, type))
                            .build(),
                        new JavaTestBuilder(id + "-Pdn71cmm", "Bitwise or")
                            .static1Args(
                                type, CodeBlock.of("$T y = ($T)(x|($T)7);", type, type, type))
                            .build(),
                        new JavaTestBuilder(id + "-ugS852Gs", "Bitwise xor")
                            .static1Args(
                                type, CodeBlock.of("$T y = ($T)(x^($T)7);", type, type, type))
                            .build(),
                        new JavaTestBuilder(id + "-RKw5XrHL", "Bitwise not")
                            .static1Args(type, CodeBlock.of("$T y = ($T)(~x);", type, type))
                            .build())),
            Stream.of(
                new JavaTestBuilder(id + "-T6iiDceD", "Less than")
                    .static1Args(type, CodeBlock.of("boolean y = x < ($T)3;", type))
                    .build(),
                new JavaTestBuilder(id + "-j61bLTda", "Less than/equal")
                    .static1Args(type, CodeBlock.of("boolean y = x <= ($T)3;", type))
                    .build(),
                new JavaTestBuilder(id + "-KVr6SBkM", "Greater than")
                    .static1Args(type, CodeBlock.of("boolean y = x > ($T)3;", type))
                    .build(),
                new JavaTestBuilder(id + "-Ec3NLe4z", "Greater than/equal")
                    .static1Args(type, CodeBlock.of("boolean y = x >= ($T)3;", type))
                    .build(),
                new JavaTestBuilder(id + "-2mc2dIDY", "Equal")
                    .static1Args(type, CodeBlock.of("boolean y = x == ($T)3;", type))
                    .build(),
                new JavaTestBuilder(id + "-lep6CjBN", "Not equal")
                    .static1Args(type, CodeBlock.of("boolean y = x != ($T)3;", type))
                    .build())),
        Stream.of());
  }

  public static Threads.Future<Test> java0ArgsTest(String id, String title, CodeBlock code) {
    return new JavaTestBuilder(id, title).static0Args(code).build();
  }

  public static Threads.Future<Test> java1ArgsTest(
      String id, String title, Class type, CodeBlock code) {
    return new JavaTestBuilder(id, title).static1Args(type, code).build();
  }

  public static TestGroup javaPrimitiveTestGroup(
      String id, Class type, Stream keyValues, Stream<Threads.Future<Test>> additionalTests) {
    return new TestGroup(
        Main.capFirst(type.getSimpleName()),
        Utils.streamCat(javaPrimitiveTests(id, type, keyValues), additionalTests),
        Stream.of());
  }

  public static Stream<Threads.Future<Test>> javaPrimitiveTests(
      String id, Class type, Stream keyValues0) {

    final List keyValues = (List) keyValues0.collect(Collectors.toList());
    return Stream.of(
        java0ArgsTest(id + "-5QrfS6Po", "Declare", CodeBlock.of("$T x;", type)),
        java0ArgsTest(
            id + "-qMfBLRNZ",
            "Initialize",
            CodeBlock.of("$T x = $L;", type, keyValues.get(3 % keyValues.size()))),
        java0ArgsTest(
            id + "-RHLYQ82X",
            "Read/write",
            CodeBlock.of("$T x = $L;$T y = x;", type, keyValues.get(3 % keyValues.size()), type)),
        new JavaTestBuilder(id + "-z4EZ08YJ", "Field read/write")
            .editMain(
                b -> {
                  b.addField(FieldSpec.builder(type, "x").build());
                })
            .main(
                CodeBlock.of(
                    "Main m = new Main(); m.x = $L; $T y = m.x;",
                    keyValues.get(3 % keyValues.size()),
                    type))
            .build(),
        new JavaTestBuilder(id + "-zAYVOOX2", "Static field read/write")
            .editMain(
                b -> {
                  b.addField(FieldSpec.builder(type, "x", Modifier.STATIC).build());
                })
            .main(
                CodeBlock.of(
                    "Main.x = $L; $T y = Main.x;", keyValues.get(3 % keyValues.size()), type))
            .build(),
        java0ArgsTest(
            id + "-03r350PB",
            "Literals",
            CodeBlock.of(
                "$T x; $L;",
                type,
                CodeBlock.join(
                    (Iterable<CodeBlock>)
                        keyValues.stream()
                            .map(v -> CodeBlock.of("x = $L;", v))
                            .collect(Collectors.toList()),
                    ""))),
        new JavaTestBuilder(id + "-AzF61WRp", "Return")
            .method(
                MethodSpec.methodBuilder("myMethod")
                    .returns(type)
                    .addCode(CodeBlock.of("return $L;", keyValues.get(3 % keyValues.size())))
                    .build())
            .build());
  }
}
