package acp;
import java.util.Random;

public abstract class Real {
  static double delta = 1.0 / (1 << 26);
  static int curPrecision = 16;
  static int maxPrecision = 256;
  static boolean handleSignException = true;
  static boolean throwPrecisionException = true;

  protected static Random random = new Random(17);
  private static double random1 () {
    return random.nextDouble() * 2 - 1;
  }

  public static Real make (double x, boolean perturb) {
    return perturb ? input(x) : constant(x);
  }

  public static Real input (double x) { 
    return constant(x * (1 + random1() * delta));
  }

  public static Real constant (double x) { 
    switch (curPrecision) {
    case 16: return new DoubleInterval(x);
    case 32: return new ModInterval(x);
    default: return new BigInterval(x);
    }
  }

  public Real plus (double d) { return plus(constant(d)); }
  public Real minus (double d) { return minus(constant(d)); }
  public Real times (double d) { return times(constant(d)); }
  public Real over (double d) { return over(constant(d)); }

  public abstract int precision ();
  public abstract Real plus (Real that);
  public abstract Real minus ();
  public abstract Real minus (Real that);
  public abstract Real times (Real that);
  public abstract Real over (Real that);
  public abstract Real sqrt ();
  public abstract int weakSign ();

  int sign () {
    int s = weakSign();
    if (s != 0)
      return s;
    if (curPrecision == maxPrecision)
      if (throwPrecisionException)
        throw new PrecisionException();
      else
        return 0;
    throw new SignException();
  }
    
  public abstract double lb ();
  public abstract double ub ();
  public double approx () { return (lb() + ub()) / 2; }
  public String toString () { return "[" + lb() + ", " + ub() + "]"; }

  Real changePrecision () {
    switch (curPrecision) {
    case 16: return new DoubleInterval(this);
    case 32: return new ModInterval(this);
    default: return new BigInterval(this);
    }
  }

  class SignException extends RuntimeException {
    SignException () {
      super("sign exception");
    }
  }

  class PrecisionException extends RuntimeException {
    PrecisionException () {
      super("precision exception");
    }
  }
}



  
      
