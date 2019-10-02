package ndfs.mcndfs_2_improved;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import ndfs.NDFS;

/**
* Implements the {@link ndfs.NDFS} interface, mostly delegating the work to a
* worker class.
*/
public class NNDFS implements NDFS {
  private final Worker[] workers;
  private final File promelaFile;
  private int numThreads;
  private SharedData sharedData;
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
    this.promelaFile = promelaFile;
    this.numThreads = numThreads;
    this.sharedData = new SharedData();

    for (int i = 0; i < this.numThreads; i++) {
      this.workers[i] = new Worker(this.promelaFile, this.sharedData);
    }
  }

  @Override
  public boolean ndfs() {
    ExecutorService pool = Executors.newFixedThreadPool(this.numThreads);

    CompletionService<Void> ecs = new ExecutorCompletionService<Void>(pool);

    for (Worker w : this.workers) {
      ecs.submit(w);
    }

    while(!this.checkWorkers());

    pool.shutdownNow();
    try {
        // Wait for the pool to actually terminate.
        pool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
        // ignore
    }

    for (int i = 0; i < this.numThreads; i++) {
      if (this.workers[i].result) return true;
    }
    return false
  }

  public boolean checkWorkers () {
    boolean allDone = true;
    for (int i = 0; i < this.numThreads; i++) {
      if (this.workers[i].result) return true;
      if (allDone && !this.workers[i].done) allDone = false;
    }
    return allDone;
  }
}
