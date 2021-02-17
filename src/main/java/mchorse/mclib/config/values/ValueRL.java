package mchorse.mclib.config.values;

import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTexturePicker;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.config.gui.GuiConfigPanel;
import mchorse.mclib.utils.resources.RLUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.List;

public class ValueRL extends Value
{
    @SideOnly(Side.CLIENT)
    public static GuiTexturePicker picker;

    private ResourceLocation value;
    private ResourceLocation defaultValue;

    private boolean useServer;
    private ResourceLocation serverValue;

    public ValueRL(String id)
    {
        super(id);
    }

    public ValueRL(String id, ResourceLocation defaultValue)
    {
        super(id);

        this.defaultValue = defaultValue;
    }

    public ResourceLocation get()
    {
        return !this.useServer ? this.value : this.serverValue;
    }

    public void set(ResourceLocation value)
    {
        this.value = value;
        this.saveLater();
    }

    public void set(String value)
    {
        this.set(RLUtils.create(value));
    }

    @Override
    public void reset()
    {
        this.set(this.defaultValue);
    }

    @Override
    public void resetServer()
    {
        this.useServer = false;
        this.serverValue = null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<GuiElement> getFields(Minecraft mc, GuiConfigPanel gui)
    {
        GuiElement element = new GuiElement(mc);
        GuiLabel label = Elements.label(IKey.lang(this.getTitleKey()), 0).anchor(0, 0.5F);
        GuiButtonElement pick = new GuiButtonElement(mc, IKey.lang("mclib.gui.pick_texture"),  (button) ->
        {
            if (picker == null)
            {
                picker = new GuiTexturePicker(mc, null);
            }

            picker.callback = this::set;
            picker.fill(this.value);
            picker.flex().relative(gui).wh(1F, 1F);
            picker.resize();

            if (picker.hasParent())
            {
                picker.removeFromParent();
            }

            gui.add(picker);
        });

        pick.flex().w(90);

        element.flex().row(0).preferred(0).height(20);
        element.add(label, pick);

        return Arrays.asList(element.tooltip(IKey.lang(this.getTooltipKey())));
    }

    @Override
    public void fromJSON(JsonElement element)
    {
        this.value = RLUtils.create(element);
    }

    @Override
    public JsonElement toJSON()
    {
        return RLUtils.writeJson(this.value);
    }

    @Override
    public void copy(IConfigValue value)
    {
        if (value instanceof ValueRL)
        {
            this.value = RLUtils.clone(((ValueRL) value).value);
        }
    }

    @Override
    public void copyServer(IConfigValue value)
    {
        if (value instanceof ValueRL)
        {
            this.useServer = true;
            this.serverValue = RLUtils.clone(((ValueRL) value).value);
        }
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        super.fromBytes(buffer);

        this.value = this.readRL(buffer);
        this.defaultValue = this.readRL(buffer);
    }

    private ResourceLocation readRL(ByteBuf buffer)
    {
        if (buffer.readBoolean())
        {
            NBTTagCompound tag = ByteBufUtils.readTag(buffer);

            return RLUtils.create(tag.getTag("RL"));
        }

        return null;
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        super.toBytes(buffer);

        this.writeRL(buffer, this.value);
        this.writeRL(buffer, this.defaultValue);
    }

    private void writeRL(ByteBuf buffer, ResourceLocation rl)
    {
        buffer.writeBoolean(rl != null);

        if (rl != null)
        {
            NBTTagCompound tag = new NBTTagCompound();

            tag.setTag("RL", RLUtils.writeNbt(rl));
            ByteBufUtils.writeTag(buffer, tag);
        }
    }
}