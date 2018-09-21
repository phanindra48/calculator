// Starter code for lp1.
// Version 1.0 (8:00 PM, Wed, Sep 5).

package pxp180031;

public class Num implements Comparable<Num> {

  static long defaultBase = 100; // Change as needed
  long base = defaultBase; // Change as needed
  long[] arr; // array to store arbitrarily large integers
  boolean isNegative; // boolean flag to represent negative numbers
  int len; // actual number of elements of array that are used; number is stored in
  // arr[0..len-1]
  int count = 0;

  public Num(String s) {
    int strLen = s.length();
    int index = strLen;
    int count = 0; // totalChunks - 1;
    int baseSize = baseSize(base);
    int totalChunks = (int) Math.ceil(strLen * 1.0 / baseSize);
    arr = new long[totalChunks];
    len = totalChunks;
    // System.out.println(count + " " + baseSize + " " + index + " " + totalChunks +
    // " " + len + " " + strLen);
    System.out.println(s);
    while (count < totalChunks) {
      int start = index / baseSize > 0 ? (index - baseSize) : 0;
      int end = index;
      arr[count++] = Long.parseLong(s.substring(start, end));
      index = index - baseSize;
    }
  }

  public Num(long x) {
    this(String.valueOf(x));
  }

  private static int baseSize(long base) {
    int intBase = (int) base;
    int baseSize = (int) Math.log10(intBase);
    return baseSize;
  }

  private static String arrayToString(long[] arr, long base) {
    if (arr.length == 0)
      return null;
    StringBuilder str = new StringBuilder();
    int baseSize = baseSize(base);

    for (int i = 0, len = arr.length; i < len; i++) {
      str.insert(0, String.format("%0" + baseSize + "d", arr[i]));
    }

    return str.toString().replaceFirst("^0+(?!$)", "");
  }

  public static Num add(Num a, Num b) {
    int carryOver = 0;
    if (a.base != b.base) {
      System.out.println("Base of two numbers are not same");
      return null;
    }
    int aLen = a.len;
    int bLen = b.len;

    int base = (int) a.base;

    if (aLen == 0)
      return b;
    if (bLen == 0)
      return a;

    int arrLen = Math.max(a.len, b.len) + 1;
    long[] arr = new long[arrLen];
    for (int i = 0; i < arrLen; i++) {
      int sum = carryOver;
      if (i < aLen)
        sum += a.arr[i];
      if (i < bLen)
        sum += b.arr[i];

      carryOver = sum / base;
      arr[i] = sum % base;
    }
    return new Num(arrayToString(arr, base));
  }

  public static Num subtract(Num a, Num b) {
    return null;
  }

  public static Num product(Num a, Num b) {
    return null;
  }

  // Use divide and conquer
  public static Num power(Num a, long n) {
    return null;
  }

  // Use binary search to calculate a/b
  public static Num divide(Num a, Num b) {
    return null;
  }

  // return a%b
  public static Num mod(Num a, Num b) {
    return null;
  }

  // Use binary search
  public static Num squareRoot(Num a) {
    return null;
  }

  // Utility functions
  // compare "this" to "other": return +1 if this is greater, 0 if equal, -1
  // otherwise
  public int compareTo(Num other) {
    if (this.base() != other.base()) {
      throw new ArithmeticException();
    }
    
    if (this.len > other.len) return 1;

    if (this.len < other.len) return -1;
    
    // compare from last index
    int pos = this.len - 1;
    
    while (pos >= 0 && this.arr[pos] == other.arr[pos]) {
      pos--;
    }
    
    if (pos == 0) return 0;
    return this.arr[pos] > other.arr[pos] ? 1 : -1;
  }

  // Output using the format "base: elements of list ..."
  // For example, if base=100, and the number stored corresponds to 10965,
  // then the output is "100: 65 9 1"
  public void printList() {
    System.out.print(base + ": ");
    for (int i = 0, len = arr.length; i < len; i++) {
      System.out.print(arr[i] + " ");
    }
    System.out.println();
  }

  // Return number to a string in base 10
  public String toString() {
    return arrayToString(this.arr, this.base);
  }

  public long base() {
    return base;
  }

  // Return number equal to "this" number, in base=newBase
  public Num convertBase(int newBase) {
    return null;
  }

  // Divide by 2, for using in binary search
  public Num by2() {
    return null;
  }

  // Evaluate an expression in postfix and return resulting number
  // Each string is one of: "*", "+", "-", "/", "%", "^", "0", or
  // a number: [1-9][0-9]*. There is no unary minus operator.
  public static Num evaluatePostfix(String[] expr) {
    return null;
  }

  // Evaluate an expression in infix and return resulting number
  // Each string is one of: "*", "+", "-", "/", "%", "^", "(", ")", "0", or
  // a number: [1-9][0-9]*. There is no unary minus operator.
  public static Num evaluateInfix(String[] expr) {
    return null;
  }

  public static void main(String[] args) {
    Num x = new Num(9922349);
    Num y = new Num("9134508");
    Num z = Num.add(x, y);
    System.out.println("toString()" + z.toString());
    if (z != null)
      z.printList();
  }
}
