package mchorse.mclib.utils.wav;

public class Wave
{
	public int audioFormat;
	public int numChannels;
	public int sampleRate;
	public int byteRate;
	public int blockAlign;
	public int bitsPerSample;

	public byte[] data;

	public Wave(int audioFormat, int numChannels, int sampleRate, int byteRate, int blockAlign, int bitsPerSample, byte[] data)
	{
		this.audioFormat = audioFormat;
		this.numChannels = numChannels;
		this.sampleRate = sampleRate;
		this.byteRate = byteRate;
		this.blockAlign = blockAlign;
		this.bitsPerSample = bitsPerSample;
		this.data = data;
	}

	public float getDuration()
	{
		return this.data.length / this.numChannels / (this.bitsPerSample / 8) / (float) this.sampleRate;
	}
}