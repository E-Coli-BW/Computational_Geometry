package circle;

import acp.*;
import pv.*;

public class CirclePoint extends Predicate {
  Circle c;
  GO<PV2> p;

  public CirclePoint (Circle c, GO<PV2> p) {
    this.c = c;
    this.p = p;
  }

  protected Real calculate () {
    PV2 cp = p.xyz().minus(c.center());
    Real r2 = c.radius2();

    // EXERCISE
    // Return value that is negative if p is inside the circle and
    // positive if outside.



    return r2; // wrong!
  }

  public static int sign (Circle c, GO<PV2> p) {
    return new CirclePoint(c, p).sign();
  }
}


    
