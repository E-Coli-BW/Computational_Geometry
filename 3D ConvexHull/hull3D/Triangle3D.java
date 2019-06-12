package hull3D;

import java.util.ArrayList;
import java.util.List;

import acp.GO;
import pv.PV3;
import pv.VolumeABCD;
import pv.UpwardNormal;

public class Triangle3D {
  @SuppressWarnings("unchecked")
  public Vertex3D[] v = new Vertex3D[3];
  public Triangle3D[] t = new Triangle3D[3];
  public KillPair list;
  public boolean live;

  public Triangle3D(Vertex3D a, Vertex3D b, Vertex3D c) {
    this.live = true;
    this.v[0] = a;
    this.v[1] = b;
    this.v[2] = c;
  }

  public boolean hasVert(Vertex3D vert) {
    return (v[0] == vert || v[1] == vert || v[2] == vert);
  }

  public boolean hasEdge(Vertex3D a, Vertex3D b) {
    return ((v[0] == a && v[1] == b) ||
            (v[1] == a && v[2] == b) ||
            (v[2] == a && v[0] == b));
  }
 
  public boolean isOutside(Vertex3D vert) {
    return VolumeABCD.sign(v[0].p, v[1].p, v[2].p, vert.p) > 0;
  }

  public int find(Vertex3D vert) {
    for(int i = 0; i < 3; i++)
      if(v[i] == vert)
        return i;
    return -1;
  }

  public int find(Vertex3D tail, Vertex3D head) {
    for(int i = 0; i < 3; i++)
      if(v[(i+1)%3] == tail && v[(i+2)%3] == head)
        return i;
    return -1;
  }

  public int find(Triangle3D that) {
    for(int i = 0; i < 3; i++)
      if(t[i] == that)
        return i;
    return -1;
  }

  public void checkNeighbors() {
    System.out.println("checking neighbors");
    for(int i = 0; i < 3; i++)
      if(t[i] != null)
        t[i].find(this);
  }

  public void updateNeighbor(int i, Triangle3D oldT) {
    if(t[i] != null)
      t[i].t[t[i].find(oldT)] = this;
  }

  public boolean upwardNormal() {
    return UpwardNormal.sign(v[0].p, v[1].p, v[2].p) > 0;
  }
}
