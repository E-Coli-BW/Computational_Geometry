package delaunay;

import java.util.List;
import java.util.ArrayList;
import java.awt.Graphics2D;
import java.awt.Color;
import acp.*;
import pv.*;
import hull.State;
import circle.Circle;
import segment.Drawer;

public class Delaunay {
  /** A snapshot of the current triangulation for animation purposes.
    * If a triangle is being checked, set check to that triangle.
    */
  public class DState implements State {
    public DState (Circle c) {
      for (Triangle t : Delaunay.this.triangles)
        if (t.c == null)
          triangles.add(t);
      this.c = c;
    }

    public List<Triangle> triangles = new ArrayList<Triangle>();
    public Circle c;

    public void draw (Graphics2D g) {
      for (Triangle t : triangles)
        t.draw(g, Color.green);
      if (c != null)
        c.draw(g, Color.red);
    }
  }

  public List<State> states = new ArrayList<State>();
  public int numStates () { return states.size(); }
  public State getState (int i) { return states.get(i); }

  public class Triangle {
    /** vertices */
    public GO<PV2>[] v = (GO<PV2>[]) new GO[3];

    /** neighbors:  t[i] is across from v[i] */
    public Triangle[] t = new Triangle[3];

    /** children:
     * If three children, then c[i] has a new vertex to replace v[i].
     */
    public Triangle[] c;

    /** Create a triangle with vertices a,b,c. Add this to triangles. */
    public Triangle (GO<PV2> a, GO<PV2> b, GO<PV2> c) {
      v[0] = a;
      v[1] = b;
      v[2] = c;
      triangles.add(this);
    }

    /** Create a triangle which is a copy of that, with the same
      * vertices and neighbors.  Add this to triangles.
      */
    public Triangle (Triangle that) {
      for (int i = 0; i < 3; i++) {
        v[i] = that.v[i];
        t[i] = that.t[i];
      }
      triangles.add(this);
    }

    public void draw (Graphics2D g, Color color) {
      Drawer.drawEdge(g, v[0].xyz(), v[1].xyz(), color, "");
      Drawer.drawEdge(g, v[1].xyz(), v[2].xyz(), color, "");
      Drawer.drawEdge(g, v[2].xyz(), v[0].xyz(), color, "");
    }

    /** Test of this triangles contains point p. */
    public boolean contains (GO<PV2> p) {

      // Return false if p is on the wrong side of an edge.
      // How to test contains?
      // For a counter-clockwise triangle, a point is inside the triangle if 
      // it is to the left of every edge--> pab, pbc and pca then should all be negative 
      // if p is inside triangle ABC
      return AreaABC.sign(p,this.v[0],this.v[1])>0 && AreaABC.sign(p,this.v[1],this.v[2])>0 && AreaABC.sign(p,this.v[2],this.v[0])>0;
    }
    
    /** Following child pointers, locate the descendent which contains p. */
    public Triangle locate (GO<PV2> p) {
      // if child is null, meaning this triangle has no chid
      // we reached the end of the child-finding process
      if (c == null){
        return this;
      }

      Triangle triangle=this;
      // Recurse on the child which contains p.
      for (Triangle child: c) {
        System.out.println("running the child checking loop...");
        if(child.contains(p)){
          System.out.println("a child contains p...");
          triangle=child.locate(p);
        }
      }

      // assert(false);
      // return null;
      return triangle;
    }

