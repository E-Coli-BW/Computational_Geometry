package segment;
import java.util.List;
import acp.GO;
import pv.PV2;

public interface Intersecter {
  List<GO<PV2>> intersect (List<Segment> in);
  int numStates ();
  State getState (int i);
}

    
  
