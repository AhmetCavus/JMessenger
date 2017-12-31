package framework.time;

/**
 * Timer interface
 * @author cinarapps
 *
 */
public interface TimerController {
	/**
	 * Starts the timer
	 * @param delay
	 * @param period
	 */
	void start(long delay, long period);
	void stop();
	void clear();
}
