package ndfs.mcndfs_2_improved;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicInteger;
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
      return this.red.getOrDefault(state, false);
    }
  }

  /**
   * Change the count of a state
   *
   * @param State state count to be changed
   * @param amount amount to change by (+1, -1)
   */
  public void changeCount (State state, int amount) {
    synchronized(this.count){
      Integer currentCount = this.count.getOrDefault(state, 0);
      currentCount = new Integer(currentCount + amount);

      this.count.put(state, currentCount);
    }
  }

  /**
   * Get the count of a state
   *
   * @param State state count to be retrieved
   */
  public Integer getCount (State state) {
      return this.count.getOrDefault(state, 0);
  }
}
