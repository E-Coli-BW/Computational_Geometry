package pv;

import acp.*;

public class UpwardNormal extends Predicate {
  GO<PV3> a, b, c;

  public UpwardNormal (GO<PV3> a, GO<PV3> b, GO<PV3> c) {
    this.a = a; this.b = b; this.c = c;
  }

  protected Real calculate () {
    PV3 ap = a.xyz();
    PV3 bp = b.xyz();
    PV3 cp = c.xyz();
    return bp.minus(ap).cross(cp.minus(ap)).y;
  }

  public static int sign (GO<PV3> a, GO<PV3> b, GO<PV3> c) {
    return new UpwardNormal(a, b, c).sign();
  }
}
