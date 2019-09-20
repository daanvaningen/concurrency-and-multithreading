package ndfs.mcndfs_1_naive;

import java.io.File;
import java.io.FileNotFoundException;

import graph.Graph;
import graph.GraphFactory;
import graph.State;

import java.util.concurrent.Callable;

/**
* This is a straightforward implementation of Figure 1 of
* <a href="http://www.cs.vu.nl/~tcs/cm/ndfs/laarman.pdf"> "the Laarman
* paper"</a>.
*/
public class Worker implements Runnable {

  private final Graph graph;
  private final Colors colors;
  private int threadNumber;
  private volatile boolean result = false;

  // Throwing an exception is a convenient way to cut off the search in case a
  // cycle is found.
  private static class CycleFoundException extends Exception {
    private static final long serialVersionUID = 1L;
  }

  /**
  * Constructs a Worker object using the specified Promela file.
  *
  * @param promelaFile
  *            the Promela file.
  * @throws FileNotFoundException
  *             is thrown in case the file could not be read.
  */
  public Worker(File promelaFile, Colors colors, int threadNumber) throws FileNotFoundException {
    this.threadNumber = threadNumber;
    this.graph = GraphFactory.createGraph(promelaFile);
    this.colors = colors;
  }

  private void dfsRed(State s) throws CycleFoundException {
    colors.setPink(s, true, this.threadNumber);
    for (State t : graph.post(s)) {
      if (colors.hasColor(t, Color.CYAN, this.threadNumber)) {
        throw new CycleFoundException();
      }
      if (!colors.isPink(t, this.threadNumber)
          && !colors.isRed(t)) {
        dfsRed(t);
      }
    }
    if (s.isAccepting()) {
      colors.changeCount(s, -1);
      while (colors.getCount(s) != 0) {}
    }
    colors.setRed(s);
    colors.setPink(s, false, this.threadNumber);
  }

  private void dfsBlue(State s) throws CycleFoundException {
    colors.color(s, Color.CYAN, this.threadNumber);
    for (State t : graph.post(s)) {
      if (colors.hasColor(t, Color.WHITE, this.threadNumber) && !colors.isRed(t)) {
        dfsBlue(t);
      }
    }
    if (s.isAccepting()) {
      lock.lock();
      colors.changeCount(s, 1);
      lock.unlock();
      dfsRed(s);
    }
    colors.color(s, Color.BLUE, this.threadNumber);
  }

  private void nndfs(State s) throws CycleFoundException {
    dfsBlue(s);
  }

  @Override
  public void run() {
    try {
      nndfs(graph.getInitialState());
    } catch (CycleFoundException e) {
      this.result = true;
      synchronized(this){
        this.notify();
      }
    }

  public boolean getResult() {
    return result;
  }
}
