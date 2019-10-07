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
    this.initCount(state);
    boolean result = true;
    do {
      Integer current = this.count.get(state);
      Integer newAmount = new Integer(current + amount);
      result = !this.count.replace(state, current, newAmount);
    } while (result);
    synchronized(this){
      this.notifyAll();
    }
  }

  public void initCount (State state) {
    synchronized(this) { // Make sure state are absolutely unique within the table
      if (!this.count.containsKey(state)) this.count.put(state, 0);
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

  public void waitUntilUpdate (){
    synchronized(this){
      try{
        this.wait(500);
      } catch (InterruptedException e) { // do nothing
      }
    }
  }
}
