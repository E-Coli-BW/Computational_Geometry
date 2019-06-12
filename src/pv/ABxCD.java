package pv;

import acp.*;

public class ABxCD extends Predicate {
  GO<PV2> a, b, c, d;

  public ABxCD (GO<PV2> a, GO<PV2> b, GO<PV2> c, GO<PV2> d) {
    this.a = a; this.b = b; this.c = c; this.d = d;
  }

  protected Real calculate () {
    return (b.xyz().minus(a.xyz()).cross(d.xyz().minus(c.xyz())));
  }

  public static int sign (GO<PV2> a, GO<PV2> b, GO<PV2> c, GO<PV2> d) {
    return new ABxCD(a, b, c, d).sign();
  }
}
