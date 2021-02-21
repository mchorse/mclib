package mchorse.mclib.client.gui.framework.elements.keyframes;

import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.utils.keyframes.Keyframe;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IAxisConverter
{
    public String format(double value);

    public double from(double x);

    public double to(double x);

    @SideOnly(Side.CLIENT)
    public void updateField(GuiTrackpadElement element);

    @SideOnly(Side.CLIENT)
    public boolean forceInteger(Keyframe keyframe, Selection selection, boolean forceInteger);
}