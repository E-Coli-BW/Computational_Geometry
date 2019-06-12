package hull3D;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import acp.GO;
import pv.InputPoint3D;
import pv.PV3;
import pv.VolumeABCD;

public class FastConvexHull3D {

  List<Vertex3D> vs;
  List<Triangle3D> ts;

  private int step;
  private STATE state;

  private int speed;

  public enum STATE {
    DEFAULT, VERTEX_CHOICE, HORIZON, BUILDING
  };

  public List<HState> states = new ArrayList<HState>();
	
  public class HState {

    public int vIndex;
    public List<Integer> liveDead;

    public HState() {
      this(null, new ArrayList<Triangle3D>());
    }
		
    public HState(Vertex3D v) {
      this(v, new ArrayList<Triangle3D>());
    }
		
    public HState(Vertex3D v, List<Triangle3D> deadTs) {
      liveDead = new ArrayList<Integer>();
      FastConvexHull3D parent = FastConvexHull3D.this;
			
      Set<Triangle3D> set = new HashSet<Triangle3D>(deadTs);
			
      vIndex = parent.vs.indexOf(v);
      liveDead = parent.ts.stream().map(t -> t.live ? 2 : (set.contains(t) ? 1 : 0)).collect(Collectors.toList());
			
      states.add(this);

    }

  }
	

  public FastConvexHull3D(List<GO<PV3>> points, int speed) {

    reset(speed);

    for (GO<PV3> p : points)
      this.vs.add(new Vertex3D(p));

  }

  public FastConvexHull3D(List<GO<PV3>> points) {
    this(points, 0);
  }

  public int getStep() {
    return step;
  }

  public STATE getState() {
    return state;
  }

  public List<Triangle3D> getTs() {
    return ts;
  }

  public List<Vertex3D> getVs() {
    return vs;
  }

  private void swap(int i, int j) {
    Vertex3D temp = vs.get(i);
    vs.set(i, vs.get(j));
    vs.set(j, temp);
  }

  public void reset() {
    reset(0);
  }

  public void reset(int speed) {
    this.vs = new ArrayList<Vertex3D>();
    this.ts = new ArrayList<Triangle3D>();
    this.step = 0;
    this.state = STATE.DEFAULT;
    if (speed < 0 || speed > 2)
      speed = 0;
    this.speed = speed;
  }

  public void makeHull() {
    if (vs.size() < 4)
      return;
		
    addTetrahedron(vs.get(0), vs.get(1), vs.get(2), vs.get(3));

    new HState();
		
    for (int i = 4; i < vs.size(); i++) {
      switch (speed) {
      case 0:
        addVertex(vs.get(i));
        break;
      case 1:
        addVertexFast(vs.get(i));
        break;
      case 2:
        addVertexFaster(vs.get(i));
        break;
      }
      new HState();
    }

    step = vs.size();
  }

  List<Triangle3D> deadTs = new ArrayList<Triangle3D>();

  int n;
  int i;
  int j;

  public List<Triangle3D> getDeadTs() {
    return deadTs;
  }

  public void step() {

    if (vs.size() < 4)
      return;

    if (step == 0) {
      addTetrahedron(vs.get(0), vs.get(1), vs.get(2), vs.get(3));
      step = 4;
    } else {

      if (step >= vs.size())
        return;

      switch (state) {
      case DEFAULT:
        state = STATE.VERTEX_CHOICE;
        break;
      case VERTEX_CHOICE:
        state = STATE.HORIZON;
        deadTs.clear();
        for (int i = 0; i < ts.size(); i++) {
          if (ts.get(i).live && vs.get(step).isOutside(ts.get(i))) {
            ts.get(i).live = false;
            deadTs.add(ts.get(i));
          }
        }
        if (deadTs.size() == 0) {
          state = STATE.DEFAULT;
          step++;
        }
        break;
      case HORIZON:
        state = STATE.BUILDING;
        n = ts.size();
        i = 0;
        j = 0;
      case BUILDING:
        boolean found = false;
        while (!found && i < deadTs.size()) {
          Triangle3D dead = deadTs.get(i);

          if (dead.t[j].live) {
            Triangle3D t = new Triangle3D(vs.get(step), dead.v[(j + 1) % 3],
                                          dead.v[(j + 2) % 3]);
            ts.add(t);
            t.t[0] = dead.t[j];
            t.updateNeighbor(0, dead);
            found = true;
          }
          j = (j + 1) % 3;
          if (j == 0)
            i++;
        }

        if (!found && i >= deadTs.size()) {
          for (int i = n; i < ts.size(); i++) {
            Triangle3D t = ts.get(i);
            t.t[1] = findEdge(t.v[0], t.v[2]);
            assert (t.t[1] != null);
            t.t[2] = findEdge(t.v[1], t.v[0]);
            assert (t.t[2] != null);
          }
          step++;
          state = STATE.DEFAULT;
        }
        break;
      }

    }
  }