    /** Split this triangle into three at p.
      * Child c[i] will have p at c[i].v[i].
      */
    public void split (GO<PV2> p) {
      // Remove when you implement:
      // if (true)
      //   return;

      // Each child starts out as copy of this triangle.
      c = new Triangle[3];
      for (int i = 0; i < 3; i++) {
        c[i] = new Triangle(this);
      }


      for(int i=0;i<3;i++){
        // set one v
        c[i].v[i]=p;
        // let the neighbor of the parent triangle know that 
        // it has a new neighbor c[i] of the parent triangle
        // error: null pointer exception occurred! 
        // t[i].t[t[i].find(c[i])]=c[i];
        // System.out.println(c[i].t[i]);
        // assign neighbors
        c[i].t[i]=t[i];
        c[i].updateNeighbor(i, this);
        // updateNeighbor (int i, Triangle oldT)

        // t[i].t[i]=c[i];
      }
      // For each child, set one v and update a neighbor.
      // For each child, make two other children neighbors.
      for (int i=0;i<3;i++){
        c[i].t[(i+1)%3]=c[(i+1)%3];
        c[i].t[(i+2)%3]=c[(i+2)%3];
      }


      // Debug
      for (int i = 0; i < 3; i++)
        c[i].checkNeighbors();
    }

    /** Find the index of neighbor that in t[].
      * Return -1 if that is not a neighbor.
      */
    public int find (Triangle that) {
      for (int i = 0; i < 3; i++)
        if (t[i] == that)
          return i;
      assert(false);
      return -1;
    }

    /** Check find method on neighbors.  Just for debugging. */
    public void checkNeighbors () {
      // System.out.println("checkNeighbors " + this);
      if (t[0] != null)
        t[0].find(this);
      if (t[1] != null)
        t[1].find(this);
      if (t[2] != null)
        t[2].find(this);
    }

    /** Inform neighbor t[i] that it now has this triangle as a
      * neighbor in place of oldT.  Call it when oldT is split.
      */
    public void updateNeighbor (int i, Triangle oldT) {
      if (t[i] == null)
        return;
      t[i].t[t[i].find(oldT)] = this;
    }      

    /** Flip this triangle with neighbor t[i].
     * Children will both have v[i] at index i.
     */

