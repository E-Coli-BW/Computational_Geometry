package pv;

import acp.Real;
import acp.XYZ;

public class PV2 implements XYZ,Cloneable {
  public Real x, y;

  public int size () { return 2; }

  public Real get (int i) {
    switch (i) {
    case 0: return x;
    case 1: return y;
    default: throw new IndexOutOfBoundsException();
    }
  }

  public Real set (int i, Real v) {
    switch (i) {
    case 0: return x = v;
    case 1: return y = v;
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

  public PV2 (Real x, Real y) { this.x = x; this.y = y; }

  static public PV2 input (double x, double y) {
    return new PV2(Real.input(x), Real.input(y));
  }
  
  static public PV2 constant (double x, double y) {
    return new PV2(Real.constant(x), Real.constant(y));
  }

  public PV2 plus (PV2 that) {
    return new PV2(this.x.plus(that.x), this.y.plus(that.y));
  }

  public PV2 minus () {
    return new PV2(this.x.minus(), this.y.minus());
  }

  public PV2 minus (PV2 that) {
    return new PV2(this.x.minus(that.x), this.y.minus(that.y));
  }

  public PV2 times (Real s) { return new PV2(x.times(s), y.times(s)); }

  public PV2 times (double s) { return new PV2(x.times(s), y.times(s)); }

  public PV2 over (Real s) { return new PV2(x.over(s), y.over(s)); }

  public PV2 over (double s) { return new PV2(x.over(s), y.over(s)); }

  public Real dot (PV2 that) { 
    return this.x.times(that.x).plus(this.y.times(that.y)); 
  }

  public Real cross (PV2 that) { 
    return this.x.times(that.y).minus(this.y.times(that.x)); 
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
  public PV2 unit () { return this.over(length()); }

  /**
   * This vector rotated by 90 degrees counterclockwise.
   */
  public PV2 rot90 () { return new PV2(y.minus(), x); }

  public Real distance (PV2 that) { return that.minus(this).length(); }

  public String toString () { 
    return "(" + x.toString() + ", " + y.toString() + ")"; 
  }
}
