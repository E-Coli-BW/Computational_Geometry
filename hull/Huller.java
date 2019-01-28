package hull;
import java.util.List;
import acp.GO;
import pv.PV2;

public interface Huller {
  List<GO<PV2>> hull (List<GO<PV2>> in);
  int numStates ();
  State getState (int i);
}

    
  
