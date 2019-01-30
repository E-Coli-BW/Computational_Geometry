package acp;

import java.util.List;
import java.util.ArrayList;

public class GO <P extends XYZ> {
  P p;

  public GO () {}

  public GO (P p) {
    assert Real.curPrecision == 16;
    this.p = (P) p.clone(); 
    for (int i = 0; i < p.size(); i++)
      assert p.get(i).lb() == p.get(i).ub() 
        : "input object has non-trivial interval";
  }

  protected P calculate () {
    assert p != null;
    changePrecision();
    return p;
  }

  int precision () {
    return p == null ? 0 : 
      p.size() == 0 ? Real.curPrecision : p.get(0).precision();
  }

  public P xyz () {
    int prec = precision();
    if (prec < Real.curPrecision) {
      if (Real.handleSignException)
        safe_setp();
      else
        p = calculate();
      if (Real.curPrecision > 16 && prec <= 16)
        increased.add(this);
    }
    else
      assert prec == Real.curPrecision;

    return (P) p.clone();
  }

  public int size () { return p.size(); }

  void safe_setp () {
    assert Real.handleSignException;
    Real.handleSignException = false;
    while (true)
      try {
        p = calculate();
        if (Real.curPrecision > 16)
          decreaseAll();
        Real.handleSignException = true;
        return;
      } catch (Real.SignException se) {
        Real.curPrecision *= 2;
      }
  }

  private static List<GO> increased = new ArrayList<GO>();

  void changePrecision () {
    for (int i = 0; i < p.size(); i++)
      p.set(i, p.get(i).changePrecision());
  }

  static void decreaseAll () {
    Real.curPrecision = 16;
    for (GO o : increased)
      o.changePrecision();
    increased.clear();
  }
}


  
