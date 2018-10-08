// Starter code for lp1.
// Version 1.0 (8:00 PM, Wed, Sep 5).

package pxp180031;

import java.util.Stack;
import java.lang.ArithmeticException;

public class Num implements Comparable<Num> {
  static long defaultBase = 1000000000; // Change as needed
  long base = defaultBase; // Change as needed
  long[] arr; // array to store arbitrarily large integers
  boolean isNegative; // boolean flag to represent negative numbers
  int len; // actual number of elements of array that are used; number is stored in
  // arr[0..len-1]

  public Num(String s, long base) {
    this.base = base;

    if (s.charAt(0) == '-') {
      isNegative = true;
      s = s.substring(1);
    }

    Num initNum = new Num(0, base);
    for (int i = 0, l = s.length(); i < l; i++) {
      // multiple existing result with 10 and add the new digit =
      initNum = add(
        product(initNum, base),
        new Num(Long.parseLong(String.valueOf(s.charAt(i))), base)
      );
    }

    this.arr = initNum.arr;
    this.len = initNum.len;
  }

  public Num(String s) {
    this(s, defaultBase);
  }

  public Num(long x) {
    this(x, defaultBase);
  }
  
  /**
   * Basic constructor with empty array and all the defaults
   */
  public Num(long[] arr, int len, long base) {
    this.arr = arr;
    this.len = len;
    this.base = base;
  }

  public Num(long x, long base) {
    // convert long integer to the given base using mod and division operators
    this.isNegative = x < 0;
    this.base = base;
    // from here on, use absolute value for operations
    long num = Math.abs(x); 
    if (num == 0) {
      // handle this case, as log of zero is undefined!
      this.arr = new long[]{ 0 };
      this.len = 1;
    } else {
      int size = baseRatio(num, base); // total numbers when converted to a given base
      long[] nums = new long[size];
      int len = 0;
      while (num > 0) {
        nums[len++] = num % base;
        num /= base;
      }

      this.len = len;
      this.arr = nums;
    }
  }

  /**
   * Create Num object if we get array of long objects
   * Assumption - all elems in array are of give base
   * * For internal use
   * @param arr
   * @param base
   */
  private Num(long[] arr, long base) {
    this.arr = arr;
    this.len = arr.length;
    this.base = base;
  }

  // private static int baseSize(long base) {
  //   int intBase = (int) base;
  //   int baseSize = (int) Math.log10(intBase);
  //   return baseSize;
  // }

  private static int baseRatio(long num, long base) {
    int baseRatio = (int) Math.ceil(Math.log10(num) / Math.log10(base)) + 1;
    return baseRatio;
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
    long carryOver = 0;
    if (a.base != b.base) {
      System.out.println("Base of two numbers are not same");
      return null;
    }
    int aLen = a.len;
    int bLen = b.len;

    if (aLen == 0)
      return b;
    if (bLen == 0)
      return a;

    int arrLen = Math.max(a.len, b.len) + 1;
    long[] arr = new long[arrLen];
    for (int i = 0; i < arrLen; i++) {
      long sum = carryOver;
      if (i < aLen)
        sum += a.arr[i];
      if (i < bLen)
        sum += b.arr[i];

      carryOver = sum / a.base;
      arr[i] = sum % a.base;
    }

    int length = 0;
    for (int i = arrLen - 1; i >= 0; i--) {
      if (arr[i] > 0)
        break;
      length++;
    }

    Num result = new Num(arr, a.base);
    result.len = arrLen - length;
    return result;
  }

  public static Num add(Num a, Num b) {
    /**
     * Perform subtraction if both number have opp sign
     * if numbers are of opp signs, xor will give truthy
     * and then call subtract method
     */
    if (a.isNegative ^ b.isNegative) {
      Num sub = unsignedSubtract(a, b);
      sub.isNegative = !(a.unsignedCompareTo(b) > 0 ^ a.isNegative);
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
    if (a.compareTo(b) == 0)
      return new Num(0, a.base);

    /**
     * swap numbers if a < b
     */
    if (a.unsignedCompareTo(b) < 0) {
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
      } else
        borrow = 0;
      arr[i] = sub;
    }
    Num result = new Num(arr, base);

    // if last elem is zero, reduce the length by 1
    int length = 0;
    for (int i = arrLen - 1; i >= 0; i--) {
      if (arr[i] > 0)
        break;
      length++;
    }

    result.len = arrLen - length;
    return result;
  }

