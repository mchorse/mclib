package mchorse.mclib.utils.wav;

import net.minecraft.client.renderer.GLAllocation;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

import java.nio.ByteBuffer;

public class WavePlayer
{
	private int buffer = -1;
	private int source = -1;

	public WavePlayer initialize(Wave wave)
	{
		this.buffer = AL10.alGenBuffers();
		ByteBuffer buffer = GLAllocation.createDirectByteBuffer(wave.data.length);

		buffer.put(wave.data);
		buffer.flip();

		AL10.alBufferData(this.buffer, wave.getALFormat(), buffer, wave.sampleRate);

		this.source = AL10.alGenSources();
		AL10.alSourcei(this.source, AL10.AL_BUFFER, this.buffer);
		AL10.alSourcei(this.source, AL10.AL_SOURCE_RELATIVE, AL10.AL_TRUE);

		return this;
	}

	public void delete()
	{
		AL10.alDeleteBuffers(this.buffer);
		AL10.alDeleteSources(this.source);

		this.buffer = -1;
		this.source = -1;
	}

	public void play()
	{
		AL10.alSourcePlay(this.source);
	}

	public void pause()
	{
		AL10.alSourcePause(this.source);
	}

	public void stop()
	{
		AL10.alSourceStop(this.source);
	}

	public boolean isPlaying()
	{
		return AL10.alGetSourcei(this.source, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
	}

	public boolean isPaused()
	{
		return AL10.alGetSourcei(this.source, AL10.AL_SOURCE_STATE) == AL10.AL_PAUSED;
	}

	public float getPlaybackPosition()
	{
		return AL10.alGetSourcef(this.source, AL11.AL_SEC_OFFSET);
	}

	public void setPlaybackPosition(float seconds)
	{
		AL10.alSourcef(this.source, AL11.AL_SEC_OFFSET, seconds);
	}

	public int getBuffer()
	{
		return this.buffer;
	}

	public int getSource()
	{
		return this.source;
	}
}