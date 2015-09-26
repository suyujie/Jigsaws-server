package gamecore.util;


/**
 * 秒级时钟。
 */
public final class Clock {

	private final static Clock CLOCK = new Clock(300);

	private long rate = 0;
	private volatile long now = 0;
	private volatile long now_s = 0;
	private boolean spinning = true;

	private Clock(long rate) {
		this.spinning = true;
		this.rate = rate;
		this.now = System.currentTimeMillis();
		this.now_s = now / 1000;
		this.start();
	}

	private void start() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				do {
					try {
						Thread.sleep(rate);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					now = System.currentTimeMillis();
					now_s = now / 1000;
				} while (spinning);
			}
		};
		thread.setName("Clock");
		thread.start();
	}

	public static void stop() {
		CLOCK.spinning = false;
	}

	/**
	 * 返回以秒为单位的当前绝对时间。
	 * @return
	 */
	public static long currentTimeSecond() {
		return CLOCK.now_s;
	}

	/**
	 * 返回以毫秒为单位的当前绝对时间。
	 * @return
	 */
	public static long currentTimeMillis() {
		return CLOCK.now;
	}

	/**
	 * 返回以秒为单位的当前绝对时间。
	 * @return
	 */
	public static long now() {
		return CLOCK.now;
	}
}
