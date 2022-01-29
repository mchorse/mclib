package mchorse.mclib.utils.wav;

import mchorse.mclib.utils.MathUtils;
import net.minecraft.client.renderer.GLAllocation;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

import java.nio.ByteBuffer;

public class WavePlayer
{
    private int buffer = -1;
    private int source = -1;
    private float duration;

    public WavePlayer initialize(Wave wave)
    {
        this.buffer = AL10.alGenBuffers();
        ByteBuffer buffer = GLAllocation.createDirectByteBuffer(wave.data.length);

        buffer.put(wave.data);
        buffer.flip();

        AL10.alBufferData(this.buffer, wave.getALFormat(), buffer, wave.sampleRate);

        this.duration = wave.getDuration();
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

    public int getSourceState()
    {
        return AL10.alGetSourcei(this.source, AL10.AL_SOURCE_STATE);
    }

    public boolean isPlaying()
    {
        return this.getSourceState() == AL10.AL_PLAYING;
    }

    public boolean isPaused()
    {
        return this.getSourceState() == AL10.AL_PAUSED;
    }

    public boolean isStopped()
    {
        int state = this.getSourceState();

        return state == AL10.AL_STOPPED || state == AL10.AL_INITIAL;
    }

    public float getPlaybackPosition()
    {
        return AL10.alGetSourcef(this.source, AL11.AL_SEC_OFFSET);
    }

    /**
     * This method sets the playback position to the provided seconds.
     * It clamps the seconds between 0 and the duration of the audio file.
     * @param seconds seconds to set the playback position to
     */
    public void setPlaybackPosition(float seconds)
    {
        seconds = MathUtils.clamp(seconds, 0, this.duration);

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