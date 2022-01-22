package com.zarbosoft.semicompiled;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestGroup {
  public final String title;
  public final List<Threads.Future<Test>> leaves;
  public final List<TestGroup> subsections;

  public TestGroup(String title, Stream<Threads.Future<Test>> leaves, Stream<TestGroup> subgroups) {
    this.title = title;
    this.leaves = leaves.collect(Collectors.toList());
    this.subsections = subgroups.collect(Collectors.toList());
  }
}
