package polygon;
import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.HashSet;
import java.util.Comparator;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.HashMap; 
import java.util.Map; 
import acp.*;
import pv.*;
import hull.State;
// import hull.Fast.FState;
import segment.Drawer;
import segment.ABintersectCD;

public class Polygon {
  
  List<Vert> verts = new ArrayList<Vert>();
  List<Edge> edges = new ArrayList<Edge>();
  List<State> states = new ArrayList<State>();


  // prog07, 1: Add a new edge list called chords to Polygon.
  List<Edge> chords = new ArrayList<Edge>();
  
  public int numStates () { return states.size(); }
  public State getState (int i) { return states.get(i); }
  
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
  
  Polygon that;
  PriorityQueue<Vert> events = new PriorityQueue<Vert>(100, new CompareVerts());
  SweepList sweep = new SlowList();
  Polygon out;
  // prog 07: 4.1: a public void monotononize and triangulate to Polygon
  public void monotonize(){
    // clear states
    // states is List<State>
    states.clear();
    // clear events
    // events is PriorityQueue<Vert>
    events.clear();
    // clear chords
    // chords is of List<Edge>
    chords.clear();
    // clear Edge helper
    for(Edge e:edges){
        e.helper=null;
    }
    

    //Set Edge next and prev fields (how?).
    //incoming's next would be outgoing (we assume we always have incoming on the left)
    // outgoing's prev would be incoming
    for(Vert v: verts){
      v.incoming.next=v.outgoing;
      v.outgoing.prev=v.incoming;
    //Offer every vert as an event.
      events.offer(v);
    }
    // while we still have vertex to be evaluated
    // i.e: Priority Queue(Prioritized by height) "events" is not empty
    // evaluate each vertex 
    while(events.size()>0){
      // pull out a vertex for examination
      // poll:returns the element at the front
      Vert v = events.poll();
      // each vertex has a point, each point has xyz coordinates
      // in xyz coordinates, we can get its y coordinate
      // states keeps a list of TStates ordered by y-coordinate value
      states.add(new TState(v.p.xyz().y));
      if (v.incoming.minY()==v.outgoing.minY()){
        // ^ shape, deem CCW be bad
        // a Sweep node is a pointer that points to a vertex
        // that has info about the incoming and outgoing edges
        SweepNode left=sweep.add(v.outgoing);
        SweepNode right=sweep.add(v.incoming);
        // isStalactite is bad
        if(v.incoming.isStalactite()){
          // note that a helper edge is just a copy ofthe previously detexted incoming
          // edge of a bad vertex
          Edge e = (Edge) right.getNext().getData();
          e.setHelper(v.incoming);
        }
        v.outgoing.setHelper(v.outgoing.prev);
      }
      else if (v.incoming.minY() == v.outgoing.maxY()){
        // / or \ out up in down
        // \    /
        SweepNode oNode = v.outgoing.node;
        // SweepNode iNode = v.incoming.node;
        oNode.setData(v.incoming);
        Edge e = (Edge) oNode.getNext().getData();
        e.setHelper(v.incoming);
      }
      else if (v.incoming.maxY() == v.outgoing.minY()){
        // / or \ out down in up
        // \    / 
        SweepNode iNode = v.incoming.node;
        iNode.setData(v.outgoing);

        v.incoming.setHelper(v.incoming);
        v.outgoing.setHelper(v.outgoing.prev);
      }
      else if (v.incoming.maxY() == v.outgoing.maxY()){
        // \ / condition left in right out
        SweepNode iNode = v.incoming.node;
        SweepNode oNode = v.outgoing.node;

        v.incoming.setHelper(v.incoming);
        // test if v is bad (stalagmite)
        if(v.incoming.isStalagmite()){
          Edge e = (Edge) oNode.getNext().getData();
          e.setHelper(v.outgoing.prev);
        }

        // this is the end of their life kill them!
        iNode.remove();
        oNode.remove();
      }


    }
  }


