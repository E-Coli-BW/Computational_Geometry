package pv;

import acp.*;

public class DiffY extends Predicate {
  GO<PV2> a, b;

  public DiffY (GO<PV2> a, GO<PV2> b) {
    this.a = a; this.b = b;
  }

  protected Real calculate () {
    return a.xyz().get(1).minus(b.xyz().get(1));
  }

  public static int sign (GO<PV2> a, GO<PV2> b) {
    return new DiffY(a, b).sign();
  }
}
