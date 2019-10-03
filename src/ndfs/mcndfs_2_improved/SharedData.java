package ndfs.mcndfs_2_improved;

import java.util.concurrent.locks.ReentrantLock;
import java.util.HashMap;
import java.util.Map;
import graph.State;

public class SharedData {
  // Shared HashMap for red states
  private volatile Map<State, Boolean> red = new HashMap<State, Boolean>();
  // Shared HashMap for the counter per state
  private volatile Map<State, Integer> count = new HashMap<State, Integer>();
  // Lock for the counter
  private final ReentrantLock Counterlock = new ReentrantLock();

  /**
   * Set a the red state to true
   *
   * @param State state to be set to true
   */
  public void setRed (State state) {
    synchronized(this){
      this.red.put(state, true);
    }
  }

  /**
   * Get the red value of a state
   *
   * @param State state to be retrieved
   */
  public Boolean getRed (State state) {
    synchronized(this){
      Boolean s = false;
      s = this.red.get(state);
      if (s == null) {
        s = false;
      }
      return s;
    }
  }

  /**
   * Change the count of a state
   *
   * @param State state count to be changed
   * @param amount amount to change by (+1, -1)
   */
  public void changeCount (State state, int amount) {
    Counterlock.lock();
    try {
      Integer currentCount = this.count.get(state);
      if (currentCount == null) {
        currentCount = new Integer(amount);
      } else {
        currentCount = new Integer(currentCount + amount);
      }
      // System.out.println(currentCount);
      this.count.put(state, currentCount);
    } finally {
      Counterlock.unlock();
    }
  }

  /**
   * Get the count of a state
   *
   * @param State state count to be retrieved
   */
  public Integer getCount (State state) {
      Integer currentCount = this.count.get(state);
      if (currentCount == null) return 0;
      return currentCount;
  }
}
