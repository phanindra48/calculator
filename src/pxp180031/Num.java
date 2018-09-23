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

  // Frequently used values
  private static final Num ONE = new Num(1);
  private static final Num ZERO = new Num(0);

  public Num(String s) {
    int strLen = s.length();
    int index = strLen;
    int count = 0;
    isNegative = false;
    int baseSize = baseSize(base);
    int totalChunks = (int) Math.ceil(strLen * 1.0 / baseSize);
    arr = new long[totalChunks];
    len = totalChunks;
    // System.out.println(count + " " + baseSize + " " + index + " " + totalChunks + " " + len + " " + strLen);
    System.out.println(s);
    // TODO: Need to handle negative signs in input strings
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

  /**
   * Irrespective of the signs, will just do addition
   * Created only to remove duplicated code, to handle cases 
   * where subtraction will need addition in actual
   * @param a
   * @param b
   * @return Num
   */
  private static Num unsignedAdd(Num a, Num b) {
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

  public static Num add(Num a, Num b) {
    /**
     * Perform subtraction if both number have opp sign
     * if numbers are of opp signs, xor will give truthy
     * and then call subtract method
     */
    if (a.isNegative ^ b.isNegative) {
      Num sub = unsignedSubtract(a, b);
      sub.isNegative = a.unsignedCompareTo(b) > 0 && a.isNegative;
      return sub;
    } 

    Num result = unsignedAdd(a, b);
    result.isNegative = a.isNegative;
    return result;
  }
  
  /**
   * Irrespective of the signs, will just do subtraction
   * Created only to remove duplicated code, to handle cases 
   * where addition will need subtraction in actual
   * @param a
   * @param b
   * @return Num
   */
  private static Num unsignedSubtract(Num a, Num b) {
    int borrow = 0;
    boolean isNegative = false;
    if (a.base() != b.base()) {
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

    /**
     * From here on, we can safely assume that two numbers are of different signs
     */
    
     /**
     * Return if they are equal, as they will result to zero
     */
    if (a.compareTo(b) == 0) return ZERO;

    /**
     * swap numbers if a < b
     */
    if (a.compareTo(b) < 0) {
      Num temp = a;
      a = b;
      b = temp;
      aLen = a.len;
      bLen = b.len;
    }

    int arrLen = Math.max(a.len, b.len);
    long[] arr = new long[arrLen];

    for (int i = 0; i < arrLen; i++) {
      int sub = 0;
      if (i < aLen) {
        sub += a.arr[i] - borrow;
      }
      if (i < bLen)
        sub -= b.arr[i];
      
      if (sub < 0) {
        sub += base;
        borrow = 1;
      } else borrow = 0;

      arr[i] = sub;
    }

    return new Num(arrayToString(arr, base));
  }

  public static Num subtract(Num a, Num b) {
    /**
     * If numbers are not of same sign, call add method
     * using xor for this
     * a(+) b(-) => a + b => add
     * a(-) b(+) => - (a + b) => add 
     */
    if (a.isNegative ^ b.isNegative) {
      // Calculate sum and then apply sign to the result
      Num addResult = add(a, b);
      // one of them is negative and take that sign
      addResult.isNegative = a.isNegative || b.isNegative;
      return addResult;
    };

    Num result = unsignedSubtract(a, b);
    // we can safely assume that both are either postive or negative
    result.isNegative = a.unsignedCompareTo(b) > 0 && a.isNegative;
    return result;
  }

  /**
   * https://en.wikipedia.org/wiki/Karatsuba_algorithm
   * Multiplication of two big numbers can be done in nlog2(3)
   * @param a
   * @param b
   * @return
   */
  private static String karatsuba(long[] a, long[] b) {
    int k = Math.max(a.length, b.length) / 2;

    long[] aLow = new long[k];
    long[] aHigh = new long[k - a.length];

    long[] bLow = new long[k];
    long[] bHigh = new long[k - b.length];
    return null;
  }

  public static Num product(Num a, Num b) {
    if (a.hasSameBase(b)) {
      throw new ArithmeticException();  
    }
    String res = karatsuba(a.arr, b.arr);
    System.out.println("karatsuba result -> " + res);
    Num result = new Num(res);
    result.isNegative = a.isNegative ^ b.isNegative;
    return result;
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

    if (this.isNegative && !other.isNegative) return -1;

    if (!this.isNegative && other.isNegative) return 1;
    
    return this.unsignedCompareTo(other);
  }

  /**
   * compares just two numbers and ignores their signs
   * @param other
   * @return
   */
  private int unsignedCompareTo(Num other) {
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
    
    // if both are same till end return 0
    if (pos == -1) return 0;

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
