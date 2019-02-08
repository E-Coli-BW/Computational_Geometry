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
      // EXERCISE 4
      // Compare the events based on y values
      // Events are ordered by the y coordinate of p.
      // if this(point) is lower than that(point)
      // return a negative number
      // else return a positive number
      return DiffY.sign(this.p,that.p); //
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
    //getSegment is used in SlowList and SweepNode
    //Check a.getSegment() and b.getSegment() for intersection.
    /*
    @para ABintersectCD (GO<PV2> a, GO<PV2> b, GO<PV2>c, GO<PV2> d)
    */


	if (a == null || b == null ) {
		return;
	}

    Segment segA = a.getSegment();
    Segment segB = b.getSegment();

	if (!segA.checked.contains(segB)) {
	// Add new FState(null, a.getSegment(), b.getSegment()) to states
		states.add( new FState(null, segA, segB) );

		if( segA.intersects(segB) ) {
			//now I see that segA intersects with segB so 
			//in segA's checked hashset, it should have added segB
			//and so should segB
			segA.checked.add(segB);
			segB.checked.add(segA);

			GO<PV2> intersection = new ABintersectCD(segA.tail, segA.head, segB.tail, segB.head);
			out.add(intersection);
			events.add( new Event(segA,segB,intersection) );
			states.add( new FState(null, segA, segB) );	
		}

	}
    // GO<PV2> tail_of_segmentA=segmentA.tail;
    // GO<PV2> head_of_segmentA=segmentA.head;
    // GO<PV2> tail_of_segmentB=segmentB.tail;
    // GO<PV2> head_of_segmentB=segmentB.head;

    // intersector=new ABintersectCD(tail_of_segmentA,head_of_segmentA,tail_of_segmentB,head_of_segmentB);
    //MY OWN CODE ENDS

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
	  //Event contains two segment and a point
      Event event = events.poll();

      // Handle three types of event.
      // Add new FState(event.p.xyz().y, null, null) before and after
      // modifying sweep list.
      // Call check with all newly adjacent pairs of nodes.
	  states.add( new FState(event.p.xyz().y, null, null) );
	  // Each event contains 2 segments and a point 
	  // the point can either be a tail, a head or an intersection

	  // if it is a tail:
	  // Add a tail events to the eventlist 
	  // Add one segment to the sweepList
	  // Tail(segment,null,s.tail) //@para type: segment,segment, point
	  //also, when we meet a tail event, add that segment to eventlist priority queue
      if (event.b == null) {
        // EXERCISE 6
        // Tail event.
		// Encountered a tail! This means we are seeing this segment for the first time
		// Add this segment to the SweepNode object and then add the SweepNode to the SweepList! 
		// (So that we know we already encountered it)
		// Add a to sweep
		sweep.add(event.a);
		// Event object contains two segments 
		
		SweepNode nodeA = event.a.node;
		// once we plug in this newly found node which we see its tail
		// check this newly inserted tail node against its neibors to see
		//if we have some new intersections or not

		// 1. check this newly inserted node against its previous node(left neighbor)
		check(nodeA.getPrevious(), nodeA);
		// 2. check this newly inserted node against its next node(right neighbor), maintain the head-tail order by 
		// maintaining the (left,right) parameter protocol
		check(nodeA, nodeA.getNext());

		
      }
	  //if we meet a haed, remove the segment from the priority queue because now we are done with it
      else if (event.a == null) {
        // EXERCISE 7
        // Head event, remove the segment we have seen completely from the eventlist priority queue

		// If we see a head, that means we are reaching the end of this segment
		// check its neighbors to see if we can find some new intersections BEFORE we remove it from
		// the SweepList!!
		SweepNode nodeB = event.b.node;
		check( nodeB.getPrevious(), nodeB.getNext() );
		// Now remove it from the list! because we are done with it!
		nodeB.remove();

      }
      else {
        // EXERCISE 8
        // Intersection event.

        // Note: node.swapWithNext() swaps the segments of node and
        // its successor node in the sweep list, but it does not
        // change the positions of the nodes in the sweep list.

		// If we see an intersection, we want to swap the nodes becasue
		// if a node is to the left of another node before we meet the intersection
		// then after the intersection as we proceed on, that node will now be on the 
		// right of the other node. i.e: Their relative positions swapped!

		// node has a method called swwapWithNext() to do the swapping operation!
		event.a.node.swapWithNext();

		SweepNode nodeB = event.a.node;
		SweepNode nodeA = event.b.node;

    // after we swap it, we need to check with their new neighbors 
    // original: c a b d
    // swapped: c b a d 
    // we need to check cb ( i.e: b.gerPrevious() which in our case sould be a.getPrevious() 
    // should be checked against b itself, so that's check(nodeA.getPrevious(), nodeA))
    // same thing for ad !!
		check(nodeA.getPrevious(), nodeA);
		check(nodeB, nodeB.getNext());
      }
    
	states.add( new FState(event.p.xyz().y, null, null) );
	}
    return out;
  }
}
