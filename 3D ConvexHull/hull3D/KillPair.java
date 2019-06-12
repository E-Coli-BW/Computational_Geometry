package hull3D;

import acp.*;
import pv.*;
// KillPair class consists of 
// a vertex and a triangle
// each KillPari also has pointers(of type KillPair)
// that points to other KillPair
public class KillPair {

  public Vertex3D v;
  public Triangle3D t;
  // vnext/vprev is the iterator pair for triangles explorations for a given vertex
  // tnext/tprev is the iterator pair for vertex explorations for a given triangles

  // vnext/vprev iterates through the list where v is constant
  // tnext/tprev iterates through the list where t is constant
  // these KillParis serves as pointers of the list
  KillPair vnext, vprev, tnext, tprev;

  public KillPair(Vertex3D v, Triangle3D t) {
    // each vertex3D instance has 
    // a GO<PV3> point
    // a Triangle3D tiangle
    // and a KillPair *list* containing the lists of *triangles*
    // this vertex sees and ready to kill

    // if my current KillPair list is null, 
    // no need to do the killing, set every pointers to this
    if(v.list == null) {
      vnext = vprev = this;
    // else if current killPair list is not empty
    // there's a need to kill!!!
    // set the current pointer to vnext
    // make updated current pointer's prev to vprev
    // this way, the current one is killed!
    } else {
      vnext = v.list;
      vprev = v.list.vprev;
    }
    // after the killing, make current current
    vnext.vprev = vprev.vnext = v.list = this;
    // do the same check for triangle list
    if(t.list == null) {
      tnext = tprev = this;
    } else {
      tnext = t.list;
      tprev = t.list.tprev;
    }

    tnext.tprev = tprev.tnext = t.list = this;

    this.v = v;
    this.t = t;
  }

  public void remove() {
    //if this is the last item in the list
    if(vnext == this) {
      assert(vprev == this);
      assert(v.list == this);
      v.list = null;
    } else {
      vnext.vprev = vprev;
      vprev.vnext = vnext;
      if(v.list == this) v.list = vnext;
    }

    vprev = vnext = null;

    //if this is the last item in the list
    if(tnext == this) {
      assert(tprev == this);
      assert(t.list == this);
      t.list = null;
    } else {
      tnext.tprev = tprev;
      tprev.tnext = tnext;
      if(t.list == this) t.list = tnext;
    }

    tprev = tnext = null;
  }
}
