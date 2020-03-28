package mchorse.mclib.utils;

public class Timer
{
	public boolean enabled;
	public long time;
	public long duration;

	public Timer(long duration)
	{
		this.duration = duration;
	}

	public void mark()
	{
		this.mark(this.duration);
	}

	public void mark(long duration)
	{
		this.enabled = true;
		this.time = System.currentTimeMillis() + duration;
	}

	public boolean checkReset()
	{
		boolean enabled = this.check();

		if (enabled)
		{
			this.enabled = false;
		}

		return enabled;
	}

	public boolean check()
	{
		return this.enabled && System.currentTimeMillis() >= this.time;
	}
}