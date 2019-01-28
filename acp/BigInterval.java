package acp;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

class BigInterval extends Real {
  int prec;
  BigDecimal l, r;

  BigInterval (double x) {
    prec = Real.curPrecision;
    l = r = new BigDecimal(x); 
  }
  BigInterval (BigDecimal il, BigDecimal ir) {
    prec = Real.curPrecision;
    l = il;
    r = ir;
  }
  BigInterval (Real x) {
    prec = Real.curPrecision;
    if (x instanceof DoubleInterval) {
      l = new BigDecimal(x.lb());
      r = new BigDecimal(x.ub());
    }
    else {
      l = ((BigInterval) x).l;
      r = ((BigInterval) x).r;
    }
  }

  MathContext dn () { return new MathContext(prec, RoundingMode.FLOOR); }
  MathContext up () { return new MathContext(prec, RoundingMode.CEILING); }

  public int precision () { return prec; }

  public Real plus (Real b) {
    BigInterval that = (BigInterval) b;
    assert this.precision() == that.precision();
    return new BigInterval(this.l.add(that.l, dn()), 
                           this.r.add(that.r, up()));
  }

  public Real minus () { 
    return new BigInterval(r.negate(), l.negate()); 
  }
  
  public Real minus (Real b) {
    BigInterval that = (BigInterval) b;
    assert this.precision() == that.precision();
    return new BigInterval(this.l.subtract(that.r, dn()), 
                           this.r.subtract(that.l, up()));
  }

  public Real times (Real b) {
    BigInterval that = (BigInterval) b;
    assert this.precision() == that.precision();
    BigDecimal sl, sr, tl, tr;
    if (r.signum() >= 0) {
      sl = this.l;
      sr = this.r;
      tl = that.l;
      tr = that.r;
    }
    else {
      sl = this.r.negate();
      sr = this.l.negate();
      tl = that.r.negate();
      tr = that.l.negate();
    }

    if (sl.signum() >=.0)
      if (tl.signum() >=.0)
        return new BigInterval(sl.multiply(tl, dn()), sr.multiply(tr, up()));
      else if (tr.signum() <= 0.0)
        return new BigInterval(sr.multiply(tl, dn()), sl.multiply(tr, up()));
      else
        return new BigInterval(sr.multiply(tl, dn()), sr.multiply(tr, up()));
    if (tl.signum() >=.0)
      return new BigInterval(sl.multiply(tr, dn()), sr.multiply(tr, up()));
    if (tr.signum() <= 0.0)
      return new BigInterval(sr.multiply(tl, dn()), sl.multiply(tl, up()));
    return new BigInterval(sl.multiply(tr, dn()).min(sr.multiply(tl, dn())),
                           sl.multiply(tl, up()).max(sr.multiply(tr, up())));
  }
  
  public Real over (Real b) {
    int bs = b.sign();
    if (bs == 0)
      throw new ArithmeticException("divide by zero");
    BigInterval that = (BigInterval) b;
    assert this.precision() == that.precision();

    if (bs > 0)
      if (l.signum() >= 0.0)
        return new BigInterval(l.divide(that.r, dn()), r.divide(that.l, up()));
      else if (r.signum() <= 0.0)
        return new BigInterval(l.divide(that.l, dn()), r.divide(that.r, up()));
      else
        return new BigInterval(l.divide(that.l, dn()), r.divide(that.l, up()));
    if (l.signum() >= 0.0)
      return new BigInterval(r.divide(that.r, dn()), l.divide(that.l, up()));
    if (r.signum() <= 0.0)
      return new BigInterval(r.divide(that.l, dn()), l.divide(that.r, up()));
    return new BigInterval(r.divide(that.r, dn()), l.divide(that.r, up()));
  }

  static BigDecimal TWO = new BigDecimal(2.0);
  
  static BigDecimal sqrt(BigDecimal x, MathContext updn) {
    MathContext mc = new MathContext(updn.getPrecision());
    BigDecimal x0 = BigDecimal.ZERO;
    BigDecimal x1 = new BigDecimal(Math.sqrt(x.doubleValue()));
    int n = 0;
    while (!x0.equals(x1) && ++n < 10) {
        x0 = x1;
        x1 = x.divide(x0, mc);
        x1 = x1.add(x0);
        x1 = x1.divide(TWO, mc);
    }
    if (n == 10)
      System.out.println("WARNING BigDecimal.sqrt:  n == " + n);
    if (updn.getRoundingMode() == RoundingMode.FLOOR)
      return x1.min(x.divide(x1, updn));
    else
      return x1.max(x.divide(x1, updn));
  }

  public Real sqrt () {
    int s = sign();
    if (s == 0)
      return new BigInterval(0.0);
    if (s < 0)
      throw new ArithmeticException("sqrt of negative number");
    return new BigInterval(sqrt(l, dn()), sqrt(r, up()));
  }

  public int weakSign () {
    return r.signum() < 0 ? -1 : l.signum() > 0 ? 1 : 0;
  }

  public double lb () { 
    double x = l.doubleValue();
    return new BigDecimal(x).compareTo(l) <= 0 ? x : DoubleInterval.prevD(x);
  }

  public double ub () { 
    double x = l.doubleValue();
    return new BigDecimal(x).compareTo(l) >= 0 ? x : DoubleInterval.nextD(x);
  }

  boolean contains (BigInterval that) {
    return l.compareTo(that.l) <= 0 && r.compareTo(that.r) >= 0;
  }
    
  public static void main (String[] args) {
    BigInterval e = new BigInterval(Math.E);
    BigInterval pi = new BigInterval(Math.PI);
    BigInterval eOpiTpi = (BigInterval) e.over(pi).times(pi);
    System.out.println(eOpiTpi.contains(e));
    BigInterval spi = (BigInterval) pi.sqrt();
    BigInterval spi2 = (BigInterval) spi.times(spi);
    System.out.println(spi2.contains(pi));
  }
}      

