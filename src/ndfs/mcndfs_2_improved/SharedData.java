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
    // System.out.println("Lock created and set.");
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
    // System.out.println("Set Red");
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
    Object Lock = this.lockmap.get(state);
    if (Lock == null){
      Lock = SetandGetLock(state);
    }
    synchronized(Lock){
      if (getRed(state)){return;}
      int ccount = this.count.getOrDefault(state, 0) + amount;
      this.count.put(state, ccount);

      System.out.println(ccount);

      if (ccount == 0){
        setRed(state);
        Lock.notifyAll();}
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

  public void waitUntilZero(State state){
    Object Lock = this.lockmap.get(state);
    synchronized(Lock){
      try{
        Lock.wait(100);
      } catch(InterruptedException e){}
    }
  }
}
