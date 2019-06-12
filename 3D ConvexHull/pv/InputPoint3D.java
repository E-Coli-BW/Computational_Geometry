package pv;

import acp.*;

public class InputPoint3D extends GO<PV3> {
  public InputPoint3D (double x, double y, double z) {
    super(PV3.input(x, y, z));
  }
}
