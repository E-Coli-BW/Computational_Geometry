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






    
    return circle;
  }

  /**
   * The smallest circle through p containing points[0..n-1]
   */
  public Circle smallestCircle (GO<PV2> p, List<GO<PV2>> points, int n) {
    // EXERCISE
    ///








    return null; // incorrect
  }

  /**
   * The smallest circle through p and q containing points[0..n-1]
   */
  public Circle smallestCircle (GO<PV2> p, GO<PV2> q, List<GO<PV2>> points, int n) {
    // EXERCISE











    return null; // incorrect
  }
}

  
