package ndfs.mcndfs_1_naive;

import java.io.File;
import java.io.FileNotFoundException;

import ndfs.NDFS;

/**
* Implements the {@link ndfs.NDFS} interface, mostly delegating the work to a
* worker class.
*/
public class NNDFS implements NDFS {
  private final Worker[] workers;
  private final Colors colors;
  private final File promelaFile;
  private int numThreads;
  /**
  * Constructs an NDFS object using the specified Promela file.
  *
  * @param promelaFile
  *            the Promela file.
  * @throws FileNotFoundException
  *             is thrown in case the file could not be read.
  */
  public NNDFS(File promelaFile, int numThreads) throws FileNotFoundException {
    this.workers = new Worker[numThreads];
    this.colors = new Colors(numThreads);
    this.promelaFile = promelaFile;
    this.numThreads = numThreads;

    for (int i = 0; i < this.numThreads; i++) {
      this.workers[i] = new Worker(this.promelaFile, this.colors, i);
    }
  }

  @Override
  public boolean ndfs() {
    Thread[] threads = new Thread[this.numThreads];
    for (int i = 0; i < this.numThreads; i++) {
      threads[i] = new Thread(workers[i]);
    }
    for (Thread t : threads) {
      t.start();
    }

    System.out.println("main() starts sleeping");
    try {
        colors.sleep();
    } catch (InterruptedException e1) {
        // ignore
    }

    for (Thread t : threads) {
        t.interrupt();
    }

    for (Thread t : threads) {
      try {
        t.join();
      } catch (InterruptedException e) {
        System.out.println("Error");
      }
    }

    return this.colors.getResult();
  }
}