  // prog 07: 4.2: a public void monotononize and triangulate to Polygon
  // this is a triangle
  public void triangulate(){
    // //In triangulate, clear states
    // states.clear();
    // //In triangulate, for each edge check if it bounds a triangle 
    // for(Edge e:edges){
    //   //if it bounds a triangle
    //   if(e.next.next.next==e){// Found a good triangle! don't need to proceed triangulating!
    //     continue;
    //   } 
    //   // TO BE FINISHED!

    //   // if not find the bottom of its loop (the edge whose head is the
    //   // lowest vertex) and call (overloaded) triangulate(bottom).
    // }
    // //and if not, find the bottom of its loop (the edge whose head is the
    // //lowest vertex) and call (overloaded) triangulate(bottom).
    states.clear();

    for (Edge e : edges)
      e.next.prev = e;
    for (Edge e : chords)
      e.next.prev = e;

    List<Edge> bottoms = new ArrayList<Edge>();

    for (Edge e : edges)
      if (e.isBottom())
        bottoms.add(e);

    for (Edge e : chords)
      if (e.isBottom())
        bottoms.add(e);

    for (Edge e : bottoms)
      triangulate(e);
  }
  // prog 07: 8.1: Triangulate(bottom) sets left (=bottom) and right (=bottom.next).

   // Figures whether it is triangulating on the left or the right
   // by comparing left.maxY() to right.maxY()

   // Calls triangulateLeft(left, right) or triangulateRight(left, right).

  // @overrided triangulate method
  public void triangulate(Edge bottom){
    states.add(new TState(bottom.head.p.xyz().y));

    if (bottom.next.next.next == bottom)
      return;

    Edge left = bottom;
    Edge right = bottom.next;

    if (DiffY.sign(left.tail.p, right.head.p) < 0)
      triangulateLeft(left, right);
    else
      triangulateRight(left, right);

  }
    // TO BE FINISHED
  public void triangulateRight(Edge left, Edge right){
    Edge last = right;
    while (last.next.head != left.tail && 
           DiffY.sign(last.next.head.p, left.tail.p) < 0) {
      last = last.next;
      states.add(new TState(last.maxY().p.xyz().y));
      System.out.println("last is " + verts.indexOf(last.tail) + " " + verts.indexOf(last.head));
      System.out.print(verts.indexOf(last.prev.tail) + " ");
      System.out.print(verts.indexOf(last.prev.head) + "\n");
      System.out.println(last.prev == last);
      while (last.prev != left && 
             AreaABC.sign(last.prev.tail.p, last.tail.p, last.head.p) > 0) {
        Edge prev = last.prev.prev;
        prev.addChordHeads(last);
        System.out.println("added right chord");
        last = prev.next;
      }
    }
    while (left.next.next.next != left && left.head != last.head) {
      left.next.addChordHeads(left.prev);
      System.out.println("added left right chord");
      left = left.next.next.twin;
    }

    if (left.next.next.next != left)
      triangulateLeft(left, left.next);
  }
    // TO BE FINISHED
  public void triangulateLeft(Edge left, Edge right){
    Edge last = left;
    while (last.prev.tail != right.head && 
           DiffY.sign(last.prev.tail.p, right.head.p) < 0) {
      last = last.prev;
      states.add(new TState(last.maxY().p.xyz().y));
      while (last.next != right && 
             AreaABC.sign(last.next.head.p, last.head.p, last.tail.p) < 0) {
        Edge next = last.next.next;
        next.addChordTails(last);
        System.out.println("added left chord");
        last = next.prev;
      }
    }

    while (right.prev.prev.prev != right && right.tail != last.tail) {
      right.prev.addChordTails(right.next);
      System.out.println("added right left chord");
      right = right.prev.prev.twin;
    }

    if (right.prev.prev.prev != right)
      triangulateRight(right,right.prev);
  }
  // Prog05, Part 4
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
  
  // Prog06, Part 5: 
  // Create a (private) invert method using the notes.  (Don't forget to flip inout!)
  // To invert a polygon, swap each edge's tail and head and inform its vertices.
  private void invert() {
    this.inout = 3 - this.getInOut();
    
    for (Edge e : edges) {
      Vert temp = e.tail;
      e.tail = e.head;
      e.head = temp;
      e.informVerts();
    }
  }
  
