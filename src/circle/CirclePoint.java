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
    // radius2 is r^2
    Real r2 = c.radius2();

    // EXERCISE
    // Return value that is negative if p is inside the circle and
    // positive if outside.



    return cp.dot(cp).minus(r2); 
  }

  public static int sign (Circle c, GO<PV2> p) {
    return new CirclePoint(c, p).sign();
  }
}


    
