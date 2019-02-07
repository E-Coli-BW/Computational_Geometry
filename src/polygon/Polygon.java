package polygon;
import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.HashSet;
import java.util.Comparator;
import java.awt.Graphics2D;
import java.awt.Color;
import acp.*;
import pv.*;
import hull.State;
import segment.Drawer;
import segment.ABintersectCD;

public class Polygon {
  class PState implements State {
    int nverts; // number of verts in out to display
    List<Edge> sedges = new ArrayList<Edge>(); // Sweep edges
    Real y;
    Edge a, b;

    PState (Real y, Edge a, Edge b) {
      if (out != null)
        nverts = out.verts.size();
      for (SweepNode n = sweep.getFirst(); n != null; n = n.getNext())
        sedges.add((Edge) n.getData());
      this.y = y;
      this.a = a;
      this.b = b;
    }

    public void draw (Graphics2D g) {
      int j = 0;
      for (Edge e : sedges)
        Drawer.drawEdge(g, e.tail.p.xyz(), e.head.p.xyz(), Color.orange, "" + j++);

      if (a != null)
        Drawer.drawEdge(g, a.tail.p.xyz(), a.head.p.xyz(), Color.red, "");

      if (b != null)
        Drawer.drawEdge(g, b.tail.p.xyz(), b.head.p.xyz(), Color.red, "");

      for (int i = 0; i < nverts; i++)
        Drawer.drawPoint(g, out.verts.get(i).p.xyz(), Color.red, "");

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

  class Vert {
    GO<PV2> p;
    Edge incoming, outgoing;
    Vert twin;

    Vert (GO<PV2> p, Edge incoming, Edge outgoing) {
      this.p = p;
      this.incoming = incoming;
      this.outgoing = outgoing;
    }

    Polygon getPolygon() { return Polygon.this; }

    void draw (Graphics2D g) {
      Drawer.drawPoint(g, p.xyz(), Color.green, "");
    }
  }

  class Edge implements SweepData {
    Vert tail, head;
    SweepNode node;
    Set<Edge> checked = new HashSet<Edge>();

    Edge (Vert tail, Vert head) {
      this.tail = tail;
      this.head = head;
    }

    Polygon getPolygon() { return Polygon.this; }

    void draw (Graphics2D g) {
      Drawer.drawEdge(g, tail.p.xyz(), head.p.xyz(), Color.blue, "");
    }

    boolean intersects (Edge that) {
      return (AreaABC.sign(tail.p, that.tail.p, that.head.p) !=
              AreaABC.sign(head.p, that.tail.p, that.head.p)
              &&
              AreaABC.sign(that.tail.p, tail.p, head.p) !=
              AreaABC.sign(that.head.p, tail.p, head.p));
    }

    GO<PV2> intersection (Edge that) {
      return new ABintersectCD(tail.p, head.p, that.tail.p, that.head.p);
    }

    Vert minY () { return DiffY.sign(tail.p, head.p) < 0 ? tail : head; }
    Vert maxY () { return DiffY.sign(tail.p, head.p) > 0 ? tail : head; }

    public int compareTo (SweepData data) {
      Edge that = (Edge) data;
      // EXERCISE 1
      // Use minY and maxY instead of tail and head.
      // Include the case that this and that have the same minY vertex.
     
     
      return 1;
    }

    public SweepNode getNode () { return node; }
    public void setNode (SweepNode node) { this.node = node; }
  }

  List<Vert> verts = new ArrayList<Vert>();
  List<Edge> edges = new ArrayList<Edge>();

  private Polygon () {}

  public Polygon (List<GO<PV2>> points) {
    for (GO<PV2> p : points)
      verts.add(new Vert(p, null, null));

    Vert prev = verts.get(verts.size()-1);
    for (Vert v : verts) {
      Edge e = new Edge(prev, v);
      edges.add(e);
      e.tail.outgoing = e;
      e.head.incoming = e;
      prev = v;
    }
  }

  public void draw (Graphics2D g) {
    for (Edge e : edges)
      e.draw(g);
    if (that != null)
      for (Edge e : that.edges)
        e.draw(g);
    if (out != null)
      for (Vert v : out.verts)
        v.draw(g);
  }

  class CompareVerts implements Comparator<Vert> {
    public int compare (Vert a, Vert b) {
      return DiffY.sign(a.p, b.p);
    }
  }

  Polygon that;
  PriorityQueue<Vert> events = new PriorityQueue<Vert>(100, new CompareVerts());
  SweepList sweep = new SlowList();
  Polygon out;

  void check (SweepNode a, SweepNode b) {
    if (a == null || b == null)
      return;
    
    Edge e = (Edge) a.getData();
    Edge f = (Edge) b.getData();

    states.add(new PState(null, e, f));
    
    // EXERCISE 2
    // Check if from same Polygon too.
    // Add a state after each check.
  


    GO<PV2> p = new ABintersectCD(e.tail.p, e.head.p, f.tail.p, f.head.p);
    Vert v = out.new Vert(p, e, f);
    out.verts.add(v);
    events.add(v);
    states.add(new PState(null, e, f));
  }    

  public Polygon union (Polygon that) {
    states.clear();

    for (Edge e : this.edges)
      e.checked.clear();
    for (Edge e : that.edges)
      e.checked.clear();

    this.that = that;
    out = new Polygon();

    for (Vert v : this.verts)
      events.offer(v);
    for (Vert v : that.verts)
      events.offer(v);

    while (events.size() > 0) {
      Vert v = events.poll();
      states.add(new PState(v.p.xyz().y, null, null));
      
      if (v.getPolygon() == out) {                //  \ /
        SweepNode left = v.incoming.getNode();    //  / \
        SweepNode right = v.outgoing.getNode();
        
        // EXERCISE 3
        // v is intersection of a this edge with a that edge.
        states.add(new PState(v.p.xyz().y, null, null)); // repeat after swap
        

      }
          
      // EXERCISE 4
      // EXERCISE 5
      // EXERCISE 6 
      // EXERCISE 7
    }


    for (Edge e : this.edges)
      e.checked.clear();
    for (Edge e : that.edges)
      e.checked.clear();

    return this;
  }
}