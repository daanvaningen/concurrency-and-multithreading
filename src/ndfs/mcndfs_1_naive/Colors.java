package ndfs.mcndfs_1_naive;

import java.util.HashMap;
import java.util.Map;

import java.util.concurrent.locks.ReentrantLock;

import graph.State;

/**
* This class provides a color map for graph states.
*/
public class Colors {
  // Store node information
  private final Map<State, Color[]> color = new HashMap<State, Color[]>();
  private volatile Map<State, Integer> count = new HashMap<State, Integer>();
<<<<<<< HEAD
  private final Map<State, Boolean[]> pink = new HashMap<State, Boolean[]>();
  private volatile Map<State, Boolean> red = new HashMap<State, Boolean>();
=======
  private final Map<State, boolean[]> pink = new HashMap<State, boolean[]>();
  private volatile Map<State, Boolean> red = new HashMap<State, Boolean>();

>>>>>>> a7e7621ed86ced01a109bb8e96d4568606703aa9
  private int numThreads;
  private final ReentrantLock lock = new ReentrantLock();
  private volatile boolean result = false;

  public Colors(int numThreads) {
    this.numThreads = numThreads;
  }
  /**
  * Returns <code>true</code> if the specified state has the specified color,
  * <code>false</code> otherwise.
  *
  * @param state
  *            the state to examine.
  * @param color
  *            the color
  * @return whether the specified state has the specified color.
  */
  public synchronized boolean hasColor(State state, Color color, int threadNumber) {
    Color[] current = this.color.get(state);
    if (current == null) { // initialise
      Color[] initColor = new Color[numThreads];
      this.color.put(state, initColor);
      current = initColor;
    }

    // The initial color is white, and is not explicitly represented.
    if (color == Color.WHITE) {
      return current[threadNumber] == null;
    } else {
      return current[threadNumber] == color;
    }
  }

  /**
  * Gives the specified state the specified color.
  *
  * @param state
  *            the state to color.
  * @param color
  *            color to give to the state.
  */
  public synchronized void color(State state, Color color, int threadNumber) {
    Color[] current = this.color.get(state);
    if (current == null) { // initialise
      Color[] initColor = new Color[numThreads];
      current = initColor;
    }

    if (color == Color.WHITE) {
      current[threadNumber] = null;
      // color.remove(state);
      this.color.put(state, current);
    } else {
      current[threadNumber] = color;
      this.color.put(state, current);
    }
  }

  /**
  * Change the count by the amount given by change (+1 or -1)
  *
  * @param state state of which to increment the count
  * @param change amount to change the current count by
  */
  public synchronized void changeCount(State state, int change) {
    lock.lock();
    try{
      Integer currentCount = this.count.get(state);
      if (currentCount == null) {
        currentCount = 0;
      }
      currentCount += change;
      this.count.put(state, currentCount);
    } finally {
      lock.unlock();
<<<<<<< HEAD
=======

>>>>>>> a7e7621ed86ced01a109bb8e96d4568606703aa9
    }
  }

  /**
  * get the count of a state
  *
  * @param state state of which to get the count
  */
  public synchronized int getCount(State state) {
    return this.count.get(state);
  }

  /**
  * Change the count by the amount given by change (+1 or -1)
  *
  * @param state state of which to increment the count
  * @param change amount to change the current count by
  */
  public synchronized void setPink(State state, boolean isPink, int threadNumber) {
<<<<<<< HEAD
    Boolean[] current = this.pink.get(state);
=======
    boolean[] current = this.pink.get(state);
>>>>>>> a7e7621ed86ced01a109bb8e96d4568606703aa9
    if (current == null) {
      boolean[] initPink = new boolean[this.numThreads];
      for (int i = 0; i < threadNumber; i++) {
        initPink[i] = false;
      }
      current = initPink;
    }
    current[threadNumber] = isPink;
    this.pink.put(state, current);
  }

  /**
   * Check whether the current state is pink for this thread
   */
<<<<<<< HEAD
  public synchronized Boolean isPink(State state, int threadNumber) {
=======

  public synchronized boolean isPink(State state, int threadNumber) {
    boolean[] current = this.pink.get(state);
    if (current == null) {
      boolean[] initPink = new boolean[this.numThreads];
      for (int i = 0; i < threadNumber; i++) {
        initPink[i] = false;
      }
      this.pink.put(state, initPink);
    }
 
>>>>>>> a7e7621ed86ced01a109bb8e96d4568606703aa9
    return this.pink.get(state)[threadNumber];
  }

  /**
  * Returns whether the state is red or not
  *
  * @param state the state to check
  */
  public synchronized Boolean isRed(State state) {
    Boolean current = this.red.get(state);
    if (current == null) {
      return false;
    }
    return current;
  }

  public synchronized void setRed(State state) {
    this.red.put(state, true);
  }

  public synchronized void setResult(){
    this.result = true;
  }

  public synchronized Boolean getResult(){
    return this.result;
  }

  // not yet in use
  public synchronized void sleep() throws InterruptedException{
    synchronized(this){
      wait();
    }
  }

  // not yet in use
  public synchronized void wakeupcall(){
    synchronized(this){
      notifyAll();
    }
  }
}
