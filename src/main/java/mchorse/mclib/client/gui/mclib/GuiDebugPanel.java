package mchorse.mclib.client.gui.mclib;

import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiSlotElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class GuiDebugPanel extends GuiDashboardPanel<GuiAbstractDashboard>
{
    public GuiButtonElement play;
    public GuiSlotElement slot;
    public GuiTextElement text;

    public GuiDebugPanel(Minecraft mc, GuiAbstractDashboard dashboard)
    {
        super(mc, dashboard);

        this.slot = new GuiSlotElement(mc, 0, (t) -> {});
        this.slot.flex().relative(this).x(0.5F).y(20).anchorX(0.5F);
        this.slot.setStack(new ItemStack(Items.BAKED_POTATO, 42));

        this.play = new GuiButtonElement(mc, IKey.str("Play me!"), null).background(false);
        this.play.flex().relative(this).xy(10, 10).w(80);

        this.text = new GuiTextElement(mc, 1000, null).background(false);
        this.text.flex().relative(this).xy(10, 40).w(80);

        this.add(this.slot, this.play, this.text);

        this.context(() ->
        {
            GuiSimpleContextMenu contextMenu = new GuiSimpleContextMenu(mc);

            for (int i = 0; i < 100; i++)
            {
                contextMenu.action(Icons.POSE, IKey.str("I came §8" + (i + 1)), null);
            }

            return contextMenu;
        });
    }

    @Override
    public boolean isClientSideOnly()
    {
        return true;
    }

    @Override
    public void resize()
    {
        super.resize();
    }

    @Override
    public void draw(GuiContext context)
    {
        super.draw(context);

        this.text.background(System.currentTimeMillis() % 2000 < 1000);
    }
}