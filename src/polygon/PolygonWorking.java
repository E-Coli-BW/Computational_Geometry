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
  
  // Part 4
  int inout = 0;
  
  // If inout is zero, sets it to 2 if bounded polygon, 1 if unbounded.
  // 2 is rightside in = bounded, 1 is inside out = unbounded
  // Returns inout.
  int getInOut() {
    if (inout == 0) {
      Vert top = verts.get(0);
      
      for (Vert v: verts) {
        if (DiffY.sign(v.p, top.p) < 0)
          top = v;
      }
      
      // counterclockwise, inside out, 1
      if (top.incoming.compareTo(top.outgoing) > 0) {
        inout = 1;
      }
      else { // clockwise, bounded, 2
        inout = 2;
      }
    }
    return inout;
  }
  
  void copyEdge (Edge e, Vert newMaxY) {
    // If e.inout == 0, that's an error!  You forgot to set it.
    if (e.inout == 0) {
      System.out.println("Error: Edge is not initialized");
      return;
    }
    
    // If e.inout == 1, it's buried.  Don't copy it to the output.  Just return.
    if (e.inout == 1) {
      return;
    }
    
    // If e.minY() is not an output vertex
      // replace it by an new output vertex (copy of e.minY())
      // (to create a copy of v use "out.new Vert(v)")
      // inform its edges
      // add it to out's list of vertices
    if ( !out.verts.contains(e.minY()) ) {
      System.out.println("MinY added!");
      Vert v = out.new Vert(e.minY());
      v.informEdges();
      out.verts.add(v);
    }
    
    // Ditto e.maxY *if* newMaxY is null
    // Copy e using out.new Edge(e)
    // If newMaxY is not null, make that the copy's maxY
    // Add the new edge to out's list.
    if ( newMaxY == null ) {
      Vert v = out.new Vert(e.maxY());
      v.informEdges();
      out.verts.add(v);
    }
    
    Edge e2 = out.new Edge(e);
    
    if (newMaxY != null)
      e2.setMaxY(newMaxY);
    
    e.informVerts();
    out.edges.add(e2);
  }
  
  class PState implements State {
    int nverts; // number of verts in out to display
    int nedges; // number of edges in out to display
    List<Edge> sedges = new ArrayList<Edge>(); // Sweep edges
    Real y;
    Edge a, b;

    PState (Real y, Edge a, Edge b) {
      if (out != null)
        nverts = out.verts.size();
      if (out != null)
        nedges = out.edges.size();
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
        Drawer.drawPoint(g, out.verts.get(i).p.xyz(), Color.black, "");
      
      for (int i = 0; i < nedges; i++)
        Drawer.drawEdge(g,  out.edges.get(i).tail.p.xyz(), 
            out.edges.get(i).head.p.xyz(), Color.black, "");

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
    
    // Part 2: copies p, incoming, and outgoing.
    Vert (Vert that) {
      this.p = that.p;
      this.incoming = that.incoming;
      this.outgoing = that.outgoing;
    }
    
    // Part 2: sets the tail or head of the incoming or outgoing edges
    // for each Edge, tail is the first input and head is the second input
    // for each Vertex, incoming.head = outgoing.tail
    // after reducing the edges due to intersection, reset edges to original value
    // Part 2: Vert is telling Edges that "I am your incoming head and outgoing tail" 
    void informEdges() {
      this.incoming.head = this;
      this.outgoing.tail = this;
    }
    
    void draw (Graphics2D g) {
      Drawer.drawPoint(g, p.xyz(), Color.green, "");
    }
  }

  class Edge implements SweepData {
    Vert tail, head;
    SweepNode node;
    Set<Edge> checked = new HashSet<Edge>();
    int inout; // 2 if outside (not buried in) the other polygon. 1 if inside (buried).

    Edge (Vert tail, Vert head) {
      this.tail = tail;
      this.head = head;
    }

    Polygon getPolygon() { return Polygon.this; }
    
    // Part 3: copy constructor
    Edge (Edge that) {
      this.tail = that.tail;
      this.head = that.head;
    }
    
    // Part 3: Edge is telling vert that "I am your incoming and outgoing"
    void informVerts() {
      this.tail.outgoing = this;
      this.head.incoming = this;
    }
 
    // Part 3: replace the current minY vertex (could be head or tail)
    void setMinY(Vert v) {
      if (this.head == this.minY())
        this.head = v;
      else
        this.tail = v;
    }
    
    // Part 3: replace the current maxY vertex (could be head or tail)
    void setMaxY(Vert v) {
      if (this.head == this.maxY())
        this.head = v;
      else
        this.tail = v;
    }

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
      
      // if the two Edge objects are on the same vertex, then we compare
      // that Edge to this.maxY(), because that and this have the same minY()
      if (this.minY() == that.minY()) {
        return AreaABC.sign(that.maxY().p, that.minY().p, this.maxY().p);
      }
      
      // if two Edge objects are not on the same vertex
      // this Edge may not be on the same polygon as that Edge
      return AreaABC.sign(that.maxY().p, that.minY().p, this.minY().p);
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
    if (a == null || b == null) {
      return;
    }
    
    Edge e = (Edge) a.getData();
    Edge f = (Edge) b.getData();

    states.add(new PState(null, e, f));
    
    // EXERCISE 2
    // Check if from same Polygon too.
    // Add a state after each check.
    
    if (e.checked.contains(f)) {
      return;
    }
    
    e.checked.add(f);
    f.checked.add(e);
    
    // if the two edges are on the same Polygon object, or if they
    // don't intersect each other, then don't check for intersection
    if ( e.getPolygon() == f.getPolygon() || !(e.intersects(f)) ) {
      return;
    }
    
    GO<PV2> p = new ABintersectCD(e.tail.p, e.head.p, f.tail.p, f.head.p);
    Vert v = out.new Vert(p, e, f);
    out.verts.add(v);
    events.add(v);
    states.add(new PState(null, e, f));
  }    

  public Polygon union (Polygon that) {
    states.clear();

    for (Edge e : this.edges) {
      e.inout = 0;
      e.checked.clear();
    }
    
    for (Edge e : that.edges) {
      e.inout = 0;
      e.checked.clear();
    }

    this.that = that;
    out = new Polygon();

    for (Vert v : this.verts)
      events.offer(v);
    for (Vert v : that.verts)
      events.offer(v);

    while (events.size() > 0) {
      Vert v = events.poll();
      
      states.add(new PState(v.p.xyz().y, null, null));
      
      if (v.getPolygon() == out) {                 //  \ /
        SweepNode iNode = v.incoming.getNode();    //  / \
        SweepNode oNode = v.outgoing.getNode();
        System.out.println("X event");
        // EXERCISE 3
        // v is intersection of a this edge with a that edge.
        states.add(new PState(v.p.xyz().y, null, null)); // repeat after swap
           
        // For intersection, iNode is always to the left of oNode
        iNode.swapWithNext();
        
        check(iNode.getPrevious(), iNode);
        check(oNode, oNode.getNext());
        
        // Part 8: For the swap event v, for each edge e that swaps
        // Call copyEdge(e, v)
        // Set the minY of e to v.
        // (before changing the inout for the edge)
        copyEdge(v.incoming, v);
        copyEdge(v.outgoing, v);
        v.incoming.setMinY(v);
        v.outgoing.setMinY(v);
        v.incoming.inout = 3 - v.incoming.inout;
        v.outgoing.inout = 3 - v.outgoing.inout;

        states.add(new PState(v.p.xyz().y, null, null));
      }
      // cannot assume incoming to outgoing is going in clockwise direction
      // instead, we check v.incoming.node with v.outgoing.node
      // If v.incoming.node.next == v.outgoing.node, 
      //   then incoming is to the right of outgoing
      // If v.incoming.node.next != v.outgoing.node, 
      //   then incoming is to the left of outgoing
      else if (v == v.incoming.minY())  { //   /\
        if (v == v.outgoing.minY()) {     //  /  \
          System.out.println("^ event");
          
          // Part 6: Set inout for both edges based on the 
          // three possibilities for the edge to the right.  
          SweepNode left = sweep.add(v.incoming);
          SweepNode right = sweep.add(v.outgoing);
          
          // figure out left and right nodes to prevent 3 branch conditions
          if (left.getNext() != right) {
            right = v.incoming.node;
            left = v.outgoing.node;
          }
          
          check(left.getPrevious(), left);
          check(right, right.getNext());
          
          // case 1: right.next is null
          // need to check if current polygon is this or that, 
          // and then set inout to the opposite polygon
          if (right.getNext() == null) {
            System.out.println("RIGHT IS NULL");
            if (v.getPolygon() == that) {
              System.out.println("this.inout " + this.inout);
              v.incoming.inout = this.getInOut();
              v.outgoing.inout = this.getInOut();
            }
            else {
              System.out.println("that.inout " + that.inout);
              v.incoming.inout = that.getInOut();
              v.outgoing.inout = that.getInOut();
            }
          }
          else {
            Edge nextEdge = (Edge) right.getNext().getData();
            
            // case 2: if right.next is an edge from the same polygon, copy its inout 
            if (nextEdge.getPolygon() == v.getPolygon()) {
              v.incoming.inout = nextEdge.inout;
              v.outgoing.inout = nextEdge.inout;
            }
            // case 3: if right.next is an edge from a different polygon
            else {
              // figure out if right.next is rightside out then v is rightside out
              // otherwise if right.next is inside, then v is inside
              // 1 is unbounded clockwise, 2 is bounded counterclockwise
              if (nextEdge.minY() == nextEdge.tail) {
                v.incoming.inout = 1;
                v.outgoing.inout = 1;
              }
              else {
                v.incoming.inout = 2;
                v.outgoing.inout = 2;
              }
            }
          }
          System.out.println("incoming inout " + v.incoming.inout);
          System.out.println("outgoing inout " + v.outgoing.inout);
          states.add(new PState(v.p.xyz().y, null, null));
        }
        if (v == v.outgoing.maxY()) {         //   o /  or  \ o
          // EXERCISE 5                       //   i \      / i
          System.out.println("< event");
          SweepNode oNode = v.outgoing.node;  
          
          // Part 9: before deleting v.outgoing by setting its data, copy it
          copyEdge(v.outgoing, null);
          
          // in the case where outgoing is on top of incoming
          // instead of removing node that points to outgoing segment, 
          // just set it to point to incoming segment
          oNode.setData(v.incoming);
          
          // Part 7: Set inout for the replacement edge (same as the one it replaces).
          v.incoming.inout = v.outgoing.inout;

          // after oNode points to incoming, check incoming with its neighbors
          check(oNode.getPrevious(), oNode);
          check(oNode, oNode.getNext());
          
          states.add(new PState(v.p.xyz().y, null, null));
        }
      }
      else if (v == v.incoming.maxY()) {
        if (v == v.outgoing.minY()) {         //   i /  or  \ i
          // EXERCISE 6                       //   o \      / o
          System.out.println("> event");
          SweepNode iNode = v.incoming.node;
          
          // Part 9: before deleting v.incoming by setting its data, copy it
          copyEdge(v.incoming, null);
          
          // in the case where incoming is on top of outgoing
          // instead of removing node that points to incoming segment, 
          // just set it to point to outgoing segment
          iNode.setData(v.outgoing);
          
          // Part 7: Set inout for the replacement edge (same as the one it replaces).
          v.outgoing.inout = v.incoming.inout;
          
          // after iNode points to outgoing, check incoming with its new neighbors
          check(iNode.getPrevious(), iNode);
          check(iNode, iNode.getNext());
          
          states.add(new PState(v.p.xyz().y, null, null));
        }
        if (v == v.outgoing.maxY()) {  //  \  /
          // EXERCISE 7                //   \/
          // iNode is to the right of oNode
          // do we remove both Edges since incoming and outgoing are at the maxY()?
          System.out.println("V event");
          SweepNode iNode = v.incoming.node;
          SweepNode oNode = v.outgoing.node;
          
          // if iNode.next is not equal to oNode, 
          // then iNode is to the right of oNode
          if (iNode.getNext() != oNode) {
            check(oNode.getPrevious(), iNode.getNext());
          }
          else {
            check(iNode.getPrevious(), oNode.getNext());
          }
          
          // Part 9: For every other event that removes an edge e 
          // from the sweep list, call copyEdge(e, null).
          copyEdge(v.incoming, null);
          copyEdge(v.outgoing, null);
          
          iNode.remove(); 
          oNode.remove(); 

          states.add(new PState(v.p.xyz().y, null, null));
        }
      }
    }

    // Part 5: Make sure you clear all the checked of 
    // both this and that edges. Also set their inout to zero.
    for (Edge e : this.edges) {
      e.checked.clear();
      e.inout = 0;
    }
    
    for (Edge e : that.edges) {
      e.checked.clear();
      e.inout = 0;
    }
    
    // Part 5: At the bottom, use informEdges to restore 
    // this's and that's head and tail pointers.
    for (Vert v: this.verts) {
      v.informEdges();
    }
    
    for (Vert v: that.verts) {
      v.informEdges();
    }
    
    // Part 5: Use informVerts to set out's incoming and outgoing pointers.
    for (Edge e: out.edges) {
      e.informVerts();
    }
    
//     return this;
    return out;
  }
}
