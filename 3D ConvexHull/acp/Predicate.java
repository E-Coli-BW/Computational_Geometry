package acp;

public abstract class Predicate {
  protected abstract Real calculate ();

  public int sign () {
    if (!Real.handleSignException)
      return calculate().sign();
    Real.handleSignException = false;
    while (true)
      try {
        int s = calculate().sign();
        if (Real.curPrecision > 16)
          GO.decreaseAll();
        Real.handleSignException = true;
        return s;
      } catch (Real.SignException se) {
        Real.curPrecision *= 2;
      }
  }
}