    public void flip (int i) {
      // Remove this when you implement flip.
      // if (true)
      //   return;

      c = new Triangle[2];
      t[i].c = c;
      // Children start out as copies of this triangle.
      c[0] = new Triangle(this);
      c[1] = new Triangle(this);
      int j=(i+1)%3;
      int k=(i+2)%3;
      int i2=t[i].find(this);
      int j2=(i2+1)%3;
      int k2=(i2+2)%3;

      c[0].v[k]=t[i].v[i2];
      c[0].t[i]=t[i].t[j2];
      c[0].updateNeighbor(i,t[i]);
      c[0].updateNeighbor(k,this);

      c[1].v[j]=t[i].v[i2];
      c[1].t[i]=t[i].t[k2];
      c[1].updateNeighbor(i,t[i]);
      c[1].updateNeighbor(j,this);

      c[0].t[j]=c[1];
      c[1].t[k]=c[0];

      // // what we want: update my children then notify my neighbors
      // // first child: i_(i-1)_iOpos
      // // second child: i_(i+1)_iOpos
      // int j=(i+1)%3;
      // int k=(i+2)%3;
      // int i2=t[i].find(this);
      // int j2=(i2+1)%3;
      // int k2=(i2+2)%3;
      // // then change j to i2
      // // change k to i2
      // c[0].v[j]=t[i].v[k2];
      // c[1].v[k]=t[i].v[i2];
      // // c[0].v[k]=t[i].v[i2];
      // // update neighbors
      // // for c[0], new neighbors are c[1] and c[k2]
      // c[0].t[i]=t[i].t[k2];
      // c[0].t[k]=c[1];
      // c[1].t[i]=t[i].t[j2];
      // c[1].t[j]=c[0];
      // c[0].updateNeighbor(i, this);
      // c[1].updateNeighbor(i,this);
      // for the other triangle
      // c2 = new Triangle[2];
      // this.c = c2;
      // // Children start out as copies of this triangle.
      // c2[0] = new Triangle(t[i]);
      // c2[1] = new Triangle(t[i]);
      // int new_i=i2;
      // int new_j=j2;
      // int new_k=k2;
      // int new_i2=i;
      // int new_j2=j;
      // int new_k2=k;
      // c2[0].v[new_j]=t[new_i].v[new_k2];
      // c2[1].v[new_k]=t[new_i].v[new_i2];
      // // c[0].v[k]=t[i].v[i2];
      // // update neighbors
      // // for c[0], new neighbors are c[1] and c[k2]
      // c[0].t[new_i]=t[new_i].t[new_k2];
      // c[0].t[new_k]=c[1];
      // c[1].t[new_i]=t[new_i].t[new_j2];
      // c[1].t[new_j]=c[0];



      // what we want: update my children then notify my neighbors
      // first child: i_(i-1)_iOpos
      // second child: i_(i+1)_iOpos
      // int j=(i+1)%3;
      // int k=(i+2)%3;
      // int i2=t[i].find(this);
      // int j2=(i2+1)%3;
      // int k2=(i2+2)%3;
      // // then change j to i2
      // // change k to i2
      // c[0].v[j]=t[i].v[k2];
      // c[1].v[k]=t[i].v[i2];
      // // c[0].v[k]=t[i].v[i2];
      // // update neighbors
      // // for c[0], new neighbors are c[1] and c[k2]
      // c[0].t[i]=t[i].t[k2];
      // c[0].t[k]=c[1];
      // c[1].t[i]=t[i].t[j2];
      // c[1].t[j]=c[0];


      // int iCurr = i;

      // Triangle tCurr = this;
      // Triangle tOpos = t[i];
      // int iOpos = tOpos.find(tCurr);
      // GO<PV2> vCurr = v[iCurr];
      // GO<PV2> vNext = v[(i+1)%3];
      // GO<PV2> vPrev = v[(i+2)%3];
      // GO<PV2> vOpos = tOpos.v[iOpos];

      // Triangle tNew = new Triangle(tCurr);
      // tNew.v[(i+1)%3]=vOpos;
      // Triangle tOposNeighbor=tOpos.t[(iOpos+2)%3];
      // tNew.t[iCurr]=tOposNeighbor;

      // // Triangle tNew = new Triangle(tCurr);
      // // tNew.v[(i+1)%3]=vOpos;
      // Triangle tCurrNeighbor=tCurr.t[(iCurr+2)%3];
      // tNew.t[iOpos]=tCurrNeighbor;






      // // Initialize j and k to "i+1" and "i+2"
      // int j=i+1;
      // int k=i+2;
      // // Initialize i2, j2, k2 to the counterparts(????) in t[i].
      // Triangle i2=t[i];
      // Triangle j2=t[j];
      // Triangle k2=t[k];

      // int v2=t[i].v[i2];
      // Set a vertex and neighbor of c[0].
      // Update two neighbors.

      // Triangle tCurr=this;
      // Triangle tOpos=t[i];
      // int iCurr = i;
      // int iOpos=tOpos.find(tCurr);
      // GO<PV2> vCurr=v[iCurr];
      // int iNext=(i+1)%3;
      // GO<PV2> vPrev=v[(i-1)%3];
      // GO<PV2> vOps=tOpos.v[iOpos];
      // tCurr.v[iNext] = vOps;
      // // update neibors
      // Triangle tNew = new Triangle(tCurr);
      // tNew.v[iNext]=vOps;
      // Triangle tOposNeighbor=tOpos.t[(iOpos+2)%3];
      // tNew.t[iCurr]=tOposNeighbor;
      // Triangle tCurrNeighbor=tCurr.t[(iCurr+2)%3];
      // tNew.t[iOpos]=tCurrNeighbor;

      // Triangle tCurr2=t[i];
      // Triangle tOpos2=this;
      // int iCurr2 = iOpos;
      // int iOpos2 = tOpos2.find(tCurr2);
      // GO<PV2> vCurr2=v[iCurr2];
      // int iNext2=(iOpos+1)%3;
      // GO<PV2> vPrev2=v[(iOpos-1)%3];
      // GO<PV2> vOps2=tOpos2.v[iOpos2];
      // tCurr2.v[iNext2] = vOps2;
      // // update neibors
      // Triangle tNew2 = new Triangle(tCurr2);
      // tNew2.v[iNext2]=vOps2;
      // Triangle tOposNeighbor2=tOpos2.t[(iOpos2+2)%3];
      // tNew2.t[iCurr2]=tOposNeighbor2;
      // Triangle tCurrNeighbor2=tCurr2.t[(iCurr2+2)%3];
      // tNew2.t[iOpos2]=tCurrNeighbor2;

      




      // Set a vertex and neighbor of c[1].
      // Update two neighbors.

      // Make c[0] and c[1] neighbors of each other.

      // debugging
      c[0].checkNeighbors();
      c[1].checkNeighbors();
    }

