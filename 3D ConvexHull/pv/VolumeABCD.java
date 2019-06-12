package pv;

import acp.*;

public class VolumeABCD extends Predicate {
  GO<PV3> a, b, c, d;

  public VolumeABCD (GO<PV3> a, GO<PV3> b, GO<PV3> c, GO<PV3> d) {
    this.a = a; this.b = b; this.c = c; this.d = d;
  }

  protected Real calculate () {
    return b.xyz().minus(a.xyz()).cross(c.xyz().minus(a.xyz()))
      .dot(d.xyz().minus(a.xyz()));
  }

  public static int sign (GO<PV3> a, GO<PV3> b, GO<PV3> c, GO<PV3> d) {
    return new VolumeABCD(a, b, c, d).sign();
  }
}
