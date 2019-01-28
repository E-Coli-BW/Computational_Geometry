package segment;
import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.awt.Graphics2D;
import java.awt.Color;
import acp.*;
import pv.*;

public class Fast implements Intersecter {
  class FState implements State {
    List<GO<PV2>> o = new ArrayList<GO<PV2>>();
    List<Segment> segs = new ArrayList<Segment>();
    Real y;
    Segment a, b;

    FState (Real y, Segment a, Segment b) {
      o.addAll(out);
      for (SweepNode n = sweep.getFirst(); n != null; n = n.getNext())
        segs.add(n.getSegment());
      this.y = y;
      this.a = a;
      this.b = b;
    }

    public void draw (Graphics2D g) {
      for (Segment s : segs)
        Drawer.drawEdge(g, s.tail.xyz(), s.head.xyz(), Color.orange, "");

      if (a != null)
        Drawer.drawEdge(g, a.tail.xyz(), a.head.xyz(), Color.red, "");

      if (b != null)
        Drawer.drawEdge(g, b.tail.xyz(), b.head.xyz(), Color.red, "");

      for (GO<PV2> p : o)
        Drawer.drawPoint(g, p.xyz(), Color.green, "");

      if (y != null) {
        PV2 pm = new PV2(Real.constant(-1000), y);
        PV2 pp = new PV2(Real.constant(1000), y);
        Drawer.drawEdge(g, pm, pp, Color.blue, "");
      }
    }
  }

  List<State> states = new ArrayList<State>();

  public int numStates () { return states.size(); }
  public State getState (int i) { return states.get(i); }

  // Tail, head, or intersection event.
  class Event implements Comparable<Event> {
    // b is null for tail.  a is null for head.
    Segment a, b;
    GO<PV2> p; // tail, head, or intersection

    Event (Segment a, Segment b, GO<PV2> p) {
      this.a = a;
      this.b = b;
      this.p = p;
    }

    public int compareTo (Event that) {
      // EXERCISE 5
      // Events are ordered by the y coordinate of p.
      return 0; // wrong
    }
  }

  // All the intersections found so far.
  List<GO<PV2>> out = new ArrayList<GO<PV2>>();

  // Set of segments intersecting current sweep line.
  SweepList sweep = new SlowList();

  // Upcoming events.
  PriorityQueue<Event> events = new PriorityQueue<Event>();
  
  void check (SweepNode a, SweepNode b) {
    // EXERCISE 5
    // Check a.getSegment() and b.getSegment() for intersection.

    // Look at the Segment class and use the hash tables (checked) to
    // avoid calling the intersect more than once so we don't
    // calculate duplicate intersection points.

    // If the segments intersect, add the intersection to out.  Also
    // add a new Event.

    // Add new FState(null, a.getSegment(), b.getSegment()) to states
    // before checking for intersection and after finding one, if you
    // do.
  }      

  public List<GO<PV2>> intersect (List<Segment> in) {
    System.out.println("START");
    states.clear();
    events.clear();
    out.clear();

    // Add events corresponding to tails and head of segments.
    for (Segment s : in) {
      s.checked.clear();
      // Tail event, event.b == null.
      // Head event, event.a == null.
      events.offer(new Event(s, null, s.tail));
      events.offer(new Event(null, s, s.head));
    }

    while (events.size() > 0) {
      Event event = events.poll();

      // Handle three types of event.
      // Add new FState(event.p.xyz().y, null, null) before and after
      // modifying sweep list.
      // Call check with all newly adjacent pairs of nodes.
      if (event.b == null) {
        // EXERCISE 6
        // Tail event.

      }
      else if (event.a == null) {
        // EXERCISE 7
        // Head event.


      }
      else {
        // EXERCISE 8
        // Intersection event.

        // Note: node.swapWithNext() swaps the segments of node and
        // its successor node in the sweep list, but it does not
        // change the positions of the nodes in the sweep list.

      }
    }

    return out;
  }
}

    
  
