package hull3D;

import acp.*;
import pv.*;

public class Vertex3D {
  public GO<PV3> p;
  KillPair list;

  // this Vertex3D is the tail of a half-dead edge of (dead) t
  Triangle3D t;
  
  public Vertex3D(GO<PV3> p) {
    this.p = p;
  }
  
  public boolean isOutside(Triangle3D t) {
    return t.isOutside(this);
  }
  
  public GO<PV3> getP() {
    return p;
  }
}
