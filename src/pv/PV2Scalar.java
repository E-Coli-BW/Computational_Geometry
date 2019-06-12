package pv;

import acp.Real;
import pv.PV2;

public class PV2Scalar extends PV2 {
  public Real s;

  public int size () { return 3; }

  public Real get (int i) {
    if (i == 2)
      return s;
    return super.get(i);
  }

  public Real set (int i, Real v) {
    if (i == 2)
      return s = v;
    return super.set(i, v);
  }

  public PV2Scalar (PV2 p, Real s) {
    super(p.x, p.y);
    this.s = s;
  }
}

  
