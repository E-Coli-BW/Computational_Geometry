package pv;
import acp.*;

public class AplusB extends GO<PV2> {
  GO<PV2> a, b;

  public AplusB (GO<PV2> a, GO<PV2> b) {
    this.a = a;
    this.b = b;
  }

  public PV2 calculate () {
    return a.xyz().plus(b.xyz());
  }
}
