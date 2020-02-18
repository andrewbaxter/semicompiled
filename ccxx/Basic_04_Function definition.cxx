void func_a() {}

void func_b(int x) {}

int func_c() { return 6; }

int func_d(int x) { return 33; }

int main() {
  func_a();
  func_b(19933);
  func_c();
  func_d(12345);
  return 0;
}