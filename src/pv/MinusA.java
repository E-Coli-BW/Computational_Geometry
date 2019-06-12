package pv;
import acp.*;

public class MinusA extends GO<PV2> {
  GO<PV2> a;

  public MinusA (GO<PV2> a) {
    this.a = a;
  }

  public PV2 calculate () {
    return a.xyz().minus();
  }
}
