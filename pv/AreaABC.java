package pv;

import acp.*;

public class AreaABC extends Predicate {
  GO<PV2> a, b, c;

  public AreaABC (GO<PV2> a, GO<PV2> b, GO<PV2> c) {
    this.a = a; this.b = b; this.c = c;
  }

  protected Real calculate () {
    return b.xyz().minus(a.xyz()).cross(c.xyz().minus(a.xyz()));
  }

  public static int sign (GO<PV2> a, GO<PV2> b, GO<PV2> c) {
    /*
    ABintersectCD.Pair ab = new ABintersectCD.Pair(a, b);
    if (c instanceof ABintersectCD) {
      if (((ABintersectCD) c).contains(ab))
        return 0;
    }
    else if (ab.contains(c))
      return 0;
    */
    return new AreaABC(a, b, c).sign();
  }
}
