package mchorse.mclib.utils.resources;

import mchorse.mclib.McLib;
import mchorse.mclib.events.MultiskinProcessedEvent;
import mchorse.mclib.utils.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class TextureProcessor
{
    public static Pixels pixels = new Pixels();
    public static Pixels target = new Pixels();

    public static BufferedImage postProcess(MultiResourceLocation multi)
    {
        BufferedImage image = process(multi);

        Minecraft.getMinecraft().addScheduledTask(() ->
        {
            McLib.EVENT_BUS.post(new MultiskinProcessedEvent(multi, image));
        });

        return image;
    }

    public static BufferedImage process(MultiResourceLocation multi)
    {
        IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
        List<BufferedImage> images = new ArrayList<BufferedImage>();

        int w = 0;
        int h = 0;

        for (int i = 0; i < multi.children.size(); i++)
        {
            FilteredResourceLocation child = multi.children.get(i);

            try
            {
                IResource resource = manager.getResource(child.path);
                BufferedImage image = ImageIO.read(resource.getInputStream());

                images.add(image);

                w = Math.max(w, child.getWidth(image.getWidth()));
                h = Math.max(h, child.getHeight(image.getHeight()));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();

        for (int i = 0; i < multi.children.size(); i++)
        {
            BufferedImage child = images.get(i);
            FilteredResourceLocation filter = multi.children.get(i);
            int iw = child.getWidth();
            int ih = child.getHeight();

            if (filter.scaleToLargest)
            {
                iw = w;
                ih = h;
            }
            else if (filter.scale != 0 && filter.scale > 0)
            {
                iw = (int) (iw * filter.scale);
                ih = (int) (ih * filter.scale);
            }

            if (iw > 0 && ih > 0)
            {
                if (filter.erase)
                {
                    processErase(image, child, filter, iw, ih);
                }
                else
                {
                    if (filter.color != 0xffffff || filter.pixelate > 1)
                    {
                        processImage(child, filter);
                    }

                    g.drawImage(child, filter.shiftX, filter.shiftY, iw, ih, null);
                }
            }
        }

        g.dispose();

        return image;
    }

    /**
     * Apply erasing
     */
    private static void processErase(BufferedImage image, BufferedImage child, FilteredResourceLocation filter, int iw, int ih)
    {
        BufferedImage mask = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics g2 = mask.getGraphics();

        g2.drawImage(child, filter.shiftX, filter.shiftY, iw, ih, null);
        g2.dispose();

        target.set(mask);
        pixels.set(image);

        for (int p = 0, c = target.getCount(); p < c; p++)
        {
            Color pixel = target.getColor(p);

            if (pixel.a > 0.999F)
            {
                pixel = pixels.getColor(p);
                pixel.a = 0;
                pixels.setColor(p, pixel);
            }
        }
    }

    /**
     * Apply filters
     */
    private static void processImage(BufferedImage child, FilteredResourceLocation frl)
    {
        pixels.set(child);

        Color filter = new Color().set(frl.color);
        Color pixel = new Color();

        for (int i = 0, c = pixels.getCount(); i < c; i++)
        {
            pixel.copy(pixels.getColor(i));

            if (pixels.hasAlpha())
            {
                if (pixel.a <= 0)
                {
                    continue;
                }
            }

            if (frl.pixelate > 1)
            {
                int x = pixels.toX(i);
                int y = pixels.toY(i);
                boolean origin = x % frl.pixelate == 0 && y % frl.pixelate == 0;

                x -= x % frl.pixelate;
                y -= y % frl.pixelate;

                pixel.copy(pixels.getColor(x, y));
                pixels.setColor(i, pixel);

                if (!origin)
                {
                    continue;
                }
            }

            pixel.r *= filter.r;
            pixel.g *= filter.g;
            pixel.b *= filter.b;
            pixel.a *= filter.a;
            pixels.setColor(i, pixel);
        }
    }
}