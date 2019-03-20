package circle;

import java.util.List;
import java.util.ArrayList;
import java.awt.Graphics2D;
import java.awt.Color;
import acp.*;
import pv.*;
import hull.State;

public class SmallestCircle {
  class CState implements State {
    Circle c;

    CState (Circle c) {
      this.c = c;
    }

    public void draw (Graphics2D g) {
      c.draw(g, Color.blue);
    }
  }

  List<State> states = new ArrayList<State>();
  public int numStates () { return states.size(); }
  public State getState (int i) { return states.get(i); }

  /**
   * The smallest circle containing points[0..n-1].
   */
  public Circle smallestCircle (List<GO<PV2>> points) {
    states.clear();

    Circle circle = new Circle(points.get(0), points.get(1));
    states.add(new CState(circle));
    int n = points.size();

    // EXERCISE
// // The smallest circle containing p1..pn
//   public Circle smallestCircle (p1..pn){
//     C = Circle(p1, p2) // the circle with p1p2 as diameter
//     for k = 3 to n
//       if pk is not inside C
//         C = smallestCircle1(p1..pk-1, pk)
//     return C
//   
    // states.add(new CState(C));
    for(int k=2;k<n;k++){
      if( !circle.contains( points.get(k) ) ){
        List<GO<PV2>> newPoints=new ArrayList<GO<PV2>>(points.subList(0, k));
        circle = smallestCircle1(newPoints,points.get(k));
      }
    }
    return circle;
  }


  /**
   * The smallest circle through p containing points[0..n-1]
   */
  public Circle smallestCircle1 (List<GO<PV2>> points,GO<PV2> a) {
    // EXERCISE
    ///
    states.clear();
    Circle circle = new Circle(points.get(0),a);
    states.add(new CState(circle));
    int n=points.size();
    for(int k=1;k<n;k++){
      if( !circle.contains( points.get(k) ) ){
        List<GO<PV2>> newPoints=new ArrayList<GO<PV2>>(points.subList(0, k));
        circle = smallestCircle2(newPoints,a,points.get(k));
      }
    }

    return circle; // incorrect
  }

  /**
   * The smallest circle through p and q containing points[0..n-1]
   */
  public Circle smallestCircle2 (List<GO<PV2>> points, GO<PV2> a,GO<PV2> b ) {
    // EXERCISE
    states.clear();
    Circle circle = new Circle(a,b);
    states.add(new CState(circle));
    int n=points.size();
    for(int k=0;k<n;k++){
      if( !circle.contains( points.get(k) ) ){
        circle = new Circle(a,b,points.get(k));
      }
    }
    return circle;

  }
}

  
