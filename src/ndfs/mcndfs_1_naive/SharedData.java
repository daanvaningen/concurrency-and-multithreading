package ndfs.mcndfs_1_naive;

import java.util.concurrent.locks.ReentrantLock;
import java.util.HashMap;
import java.util.Map;
import graph.State;

public class SharedData {
  private volatile Map<State, Boolean> red = new HashMap<State, Boolean>();
  private volatile Map<State, Integer> count = new HashMap<State, Integer>();
  private final ReentrantLock Lock = new ReentrantLock();

  public void setRed (State state) {
    // System.out.println("setRed");
    Lock.lock();
    try {
      this.red.put(state, true);
    } finally {
      Lock.unlock();
    }
  }

  public Boolean getRed (State state) {
    Boolean s = false;
    Lock.lock();
    // System.out.println("getRed");
    try {
      s = this.red.get(state);
      if (s == null) s = false;
    } catch (Exception e) {
      System.out.println("getRed Error");
    } finally {
      // System.out.println("Unlock getRed");
      Lock.unlock();
    }

    return s;
  }

  public void changeCount (State state, int amount) {
    Lock.lock();
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
      Lock.unlock();
    }
  }

  public Integer getCount (State state) {
    // Lock.lock();
    // try {
    //   Integer currentCount = this.count.get(state);
    //   if (currentCount == null) return 0;
    //   return currentCount;
    // } finally {
    //   Lock.unlock();
    // }
    Integer currentCount = this.count.get(state);
    if (currentCount == null) return 0;
    return currentCount;
  }
}