  public static Num subtract(Num a, Num b) {
    /**
     * If numbers are not of same sign, call add method using xor for this a(+) b(-)
     * => a + b => add a(-) b(+) => - (a + b) => add
     */
    if (a.isNegative ^ b.isNegative) {
      // Calculate sum and then apply sign to the result
      Num addResult = unsignedAdd(a, b);
      // one of them is negative and take that sign
      addResult.isNegative = a.isNegative;
      return addResult;
    }
    ;

    Num result = unsignedSubtract(a, b);
    // we can safely assume that both are either postive or negative
    result.isNegative = !(a.unsignedCompareTo(b) > 0 ^ a.isNegative);
    return result;
  }

  /**
   * Utility function for karatsuba multiplication method Assumption - Num and
   * long are in same base
   * 
   * @param a
   * @param b
   * @return
   */
  private static Num product(Num a, long b) {
    if (b == 0)
      return new Num(0, a.base);

    if (b == 1)
      return a;

    if (a.len == 0)
      return a;

    int count = 0;

    long carryOver = 0;
    int ratio = baseRatio(b, a.base);
    long[] arr = new long[a.len * ratio];
    while (count < a.len || carryOver > 0) {
      long sum = (count < a.len ? a.arr[count] : 0) * b + carryOver;
      arr[count] = sum % a.base;
      carryOver = sum / a.base;
      count++;
    }

    Num result = new Num(arr, a.base);
    result.isNegative = a.isNegative;
    result.len = count;
    return result;
  }

  public static Num product(Num a, Num b) {
    if (a.base != b.base) {
      throw new ArithmeticException();
    }
    Num result = karatsuba(a, b);
    result.isNegative = a.isNegative ^ b.isNegative;
    result.base = a.base;
    return result;
  }

  /**
   * Utility function for karatsuba multiplication method
   * 
   * @param a
   * @param index
   * @param length
   * @return
   */
  private static Num splitter(Num a, int index, int length) {
    if (length < 0) return new Num(0, a.base);

    long[] arr = new long[length];
    for (int i = 0; i < Math.min(a.len, length); i++) {
      arr[i] = a.arr[i + index];
    }
    return new Num(arr, a.base);
  }

  /**
   * Utility function for karatsuba multiplication method
   * Desc - Pad array by k digits to the left
   * @param a
   * @param length
   * @return
   */
  private static Num leftPad(Num a, int length) {
    long[] arr = new long[a.len + length];
    for (int i = 0; i < a.len + length; i++) {
      if (i < length) arr[i] = 0l;
      else arr[i] = a.arr[i - length];
    }
    return new Num(arr, a.base);
  }

  /**
   * https://en.wikipedia.org/wiki/Karatsuba_algorithm Multiplication of two big
   * numbers can be done in nlog2(3)
   * 
   * @param a
   * @param b
   * @return
   */
  private static Num karatsuba(Num a, Num b) {
    if (a.len == 1 || b.len == 1) {
      return a.len == 1 ? product(b, a.arr[0]) : product(a, b.arr[0]);
    } else if (a.len == 0 || b.len == 0)
      return new Num(0, a.base);

    int k = Math.max(a.len, b.len) / 2;

    Num aLow = splitter(a, 0, k);
    Num aHigh = splitter(a, k, a.len - k);

    Num bLow = splitter(b, 0, k);
    Num bHigh = splitter(b, k, b.len - k);

    Num z0 = karatsuba(aLow, bLow);
    Num z1 = karatsuba(unsignedAdd(aLow, aHigh), unsignedAdd(bLow, bHigh));
    Num z2 = karatsuba(aHigh, bHigh);

    // (z2 * 10 ^ (m2 * 2)) + ((z1 - z2 - z0) * 10 ^ m2) + z0
    Num result = unsignedAdd(
      unsignedAdd(
        leftPad(z2, 2 * k), 
        leftPad(unsignedSubtract(z1, unsignedAdd(z2, z0)), k)
      ),
      z0
    );

    return result;
  }

  // Use divide and conquer
  public static Num power(Num a, long n) {

    Num ONE = new Num(1, a.base);
    Num ZERO = new Num(0, a.base);

    if (n < 0)
      return ZERO;
    if (n == 0) {
      if (a.unsignedCompareTo(ZERO) == 0) {
        throw new ArithmeticException("0^0 is undefined");
      } else {
        return ONE;
      }
    }

    if (n % 2 == 0) {
      Num p = power(a, n / 2);
      return product(p, p);
    }

    return product(a, power(a, n - 1));
  }

