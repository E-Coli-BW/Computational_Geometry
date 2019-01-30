package hull;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Arrays;
import java.awt.Graphics2D;
import java.awt.Color;

import acp.*;
import pv.*;

public class Medium2 implements Huller {
  private static class Edge {
    GO<PV2> tail, head;
    Edge (GO<PV2> tail, GO<PV2> head) {
      this.tail = tail;
      this.head = head;
    }
  }

  private static class MState implements hull.State {
    List<GO<PV2>> out = new ArrayList<GO<PV2>>();
    GO<PV2> p, q, r;
    String pq, pr;

    MState (List<GO<PV2>> out, GO<PV2> p, GO<PV2> q, GO<PV2> r,
            String pq, String pr) {
      this.out.addAll(out);
      this.p = p;
      this.q = q;
      this.r = r;
      this.pq = pq;
      this.pr = pr;
    }
    public void draw (Graphics2D g) {
      for (int i = 1; i < out.size(); i++)
        Drawer.drawEdge(g, out.get(i-1).xyz(), out.get(i).xyz(), Color.green, "");
      Drawer.drawPoint(g, p.xyz(), Color.red, "p"); 
      Drawer.drawPoint(g, q.xyz(), Color.red, "q"); 

      Drawer.drawEdge(g, p.xyz(), q.xyz(), Color.black, pq);

      if (r != null) {
        Drawer.drawPoint(g, r.xyz(), Color.red, "r"); 
        Drawer.drawEdge(g, p.xyz(), r.xyz(), Color.red, pr);
      }
    }
  }
  
  List<State> states = new ArrayList<State>();
  public int numStates () { return states.size(); }
  public State getState (int i) { return states.get(i); }      

  /**
   * Calculate the convex hull of a set of points.
   * @param 	in	input points
   * @return list of point on hull in clockwise order (will appear counterclockwise on screen)
   */
  //Accepting a List of input of type GO<PV2> (Geometric Object of type Point Vector of 2 Dimension)
  



  //For the Medium2 method, you need two steps:
  //1. Figure out if p is inside or on convex hull;
  //2. If p is on convex hull, you can use the same Medium part2 to find the convex hull;
  //   If o is inside, pick a new p and try again until you get a point on the convex hull;


  public List<GO<PV2>> hull (List<GO<PV2>> in) {
    states.clear();

    List<GO<PV2>> out = new ArrayList<GO<PV2>>();

    if (in.size() < 2) {
      out.addAll(in);
      return out;
    }

    // GO<PV2> last_p=null;
    // GO<PV2> last_q=null;
    // GO<PV2> last_r=null;

    GO<PV2> p = in.get(0);
    //GO<PV2> q = in.get(1);
    
    //TRY EVERY q and r paris to test is there any pq pair (Note that now p is fixed)
    // that can pass all the test(i.e: AreaABC sign all the same)
    for(GO<PV2> q:in){
      if(q==p){
        continue;
      }
      for(GO<PV2> r:in){

        if(r==p || r==q){
          continue;
      }
      else if(AreaABC.sign(p,q,r)>0){
        q=r;
        states.add(new MState(out, p, q, r, "", "Make r the new q"));
      }
      //After all these checks on q, we never find a good q, this means p is not good
      //so after all these loop, let's check the last pqr pair we examined
      //if that one still fails, it means
      //OUR P IS ACTUALLY WRONG! TRY ANOTHER P (thus assigned q to p and start over)
      if(AreaABC.sign(p,q,in.get(in.size()-1))>0){
        p=q;
      }




      //last_p=p;
      //last_q=q;
      //last_r=r;
    }
        
        // if(AreaABC.sign(last_p,last_q,last_r)>0){
        // p=last_q;
        // states.add(new MState(out, p, last_q, last_r, "", "Make q the new p"));

      //r=null;
    }

  

    GO<PV2> first = p;

    int count = 0;
    do {
      out.add(p);

      GO<PV2> q = null;
      for (GO<PV2> r : in) {
        if (r == p || r == q)
          continue;
        if (q == null) {
          q = r;
          states.add(new MState(out, p, q, null, "FIRST", ""));
        }
        else {
          if (AreaABC.sign(p, q, r) < 0) {
            states.add(new MState(out, p, q, r, "", "BETTER"));
            q = r;
          }
          else
            states.add(new MState(out, p, q, r, "", "WORSE"));
        }
      }
      p = q;
    } while (p != first && count++ < 100);
    
    //Let's see what's the order of ourput nodes
    System.out.println("Output nodes list:"+out);
    System.out.println("Input nodes list:"+in);

    return out;
  }
}
