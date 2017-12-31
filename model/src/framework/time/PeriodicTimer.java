package framework.time;

import java.util.Timer;
import java.util.TimerTask;

public class PeriodicTimer implements TimerController{
	
	private long mTime;
	private long mPeriod;
	private Timer mTimer;
	private TimerTask mTask;
	private int mPeriodCounter;
	private PeriodicTimer mThis = this;
	private PeriodListener mPeriodListener;
	private final String mTimerId = "PeriodicTimer";

	public static void main(String[] args) {
		final PeriodicTimer pt = new PeriodicTimer(new PeriodListener() {
			@Override
			public void onPeriod(PeriodicTimer timer, int periodCount, long time) {
				System.out.println("Count of period: " + periodCount + ", time: " + time);
				if(periodCount >= 7){
					timer.stop();
					timer.clear();
				}
			}
		});
		pt.start(200, 501);
	}

	public PeriodicTimer(PeriodListener periodListener){
		mTimer = new Timer(mTimerId);
		mPeriodListener = periodListener;
	}

	private TimerTask createNewTask(){
		mTask = new TimerTask() {
			@Override
			public void run() {
				if(mPeriodListener == null) return;
				mPeriodCounter+= 1;
				mTime+= mPeriod;
				mPeriodListener.onPeriod(mThis, mPeriodCounter, mTime);
			}
		};
		return mTask;
	}
	
	@Override
	public void start(long delay, long period) {
		mPeriodCounter = 0;
		mTime = delay;
		mPeriod = period;
		mTimer.schedule(createNewTask(), delay, period);
	}



	@Override
	public void stop() {
		mTimer.cancel();
		mTimer.purge();
		mTask.cancel();
	}


	@Override
	public void clear() {
		stop();
		mTask = null;
		mTimer = new Timer(mTimerId);
	}

	public interface PeriodListener{
		/**
		 * @param timer - The referenced object timer
		 * @param periodCount - Counts of the period
		 * @param time - The whole time since start of timer
		 */
		void onPeriod(PeriodicTimer timer, int periodCount, long time);
	}

}