  private void addTetrahedron(Vertex3D a, Vertex3D b, Vertex3D c, Vertex3D d) {

    if (VolumeABCD.sign(a.p, b.p, c.p, d.p) == -1) {
      Vertex3D temp = c;
      c = d;
      d = temp;
    }

    ts.add(new Triangle3D(b, a, c));
    ts.add(new Triangle3D(c, a, d));
    ts.add(new Triangle3D(d, a, b));
    ts.add(new Triangle3D(b, c, d));

    for (int i = 0; i < ts.size(); i++) {

      Triangle3D t = ts.get(i);
      for (int j = 0; j < 3; j++) {
        Vertex3D ta = t.v[(j + 1) % 3];
        Vertex3D tb = t.v[(j + 2) % 3];
        t.t[j] = findEdge(tb, ta);
      }

      for (int j = 4; j < vs.size(); j++) {
        Vertex3D v = vs.get(j);

        if (v.isOutside(t)) {
          new KillPair(v, t);
        }
      }

    }

  }

  private void addVertex(Vertex3D v) {

    new HState(v);
		
    List<Triangle3D> deadTs = new ArrayList<Triangle3D>();

    // check which triangles to be killed by the newly added vertex
    for (int i = 0; i < ts.size(); i++) {
      if (ts.get(i).live && v.isOutside(ts.get(i))) {
        ts.get(i).live = false;
        deadTs.add(ts.get(i));
      }
    }

    new HState(v, deadTs);
		
    int n = ts.size();

    // find all the half-dead edges(border of dead and alive) 
    // and create triangles using halfdead edge and the newly added 
    // vertex
    for (int i = 0; i < deadTs.size(); i++) {
      Triangle3D dead = deadTs.get(i);
      for (int j = 0; j < 3; j++) {
        if (dead.t[j].live) {
          Triangle3D t = new Triangle3D(v, dead.v[(j + 1) % 3], dead.v[(j + 2) % 3]);
          ts.add(t);
          t.t[0] = dead.t[j];
          t.updateNeighbor(0, dead);
          new HState(v, deadTs);
        }
      }
    }

    // assert that the newly created triangles
    // are consists of a half-dead edge and the 
    // newly-added vertex
    for (int i = n; i < ts.size(); i++) {
      Triangle3D t = ts.get(i);
      t.t[1] = findEdge(t.v[0], t.v[2]);
      assert (t.t[1] != null);
      t.t[2] = findEdge(t.v[1], t.v[0]);
      assert (t.t[2] != null);
    }

		
  }

  private void addVertexFast(Vertex3D v) {
    addVertex(v);
  }

  int ijk (int i) {
    return (i + 1) % 3;
  }

  // In the following five get methods, tail is the tail of a
  // half-dead edge of triangle tail.t.

  // the head of the edge
  Vertex3D getHead (Vertex3D tail) {
    return tail.t.v[ijk(tail.t.find(tail))];
  }

  // the index of the edge
  int getEdge (Vertex3D tail) {
    return ijk(ijk(tail.t.find(tail)));
  }

  // the dead triangle
  Triangle3D getDeadT (Vertex3D tail) {
    return tail.t;
  }

  // the live triangle
  Triangle3D getLiveT (Vertex3D tail) {
    return tail.t.t[getEdge(tail)];
  }

  // the new triangle that replaced the dead triangle
  Triangle3D getNewT (Vertex3D tail) {
    Triangle3D liveT = getLiveT(tail);
    return liveT.t[liveT.find(getHead(tail), tail)];
  }

  int ncalls = 0;
  
