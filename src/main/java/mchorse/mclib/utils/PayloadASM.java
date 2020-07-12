package mchorse.mclib.utils;

import mchorse.mclib.McLib;

public class PayloadASM
{
	public static final int MIN_SIZE = 32767;

	/**
	 * ASM hook which is used in {@link mchorse.mclib.core.transformers.CPacketCustomPayloadTransformer}
	 */
	public static int getPayloadSize()
	{
		return Math.max(MIN_SIZE, McLib.maxPacketSize.get());
	}
}