package ndfs.mcndfs_2_improved;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.Map;
import graph.State;

public class SharedData {
  // Shared HashMap for red states
  private volatile Map<State, Boolean> red = new ConcurrentHashMap<State, Boolean>();
  // Shared HashMap for the counter per state
  private volatile Map<State, Integer> count = new ConcurrentHashMap<State, Integer>();

  /**
   * Set a the red state to true
   *
   * @param State state to be set to true
   */
  public void setRed (State state) {
    this.red.put(state, true);
  }

  /**
   * Get the red value of a state
   *
   * @param State state to be retrieved
   */
  public Boolean getRed (State state) {
      return this.red.getOrDefault(state, false);
  }

  /**
   * Change the count of a state
   *
   * @param State state count to be changed
   * @param amount amount to change by (+1, -1)
   */
  public void changeCount (State state, int amount) {
    synchronized(this.count){
      int ccount = this.count.getOrDefault(state, 0) + amount;
      this.count.put(state, ccount);
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