  public static Num power(Num a, Num b) {
    return power(a, Long.parseLong(b.toString()));
  }

  // Use binary search to calculate a/b
  public static Num divide(Num a, Num b) {
    if (a.base != b.base) {
      throw new ArithmeticException();
    }

    // handle divide by zero exception
    if (b.len == 1 && b.arr[0] == 0) {
      throw new IllegalArgumentException("Dividing by 0");
    }

    // if dividend is less, it will result to zero
    if (a.unsignedCompareTo(b) < 0)
      return new Num(0, a.base);

    // if both are same, it results to one and choose sign accordingly
    if (a.unsignedCompareTo(b) == 0) {
      Num result = new Num(1, b.base);
      result.isNegative = a.isNegative ^ b.isNegative;
      return result;
    }
    Num low = new Num(1, a.base);
    Num high = a;
    Num result = binarySearch(low, high, a, b);
    result.isNegative = a.isNegative ^ b.isNegative;
    return result;
  }

  private static Num binarySearch(Num low, Num high, Num x, Num y) {
    Num mid = unsignedAdd(low, high).by2();
    Num prod = product(mid, y);
    Num right = unsignedAdd(prod, y);
    int leftSide = prod.unsignedCompareTo(x);
    int rightSide = x.unsignedCompareTo(right);

    Num ONE = new Num(1, x.base);
    if (leftSide > 0) {
      return binarySearch(low, unsignedSubtract(mid, ONE), x, y);
    } else if (leftSide <= 0 && rightSide < 0) {
      return mid;
    } else {
      return binarySearch(unsignedAdd(mid, ONE), high, x, y);
    }
  }

  // return a%b
  public static Num mod(Num a, Num b) {
    if (a.base != b.base) {
      throw new ArithmeticException();
    }
    Num ONE = new Num(1, a.base);
    Num ZERO = new Num(0, a.base);
    if (b.unsignedCompareTo(ZERO) == 0)
      throw new ArithmeticException("Modulo by 0 is undefined");

    // handle all edge and trivial cases first
    if (a.unsignedCompareTo(ZERO) <= 0)
      return ZERO;

    if (a.unsignedCompareTo(ONE) == 0)
      return ONE;

    if (a.unsignedCompareTo(b) == 0)
      return ZERO;

    if (a.unsignedCompareTo(b) < 1)
      return a;

    Num result = unsignedSubtract(a, product(divide(a, b), b));
    result.isNegative = a.isNegative || b.isNegative;
    return result;
  }

  // 8068373400034984444236073738502178458216504
  // 707227791954527937705075407254340009775794529113784
  // Use binary search
  public static Num squareRoot(Num a) {
    if (a.isNegative)
      throw new ArithmeticException();

    Num ZERO = new Num(0, a.base);
    Num ONE = new Num(1, a.base);

    if (a.unsignedCompareTo(ZERO) == 0)
      return ZERO;

    if (a.unsignedCompareTo(ONE) == 0)
      return ONE;

    return binarySearchForSqareRoot(ONE, a.by2(), a);
  }

  /**
   * Utility for finding square root using binary search
   * @param low
   * @param high
   * @param a
   * @return
   */
  private static Num binarySearchForSqareRoot(Num low, Num high, Num a) {
    Num start, answer = null, end, mid = null;
    start = low;
    Num ONE = new Num(1, a.base);
    Num ZERO = new Num(0, a.base);
    end = high;
    if (end.compareTo(ZERO) == 0 || end.compareTo(ONE) == 0) {
      return end;
    }
    while (start.compareTo(end) == -1 || start.compareTo(end) == 0) {
      mid = add(start, end).by2();
      if (product(mid, mid).compareTo(a) == 0) {
        return mid;
      }
      if (product(mid, mid).compareTo(a) == -1) {
        start = add(mid, ONE);
        answer = mid;
      } else {
        end = subtract(mid, ONE);
      }
    }
    return answer;
  }

