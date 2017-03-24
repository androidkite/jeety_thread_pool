

import org.eclipse.jetty.util.thread.LifeCycle;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import java.util.Random;


public class TestThreadPool
{

	public static void main(String[] args) throws Exception
	{
		QueuedThreadPool threadPool = new QueuedThreadPool(200,8,10);
		threadPool.start();
		for(int i=0;i<1000;i++)
		{
			threadPool.execute(new Task(String.valueOf(i),threadPool));
		}
		threadPool.setStopTimeout(Long.MAX_VALUE);
		Thread.sleep(3000);
		threadPool.stop();
	}
	
	
	static class Task implements Runnable
	{
		
		private String name;
		private QueuedThreadPool queuedThreadPool;
		

		public Task(String name,QueuedThreadPool queuedThreadPool)
		{
			super();
			this.name = name;
			this.queuedThreadPool = queuedThreadPool;
		}



		@Override
		public void run()
		{
			System.out.println("execute_s_" + name + "_" + Thread.currentThread().getName() + "_" + queuedThreadPool.toString());
			long time = 100 + new Random().nextInt(1000);
			
			try
			{
			
				Thread.sleep(time);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("execute_e_" + name+ "_" + Thread.currentThread().getName() + "_" + time +  "_" + queuedThreadPool.toString());
			
		}
		
	}

}