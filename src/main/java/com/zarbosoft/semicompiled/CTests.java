package com.zarbosoft.semicompiled;

import com.zarbosoft.semicompiled.html.Element;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CTests {
  public static final String[] SIGNED_KEY_VALUES = {
    "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "-1", "-2", "-3", "-4", "-5", "-6",
    "-7", "-8", "-9", "-10"
  };
  public static final String[] UNSIGNED_KEY_VALUES = {
    "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"
  };
  public static final CType TYPE_INT = new CType("int", true, true, false);
  public static final CType TYPE_UNSIGNED_INT = new CType("unsigned int", false, true, false);
  public static final CType TYPE_SHORT_INT = new CType("short int", true, true, false);
  public static final CType TYPE_UNSIGNED_SHORT_INT =
      new CType("unsigned short int", false, true, false);
  public static final CType TYPE_LONG_INT = new CType("long int", true, true, false);
  public static final CType TYPE_UNSIGNED_LONG_INT =
      new CType("unsigned long int", false, true, false);
  public static final CType TYPE_CHAR = new CType("char", true, true, false);
  public static final CType TYPE_UNSIGNED_CHAR = new CType("unsigned char", false, true, false);
  public static final CType TYPE_FLOAT = new CType("float", true, false, false);
  public static final CType TYPE_DOUBLE = new CType("double", true, false, false);

  public static TestGroup build() {
    return new TestGroup(
        "C 99",
        Stream.of(
            new CTestBuilder(
                    "zwZuhg0Y",
                    "Hello world",
                    "void puts(char *); void myEntry(void) { puts(\"Hello world\"); }")
                .build()),
        Stream.of(
            new TestGroup(
                "Data types",
                Stream.of(),
                Stream.of(
                    primitiveIntTestGroup("gIc0ILNN", TYPE_INT, SIGNED_KEY_VALUES),
                    primitiveIntTestGroup("NpDk8ftx", TYPE_UNSIGNED_INT, UNSIGNED_KEY_VALUES),
                    primitiveIntTestGroup("NTiGusxA", TYPE_SHORT_INT, SIGNED_KEY_VALUES),
                    primitiveIntTestGroup("XFaOY2nM", TYPE_UNSIGNED_SHORT_INT, UNSIGNED_KEY_VALUES),
                    primitiveIntTestGroup("xR2h5IKU", TYPE_LONG_INT, SIGNED_KEY_VALUES),
                    primitiveIntTestGroup("tiWZ7qSu", TYPE_UNSIGNED_LONG_INT, UNSIGNED_KEY_VALUES),
                    primitiveIntTestGroup("BPiQWBvX", TYPE_CHAR, SIGNED_KEY_VALUES),
                    primitiveIntTestGroup("XsnJGMYj", TYPE_UNSIGNED_CHAR, UNSIGNED_KEY_VALUES),
                    primitiveIntTestGroup("MhmPB8eL", TYPE_FLOAT, SIGNED_KEY_VALUES),
                    primitiveIntTestGroup("JYjtNyBX", TYPE_DOUBLE, SIGNED_KEY_VALUES),
                    primitiveIntTestGroup("UOTRxMXf", TYPE_INT.pointer(), new String[] {"0"}),
                    new TestGroup(
                        "Array",
                        Stream.of(
                            new CTestBuilder(
                                    "MufuKgzw",
                                    "Global",
                                    "int x[3] = {1,2,3}; int y[3] = {4,5,6}; void myEntry(void) { x[1] = y[2]; }")
                                .build(),
                            new CTestBuilder(
                                    "20ZCJrcn",
                                    "Static",
                                    "static int x[3] = {1,2,3}; int y[3] = {4,5,6}; void myEntry(void) { x[1] = y[2]; }")
                                .build(),
                            new CTestBuilder(
                                    "yzgYdi5h",
                                    "Local",
                                    "int produce(void); void myEntry(void) { int x[3] = {1,2,3}; int y[3] = {4,5,6}; if (produce()) { x[1] = y[2]; } }")
                                .build(),
                            new CTestBuilder(
                                    "NaV0YevY",
                                    "Write element",
                                    "int produce(void); void myEntry() { int x[] = {4, 5, 6}; x[produce()] = produce(); }")
                                .build(),
                            new CTestBuilder(
                                    "1GwwPVCu",
                                    "Read element",
                                    "int produce(void); void consume(int); void myEntry() { int x[] = {4, 5, 6}; consume(x[produce()]); }")
                                .build()),
                        Stream.of()),
                    new TestGroup(
                        "Struct",
                        Stream.of(
                            new CTestBuilder(
                                    "fqXS3WTY",
                                    "Global",
                                    "typedef struct { int a; } MyStruct; MyStruct x; void myEntry(void) { x.a = 4; }")
                                .build(),
                            new CTestBuilder(
                                    "k6pOD3Ck",
                                    "Static",
                                    "typedef struct { int a; } MyStruct; static MyStruct x; void myEntry(void) { x.a = 4; }")
                                .build(),
                            new CTestBuilder(
                                    "aSx0m2Mw",
                                    "Local",
                                    "typedef struct { int a; } MyStruct; void myEntry(void) { MyStruct x; x.a = 4; }")
                                .build(),
                            new CTestBuilder(
                                    "8REopc2y",
                                    "Field read",
                                    "typedef struct { int a; } MyStruct; MyStruct x; void consume(int); void myEntry(void) { consume(x.a); }")
                                .build(),
                            new CTestBuilder(
                                    "uXK4b1Hq",
                                    "Field write",
                                    "typedef struct { int a; } MyStruct; MyStruct x; void myEntry(void) { x.a = 4; }")
                                .build(),
                            new CTestBuilder(
                                    "y7VyZSOj",
                                    "Bitfield read",
                                    "typedef struct { int a:1; int b:2; } MyStruct; MyStruct x; void consume(int); void myEntry(void) { consume(x.b); }")
                                .build(),
                            new CTestBuilder(
                                    "GJ0RmbOb",
                                    "Bitfield write",
                                    "typedef struct { int a:1; int b:2; } MyStruct; MyStruct x; void myEntry(void) { x.b = 3; }")
                                .build(),
                            new CTestBuilder(
                                    "AwZPnoUL",
                                    "Assignment",
                                    "typedef struct { int a; } MyStruct; MyStruct produce(void); void myEntry(void) { MyStruct x; x = produce(); }")
                                .build(),
                            new CTestBuilder(
                                    "3GR4ky7D",
                                    "Return",
                                    "typedef struct { int a; } MyStruct; MyStruct myEntry(void) { MyStruct x; return x; }")
                                .noEbpf(true, Element.p().t("eBPF doesn't support struct returns."))
                                .build(),
                            new CTestBuilder(
                                    "gBo6oJQg",
                                    "Reference",
                                    "typedef struct { int a; } MyStruct; MyStruct x; MyStruct* myEntry(void) { return &x; }")
                                .build(),
                            new CTestBuilder(
                                    "luM6Ryi0",
                                    "Dereference",
                                    "typedef struct { int a; } MyStruct; MyStruct* produce(void); MyStruct myEntry(void) { return *produce(); }")
                                .noEbpf(
                                    true,
                                    Element.p()
                                        .t("eBPF doesn't support struct returns or pass by value."))
                                .build(),
                            new CTestBuilder(
                                    "OfZKowbZ",
                                    "Pointer field read",
                                    "typedef struct { int a; } MyStruct; MyStruct* x; void consume(int); void myEntry(void) { consume(x->a); }")
                                .build(),
                            new CTestBuilder(
                                    "0WuZLVqs",
                                    "Pointer field write",
                                    "typedef struct { int a; } MyStruct; MyStruct* x; void myEntry(void) { x->a = 7; }")
                                .build()),
                        Stream.of()))),
            new TestGroup(
                "Functions",
                Stream.of(
                    new CTestBuilder(
                            "V450kU2F",
                            "No arg, no return",
                            "void work(void); void myFunc(void) { work(); }")
                        .build(),
                    new CTestBuilder(
                            "8LQHYUjF",
                            "No arg, return",
                            "void work(void); int myFunc(void) { work(); return 7; }")
                        .build(),
                    new CTestBuilder(
                            "cN5uvCyN",
                            "One arg",
                            "void consume(int); void myFunc(int x) { consume(x); }")
                        .build(),
                    new CTestBuilder(
                            "39Lxowq8",
                            "Two args",
                            "void consume(long int); void myFunc(int x, long int y) { consume(y); }")
                        .build(),
                    new CTestBuilder(
                            "vV6JIoIT", "Recursive", "void myFunc(int x) { myFunc(x + 1); }")
                        .build()),
                Stream.of()),
            new TestGroup(
                "Control flow",
                Stream.of(),
                Stream.of(
                    new TestGroup(
                        "Branch",
                        Stream.of(
                            produceTest(
                                    "nvPmQwiX",
                                    "If",
                                    TYPE_INT,
                                    "int x = 3; if (produce()) { x = 4; }")
                                .build(),
                            produceTest(
                                    "O7nVUTPk",
                                    "If, else",
                                    TYPE_INT,
                                    "int x = 3; if (produce()) { x = 4; } else { x = 7; }")
                                .build(),
                            produceTest(
                                    "tYr0BYLF",
                                    "Branched initialization",
                                    TYPE_INT,
                                    "int x; if (produce()) { int y = 7; x = x + y; } else { x = 2; }")
                                .build(),
                            produceTest(
                                    "4JDgRvOn",
                                    "If, else if",
                                    TYPE_INT,
                                    "int x = 3; if (produce()) { x = 4; } else if (produce()) { x = 5; }")
                                .build(),
                            produceTest(
                                    "BsG5eICI",
                                    "If, else if, else",
                                    TYPE_INT,
                                    "int x = 3; if (produce()) { x = 4; } else if (produce()) { x = 12; } else { x = 7; }")
                                .build()),
                        Stream.of()),
                    new TestGroup(
                        "Loop",
                        Stream.of(
                            produceTest(
                                    "dI9eRrHe",
                                    "While",
                                    TYPE_INT,
                                    "int x = 0; while (produce()) { x += 1; }")
                                .build(),
                            produceTest(
                                    "OYzpX2MJ",
                                    "Continue",
                                    TYPE_INT,
                                    "int x = 0; while (produce()) { x += 1; if (produce()) {continue; }x += 4; }")
                                .build(),
                            produceTest(
                                    "1Videmob",
                                    "Break",
                                    TYPE_INT,
                                    "int x = 0; while (produce()) { x += 1; if (produce()) {break; } x += 2;}")
                                .build()),
                        Stream.of()),
                    new TestGroup(
                        "Switch",
                        Stream.of(
                            produceTest(
                                    "A4IoAh9K",
                                    "Integer cases",
                                    TYPE_INT,
                                    "int y; switch (produce()) { case 0: y = 0; break; case 1: y = 33; break; }")
                                .build(),
                            produceTest(
                                    "bdhYLGnc",
                                    "Default case",
                                    TYPE_INT,
                                    "int y; switch (produce()) { case 0: y = 0; break; case 1: y = 33; break; default: y = 72; break; }")
                                .build(),
                            produceTest(
                                    "nobGBrYb",
                                    "Fallthrough",
                                    TYPE_INT,
                                    "int y; switch (produce()) { case 0: y = 0; case 1: y = 33; break; }")
                                .build(),
                            produceTest(
                                    "qc9B85yH",
                                    "Return",
                                    TYPE_INT,
                                    "int y; switch (produce()) { case 0: y = 0; break; case 1: y = 33; return; }")
                                .build()),
                        Stream.of())))));
  }

  public static CTestBuilder produceTest(String id, String title, CType type, String inner) {
    return new CTestBuilder(
        id, title, String.format("%s produce(void); void myEntry(void) { %s }", type, inner));
  }

  public static CTestBuilder produceConsumeTest(String id, String title, CType type, String inner) {
    return new CTestBuilder(
        id,
        title,
        String.format(
            "%s produce(void); void consume(%s); void myEntry(void) { %s }", type, type, inner));
  }

  public static CTestBuilder produceConsume2Test(
      String id, String title, CType type1, CType type2, String inner) {
    return new CTestBuilder(
        id,
        title,
        String.format(
            "%s produce(void); void consume(%s); void myEntry(void) { %s }", type1, type2, inner));
  }

  public static TestGroup primitiveIntTestGroup(String id, CType type, String[] keyValues) {
    return new TestGroup(
        Main.capFirst(type.name),
        Utils.streamCat(
            typeTest(id, type, keyValues),
            Utils.streamOpt(
                !type.pointer,
                () ->
                    Stream.of(
                            TYPE_INT,
                            TYPE_UNSIGNED_INT,
                            TYPE_SHORT_INT,
                            TYPE_UNSIGNED_SHORT_INT,
                            TYPE_LONG_INT,
                            TYPE_UNSIGNED_LONG_INT,
                            TYPE_CHAR,
                            TYPE_UNSIGNED_CHAR,
                            TYPE_FLOAT,
                            TYPE_DOUBLE)
                        .filter(c -> c != type)
                        .map(
                            c ->
                                new CTestBuilder(
                                        String.format(
                                            "%s-10RS44Bd-%s-%s",
                                            id,
                                            type.name.replaceAll(" ", "_"),
                                            c.name.replaceAll(" ", "_")),
                                        String.format("Cast from %s", c),
                                        String.format(
                                            "%s produce(void); void consume(%s); void myEntry(void) { consume((%s) produce()); }",
                                            c, type, type))
                                    .noEbpf(
                                        !type.integer || !c.integer,
                                        Element.p()
                                            .t("eBPF doesn't natively support float conversion."))
                                    .build())),
            Stream.of(
                produceConsumeTest(id + "-WpwzTmB0", "Add", type, "consume(produce() + 2);")
                    .noEbpf(
                        !type.integer,
                        Element.p().t("eBPF doesn't natively support float addition."))
                    .build(),
                produceConsumeTest(id + "-aFNvsXus", "Subtract", type, "consume(produce() - 3);")
                    .noEbpf(
                        !type.integer,
                        Element.p().t("eBPF doesn't natively support float subtraction."))
                    .build()),
            Utils.streamOpt(
                !type.pointer,
                () ->
                    Stream.of(
                        produceConsumeTest(
                                id + "-nZttzvLl", "Multiply", type, "consume(produce() * 5);")
                            .noEbpf(
                                !type.integer,
                                Element.p()
                                    .t("eBPF doesn't natively support float multiplication."))
                            .build(),
                        produceConsumeTest(
                                id + "-jETMJqJQ", "Divide", type, "consume(produce() / 7);")
                            .noEbpf(
                                type.signed, Element.p().t("eBPF doesn't support signed division."))
                            .noEbpf(
                                !type.integer,
                                Element.p().t("eBPF doesn't natively support float division."))
                            .build())),
            Utils.streamOpt(
                type.integer && !type.pointer,
                () ->
                    Stream.of(
                        produceConsumeTest(
                                id + "-H75LY5bB", "Mod", type, "consume(produce() % 12);")
                            .noEbpf(
                                type.signed, Element.p().t("eBPF doesn't support signed division."))
                            .build(),
                        produceConsumeTest(
                                id + "-BG6ajLVC", "Shift left", type, "consume(produce() << 2);")
                            .build(),
                        produceConsumeTest(
                                id + "-xlNuk7ZE", "Shift right", type, "consume(produce() >> 2);")
                            .build(),
                        produceConsumeTest(
                                id + "-xBxgkHiC", "Bitwise and", type, "consume(produce() & 2);")
                            .build(),
                        produceConsumeTest(
                                id + "-004nwJhs", "Bitwise or", type, "consume(produce() | 2);")
                            .build(),
                        produceConsumeTest(
                                id + "-R80HKLjQ", "Bitwise xor", type, "consume(produce() ^ 2);")
                            .build(),
                        produceConsumeTest(
                                id + "-igkEX8ZV", "Bitwise not", type, "consume(~produce());")
                            .build())),
            Stream.of(
                produceConsume2Test(
                        id + "-FK0zr06o", "Less than", type, TYPE_INT, "consume(produce() < 3);")
                    .noEbpf(
                        !type.integer,
                        Element.p().t("eBPF doesn't natively support float comparison."))
                    .build(),
                produceConsume2Test(
                        id + "-gOsyz2t9",
                        "Less than/equal",
                        type,
                        TYPE_INT,
                        "consume(produce() <= 3);")
                    .noEbpf(
                        !type.integer,
                        Element.p().t("eBPF doesn't natively support float comparison."))
                    .build(),
                produceConsume2Test(
                        id + "-Z383cu5l", "Greater than", type, TYPE_INT, "consume(produce() > 3);")
                    .noEbpf(
                        !type.integer,
                        Element.p().t("eBPF doesn't natively support float comparison."))
                    .build(),
                produceConsume2Test(
                        id + "-W0TdiBc6",
                        "Greater than/equal",
                        type,
                        TYPE_INT,
                        "consume(produce() >= 3);")
                    .noEbpf(
                        !type.integer,
                        Element.p().t("eBPF doesn't natively support float comparison."))
                    .build(),
                produceConsume2Test(
                        id + "-192IHVvO", "Equal", type, TYPE_INT, "consume(produce() == 3);")
                    .noEbpf(
                        !type.integer,
                        Element.p().t("eBPF doesn't natively support float comparison."))
                    .build(),
                produceConsume2Test(
                        id + "-YvkqnW5k", "Not equal", type, TYPE_INT, "consume(produce() != 3);")
                    .noEbpf(
                        !type.integer,
                        Element.p().t("eBPF doesn't natively support float comparison."))
                    .build()),
            Utils.streamOpt(
                type == TYPE_INT,
                () ->
                    Stream.of(
                        produceConsume2Test(
                                id + "-THm5z5Ep",
                                "And",
                                type,
                                TYPE_INT,
                                "consume(produce() && produce());")
                            .build(),
                        produceConsume2Test(
                                id + "-4U0FApKv",
                                "Or",
                                type,
                                TYPE_INT,
                                "consume(produce() || produce());")
                            .build()))),
        Stream.of());
  }

  public static TestGroup primitiveTestGroup(String id, CType type, String[] keyValues) {
    return new TestGroup(Main.capFirst(type.name), typeTest(id, type, keyValues), Stream.of());
  }

  public static Stream<Threads.Future<Test>> typeTest(String id, CType type, String[] keyValues) {
    return Stream.of(
        new CTestBuilder(
                id + "-RtZY5cgT",
                "Global",
                String.format(
                    "%s produce(void); void consume(%s); %s x = %s; %s y = %s; void myEntry(void) { x = produce(); consume(y); }",
                    type,
                    type,
                    type,
                    keyValues[3 % keyValues.length],
                    type,
                    keyValues[4 % keyValues.length]))
            .build(),
        new CTestBuilder(
                id + "2HtCueYu",
                "Static",
                String.format(
                    "%s produce(void); void consume(%s); static %s x = %s; %s y = %s; void myEntry(void) { x = produce(); consume(y); }",
                    type,
                    type,
                    type,
                    keyValues[3 % keyValues.length],
                    type,
                    keyValues[4 % keyValues.length]))
            .build(),
        new CTestBuilder(
                id + "-MtOsRhFD",
                "Local",
                String.format(
                    "%s produce(void); void consume(%s); void myEntry(void) {%s x = %s; %s y = %s;  x = produce(); consume(y); }",
                    type,
                    type,
                    type,
                    keyValues[3 % keyValues.length],
                    type,
                    keyValues[4 % keyValues.length]))
            .build(),
        new CTestBuilder(
                id + "-Wkt3S0bV",
                "Literals",
                String.format(
                    "void consume(%s);" + "void myEntry(void) { %s }",
                    type,
                    Stream.of(keyValues)
                        .map(v -> String.format("consume(%s);", v))
                        .collect(Collectors.joining())))
            .build(),
        new CTestBuilder(
                id + "-Z63cwV9G",
                "Return",
                String.format(
                    "%s produce(void); %s myEntry(void) { return produce(); }", type, type))
            .build(),
        produceConsume2Test(
                id + "-keBYb6WS",
                "Reference",
                type,
                type.pointer(),
                String.format("%s x = produce(); consume(&x);", type))
            .build(),
        produceConsume2Test(
                id + "-ResXVmNq",
                "Dereference",
                type.pointer(),
                type,
                String.format("%s x = produce(); consume(*x);", type.pointer()))
            .build());
  }

  public static class CType {
    public final String name;
    public final boolean signed;
    public final boolean integer;
    public final boolean pointer;

    public CType(String name, boolean signed, boolean integer, boolean pointer) {
      this.name = name;
      this.signed = signed;
      this.integer = integer;
      this.pointer = pointer;
    }

    @Override
    public String toString() {
      return name;
    }

    public CType pointer() {
      return new CType(name + "*", false, true, true);
    }
  }
}
