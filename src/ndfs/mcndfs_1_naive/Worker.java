package ndfs.mcndfs_1_naive;

import java.io.File;
import java.io.FileNotFoundException;

import graph.Graph;
import graph.GraphFactory;
import graph.State;

import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.*;
/**
* This is an implementation of Figure 2 of
* <a href="http://www.cs.vu.nl/~tcs/cm/ndfs/laarman.pdf"> "the Laarman
* paper"</a>.
*/
public class Worker implements Callable<Void> {

  private final Graph graph;
  private final Colors colors = new Colors();
  private SharedData sharedData; // Data object that the threads share
  public boolean done = false; // Indicating whether thread has finished
  public boolean result = false; // Result of the thread

  // Throwing an exception is a convenient way to cut off the search in case a
  // cycle is found.
  private static class CycleFoundException extends Exception {
    private static final long serialVersionUID = 1L;
  }

  private static class InterruptedException extends Exception {
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
  public Worker(File promelaFile, SharedData sharedData) throws FileNotFoundException {
    this.graph = GraphFactory.createGraph(promelaFile);
    this.sharedData = sharedData;
  }


  /**
   * Does the coloring of red / pink states as described in Fig 2 of Laarman.
   *
   * @param State Node of the graph to be checked.
   */
  private void dfsRed(State s) throws CycleFoundException, InterruptedException {
    colors.setPink(s, true);

    // Shuffle the post list of s
    List<State> postRed = graph.post(s);
    Collections.shuffle(postRed);

    for (State t : postRed) {
      if (Thread.interrupted()){ // Check if the thread has been interrupted
        throw new InterruptedException();
      }
      if (colors.hasColor(t, Color.CYAN)) {
        throw new CycleFoundException();
      }
      if (!colors.isPink(t)
          && !sharedData.getRed(t)) {
        dfsRed(t);
      }
    }
    if (s.isAccepting()) {
      sharedData.changeCount(s, -1);
      while (sharedData.getCount(s) != 0 && !Thread.interrupted()) {}
    }
    sharedData.setRed(s);
    colors.setPink(s, false);
  }

  /**
   * Does the coloring of blue / cyan states as described in Fig 2 of Laarman.
   *
   * @param State Node of the graph to be checked.
   */
  private void dfsBlue(State s) throws CycleFoundException, InterruptedException {
    colors.color(s, Color.CYAN);

    // Shuffle the post list of s
    List<State> post = graph.post(s);
    Collections.shuffle(post);

    for (State t : post) {
      if (Thread.interrupted()){
        throw new InterruptedException();
      }

      if (colors.hasColor(t, Color.WHITE) && !sharedData.getRed(t)) {
        dfsBlue(t);
      }
    }
    if (s.isAccepting()) {
      sharedData.changeCount(s, 1);
      dfsRed(s);
    }
    colors.color(s, Color.BLUE);
  }

  /**
   * Initialises the NNDFS algorithm with the initial state
   *
   * @param State Initial state of the graph
   */
  private void nndfs(State s) throws CycleFoundException, InterruptedException {
    dfsBlue(s);
  }

  /**
   * Call function of the ExecutorCompletionService
   */
  @Override
  public Void call() {
    try {
      nndfs(graph.getInitialState());
      this.done = true;
      System.out.println("Done reached");
    } catch (CycleFoundException e) {
      this.result = true;
      System.out.println("Cycle Found");
    } catch (InterruptedException e){
      System.out.println("Thread has been interrupted.");
    }

    return null;
  }
}
