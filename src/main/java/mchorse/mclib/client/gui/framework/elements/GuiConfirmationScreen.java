package mchorse.mclib.client.gui.framework.elements;

import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.modals.GuiConfirmModal;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;

import java.util.function.Consumer;

public class GuiConfirmationScreen extends GuiBase
{
    public GuiConfirmationScreen(IKey label, Consumer<Boolean> callback)
    {
        super();

        this.root.add(GuiConfirmModal.createTemplate(Minecraft.getMinecraft(), this.viewport, label, (value) ->
        {
            callback.accept(value);
            this.closeScreen();
        }));
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    protected void closeScreen()
    {
        super.closeScreen();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
