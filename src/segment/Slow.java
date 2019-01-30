package segment;
import java.util.List;
import java.util.ArrayList;
import java.awt.Graphics2D;
import java.awt.Color;
import acp.*;
import pv.*;

public class Slow implements Intersecter {
  class SState implements State {
    List<GO<PV2>> points = new ArrayList<GO<PV2>>();
    Segment si, sj;

    SState (List<GO<PV2>> out, Segment si, Segment sj) { 
      points.addAll(out); 
      this.si = si;
      this.sj = sj;
    }

    public void draw (Graphics2D g) {
      for (GO<PV2> p : points)
        Drawer.drawPoint(g, p.xyz(), Color.green, "");

      if (si != null)
        Drawer.drawEdge(g, si.tail.xyz(), si.head.xyz(), Color.red, "");

      if (sj != null)
        Drawer.drawEdge(g, sj.tail.xyz(), sj.head.xyz(), Color.red, "");
    }
  }

  List<State> states = new ArrayList<State>();

  public int numStates () { return states.size(); }
  public State getState (int i) { return states.get(i); }

  public List<GO<PV2>> intersect (List<Segment> in) {
    states.clear();
    List<GO<PV2>> out = new ArrayList<GO<PV2>>();

    for (int i = 0; i < in.size(); i++) {
      Segment si = in.get(i);
      for (int j = i+1; j < in.size(); j++) {
        Segment sj = in.get(j);
        states.add(new SState(out, si, sj));
        if (si.intersects(sj)) {
          out.add(new ABintersectCD(si.tail, si.head, sj.tail, sj.head));
          states.add(new SState(out, si, sj));
        }
      }
    }
    
    return out;
  }
}

    
  
