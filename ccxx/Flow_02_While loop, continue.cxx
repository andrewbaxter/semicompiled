void do_something(int arg);

int main() {
  int i = 4;
  do_something(2);
  while (i < 7) {
    ++i;
    do_something(13);
    if (i == 7)
      continue;
    do_something(99);
  }
  do_something(128);
  return 0;
}
