package circle;

import java.awt.Graphics2D;
import java.awt.Color;
import acp.*;
import pv.*;
import segment.*;
import java.util.ArrayList;

public class Circle extends GO<PV2Scalar> {
  GO<PV2> a, b, c;

  public PV2 center () { return xyz(); }
  public Real radius2 () { return xyz().s; }

  public Circle (GO<PV2> a, GO<PV2> b) {
    this.a = a;
    this.b = b;
  }

  public Circle (GO<PV2> a, GO<PV2> b, GO<PV2> c) {
    this.a = a;
    this.b = b;
    this.c = c;
  }



  public PV2Scalar calculate () {
    PV2 center;
    PV2 ap = a.xyz();
    PV2 bp = b.xyz();

    if (c == null) { // two-point circle
      center = ap.plus(bp).over(2);
    }
    else {
      PV2 cp = c.xyz();

      // EXERCISE:  calculate center for three-point circle
      // PV2 cp = c.xyz();


      // EXERCISE:  calculate center for three-point circle
      PV2 ab1=ap.plus(bp).over(2);
      PV2 ab2=ab1.plus(ap.minus(ab1).rot90());
      PV2 bc1=bp.plus(cp).over(2);
      PV2 bc2=bc1.plus(bp.minus(bc1).rot90());
      center=new ABintersectCDPV2(ab1,ab2,bc1,bc2).calculate();

    }

    Real radius2 = ap.minus(center).dot(ap.minus(center));
    return new PV2Scalar(center, radius2);
  }

  public boolean contains (GO<PV2> p) {
    return CirclePoint.sign(this, p) < 0;
  }

  public void draw (Graphics2D g, Color color) {
    PV2 c = center();
    Real r2 = radius2();
    double x = c.x.approx();
    double y = c.y.approx();
    double r = Math.sqrt(r2.approx());
    g.setColor(color);
    g.drawArc((int) (x - r), (int) (y - r), (int) (2 * r), (int) (2 * r),
              0, 360);
  }
}
