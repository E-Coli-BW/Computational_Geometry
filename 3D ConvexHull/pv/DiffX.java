package pv;

import acp.*;

public class DiffX extends Predicate {
  GO<PV2> a, b;

  public DiffX (GO<PV2> a, GO<PV2> b) {
    this.a = a; this.b = b;
  }

  protected Real calculate () {
    return a.xyz().get(0).minus(b.xyz().get(0));
  }

  public static int sign (GO<PV2> a, GO<PV2> b) {
    return new DiffX(a, b).sign();
  }
}
