package acp;

class DoubleInterval extends Real {
  double l, r;

  DoubleInterval (double x) { l = r = x; }
  DoubleInterval (double il, double ir) { l = il; r = ir; }
  DoubleInterval (Real x) { l = x.lb(); r = x.ub(); }

  static double prevD (double x) { 
    return Math.nextAfter(x, -Double.MAX_VALUE); 
  }

  static double nextD (double x) { 
    return Math.nextAfter(x, Double.MAX_VALUE); 
  }

  public int precision () { return 16; }

  public Real plus (Real b) {
    DoubleInterval that = (DoubleInterval) b;
    assert this.precision() == that.precision();
    return new DoubleInterval(prevD(this.l + that.l), nextD(this.r + that.r));
  }

  public Real minus () { return new DoubleInterval(-r, -l); }
  
  public Real minus (Real b) {
    DoubleInterval that = (DoubleInterval) b;
    assert this.precision() == that.precision();
    return new DoubleInterval(prevD(this.l - that.r), nextD(this.r - that.l));
  }
  
  public Real times (Real b) {
    DoubleInterval that = (DoubleInterval) b;
    assert this.precision() == that.precision();
    double sl, sr, tl, tr;
    if (r >= 0) {
      sl = this.l;
      sr = this.r;
      tl = that.l;
      tr = that.r;
    }
    else {
      sl = -this.r;
      sr = -this.l;
      tl = -that.r;
      tr = -that.l;
    }

    if (sl >= 0.0)
      if (tl >= 0.0)
        return new DoubleInterval(prevD(sl*tl), nextD(sr*tr));
      else if (tr <= 0.0)
        return new DoubleInterval(prevD(sr*tl), nextD(sl*tr));
      else
        return new DoubleInterval(prevD(sr*tl), nextD(sr*tr));
    if (tl >= 0.0)
      return new DoubleInterval(prevD(sl*tr), nextD(sr*tr));
    if (tr <= 0.0)
      return new DoubleInterval(prevD(sr*tl), nextD(sl*tl));
    double k1 = sl*tr, k2 = sr*tl, nl = k1 < k2 ? k1 : k2,
      k3 = sl*tl, k4 = sr*tr, nu = k3 < k4 ? k4 : k3;
    return new DoubleInterval(prevD(nl), nextD(nu));
  }
  
  public Real over (Real b) {
    int bs = b.sign();
    if (bs == 0)
      throw new ArithmeticException("divide by zero");
    DoubleInterval that = (DoubleInterval) b;
    assert this.precision() == that.precision();

    if (bs > 0)
      if (l >= 0.0)
        return new DoubleInterval(prevD(l/that.r), nextD(r/that.l));
      else if (r <= 0.0)
        return new DoubleInterval(prevD(l/that.l), nextD(r/that.r));
      else
        return new DoubleInterval(prevD(l/that.l), nextD(r/that.l));
    if (l >= 0.0)
      return new DoubleInterval(prevD(r/that.r), nextD(l/that.l));
    if (r <= 0.0)
      return new DoubleInterval(prevD(r/that.l), nextD(l/that.r));
    return new DoubleInterval(prevD(r/that.r), nextD(l/that.r));
  }

  public Real sqrt () {
    int s = sign();
    if (s == 0)
      return new DoubleInterval(0.0);
    if (s < 0)
      throw new ArithmeticException("sqrt of negative number");
    return new DoubleInterval(prevD(Math.sqrt(l)), nextD(Math.sqrt(r)));
  }

  public int weakSign () {
    return r < 0 ? -1 : l > 0 ? 1 : 0;
  }

  public double lb () { return l; }
  public double ub () { return r; }

  boolean contains (DoubleInterval that) {
    return l <= that.l && r >= that.r;
  }
    
  public static void main (String[] args) {
    DoubleInterval e = new DoubleInterval(Math.E);
    DoubleInterval pi = new DoubleInterval(Math.PI);
    DoubleInterval eOpiTpi = (DoubleInterval) e.over(pi).times(pi);
    System.out.println(eOpiTpi.contains(e));
    DoubleInterval spi = (DoubleInterval) pi.sqrt();
    DoubleInterval spi2 = (DoubleInterval) spi.times(spi);
    System.out.println(spi2.contains(pi));
  }
}      

