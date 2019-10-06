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
  // a object map where object synchronize locks
  private volatile Map<State, Object> lockmap = new HashMap<State, Object>();

  private volatile Object initialLock;

  private volatile Object CountLock = new Object();

  public Object SetandGetLock(State state){
    system.out.println("Lock created and set.");
    synchronized(this){
      initialLock = this.lockmap.get(state);
      if (initialLock == null){
        initialLock = new Object();
        this.lockmap.put(state, initialLock);
      }
      return initialLock;
    }
  }

  /**
   * Set a the red state to true
   *
   * @param State state to be set to true
   */
  public void setRed (State state) {
    // acquire the object which corresponds with the state
    System.out.println("Set Red");
    Object Lock = this.lockmap.get(state);
    if (Lock == null){
      System.out.println("This should not happen: Lock should already been set by changecount.");
      Lock = SetandGetLock(state);
    }
    synchronized(Lock){
      this.red.put(state, true);
    }
  }

  /**
   * Get the red value of a state
   *
   * @param State state to be retrieved
   */
  public Boolean getRed (State state) {
    // acquire the object which corresponds with the state
    Object Lock = this.lockmap.get(state);
    if (Lock == null){
      Lock = SetandGetLock(state);
    }
    synchronized(Lock){
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
    synchronized(this.CountLock){
      int ccount = this.count.getOrDefault(state, 0) + amount;
      System.out.println(ccount);
      this.count.put(state, ccount);
    }
  }

  /**
   * Get the count of a state
   *
   * @param State state count to be retrieved
   */
  public Integer getCount (State state) {
    synchronized(this.CountLock){
      return this.count.getOrDefault(state, 0);
    }
  }
}
