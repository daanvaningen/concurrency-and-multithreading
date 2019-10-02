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
    Lock.lock();
    try {
      this.red.put(state, true);
    } finally {
      Lock.unlock();
    }
  }

  public boolean getRed (State state) {
    Lock.lock();
    try {
      Boolean s = this.red.get(state);
      if (s == null) return false;
      return true;
    } finally {
      Lock.unlock();
    }
  }

  public void changeCount (State state, int amount) {
    Lock.lock();
    try {
      Integer currentCount = this.count.get(state);
      if (currentCount == null) {
        currentCount = new Integer(currentCount + amount);
      }
      this.count.put(state, currentCount);
    } finally {
      Lock.unlock();
    }
  }

  public int getCount (State state) {
    Lock.lock();
    try {
      Integer currentCount = this.count.get(state);
      if (currentCount == null) return 0;
      return currentCount;
    } finally {
      Lock.unlock();
    }
  }
}
