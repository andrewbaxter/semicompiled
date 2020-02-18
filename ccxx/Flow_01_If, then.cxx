void do_something(int arg);

int main(int argc, char **argv) {
  do_something(0);
  if (argc == 3) {
    do_something(4);
  } else {
    do_something(19);
  }
  do_something(99);
}