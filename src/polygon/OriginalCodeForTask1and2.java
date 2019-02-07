      public int compareTo (SweepData data) {
      Edge that = (Edge) data;
      // EXERCISE 1
      // Use minY and maxY instead of tail and head.
      // Include the case that this and that have the same minY vertex.
      // We need to use Edge     

      // Edge class has a method minY and maxY to get the corresponding Vert out from an edge
      // Two cases: 1. Share the same minY( the vertex)
            //2. don't have vertex
      if(that.minY()==this.minY()){
      if(AreaABC.sign(this.minY().p,this.maxY().p,that.maxY().p)<0){
      //this is on the left of that
        return -1;
      }
      else{
        //this is on the right of that
      return 1;
      }
      }


        if(AreaABC.sign(this.minY().p,this.maxY().p,that.minY().p)<0 ){
          return -1;
      }
      else{
        return 1;
      }







      // that.minY

      // return 0; // wrong
    }


  void check (SweepNode a, SweepNode b) {
    //if one of the node is null don't need to check them
    if (a == null || b == null)
      return;
    Edge e = (Edge) a.getData();
    Edge f = (Edge) b.getData();

    states.add(new PState(null, e, f));
    // EXERCISE 2
    // Check if from same Polygon too.
    // Add a state after each check.
    // Edge has a hashset that record which one they have seen already
    // if they met alreday, don't check~!
    if(e.checked.contains(f)){
      return;
    }

    Polygon polye = e.getPolygon();
    Polygon polyf = f.getPolygon();
    // if they are the same polygon, don't bother chekcking
    if( polye==polyf || !( e.intersects(f)) ) {
      return;
    }
    //check if we have already chekced e and f or not'
    // if ( !polye.checked.contains(polyf) ) {
      //states.add( new PState(null, e, f) );

      // if not check if these two Edges intersect
 
        // if e and f intersects, add each other's corresponding to their hashset

        GO<PV2> intersection = new ABintersectCD(e.tail.p, e.head.p, f.tail.p, f.head.p);
        
        // out is a polygon and you can instantiate a bew Vert from Polygon
        Vert v=out.new Vert(intersection, e, f);

        // add this newly instantiated Vert into the verts list for keeping the record in out 
        out.verts.add(v);

        // This is an event, we discovered a new Vertex! Add it to the events list
        events.add(v);
        // State changes, add a new state to show the changes
        states.add( new PState(null, e, f) ); 


      

    // }

    // GO<PV2> p = new ABintersectCD(e.tail.p, e.head.p, f.tail.p, f.head.p);
    // Vert v = out.new Vert(p, e, f);
    // out.verts.add(v);
    // events.add(v);
    // states.add(new PState(null, e, f));
  }    