  private void addVertexFaster (Vertex3D v) {
    new HState(v);
    System.out.println("Calling addVertexFaster");
    if (++ncalls >= 1000)
      return;
    System.out.println("test im running my own java file ...ncalls " + ncalls);
    
    // EXERCISE 1 Finished
    // change true to false to see if it still works...
    if (false) {
      addVertex(v);
      return;
    }
    
    if (v.list == null)
      return;

    List<Triangle3D> deadTs = new ArrayList<Triangle3D>();

    // Set t.live=false for all dead t's.
    // Add them to deadTs for display purposes.
    if (false) {
      // ts: triangles
      // vs: vertices
      for (int i = 0; i < ts.size(); i++) {
        if (ts.get(i).live && v.isOutside(ts.get(i))) {
          ts.get(i).live = false;
          deadTs.add(ts.get(i));
        }
      }
    }
    else {
      System.out.println("ran into first if else...");
      KillPair pair = v.list;
      do {
        // EXERCISE 5 Finished
        // Make the triangle dead
        // and add it to deadTs.
        ///
        pair.t.live = false;
        deadTs.add(pair.t);


        ///
        // Running time now O(dead) instead of O(ts.size())
      } while ((pair = pair.vnext) != v.list);
    }

    new HState(v, deadTs);
    if (deadTs.size() == 0)
    	return;
    
    // Set v.t for tails of half-dead edges
    // and save one in firstTail.
    Vertex3D firstTail = null;
    if (false) {
      for (int i = 0; i < deadTs.size(); i++) {
        Triangle3D deadT = deadTs.get(i);
        for (int j = 0; j < 3; j++) {
          if (deadT.t[j].live) {
            firstTail = deadT.v[ijk(j)];
            firstTail.t = deadT;
          }
        }
      }
    }
    else {
      System.out.println("ran into second if else...");
      // EXERCISE 6 Finished!
      // Switch to v.list instead of deadTs array.
      ///
      KillPair pair = v.list;
      do{
        Triangle3D deadT=pair.t;
        for(int j=0;j<3;j++){
          if (deadT.t[j].live) {
            firstTail = deadT.v[ijk(j)];
            firstTail.t = deadT;
          }
        }
        //v.list!=null
      }while((pair=pair.vnext)!=v.list); // it seems that (pair=pair.vnext)!=v.list) also works
      // while(v.list!=null){

      // }
      // for (int i = 0; i < deadTs.size(); i++) {
      //   Triangle3D deadT = deadTs.get(i);
      //   for (int j = 0; j < 3; j++) {
      //     if (deadT.t[j].live) {
      //       firstTail = deadT.v[ijk(j)];
      //       firstTail.t = deadT;
      //     }
      //   }
      // }











      ///
      // Eliminates the need to allocate the additional array.
      // Although we still do it for the animation.
    }

    // Create each new triangle and make it the neighbor of the live triangle.
    // got some error in third if else...
    if (false) {
      for (int i = 0; i < deadTs.size(); i++) {
        Triangle3D deadT = deadTs.get(i);
        for (int j = 0; j < 3; j++) {
          if (deadT.t[j].live) {
            Triangle3D t = new Triangle3D(v, deadT.v[(j + 1) % 3], deadT.v[(j + 2) % 3]);
            ts.add(t);
            t.t[0] = deadT.t[j];
            t.updateNeighbor(0, deadT);
            new HState(v, deadTs);
          }
        }
      }
    }
    else {
      System.out.println("ran into third if else...");
      Vertex3D tail = firstTail;
      do {
        Triangle3D newT = new Triangle3D(v, tail, getHead(tail));
        ts.add(newT);
        new HState(v, deadTs);

        Triangle3D deadT = getDeadT(tail);
        Triangle3D liveT = getLiveT(tail);

        // EXERCISE 2 Finished
        // make newT and liveT neighbors
        // because deadT will no longer exist
        // (instead of deadT and liveT)
        
        // make the liveT newT's 0-index neighbor
        newT.t[0]=liveT;
        // why deadT???????????
        newT.updateNeighbor(0,deadT);

        ///
        // Triangles should now be added in counterclockwise order.
        // (pair=pair.vnext)!=v.list
      } while ((tail = getHead(tail)) != firstTail);
    }
    
    // Make the new triangles neighbors of each other.
    if (false) {
      // O(new^2) algorithm
      for (int i = n; i < ts.size(); i++) {
        Triangle3D t = ts.get(i);
        t.t[1] = findEdge(t.v[0], t.v[2]);
        assert (t.t[1] != null);
        t.t[2] = findEdge(t.v[1], t.v[0]);
        assert (t.t[2] != null);
      }
    }
    else {
      System.out.println("ran into fourth if else...");
      // EXERCISE 3 Finished
      // See previous exercise to see how to step through the new
      // triangles in order.
      // Make each pair of *consecutive* triangles neighbors.
      ///
      // the new triangle that replaced the dead triangle
      Vertex3D tail = firstTail;
      do {
        // getNewT returns the newT that tail sees and replaces the deadT
        // the para to pass in is the new vertex you created
        Triangle3D t=getNewT(tail);
        t.t[1] = findEdge(t.v[0], t.v[2]);
        assert (t.t[1] != null);
        t.t[2] = findEdge(t.v[1], t.v[0]);
        assert (t.t[2] != null);



        ///
        // Triangles should now be added in counterclockwise order.
      } while ((tail = getHead(tail)) != firstTail);


      ///
      // Running time is now O(new), but it will look the same.
    }

    if (false /* EXERCISE 4 FINISHED*/) {
    }
    else {
      System.out.println("ran into fifth if else...");
      // Create kill pairs for each new triangle
      Vertex3D tail = firstTail;
      do {
        Triangle3D newT = getNewT(tail);

        if (false /* EXERCISE 7 Finished!!!*/) {
          for (int i = 0; i < vs.size(); i++) {
            Vertex3D vert = vs.get(i);
            if (vert.isOutside(newT))
              new KillPair(vert, newT);
          }
        }
        else {
          Triangle3D deadT = getDeadT(tail);
          Triangle3D liveT = getLiveT(tail);
          if (deadT.list != null) {
            // EXERCISE 7
            // For each vertex that kills deadT, // how do I test if a vertex kills a deadT???
            // create a kill pair with newT if
            // it's not v and
            // and it's outside newT.
            // 

            for (int i = 0; i < vs.size(); i++) {
              Vertex3D vert = vs.get(i);
            if (vert.isOutside(newT)&&vert!=v)
              new KillPair(vert, newT);
          }







            ///
          }
          
          if (liveT.list != null) {
            // EXERCISE 7 continued
            // For each vertex that kills liveT,
            // create a kill pair with newT if
            // you did not create the pair already // HOW????
            // (hint:  look at vertex.list) vertex has a list that contains all the KillPair it has
            // and it's not v
            // and it's outside newT
            ///
            KillPair pair=liveT.list;
            do{
              Vertex3D addv=pair.v;
              if(addv!=v && addv.isOutside(newT) && addv.list.t!=newT)
              new KillPair(addv,newT);
            } while((pair = pair.tnext)!=liveT.list);

            // for(int i =0; i< vs.size(); i++){
            //   Vertex3D vert = vs.get(i);
            // if(vert.isOutside(newT)&&vert!=v)
            //   new KillPair(vert,newT)
            // }







            ///
            // Running time is now O(n log n)!
          }
        }
      } while ((tail = getHead(tail)) != firstTail);

      // EXERCISE 4 FINISHED
      // While v's list is not empty, remove its head(first pair in list) pair, and
      // remove all the pairs from that pair's dead triangle.
      // Set the "EXERCISE 4" true to false.
      ///
      do{
        KillPair headPair=v.list;
        v.list.remove();
        headPair.t.list.remove();

      }while(v.list!=null); 









      ///
      // No change.
    }
  }
  
