package server;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NetworkService implements Runnable {
  private String name;

  public NetworkService(String name) {
    this.name = name;
  }

  @Override
  public void run() {
    while (true) {
      if (Main.Surplus < 0)
        return;
      synchronized (name) {
		Main.Surplus--;
      System.out.println(name + " " + Main.Surplus);
	}
      
    }
  }
  public static void main(String[] args) {

		 NetworkService runnable = new NetworkService("runnable1");
		 NetworkService runnable2 = new NetworkService("runnable2");

	     Thread t1 = new Thread(runnable);
	     Thread t2 = new Thread(runnable2);

	     t1.start();
	     t2.start();

	  }
}
class Main {
	  public static int Surplus = 10;

	  private ExecutorService executor = Executors.newSingleThreadExecutor();

	  void addTask(Runnable runnable) {
	    executor.execute(runnable);
	  }

	  <V> V addTask(Callable<V> callable) {
	    Future<V> submit = executor.submit(callable);
	    try {
	      return submit.get();
	    } catch (InterruptedException e) {
	      System.out.println("InterruptedException" + e.toString());
	    } catch (ExecutionException e) {
	      System.out.println("ExecutionException" + e.toString());
	    }
	    return null;
	  }

	  public void testAddTask(String name) {
	    addTask(new Runnable() {
	      @Override
	      public void run() {
	        for (int i = 0; i < 3; i++) {
	          if (Main.Surplus <= 0)
	            return;
	          Main.Surplus--;
	          System.out.println(name + " " + Main.Surplus);
	        }

	      }
	    });
	  }

	  public void testAddTask2(String name) {
	    int count = addTask(new Callable<Integer>() {
	      @Override
	      public Integer call() throws Exception {
	        for (int i = 0; i < 3; i++) {
	          if (Main.Surplus <= 0)
	            return 0;
	          Main.Surplus--;
	          System.out.println(name + " " + Main.Surplus);
	        }
	        return Main.Surplus;
	      }
	    });

	  }

	  public void close() {
	    executor.shutdown();
	  }

//	  public static void main(String[] args) {
//	    Main main = new Main();
//	    main.testAddTask("task1");
//	    main.testAddTask2("task2");
//	    main.testAddTask("task3");
//	    main.testAddTask2("task4");
//	    main.close();
//	  }
	}