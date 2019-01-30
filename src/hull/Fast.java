package hull;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Arrays;
import java.util.Collections;
import java.awt.Graphics2D;
import java.awt.Color;

import acp.*;
import pv.*;

//Implement Fast hull algorithm
public class Fast implements Huller {
  private static class Edge {
    GO<PV2> tail, head;
    Edge (GO<PV2> tail, GO<PV2> head) {
      this.tail = tail;
      this.head = head;
    }
  }

  private static class FState implements hull.State {
    List<GO<PV2>> out = new ArrayList<GO<PV2>>();
    GO<PV2> p, q, r;
    String pq, pr;

    FState (List<GO<PV2>> out, GO<PV2> p, GO<PV2> q, GO<PV2> r,
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

  public class SortByX implements Comparator <GO<PV2>>{
    public int compare(GO<PV2> o1, GO<PV2> o2){
      //use approx because x(type Real) and y values are actually intervals
      if(o1.xyz().get(0).approx()-o2.xyz().get(0).approx()<0){
      //if(DiffX.sign(o1,o2)<0){
        return -1;
      }
      else{
        return 1;
      }
    }
  }
  /**
   * Calculate the convex hull of a set of points.
   * @param 	in	input points
   * @return list of point on hull in clockwise order (will appear counterclockwise on screen)
   */
  public List<GO<PV2>> hull (List<GO<PV2>> in) {
    states.clear();

    List<GO<PV2>> out = new ArrayList<GO<PV2>>();

    if (in.size() < 2) {
      out.addAll(in);
      return out;
    }

    System.out.println("This is my input n points list:"+in);
    //make a copy of input points
    List<GO<PV2>> sorted_in=in;

    //Sort the x-cordinate
    //Collections.sort(List)

/*
    //sorted_out.sort((o1,o2) -> (Integer.valueOf(o1.xyz().get(0).toString())<Integer.valueOf(o2.xyz().get(0).toString())?o1:o2));
    //correct way of doing customized object sorting
    sorted_in.sort((o1,o2) -> (o1.xyz().get(0).toString().compareTo(o2.xyz().get(0).toString())));
    System.out.println("This is my sorted out input n points list:"+sorted_in);
    //System.out.println(in);

    //Now we have sorted the input GO<PV2> array, from lowest x to highest x
    //
*/
    //Using comparator to sort by X
    //Syntax Collections.sort(arrays_to_be_sorted,new Your_Self_defined_Comparator)
    Collections.sort(sorted_in,new SortByX());
    System.out.println("This is my sorted input n points list:"+sorted_in);

    //read in the first point
    GO<PV2> p=sorted_in.get(0);
    GO<PV2> q=sorted_in.get(1);
    out.add(p);
    out.add(q);
    states.add(new FState(out, p, q, null, "FIRST", ""));

    //GO<PV2> r=sorted_in.get(2);
    for(int i=2;i<sorted_in.size();i++){
      GO<PV2> r=sorted_in.get(i);
      //If Area is negative, turning clockwise , not good! Don't add it to stack
      //otherwise, add it to stack
      if(r==p || r==q){
        continue;
      }

      if(AreaABC.sign(p,q,r) > 0){
        p=q;
        q=r;
        out.add(r);
        states.add(new FState(out, p, q, r, "GOOD", ""));
      }
      else{
        do{
          //remove q
          out.remove(out.size()-1);
          states.add(new FState(out, p, q, r, "BAD", ""));
          if(out.size()>=2){
          q=out.get(out.size()-1);
          p=out.get(out.size()-2); 
          }
          

      } while(AreaABC.sign(p,q,r)<0 && out.size() >= 2);
      //this is because we want p and q always be the last two in the OUTPUT list!!!
      out.add(r);
      states.add(new FState(out, p, q, null, "ADDED", ""));
      q=out.get(out.size()-1);
      p=out.get(out.size()-2); 
      
        //p=p;
      }

    }

    // GO<PV2> p=sorted_in.get(in.size()-1);
    // GO<PV2> q=sorted_in.get(in.size()-2);
    p=sorted_in.get(in.size()-1);
    q=sorted_in.get(in.size()-2);
    out.add(p);
    out.add(q);
    states.add(new FState(out, p, q, null, "FIRST", ""));

    //GO<PV2> r=sorted_in.get(2);
    for(int i=in.size()-3;i>=0;i--){
      GO<PV2> r=sorted_in.get(i);
      //If Area is negative, turning clockwise , not good! Don't add it to stack
      //otherwise, add it to stack
      if(r==p || r==q){
        continue;
      }

      if(AreaABC.sign(p,q,r) > 0){
        p=q;
        q=r;
        out.add(r);
        states.add(new FState(out, p, q, r, "GOOD", ""));
      }
      else{
        do{
          //remove q
          out.remove(out.size()-1);
          states.add(new FState(out, p, q, r, "BAD", ""));
          if(out.size()>=2){
          q=out.get(out.size()-1);
          p=out.get(out.size()-2); 
          }
          

      } while(AreaABC.sign(p,q,r)<0 && out.size() >= 2);
      //this is because we want p and q always be the last two in the OUTPUT list!!!
      out.add(r);
      states.add(new FState(out, p, q, null, "ADDED", ""));
      q=out.get(out.size()-1);
      p=out.get(out.size()-2); 
      
        //p=p;
      }

    }


    // GO<PV2> first = p;

    // int count = 0;
    // do {
    //   out.add(p);

    //   GO<PV2> q = null;
    //   for (GO<PV2> r : in) {
    //     if (r == p || r == q)
    //       continue;
    //     if (q == null) {
    //       q = r;
    //       states.add(new FState(out, p, q, null, "FIRST", ""));
    //     }
    //     else {
    //       if (AreaABC.sign(p, q, r) < 0) {
    //         states.add(new FState(out, p, q, r, "", "BETTER"));
    //         q = r;
    //       }
    //       else
    //         states.add(new FState(out, p, q, r, "", "WORSE"));
    //     }
    //   }
    //   p = q;
    // } while (p != first && count++ < 100);

    return out;
  }
}