  public Triangle3D findEdge(Vertex3D a, Vertex3D b) {

    for (int i = 0; i < ts.size(); i++) {
      if (ts.get(i).live && ts.get(i).hasEdge(a, b))
        return ts.get(i);
    }

    return null;

  }

  public boolean contains(FastConvexHull3D other) {

    for (Triangle3D t : other.ts) {

      if (t.live) {

        boolean found = false;

        for (Triangle3D u : ts) {
          if (u.live && u.hasVert(t.v[0]) && u.hasVert(t.v[1]) && u.hasVert(t.v[1])) {
            found = true;
            break;
          }
        }

        if (!found)
          return false;
      }

    }

    return true;
  }

  private static void randomize(List<GO<PV3>> list) {

    Random r = new Random();

    for (int i = 0; i < list.size(); i++) {
      int x = i + r.nextInt(list.size() - i);

      GO<PV3> temp = list.get(x);
      list.set(x, list.get(i));
      list.set(i, temp);
    }

  }

  public static void mainX(String[] args) {

    List<GO<PV3>> points = new ArrayList<GO<PV3>>();

    for (int i = 0; i < 50; i++) {
      double x = Math.random();
      double y = Math.random();
      double z = Math.random();
      points.add(new InputPoint3D(x, y, z));
    }

    FastConvexHull3D fast = new FastConvexHull3D(points, 0);

    randomize(points);

    FastConvexHull3D faster = new FastConvexHull3D(points, 1);

    randomize(points);

    FastConvexHull3D fastest = new FastConvexHull3D(points, 2);

    fast.makeHull();
    faster.makeHull();
    fastest.makeHull();

    int c = 0;
    for (Triangle3D t : fast.ts) {
      c += t.live ? 1 : 0;
    }

    System.out.println(c);

    c = 0;
    for (Triangle3D t : faster.ts) {
      c += t.live ? 1 : 0;
    }

    System.out.println(c);

    c = 0;
    for (Triangle3D t : fastest.ts) {
      c += t.live ? 1 : 0;
    }

    System.out.println(c);

    System.out.println(fast.contains(faster));
    System.out.println(faster.contains(fast));

    System.out.println(fast.contains(fastest));
    System.out.println(fastest.contains(fast));

    System.out.println(faster.contains(fastest));
    System.out.println(fastest.contains(faster));

  }
}
