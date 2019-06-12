package pv;

import acp.Real;
import acp.XYZ;

public class PV3 implements XYZ,Cloneable {
  public Real x, y, z;

  public static final PV3 X = PV3.constant(1, 0, 0);
  public static final PV3 Y = PV3.constant(0, 1, 0);
  public static final PV3 Z = PV3.constant(0, 0, 1);

  
  public int size () { return 3; }

  public Real get (int i) {
    switch (i) {
    case 0: return x;
    case 1: return y;
    case 2: return z;
    default: throw new IndexOutOfBoundsException();
    }
  }

  public Real set (int i, Real v) {
    switch (i) {
    case 0: return x = v;
    case 1: return y = v;
    case 2: return z = v;
    default: throw new IndexOutOfBoundsException();
    }
  }

  public Object clone () {
    try {
      return super.clone(); 
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  public PV3 (Real x, Real y, Real z) { this.x = x; this.y = y; this.z = z; }
  
  static public PV3 input (double x, double y, double z) {
    return new PV3(Real.input(x), Real.input(y), Real.input(z));
  }

  static public PV3 constant (double x, double y, double z) {
    return new PV3(Real.constant(x), Real.constant(y), Real.constant(z));
  }

  public PV3 plus (PV3 that) {
    return new PV3(this.x.plus(that.x), this.y.plus(that.y), this.z.plus(that.z));
  }

  public PV3 minus (PV3 that) {
    return new PV3(this.x.minus(that.x), this.y.minus(that.y), this.z.minus(that.z));
  }

  public PV3 times (Real s) { return new PV3(x.times(s), y.times(s), z.times(s)); }

  public PV3 over (Real s) { return new PV3(x.over(s), y.over(s), z.over(s)); }

  public Real dot (PV3 that) { 
    return this.x.times(that.x).plus(this.y.times(that.y)).plus(this.z.times(that.z)); 
  }

  public PV3 cross (PV3 that) { 
    return new PV3(this.y.times(that.z).minus(this.z.times(that.y)),
                   this.z.times(that.x).minus(this.x.times(that.z)),
                   this.x.times(that.y).minus(this.y.times(that.x)));
  }

  /**
   * The norm is x^2 + y^2, but x and y should not appear in your
   * implementation!
   */
  public Real norm () { return this.dot(this); }

  public Real length () { return norm().sqrt(); }

  /**
   * The unit vector pointing in the same direction.
   */
  public PV3 unit () { return this.over(length()); }

  public Real distance (PV3 that) { return that.minus(this).length(); }
}