  // Utility functions
  // compare "this" to "other": return +1 if this is greater, 0 if equal, -1
  // otherwise
  public int compareTo(Num other) {
    if (this.base() != other.base()) {
      throw new ArithmeticException();
    }

    if (this.isNegative && !other.isNegative)
      return -1;

    if (!this.isNegative && other.isNegative)
      return 1;

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

    if (this.len > other.len)
      return 1;

    if (this.len < other.len)
      return -1;

    // compare from last index
    int pos = this.len - 1;

    while (pos >= 0 && this.arr[pos] == other.arr[pos]) {
      pos--;
    }

    // if both are same till end return 0
    if (pos == -1)
      return 0;

    return this.arr[pos] > other.arr[pos] ? 1 : -1;
  }

  // Output using the format "base: elements of list ..."
  // For example, if base=100, and the number stored corresponds to 10965,
  // then the output is "100: 65 9 1"
  public void printList() {
    System.out.print(this.base + ": ");
    if (this.isNegative)
      System.out.println("-");
    for (int i = 0, len = this.len; i < len; i++) {
      System.out.print(arr[i] + " ");
    }
    System.out.println();
  }

  // Return number to a string in base 10
  public String toString() {
    if (this.len == 0) return null;
    Num numInBase10 = this.convertBase(10);
    String value = null;
    StringBuilder str = new StringBuilder();

    long[] arr = numInBase10.arr;
    int len = numInBase10.len;
    for (int i = 0; i < len; i++) {
      str.insert(0, String.format("%01d", arr[i]));
    }
    
    value = str.toString().replaceFirst("^0+(?!$)", "");
    if (this.isNegative) {
      value = "-" + value;
    }
    return value;
  }

  public long base() {
    return base;
  }

  // Return number equal to "this" number, in base=newBase
  public Num convertBase(int newBase) {
    long base = this.base;
    if (base == newBase)
      return this;
    
    Num number = new Num(0, newBase);

    for (int i = this.len - 1; i >= 0; i--) {
      number = add(
        product(number, this.base),
        new Num(this.arr[i], newBase)
      );
    }
    number.base = newBase;
    number.isNegative = this.isNegative;
    return number;
  }

  // low 1 high 707227791954527937705075407254340009775794547158627 a
  // 707227791954527937705075407254340009775794547158627 b 87654321

  /**
   * Utility function for division by 2
   * Desc - Shifts numbers by k digits to right
   * @param a
   * @param length
   * @return
   */
  private static long[] shift(long[] a, int count) {
    long[] shiftedArray = new long[a.length - count];
    for (int i = 0; i < a.length - count; i++) {
      shiftedArray[i] = a[count + i];
    }
    return shiftedArray;
  }

  // Evaluate an expression in postfix and return resulting number
  // Each string is one of: "*", "+", "-", "/", "%", "^", "0", or
  // a number: [1-9][0-9]*. There is no unary minus operator.
  public static Num evaluatePostfix(String[] expr) {
    Stack<Num> s = new Stack<Num>();
    int len = expr.length;

    for (int i = 0; i < len; i++) {
      String token = expr[i];
      if (token != "+" && token != "-" && token != "*" && token != "/" && token != "^" && token != "%") {
        s.push(new Num(token, 10));
      } else {
        Num n1 = s.pop();
        Num n2 = s.pop();
        String operator = expr[i];

        if (operator == "+") {
          s.push(add(n2, n1));
        } else if (operator == "-") {
          s.push(subtract(n2, n1));
        } else if (operator == "*") {
          s.push(product(n2, n1));
        } else if (operator == "/") {
          s.push(divide(n2, n1));
        } else if (operator == "%") {
          s.push(mod(n2, n1));
        } else if (operator == "^") {
          s.push(power(n2, n1));
        }
      }
    }
    return s.pop();
  }

  // Evaluate an expression in infix and return resulting number
  // Each string is one of: "*", "+", "-", "/", "%", "^", "(", ")", "0", or
  // a number: [1-9][0-9]*. There is no unary minus operator.
  public static Num evaluateInfix(String[] expr) {
    Stack<Num> values = new Stack<Num>();
    char ch;
    Stack<String> operators = new Stack<String>();
    for (int i = 0; i < expr.length; i++) {
      if (expr[i].matches("\\d+.*")) {
        values.push(new Num(expr[i]));
      } else if (expr[i] == "(") {
        operators.push((expr[i]));
      } else if (expr[i] == ")") {
        if (operators.size() == 0) {
          break;
        } else {
          while (operators.peek() != "(") {
            values.push((applyOperation(operators.pop(), values.pop(), values.pop())));
          }
          operators.pop();
        }

      }

      else { // current token is an operator
        while (!operators.empty() && hasPrecedence(expr[i], operators.peek())) {
          values.push((applyOperation(operators.pop(), values.pop(), values.pop())));
        }
        operators.push(expr[i]);
      }
    }
    while (!operators.empty()) {
      values.push((applyOperation(operators.pop(), values.pop(), values.pop())));
    }
    return values.pop();
  }

