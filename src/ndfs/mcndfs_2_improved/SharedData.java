package ndfs.mcndfs_2_improved;

import java.util.concurrent.locks.ReentrantLock;
import java.util.HashMap;
import java.util.Map;
import graph.State;

public class SharedData {
  private volatile Map<State, Boolean> red = new HashMap<State, Boolean>();
  private volatile Map<State, Integer> count = new HashMap<State, Integer>();
  private final ReentrantLock Counterlock = new ReentrantLock();
  private final ReentrantLock Redlock = new ReentrantLock();

  public void setRed (State state) {
    synchronized(state){
      this.red.put(state, true);
    }
  }

  public Boolean getRed (State state) {
    Boolean s = false;
    synchronized(state){

      s = this.red.get(state);
      if (s == null) s = false;

    }
    
    return s;
  }

  public void changeCount (State state, int amount) {
    Counterlock.lock();
    try {
      Integer currentCount = this.count.get(state);
      if (currentCount == null) {
        currentCount = new Integer(amount);
      } else {
        currentCount = new Integer(currentCount + amount);
      }
      this.count.put(state, currentCount);
    } catch (Exception e) {
      System.out.println("exception");
    } finally {
      Counterlock.unlock();
    }
  }

  public Integer getCount (State state) {
    Integer currentCount = this.count.get(state);
    if (currentCount == null) return 0;
    return currentCount;
  }
}
