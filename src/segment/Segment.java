package segment;
import java.util.Set;
import java.util.HashSet;
import acp.*;
import pv.*;
//Note!!! Comparable is NOT Comparator!
//In Fast.java we are implementing Comparator<T> interface which requires implementing a method int compare(<T> o1, <T> o2);
//In this file Segment.java we are implementing Comparable<T> interface ! 
//
public class Segment implements Comparable<Segment> {
  public final GO<PV2> tail, head;
  SweepNode node;
  Set<Segment> checked = new HashSet<Segment>();

  public Segment (GO<PV2> a, GO<PV2> b) {
    if (DiffY.sign(a, b) < 0) {
      tail = a;
      head = b;
    }
    else {
      tail = b;
      head = a;
    }
  }

  boolean intersects (Segment that) {
    // EXERCISE 1
    // Return true if this intersects that.

    //1. pick one line first e.g ab and cd choose ab first
    //2. check if AreaABC.sign(a,b,c)*AreaABC.sign(a,b,d)<0
    //3. pick the other line cd
    //4. Do the same check:
    // if AreaABC.sign(c,d,a)*AreaABC.sign(c,d,b)<0
    //5. If in both 3 and 4 we got true then ab and cd intersects
    return AreaABC.sign(this.head,this.tail,that.tail)*AreaABC.sign(this.head,this.tail,that.head)<0 && 
    AreaABC.sign(that.head,that.tail,this.head)*AreaABC.sign(that.head,that.tail,this.tail)<0 ;    
    
    //return false;
  }

  GO<PV2> intersection (Segment that) {
    return new ABintersectCD(tail, head, that.tail, that.head);
  }

  public int compareTo (Segment that) {
    // EXERCISE 3
    // Compare the x position of this and that at the smallest value
    // of y they have in common.
    //if(this)
    // (The graphics window has y increasing downwards and its
    // clockwise and counterclockwise are switched.)
    //Should be sth like this:
    //if o1<o2 then o1.compareTo(o2) returns value of your choice
    //if o1>o2 then o1.compareTo(o2) returns another value your choice

    //So things need to be done are:
    //1. Way to find smallest y in common;
    //2. Compare x position of a point on this and that (type Segment) which has the smallest common y
    //Note: Segment object has tail and head, each of them is of type GO<PV2>
    //Which means we can do o.tail(type GO<PV2>) then o.tail.x gives the x coordinate(o.tail.x is of type Real) of o.tail
    //Now we can use approx() (which returns a double) in Real class to compare 
    //So it should look like this: for this and that, x1-x2 can be implemented as this.tail.x.approx()-that.tail.x.approx()

    //The lines are always ordered according to y-axis values
    // |<--tail     0
    // |            |
    //\ /<--head   \ /
    //              max-y

    if(AreaABC.sign(that.tail,that.head,this.tail)<0){
      //this is on the left of that
      return -1;
    }
    else{
      //this is on the right of that
	  return 1;
    }
}
}

