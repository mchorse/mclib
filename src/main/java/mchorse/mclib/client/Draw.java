package mchorse.mclib.client;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class Draw
{
    public static void axis(float length)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GL11.glLineWidth(5);
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(0, 0, 0).color(0, 0, 0, 1F).endVertex();
        buffer.pos(length, 0, 0).color(0, 0, 0, 1F).endVertex();
        buffer.pos(0, 0, 0).color(0, 0, 0, 1F).endVertex();
        buffer.pos(0, length, 0).color(0, 0, 0, 1F).endVertex();
        buffer.pos(0, 0, 0).color(0, 0, 0, 1F).endVertex();
        buffer.pos(0, 0, length).color(0, 0, 0, 1F).endVertex();
        tessellator.draw();

        GL11.glLineWidth(3);
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(0, 0, 0).color(1F, 0, 0, 1F).endVertex();
        buffer.pos(length, 0, 0).color(1F, 0, 0, 1F).endVertex();
        buffer.pos(0, 0, 0).color(0, 1F, 0, 1F).endVertex();
        buffer.pos(0, length, 0).color(0, 1F, 0, 1F).endVertex();
        buffer.pos(0, 0, 0).color(0, 0, 1F, 1F).endVertex();
        buffer.pos(0, 0, length).color(0, 0, 1F, 1F).endVertex();
        tessellator.draw();
        GL11.glLineWidth(1);

        point(0, 0, 0);
    }

    public static void point(double x, double y, double z)
    {
        GL11.glPointSize(12);
        GL11.glBegin(GL11.GL_POINTS);
        GL11.glColor3d(0, 0, 0);
        GL11.glVertex3d(0, 0, 0);
        GL11.glEnd();

        GL11.glPointSize(10);
        GL11.glBegin(GL11.GL_POINTS);
        GL11.glColor3d(1, 1, 1);
        GL11.glVertex3d(0, 0, 0);
        GL11.glEnd();
        GL11.glPointSize(1);
    }

    public static void cube(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        cube(buffer, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
        tessellator.draw();
    }

    public static void cube(BufferBuilder buffer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha)
    {
        /* Top */
        buffer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();

        /* Bottom */
        buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();

        /* Left */
        buffer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();

        /* Right */
        buffer.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();

        /* Front */
        buffer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();

        /* Back */
        buffer.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
    }
}