    /** Check if this triangle and neighbor t[i] should be flipped. If
      * it should, do the flip and recursively check the two new
      * triangles.
      */
    public void checkForFlip (int i) {
      System.out.println(" Check for flip.... ");
      Circle circle = new Circle(v[0], v[1], v[2]);
      states.add(new DState(circle));

      // Return if t[i] is null or
      // either new triangle would be clockwise or
      // circle does not contain opposite vertex of t[i].
      // Task1: How to detect clockwise triangle -> use AreaABC(a,b,c).sign() abc is the new triangle
      // Task2: How to represent opposite vertex of t[i]-> v[i], use contains(v[i]) 
      if( t[i]==null ){
        System.out.println(" t[i] is null! ");
        return;
      }      
      int i2=t[i].find(this);
      // if( AreaABC.sign(v[i],v[(i+1)%3],t[i].v[i2])>0 || AreaABC.sign(v[i],v[(i+2)%3],t[i].v[i2])>0 ){
      //   System.out.println(" triangles are clockwise!  ");
      //   return;
      // }
      if(!circle.contains(t[i].v[i2])){
        System.out.println(" circle does not contain p! ");
        return;
      }
      System.out.println(" circle CONTAINS p! ");

      // Until you implement flip, the extra appearance of the circle
      // will signal that you detected the need to flip.

      states.add(new DState(circle));

      flip(i);
      states.add(new DState(null));

      // Recursive calls for when you have implemented flip.
      // if (false) {
        this.c[0].checkForFlip(i);
        this.c[1].checkForFlip(i);
      // }
    }
  }

  public List<Triangle> triangles = new ArrayList<Triangle>();
  public Triangle root;

  public void draw (Graphics2D g) {
    for (Triangle t : triangles)
      if (t.c == null)
        t.draw(g, Color.blue);
  }

  public void triangulate (List<GO<PV2>> in) {
    states.clear();
    triangles.clear();
    
    double minX = Double.POSITIVE_INFINITY;
    double minY = Double.POSITIVE_INFINITY;
    double maxX = Double.NEGATIVE_INFINITY;
    double maxY = Double.NEGATIVE_INFINITY;
    
    for (GO<PV2> p : in) {
      PV2 pp = p.xyz();
      double x = pp.x.approx();
      double y = pp.y.approx();
      if (minX > x)
        minX = x;
      if (minY > y)
        minY = y;
      if (maxX < x)
        maxX = x;
      if (maxY < y)
        maxY = y;
    }
    System.out.println(minX + " " + minY + " " + maxX + " " + maxY);
    root = new Triangle(new InputPoint(minX - 100, minY - 100),
                        new InputPoint(2 * maxX - minX + 200, minY - 100),
                        new InputPoint(minX - 100, 2 * maxY - minY + 200));

    for (GO<PV2> p : in) {
      states.add(new DState(null));

      Triangle t = root.locate(p);
      t.split(p);

      states.add(new DState(null));
      // System.out.println("about to call checkForFlip");
      for (int i = 0; i < 3; i++)
        t.c[i].checkForFlip(i);

    }
  }
}