  // Prog06, Part 6:
  // Create a public copy method. It allocates out, a new Polygon, and copies
  // "this" to out using the notes. (Don't forget to copy inout.)
  // To copy a polygon:
  // Copy each vertex and inform its edges (mutilating the input) and add it to the output.
  // Copy each edge and inform its vertices and add it to the output.
  // Have the original vertices inform their edges (unmutilating).
  public Polygon copy() {
    
    out = new Polygon();
    out.inout = this.inout;
    
    for (Vert v: verts) {
      Vert vertCopy = out.new Vert(v.p, v.incoming, v.outgoing);
      vertCopy.informEdges();
      out.verts.add(vertCopy);
    }
    
    for (Edge e : edges) {
      Edge edgeCopy = out.new Edge(e);
      edgeCopy.informVerts();
      out.edges.add(edgeCopy);
    }
    
    for (Vert v: verts) {
      v.informEdges();
    } 
    
    return out;
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
    if ( !out.verts.contains(e.maxY()) && newMaxY == null ) {
      Vert v = out.new Vert(e.maxY());
      v.informEdges();
      out.verts.add(v);
    }
    
    Edge e2 = out.new Edge(e);
    
    if (newMaxY != null) {
      e2.setMaxY(newMaxY);
    }
    
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
  
  // prog07: 1.4: Add a new state class:
  class TState implements State {
    int nchords; // number of chords to display
    List<Edge> sedges = new ArrayList<Edge>(); // Sweep edges
    Real y;

    TState (Real y) {
      nchords = chords.size();
      for (SweepNode n = sweep.getFirst(); n != null; n = n.getNext())
        sedges.add((Edge) n.getData());
      this.y = y;
    }

    public void draw (Graphics2D g) {
      int j = 0;
      for (Edge e : sedges)
        Drawer.drawEdge(g, e.tail.p.xyz(), e.head.p.xyz(), Color.orange, "" + j++);

      for (int i = 0; i < nchords; i++)
        Drawer.drawEdge(g, chords.get(i).tail.p.xyz(), 
                        chords.get(i).head.p.xyz(), Color.red, "");

      if (y != null) {
        PV2 pm = new PV2(Real.constant(-1000), y);
        PV2 pp = new PV2(Real.constant(1000), y);
        Drawer.drawEdge(g, pm, pp, Color.blue, "");
      }
    }
  }




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
    
    // Prog05, Part 2: 
    // copies p, incoming, and outgoing.
    Vert (Vert that) {
      this.p = that.p;
      this.incoming = that.incoming;
      this.outgoing = that.outgoing;
    }
    
    // Prog05, Part 2: 
    // sets the tail or head of the incoming or outgoing edges
    // for each Edge, tail is the first input and head is the second input
    // for each Vertex, incoming.head = outgoing.tail
    // after reducing the edges due to intersection, reset edges to original value
    // Vert is telling Edges that "I am your incoming head and outgoing tail" 
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
    
    // prog07,2.1,2.2,2.3
    Edge prev, next, twin;
    //prog07, 2.4 isStalagmite and isStalactite
    boolean isStalagmite(){
      return (DiffY.sign(tail.p,head.p)<0 && DiffY.sign(next.tail.p,next.head.p)>0 && AreaABC.sign(tail.p,head.p,next.head.p)<0);
    }
    
    boolean isStalactite(){
      return (DiffY.sign(tail.p,head.p)>0 && DiffY.sign(next.tail.p,next.head.p)<0 && AreaABC.sign(tail.p,head.p,next.head.p)<0);
    }
    boolean isBottom(){
      return (DiffY.sign(tail.p,head.p)>0 && DiffY.sign(next.tail.p,next.head.p)<0 && AreaABC.sign(tail.p,head.p,next.head.p)>0);
    }
    // Prog07, 3.1, a helper Edge
    Edge helper;
    
    Edge (Vert tail, Vert head) {
      this.tail = tail;
      this.head = head;
    }
    // Prog07, 3.2, a setHelper method
    void setHelper (Edge newHelper) {
      if (helper != null && 
          (helper.isStalagmite() || newHelper.isStalactite()))
        helper.addChord(newHelper);
      helper = newHelper;
    }

    // Prog07, 3.3: an addCHord method
    void addChord(Edge that){
   //       Look at the figures (notes) to see how it works:
      // Edge e=this.next
   // a is this and c is that

   // Create e and f and make them twins.

   // a.next = e, e.prev = a, etc.
      Edge thisnext = this.next;
      Edge thatnext = that.next;
      this.next = new Edge(this.head, that.head);
      that.next = new Edge(that.head, this.head);
      this.next.twin = that.next;
      that.next.twin = this.next;
      this.next.next = thatnext;
      that.next.next = thisnext;
      chords.add(this.next);
      chords.add(this.next.twin);
      states.add(new TState(that.head.p.xyz().y));
    }
    // Prog07, 6.1: Create addChordHeads that calls addChords.
    void addChordHeads(Edge that){
      this.addChord(that);
      // Edge thisnext = this.next;
      // Edge thatnext = that.next;
      // this.next = new Edge(this.head,that.head);
      // this.next.prev=this;
      // that.next=new Edge(that.head,this.head);
      // that.next.prev=that;
      // this.next.twin=that.next;
      // that.next.twin=this.next;
      // this.next.next.prev=this.next;
      // that.next.next=thisnext;
      // that.next.next.prev=that.next;
      // chords.add(this.next);
      // chords.add(this.next.twin);
      // states.add(new TState(that.head.p.xyz().y));

    }

    // Prog07, 6.2:Create addChordTails that creates a chord from the tail of this to
    //the tail of that.

    void addChordTails(Edge that){
      // Edge thisprev = this.prev;
      // Edge thatprev = that.prev;
      // this.prev = new Edge(that.tail,this.tail);
      // this.prev.next=this;
      // that.prev=new Edge(this.tail,that.tail);
      // that.prev.next=that;
      // this.prev.twin=that.prev;
      // that.prev.twin=this.prev;
      // this.prev.prev=thatprev;
      // this.prev.prev.next=this.prev;
      // that.prev.prev = thisprev;
      // that.prev.prev.next = that.prev;
      // chords.add(this.prev);
      // chords.add(this.prev.twin);
      // states.add(new TState(that.tail.p.xyz().y));
      Edge e = new Edge(this.tail, that.tail);
      Edge f = new Edge(that.tail, this.tail);
      e.twin = f;
      f.twin = e;
      
      Edge b = this.next;
      Edge d = this.prev;
      
      b.next = f;
      f.prev = b;
      
      f.next = this;
      this.next = f;
      
      d.next = e;
      e.prev = d;
      
      e.next = that;
      that.prev = e;
      
      chords.add(e);
    }


    Polygon getPolygon() { return Polygon.this; }
    
    // Prog05, Part 3: copy constructor
    Edge (Edge that) {
      this.tail = that.tail;
      this.head = that.head;
    }
    
    // Prog05, Part 3: 
    // Edge is telling vert that "I am your incoming and outgoing"
    void informVerts() {
      this.tail.outgoing = this;
      this.head.incoming = this;
    }
 
    // Prog05, Part 3: 
    // replace the current minY vertex (could be head or tail)
    void setMinY(Vert v) {
      if (this.head == this.minY())
        this.head = v;
      else
        this.tail = v;
    }
    
    // Prog05, Part 3: 
    // replace the current maxY vertex (could be head or tail)
    void setMaxY(Vert v) {
      if (this.head == this.maxY())
        this.head = v;
      else
        this.tail = v;
    }

    void draw (Graphics2D g) {
      Drawer.drawEdge(g, tail.p.xyz(), head.p.xyz(), Color.blue, "");
    }
    
// prog07:1.2 Add a new draw method to Edge that takes a color.

    void draw (Graphics2D g, Color color) {
      Drawer.drawEdge(g, tail.p.xyz(), head.p.xyz(), color, "");
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
      // Prog04, EXERCISE 1
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
  
  // Prog06, Part 4
  // In the Polygon draw method, draw the polygon blue (Color.blue) if
  // it is rightside-in and red if it is inside-out.
  public void draw (Graphics2D g) {
    
    if (this.getInOut() == 2) {
      for (Edge e : edges) {
        e.draw(g, Color.blue);
      }
    }
    
    if (this.getInOut() == 1) {
      for (Edge e : edges) {
        e.draw(g, Color.red);
      }
    }
    
    if (that != null) {
      
      if (that.getInOut() == 2) {
        for (Edge e : that.edges) {
          e.draw(g, Color.blue);
        }
      }
      
      if (that.getInOut() == 1) {
        for (Edge e : that.edges) {
          e.draw(g, Color.red);
        }
      }
    }
    
    if (out != null) {
      for (Vert v : out.verts) {
        v.draw(g);
      }
    }
    
    // Prog07, 1.3: In Polygon.draw, draw the chords green.
    if (chords != null) {
      for (Edge e: chords) {
        e.draw(g, Color.green);
      }
    }
  }

  class CompareVerts implements Comparator<Vert> {
    public int compare (Vert a, Vert b) {
      return DiffY.sign(a.p, b.p);
    }
  }

  void check (SweepNode a, SweepNode b) {
    if (a == null || b == null) {
      return;
    }
    
    Edge e = (Edge) a.getData();
    Edge f = (Edge) b.getData();

    states.add(new PState(null, e, f));
    
    // Prog04, EXERCISE 2
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

  // Prog06, Part 7:
  // Create a public complement method which returns the complement. 
  // NOTE: Dr. Milenkovic said in class to make a copy and then invert.
  public Polygon complement() {
    Polygon complement = this.copy();
    complement.invert();

    return complement;
  }
  
  // Prog06, Part 8:
  // Create the intersection and difference methods
  // Intersection: By DeMorgan's Law, the
  // complement(A intersect B) = complement(A) union complement(B)
  // Then, A intersect B = complement(complement(A union B))
  public Polygon intersection(Polygon that) {
    this.invert();
    that.invert();
    
    out = this.union(that);
    out.invert();
    
    this.invert();
    that.invert();
    
    return out;
  }
  
  // Prog06, Part 8:
  // A - B = A.intersection(complement(B))
  // A - B = complement(complement(A) union B)
  public Polygon difference(Polygon that) {
    that.invert();
    out = this.intersection(that);
    that.invert();
    
    return out;
  }


  public Polygon union (Polygon that) {
    out = new Polygon();
    if(this.getInOut()==2 && that.getInOut()==2){
      out.inout=2;
    }
    else{
      out.inout=1;
    }

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
 

    for (Vert v : this.verts)
      events.offer(v);
    for (Vert v : that.verts)
      events.offer(v);

    while (events.size() > 0) {
      Vert v = events.poll();
      states.add(new PState(v.p.xyz().y, null, null));
      System.out.println("WHILING...");
      System.out.println("YOU BETTER FUCKING WORKING NOW!!!!!!");

      
      if (v.getPolygon() == out) {                    //  \ /
        SweepNode iNode = v.incoming.getNode();       //  / \
        SweepNode oNode = v.outgoing.getNode();
        // SweepNode iNode = sweep.add(v.incoming);
        // SweepNode oNode = sweep.add(v.outgoing);
        // System.out.println("CROSSING");
        // EXERCISE 3  X
        // v is intersection of a this edge with a that edge.
        states.add(new PState(v.p.xyz().y, null, null)); // repeat after swap
        // System.out.println(iNode);
        // if(iNode.getNext()!= oNode){
        //   //System.out.println(iNode.getNext());
        //   // System.out.println(oNode);
        //   oNode.swapWithNext();
        // }
        // else{
        //   iNode.swapWithNext();
        // }
        
        // check(iNode.getPrevious(),iNode);
        // check(oNode,oNode.getNext());
        iNode.swapWithNext();
        states.add(new PState(v.p.xyz().y, null, null)); // repeat after swap
        check(iNode.getPrevious(),iNode);
        check(oNode,oNode.getNext());
        copyEdge(v.incoming, v);
        copyEdge(v.outgoing, v);
        //After recording the edges to be added, make the change!(change minY to new position!)
        v.incoming.setMinY(v);
        v.outgoing.setMinY(v);
        v.incoming.inout = 3 - v.incoming.inout;
        v.outgoing.inout = 3 - v.outgoing.inout;

        states.add(new PState(v.p.xyz().y, null, null));

      }

          
      // EXERCISE 4  ^
      // This situation is the MOST DIFFICULT PART AMONG ALL other cases as you need to determine the 
      // iniital correct inout values!!! 
      else if (v.incoming.minY() == v.outgoing.minY()){
        // if we dont add them to sweep list, there will be NullPointer Exception
        SweepNode iNode = sweep.add(v.incoming);
        SweepNode oNode = sweep.add(v.outgoing);
        states.add(new PState(v.p.xyz().y, null, null)); // repeat after swap
        System.out.println("Meet for first time, lines ADDED");

        if(iNode.getNext()!=oNode){
          oNode=v.incoming.node;
          iNode=v.outgoing.node;

          // check(oNode.getPrevious(),oNode);
          // check(iNode,iNode.getNext());

        }



          check(iNode.getPrevious(),iNode);
          check(oNode,oNode.getNext());  
        // states.add(new PState(v.p.xyz().y, null, null));

        // this and that are all Polygons!
        // Note that previously we add this and that polygons' verts to events 
        //
        // for (Vert v : this.verts)
        //   events.offer(v);
        // for (Vert v : that.verts)
        //   events.offer(v);
        //
        // Now, we don't know which polygon vert(v) is from ! 


      if (oNode.getNext() == null) {
          // System.out.println("RIGHT IS NULL");
          if (v.getPolygon() == that) {
              // System.out.println("this.inout " + this.inout);
              v.incoming.inout = this.getInOut();
              v.outgoing.inout = this.getInOut();
            }
          else {
              // System.out.println("that.inout " + that.inout);
              v.incoming.inout = that.getInOut();
              v.outgoing.inout = that.getInOut();
            }
          }
        else {
            Edge nextEdge = (Edge) oNode.getNext().getData();
            
            // if oNode.next is an edge from the same polygon, that means the edges 
            // associated with the vertex(v) is from same polygon. This means
            // I see myself on the right, which means I should have the same inout value as 
            // the other one I see!  i.e: v's incoming and outgoing's inout should be the same as 
            // the nextEdge we see!
          if (nextEdge.getPolygon() == v.getPolygon()) {
              v.incoming.inout = nextEdge.inout;
              v.outgoing.inout = nextEdge.inout;
          }
            // if not, that means oNode.next is from another polygon! 
          else {
              // figure out if oNode.next is out then v is out
              // otherwise if oNode.next is inside, then v is inside
              // 1 is unbounded( drawn clockwise ), 2 is bounded ( drawn counterclockwise )
            // if nextEdge's minY is tail, that means the at the point we inspect, the nextEdge is 
            // going DOWN(Note we are in Graphical Coordinate!!), which means it's clockwise -> unbounded!
            if (nextEdge.minY() == nextEdge.tail) {
                v.incoming.inout = 1; // unbounded, clockwise
                v.outgoing.inout = 1; // unbounded, clockwise
              }
            // else the other situation
            else {
                v.incoming.inout = 2; // bounded, counterclockwise
                v.outgoing.inout = 2; // bounded, counterclockwise
            }
          }
        }
          // System.out.println("incoming inout " + v.incoming.inout);
          // System.out.println("outgoing inout " + v.outgoing.inout);


        states.add(new PState(v.p.xyz().y, null, null)); // repeat after swap

      }
      // EXERCISE 5  \/
      if (v.incoming.maxY() == v.outgoing.maxY() ){
        // SweepNode iNode = sweep.add(v.incoming);
        // SweepNode oNode = sweep.add(v.outgoing);
        SweepNode iNode = v.incoming.getNode();    
        SweepNode oNode = v.outgoing.getNode();
        states.add(new PState(v.p.xyz().y, null, null)); // repeat after swap

        if(iNode.getNext()!=oNode){

          check(oNode.getPrevious(), iNode.getNext());


        }
        else{

          check(iNode.getPrevious(), oNode.getNext());
     
        }

        // After checking, you need to change oNode pointer and make it 
        // point to iNode
        // oNode.setData(v.incoming);
        // //iNode.setData(v.outgoing);
        // Remove everything, so copyEdge should not copy anything!
        copyEdge(v.incoming, null);
        copyEdge(v.outgoing, null);

        iNode.remove();
        oNode.remove();


        states.add(new PState(v.p.xyz().y, null, null)); // repeat after swap
      }
      // EXERCISE 6  i on o
      if (v.incoming.maxY() == v.outgoing.minY() ){
        // SweepNode iNode = sweep.add(v.incoming);
        SweepNode iNode = v.incoming.getNode();
        copyEdge(v.incoming, null); 
        
        iNode.setData(v.outgoing);
        v.outgoing.inout = v.incoming.inout;
        states.add(new PState(v.p.xyz().y, null, null)); // repeat after swap

        check(iNode.getPrevious(), iNode);
        check(iNode, iNode.getNext());

          // check(iNode.getPrevious(), iNode);
          // check(oNode, oNode.getNext());
          // check(iNode.getPrevious(), oNode.getNext()); 

        // iNode.setData(v.outgoing);
        // oNode.setData(v.incoming);
        states.add(new PState(v.p.xyz().y, null, null)); // repeat after swap
      }
      // EXERCISE 7  o on i
      if (v.incoming.minY() == v.outgoing.maxY() ){
        SweepNode oNode = v.outgoing.getNode();
        copyEdge(v.outgoing, null);

        check(oNode.getPrevious(), oNode.getNext());
        // we remove oNode by setting incoming's data to oNode so that oNode is not 
        // pointing to the original oNode we are about to remove.
        oNode.setData(v.incoming);

        v.incoming.inout = v.outgoing.inout;


        check(oNode.getPrevious(), oNode);
        check(oNode, oNode.getNext());
        states.add(new PState(v.p.xyz().y, null, null)); // repeat after swap

        // remove the oNode by setting the data of oNode to iNode's data 

          // check(iNode.getPrevious(), iNode);
          // check(oNode, oNode.getNext());
          // check(iNode.getPrevious(), oNode.getNext()); 
        // iNode.remove();
        // oNode.setData(v.incoming);
        states.add(new PState(v.p.xyz().y, null, null)); // repeat after swap
      }
    }



    for (Edge e : this.edges){
      e.checked.clear();
      e.inout = 0;
    }
    
    for (Edge e : that.edges){
      e.checked.clear();
      e.inout = 0;
    }

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

    // // PLEASE RETURN THIS!!! NOT OUT!!! If you return OUT, you ONLY GET THE OUTPUT POINTS!!!
    // return this;
    System.out.println("THIS Polygon's INOUT: " + this.inout);
    System.out.println("THAT Polygon's INOUT: " + that.inout);
    System.out.println("OUT Polygon's INOUT: " + out.inout);
    // For the latest Assignment, we indeed need to return out as we only need the final output of the shape!
    return out;
    // return this;
}
// insert Prog11 here
public void draw (Graphics2D g, Color color) {
  for (Edge e : edges)
    e.draw(g, color);
}

  public void draw (Graphics2D g, Color color, PV2 trans, double scalar) {
    for (Edge e : edges) {
      PV2 t = trans.plus(e.tail.p.xyz().times(scalar));
      PV2 h = trans.plus(e.head.p.xyz().times(scalar));
      Drawer.drawEdge(g, t, h, color, "");
    }
  }

  class VertPair {
    final Vert a, b;

    VertPair (Vert a, Vert b) { this.a = a; this.b = b; }

    public int hashCode () {
      return a.hashCode() + b.hashCode();
    }

    public boolean equals (Object other) {
      VertPair that = (VertPair) other;
      return a == that.a && b == that.b;
    }
  }

  Map<VertPair, Vert> vertMap;
  List<Vert> sumVerts;

  Vert getSum (Vert a, Vert b) {
    if (a.getPolygon() != this)
      return getSum(b, a);

    VertPair pair = new VertPair(a, b);
    if (vertMap.containsKey(pair))
      return vertMap.get(pair);

    Vert v = out.new Vert(new AplusB(a.p, b.p), null, null);
    vertMap.put(pair, v);
    return v;
  }

  List<Edge> sumEdges;

  void getSumEdges (Polygon a, Polygon b) {
    for (Vert v : a.verts)
      for (Edge e : b.edges) {
        GO<PV2> p = v.incoming.tail.p;
        GO<PV2> q = v.p;
        GO<PV2> r = v.outgoing.head.p;

        GO<PV2> s = e.tail.p;
        GO<PV2> t = e.head.p;


        // Continue if three conditions are not satisfied.
        // EXERCISE
        if (false) // change to false
          continue;

        Edge sumEdge = null;
        // Create the sum Edge (out.new Edge....).
        // Use getSum to add Verts together.
        // EXERCISE
        // Edge constructor only accepts verts
        sumEdge=out.new Edge(getSum(v,e.tail),getSum(v,e.head));
        sumEdges.add(sumEdge);
      }
  }

  List<List<Vert>> intVerts;
  List<Edge> subEdges;

  public Polygon sum (Polygon that) {
    out = new Polygon ();
    vertMap = new HashMap<VertPair, Vert>();
    sumVerts = new ArrayList<Vert>();
    sumEdges = new ArrayList<Edge>();
    intVerts = new ArrayList<List<Vert>>();
    subEdges = new ArrayList<Edge>();

    getSumEdges(this, that);
    getSumEdges(that, this);

    if (false) {
      for (Edge e : sumEdges)
        out.edges.add(e);
      return out;
    }

    // intVerts.get(i) contains the Verts at which sum Edge i intersects the other sum Edges
    for (int i = 0; i < sumEdges.size(); i++)
      intVerts.add(new ArrayList<Vert>());

    for (int i = 0; i < sumEdges.size(); i++) {
      Edge ei = sumEdges.get(i);
      for (int j = i+1; j < sumEdges.size(); j++) {
        Edge ej = sumEdges.get(j);

        // EXERCISE
        if(ei.intersects(ej)){

          GO<PV2> intersectionPoint=new ABintersectCD(ei.head.p,ei.tail.p,ej.head.p,ej.tail.p).getIntersection();
          // 1 GO<PV2> two Edges (GO<PV2>, Edge incoming,Edge outgoing)
          Vert newVert=out.new Vert(intersectionPoint,ei,ej);
          intVerts.get(i).add(newVert);
          intVerts.get(j).add(newVert);

        }
        // If ei and ej intersect, find their intersection point
        // (ABintersectCD) and create a out.new Vert.  Set its
        // incoming and outgoing to ei and ej (temporary).
        // Add the Vert to both intVerts.get(i) and intVerts.get(j).
        // test intersection
      }
    }

    // Sort Verts on each edge:
    Comparator<Vert> xLess = new XLess();
    Comparator<Vert> xMore = new XMore();
    for (int i = 0; i < sumEdges.size(); i++) {
      Edge e = sumEdges.get(i);

      if (DiffX.sign(e.tail.p, e.head.p) < 0)
        intVerts.get(i).sort(xLess);
      else
        intVerts.get(i).sort(xMore);
    }

    for (int i = 0; i < sumEdges.size(); i++) {
      Edge e = sumEdges.get(i);
      List<Vert> vs = intVerts.get(i);

      // EXERCISE
      // Call checkSubEdge on every possible sub edge.



    }

    // Gives intersection vertices the correct incoming and outgoing.
    for (List<Vert> l : intVerts)
      for (Vert v : l)
        v.incoming = v.outgoing = null;

    for (Edge e : subEdges)
      e.informVerts();

    // EXERCISE
    // Trim off dangling edges.
    // Set tail.outgoing and head.incoming for a dangling edge to null.
    // Set its tail and head to null.






    for (Edge e : subEdges) {
      if (e.tail == null)
        continue;
      out.edges.add(e);
      out.verts.add(e.tail);
    }

    return out;
  }

  class XLess implements Comparator<Vert> {
    public int compare (Vert a, Vert b) {
      if (a == b)
        return 0;
      return DiffX.sign(a.p, b.p);
    }
  }

  class XMore implements Comparator<Vert> {
    public int compare (Vert a, Vert b) {
      if (a == b)
        return 0;
      return -DiffX.sign(a.p, b.p);
    }
  }

  boolean blocked (Edge ab, Vert v, int sign) {
    if (!(v.p instanceof ABintersectCD))
      return false;

    if (ab != v.incoming && ab != v.outgoing) {
      System.out.println("blocked uh oh ");
    }

    Edge cd = v.incoming == ab ? v.outgoing : v.incoming;

    // EXERCISE
    // If sign==1, return true if ab is inside the Minkowski sum (in
    // blocked space) to the tail side of its intersection v with cd.
    // If sign==-1, head side.



    return false;
  }

  void checkSubEdge (Edge e, Vert a, Vert b) {
    if (blocked(e, a, -1))
      return;
    if (blocked(e, b, 1))
      return;

    Edge s = out.new Edge(a, b);
    subEdges.add(s);
  }

  Polygon rot180 () {
    Polygon out = new Polygon();

    for (Vert v : verts) {
      Vert w = out.new Vert(new MinusA(v.p), null, null);
      v.incoming.head = v.outgoing.tail = w;
      out.verts.add(w);
    }

    for (Edge e : edges) {
      Edge f = out.new Edge(e.tail, e.head);
      f.informVerts();
      out.edges.add(f);
    }

    for (Vert v : verts)
      v.informEdges();

    return out;
  }

}