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
      if(that.minY()==this.minY()){
        return AreaABC.sign(that.maxY().p, that.minY().p, this.maxY().p);
        //if(AreaABC.sign(this.minY().p,this.maxY().p,that.maxY().p)<0){
        //this is on the left of that
        //  return -1;
        //}
        //else{
        //this is on the right of that
        //  return 1;
        //}
      }
      return AreaABC.sign(that.maxY().p, that.minY().p, this.minY().p);
      //if(AreaABC.sign(this.minY().p,this.maxY().p,that.minY().p)<0 ){
      //    return -1;
      //}
      //else{
      //  return 1;
      //}
      // return 1;
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

    // if(e.checked.contains(f)){
    //   System.out.println("CHECKED");
    //   return;
    // }

    e.checked.add(f);
    f.checked.add(e);
    if (e.getPolygon() == f.getPolygon()) {
      System.out.println("SAME");
      return;
    }
    if (!(e.intersects(f))){
      System.out.println("NOTINTERSECTING");
      return;
    }

    System.out.println("PROCEEDING");
    GO<PV2> p = new ABintersectCD(e.tail.p, e.head.p, f.tail.p, f.head.p);
    System.out.println("intersection calculated");
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
      System.out.println("WHILING...");

      
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

      }

          
      // EXERCISE 4  ^
      if (v.incoming.minY() == v.outgoing.minY()){
        // if we dont add them to sweep list, there will be NullPointer Exception
        SweepNode iNode = sweep.add(v.incoming);
        SweepNode oNode = sweep.add(v.outgoing);
        states.add(new PState(v.p.xyz().y, null, null)); // repeat after swap
        System.out.println("Meet for first time, lines ADDED");

        if(iNode.getNext()!=oNode){

          check(oNode.getPrevious(),oNode);
          check(iNode,iNode.getNext());

        }

        else{

          check(iNode.getPrevious(),iNode);
          check(oNode,oNode.getNext());  

        }
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
        iNode.remove();
        oNode.remove();


        states.add(new PState(v.p.xyz().y, null, null)); // repeat after swap
      }
      // EXERCISE 6  i on o
      if (v.incoming.maxY() == v.outgoing.minY() ){
        // SweepNode iNode = sweep.add(v.incoming);
        SweepNode iNode = v.incoming.getNode(); 
        iNode.setData(v.outgoing);
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
        check(oNode.getPrevious(), oNode.getNext());
        // we remove oNode by setting incoming's data to oNode so that oNode is not 
        // pointing to the original oNode we are about to remove.
        oNode.setData(v.incoming);
        states.add(new PState(v.p.xyz().y, null, null)); // repeat after swap

        check(oNode.getPrevious(), oNode);
        check(oNode, oNode.getNext());

        // remove the oNode by setting the data of oNode to iNode's data 

          // check(iNode.getPrevious(), iNode);
          // check(oNode, oNode.getNext());
          // check(iNode.getPrevious(), oNode.getNext()); 
        // iNode.remove();
        // oNode.setData(v.incoming);

        states.add(new PState(v.p.xyz().y, null, null)); // repeat after swap
      }
    }


    for (Edge e : this.edges)
      e.checked.clear();
    for (Edge e : that.edges)
      e.checked.clear();

    return this;
  }
}