  private static Num applyOperation(String op, Num num2, Num num1) {
    switch (op) {
    case "+":
      return add(num1, num2);
    case "-":
      return subtract(num1, num2);
    case "*":
      return product(num1, num2);
    case "/":
      return divide(num1, num2);
    case "%":
      return mod(num1, num2);
    case "^":
      return power(num1, num2);
    }

    return null;
  }

  private static boolean hasPrecedence(String op1, String op2) {
    if (op2 == "(" || op2 == ")")
      return false;
    if ((op1 == "^" || op1 == "*" || op1 == "/" || op1 == "%") && (op2 == "+" || op2 == "-"))
      return false;
    else
      return true;
  }

  // Divide by 2, for using in binary search
  public Num by2() {
    Num prod = product(this, new Num(this.base / 2, this.base));
    Num result = new Num(shift(prod.arr, 1), this.base);
    if (this.isNegative)
      result.isNegative = true;
    return result;
  }

  public static void main(String[] args) {
    Num x = new Num(3000);
    Num y = new Num("896");
    Num z;
    
    x.printList();
    y.printList();
    z = Num.subtract(x, y);
    if (z != null) {
      System.out.print("Subtraction result: ");
      z.printList();
      System.out.println(z.toString());
    }

    x = new Num(-3000);
    y = new Num("-1120");
    z = Num.subtract(x, y);
    if (z != null) {
      System.out.print("Subtraction result: ");
      z.printList();
      System.out.println(z.toString());
    }

    z = Num.add(x, y);
    if (z != null) {
      System.out.print("Addition result: ");
      z.printList();
      System.out.println(z.toString());
    }

    z = Num.product(x, -30);
    if (z != null) {
      System.out.print("Product(Num, long) result: ");
      z.printList();
      System.out.println(z.toString());
    }

    x = new Num("23242348234288");
    x.by2().printList();

    x = new Num("1000");
    y = new Num("5");
    System.out.println("x/y result 22: " + Num.divide(x, y).toString());

    x = new Num("-12345674824890223483094848923");
    y = new Num("76543434345453");
    System.out.println(x.by2().toString());
    System.out.println("x/y result: " + Num.divide(x, y).toString());

    z = evaluateInfix(new String[] { "5", "+", "2", "^", "3" });
    System.out.println("Infix Expression " + z.toString());

    z = evaluatePostfix(new String[] { "5", "3", "4", "*", "2", "/", "+", "6", "6", "%", "-", "6", "2", "^", "+" });
    System.out.println("Postfix Expression" + z.toString());

    x = new Num("711", 10);
    x.printList();
    z = x.convertBase(12);
    z.printList();
    System.out.println("convertBase: " + z.len + " " + z);

    x = new Num("-1234");
    System.out.println("x is " + x);
    y = new Num("-76");
    System.out.println("y is " + y);
    long startTime = System.nanoTime();
    z = Num.product(x, y);
    long endTime = System.nanoTime();
    long duration = (endTime - startTime);
    if (z != null) {
      System.out.print("Product(Num, Num) result: ");
      z.printList();
      System.out.println(z.toString());
      System.out.println("Duration: " + duration);
    }

    startTime = System.nanoTime();
    // z = Num.power(new Num(3), 999999);
    endTime = System.nanoTime();
    duration = (endTime - startTime);
    System.out.println("Duration for power(3, 999999): " + duration / 1000000000.0);

    x = new Num("707227791954527937705075407254340009775794547158627");
    System.out.println("converted number: " + x.convertBase(87654321));
    y = new Num("87654321");

    z = new Num("3659535532566681673026857047264590495633096120170316011130546064934049533282760410899967541");

    System.out.println(mod(z, new Num(87654321)));
    System.out.println("converted number: " + z.convertBase(87654321));
    // System.out.println("compareTo x,y " + x.compareTo(y));
  }
}