package mchorse.mclib.utils;

import mchorse.mclib.McLib;

public class PayloadASM
{
	public static final int MIN_SIZE = 32767;

	public static int getPayloadSize()
	{
		return Math.max(MIN_SIZE, McLib.maxPacketSize.get());
	}
}