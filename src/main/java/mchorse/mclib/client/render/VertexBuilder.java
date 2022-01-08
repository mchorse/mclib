package mchorse.mclib.client.render;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;

/**
 * Create a vertex format compatible with Optifine shaders.
 * 
 * Of course it can also be used for vanilla.
 */
public class VertexBuilder
{
    public static final VertexFormat[] cache = new VertexFormat[4];

    /* location */
    public static int entityAttrib = -1;
    public static int midTexCoordAttrib = -1;
    public static int tangentAttrib = -1;

    /* Prepare for future */
    public static int velocityAttrib = -1;
    public static int midBlockAttrib = -1;

    static
    {
        try
        {
            Class<?> clazz = Class.forName("net.optifine.shaders.Shaders");

            Field fieldEntity = clazz.getField("entityAttrib");
            Field fieldMidTexCoord = clazz.getField("midTexCoordAttrib");
            Field fieldTangent = clazz.getField("tangentAttrib");

            entityAttrib = fieldEntity.getInt(null);
            midTexCoordAttrib = fieldMidTexCoord.getInt(null);
            tangentAttrib = fieldTangent.getInt(null);

            Field fieldVelocity = clazz.getField("velocityAttrib");
            Field fieldMidBlock = clazz.getField("midBlockAttrib");

            velocityAttrib = fieldVelocity.getInt(null);
            midBlockAttrib = fieldMidBlock.getInt(null);
        }
        catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
        {
        }
    }

    public static VertexFormat getFormat(boolean color, boolean tex, boolean lightmap, boolean normal)
    {
        int index = (color ? 1 : 0) | (lightmap ? 1 << 1 : 0);

        if (cache[index] == null)
        {
            VertexFormat vertexformat = new VertexFormat();

            vertexformat.addElement(new VertexFormatElement(0, EnumType.FLOAT, EnumUsage.POSITION, 3));
            vertexformat.addElement(new VertexFormatElement(0, EnumType.UBYTE, color ? EnumUsage.COLOR : EnumUsage.PADDING, 4));
            vertexformat.addElement(new VertexFormatElement(0, EnumType.FLOAT, tex ? EnumUsage.UV : EnumUsage.PADDING, 2));
            vertexformat.addElement(new VertexFormatElement(lightmap ? 1 : 0, EnumType.SHORT, lightmap ? EnumUsage.UV : EnumUsage.PADDING, 2));
            vertexformat.addElement(new VertexFormatElement(0, EnumType.BYTE, normal ? EnumUsage.NORMAL : EnumUsage.PADDING, 3));
            vertexformat.addElement(new VertexFormatElement(0, EnumType.BYTE, EnumUsage.PADDING, 1));

            if (entityAttrib != -1)
            {
                if (velocityAttrib != -1)
                {
                    /* attribute vec3 at_midBlock; */
                    vertexformat.addElement(new VertexFormatElement(0, EnumType.BYTE, EnumUsage.PADDING, 4));
                }

                /* attribute vec2 mc_midTexCoord; */
                vertexformat.addElement(new VertexFormatElement(0, EnumType.FLOAT, EnumUsage.PADDING, 2));

                /* attribute vec4 at_tangent; */
                vertexformat.addElement(new VertexFormatElement(0, EnumType.SHORT, EnumUsage.PADDING, 4));

                /* attribute vec3 mc_Entity; */
                vertexformat.addElement(new VertexFormatElement(0, EnumType.SHORT, EnumUsage.PADDING, 4));

                if (velocityAttrib != -1)
                {
                    /* attribute vec3 at_velocity; */
                    vertexformat.addElement(new VertexFormatElement(0, EnumType.FLOAT, EnumUsage.PADDING, 3));
                }
            }

            cache[index] = vertexformat;
        }

        return cache[index];
    }

    /**
     * attribute vec3 mc_Entity;<br>
     * 
     * By default it is used for blocks.<br>
     * Please call this method after endVertex for each <b>vertex</b>.
     * 
     * @param x block id
     * @param y render type {@link net.minecraft.util.EnumBlockRenderType}
     * @param z metadata
     */
    public static void fillEntity(BufferBuilder builder, int x, int y, int z)
    {
        if (builder.getVertexFormat().getNextOffset() != (velocityAttrib == -1 ? 56 : 72))
        {
            return;
        }

        int baseIndex = (builder.getVertexCount() - 1) * builder.getVertexFormat().getIntegerSize();

        if (baseIndex < 0)
        {
            return;
        }

        ByteBuffer byteBuf = builder.getByteBuffer();

        int pos = byteBuf.position();
        byteBuf.position(0);

        int offset = velocityAttrib == -1 ? 12 : 13;
        IntBuffer buffer = builder.getByteBuffer().asIntBuffer();

        int i = (y & 0xFFFF) << 16 | x & 0xFFFF;
        int j = z & 0xFFFF;

        buffer.put(baseIndex + offset, i);
        buffer.put(baseIndex + offset + 1, j);
    }

    /**
     * attribute vec2 mc_midTexCoord;<br>
     * 
     * Please call this method after endVertex for each <b>vertex</b>.
     */
    public static void fillMidTexCoord(BufferBuilder builder, float u, float v)
    {
        if (builder.getVertexFormat().getNextOffset() != (velocityAttrib == -1 ? 56 : 72))
        {
            return;
        }

        int baseIndex = (builder.getVertexCount() - 1) * builder.getVertexFormat().getIntegerSize();

        if (baseIndex < 0)
        {
            return;
        }

        ByteBuffer byteBuf = builder.getByteBuffer();

        int pos = byteBuf.position();
        byteBuf.position(0);

        int offset = velocityAttrib == -1 ? 8 : 9;
        FloatBuffer buffer = builder.getByteBuffer().asFloatBuffer();

        byteBuf.position(pos);

        buffer.put(baseIndex + offset, u);
        buffer.put(baseIndex + offset + 1, v);
    }

    /*
     * Optifine will recalculate the normals for GL11.QUADS when calling endVertex or something similar
     * 
     * So you can reset it with this method
     */
    public static void resetNormal(BufferBuilder builder, float x, float y, float z)
    {
        if (builder.getDrawMode() != GL11.GL_QUADS || !builder.getVertexFormat().hasNormal() || builder.getVertexFormat().getNextOffset() != (velocityAttrib == -1 ? 56 : 72))
        {
            return;
        }

        int vertexCount = builder.getDrawMode() == GL11.GL_QUADS ? 4 : 3;

        if (builder.getVertexCount() % vertexCount != 0)
        {
            return;
        }

        int vertexSize = builder.getVertexFormat().getNextOffset();
        int normalOffset = builder.getVertexFormat().getNormalOffset();
        int baseIndex = (builder.getVertexCount() - vertexCount) * vertexSize;

        ByteBuffer byteBuf = builder.getByteBuffer();

        int pos = byteBuf.position();
        byteBuf.position(0);

        float lenSquared = x * x + y * y + z * z;

        if (lenSquared > 0.0001F)
        {
            x /= Math.sqrt(lenSquared);
            y /= Math.sqrt(lenSquared);
            z /= Math.sqrt(lenSquared);
        }

        for (int i = 0; i < vertexCount; i++)
        {
            byteBuf.put(baseIndex + vertexSize * i + normalOffset + 0, (byte) ((int) (x * 0x7F) & 0xFF));
            byteBuf.put(baseIndex + vertexSize * i + normalOffset + 1, (byte) ((int) (y * 0x7F) & 0xFF));
            byteBuf.put(baseIndex + vertexSize * i + normalOffset + 2, (byte) ((int) (z * 0x7F) & 0xFF));
        }

        byteBuf.position(pos);
    }

    /**
     * attribute vec4 at_tangent;<br>
     * 
     * Please call this method after endVertex for each <b>face (not vertex)</b>.
     * 
     * @param calcNormal calculate normal vector
     */
    public static void calcTangent(BufferBuilder builder, boolean calcNormal)
    {
        if (builder.getDrawMode() != GL11.GL_QUADS && builder.getDrawMode() != GL11.GL_TRIANGLES || !builder.getVertexFormat().hasNormal() || builder.getVertexFormat().getNextOffset() != (velocityAttrib == -1 ? 56 : 72))
        {
            return;
        }

        int vertexCount = builder.getDrawMode() == GL11.GL_QUADS ? 4 : 3;

        if (builder.getVertexCount() % vertexCount != 0)
        {
            return;
        }

        int vertexSize = builder.getVertexFormat().getIntegerSize();
        int normalOffset = builder.getVertexFormat().getNormalOffset() / 4;
        int baseIndex = (builder.getVertexCount() - vertexCount) * vertexSize;

        ByteBuffer byteBuf = builder.getByteBuffer();

        int pos = byteBuf.position();
        byteBuf.position(0);

        IntBuffer intBuf = builder.getByteBuffer().asIntBuffer();
        FloatBuffer floatBuf = builder.getByteBuffer().asFloatBuffer();

        byteBuf.position(pos);

        Point3f v0 = new Point3f();
        Point3f v1 = new Point3f();
        Point3f v2 = new Point3f();

        v0.x = floatBuf.get(baseIndex + vertexSize * 0 + 0);
        v0.y = floatBuf.get(baseIndex + vertexSize * 0 + 1);
        v0.z = floatBuf.get(baseIndex + vertexSize * 0 + 2);

        v1.x = floatBuf.get(baseIndex + vertexSize * 1 + 0);
        v1.y = floatBuf.get(baseIndex + vertexSize * 1 + 1);
        v1.z = floatBuf.get(baseIndex + vertexSize * 1 + 2);

        v2.x = floatBuf.get(baseIndex + vertexSize * 2 + 0);
        v2.y = floatBuf.get(baseIndex + vertexSize * 2 + 1);
        v2.z = floatBuf.get(baseIndex + vertexSize * 2 + 2);

        Vector3f e1 = new Vector3f();
        Vector3f e2 = new Vector3f();

        e1.sub(v1, v0);
        e2.sub(v2, v0);

        if (calcNormal)
        {
            Vector3f normal = new Vector3f();

            normal.cross(e1, e2);

            if (normal.lengthSquared() > 0.0001F)
            {
                normal.normalize();
            }
            else
            {
                normal.set(0, 0, 1);
            }

            int packedNormal = ((int) (normal.z * 0x7F) & 0xFF) << 16 | ((int) (normal.y * 0x7F) & 0xFF) << 8 | (int) (normal.x * 0x7F) & 0xFF;

            intBuf.put(baseIndex + vertexSize * 0 + normalOffset, packedNormal);
            intBuf.put(baseIndex + vertexSize * 1 + normalOffset, packedNormal);
            intBuf.put(baseIndex + vertexSize * 2 + normalOffset, packedNormal);
        }

        if (!builder.getVertexFormat().hasUvOffset(0))
        {
            return;
        }

        int uvOffset = builder.getVertexFormat().getUvOffsetById(0) / 4;
        int tangentOffset = velocityAttrib == -1 ? 10 : 11;

        Point2f uv0 = new Point2f();
        Point2f uv1 = new Point2f();
        Point2f uv2 = new Point2f();

        uv0.x = floatBuf.get(baseIndex + vertexSize * 0 + uvOffset + 0);
        uv0.y = floatBuf.get(baseIndex + vertexSize * 0 + uvOffset + 1);

        uv1.x = floatBuf.get(baseIndex + vertexSize * 1 + uvOffset + 0);
        uv1.y = floatBuf.get(baseIndex + vertexSize * 1 + uvOffset + 1);

        uv2.x = floatBuf.get(baseIndex + vertexSize * 2 + uvOffset + 0);
        uv2.y = floatBuf.get(baseIndex + vertexSize * 2 + uvOffset + 1);

        Vector2f duv1 = new Vector2f();
        Vector2f duv2 = new Vector2f();

        duv1.sub(uv1, uv0);
        duv2.sub(uv2, uv0);

        Vector3f tangent = new Vector3f();
        Vector3f binormal = new Vector3f();

        float scale = duv1.y * duv2.x - duv1.x * duv2.y;

        if (Math.abs(scale) <= 0.0001F)
        {
            scale = 1.0f;
        }

        tangent.scale(duv1.y, e2);
        tangent.scaleAdd(-duv2.y, e1, tangent);
        tangent.scale(1.0f / scale);

        binormal.scale(duv2.x, e1);
        binormal.scaleAdd(-duv1.x, e2, binormal);
        binormal.scale(1.0f / scale);

        if (tangent.lengthSquared() > 0.0001F)
        {
            tangent.normalize();
        }

        if (binormal.lengthSquared() > 0.0001F)
        {
            binormal.normalize();
        }

        int packedNormal = intBuf.get(baseIndex + vertexSize * 0 + normalOffset);
        Vector3f normal = new Vector3f();

        normal.x = (byte) packedNormal;
        normal.y = (byte) (packedNormal >> 8);
        normal.z = (byte) (packedNormal >> 16);

        if (normal.lengthSquared() <= 0.0001F)
        {
            normal.cross(e1, e2);
        }

        normal.normalize();

        Vector3f binormalCheck = new Vector3f();

        binormalCheck.cross(normal, tangent);

        float w = binormalCheck.dot(binormal) < 0 ? -1F : 1F;

        tangent.scaleAdd(-normal.dot(tangent), normal, tangent);

        if (tangent.lengthSquared() > 0.0001F)
        {
            tangent.normalize();
        }

        int p1 = ((int) (tangent.y * 0x7FFF) & 0xFFFF) << 16 | (int) (tangent.x * 0x7FFF) & 0xFFFF;
        int p2 = ((int) (w * 0x7FFF) & 0xFFFF) << 16 | (int) (tangent.z * 0x7FFF) & 0xFFFF;

        for (int i = 0; i < vertexCount; i++)
        {
            intBuf.put(baseIndex + vertexSize * i + tangentOffset + 0, p1);
            intBuf.put(baseIndex + vertexSize * i + tangentOffset + 1, p2);
        }
    }

    /**
     * attribute vec3 at_velocity;<br>
     * 
     * Hope it could work in future.<br>
     * 
     * Please call this method after endVertex for each <b>vertex</b>.
     */
    public static void fillVelocity(BufferBuilder builder, float x, float y, float z)
    {
        if (velocityAttrib == -1 || builder.getVertexFormat().getNextOffset() != 72)
        {
            return;
        }

        int baseIndex = (builder.getVertexCount() - 1) * builder.getVertexFormat().getIntegerSize();

        if (baseIndex < 0)
        {
            return;
        }

        ByteBuffer byteBuf = builder.getByteBuffer();

        int pos = byteBuf.position();
        byteBuf.position(0);

        int offset = 15;
        FloatBuffer buffer = builder.getByteBuffer().asFloatBuffer();

        byteBuf.position(pos);

        buffer.put(baseIndex + offset, x);
        buffer.put(baseIndex + offset + 1, y);
        buffer.put(baseIndex + offset + 2, z);
    }

    /**
     * attribute vec3 at_velocity;<br>
     * 
     * Hope it could work in future.<br>
     * 
     * Please call this method after endVertex for each <b>vertex</b>.
     */
    public static void fillMidBlock(BufferBuilder builder, int x, int y, int z)
    {
        if (velocityAttrib == -1 || builder.getVertexFormat().getNextOffset() != 72)
        {
            return;
        }

        int baseIndex = (builder.getVertexCount() - 1) * builder.getVertexFormat().getIntegerSize();

        if (baseIndex < 0)
        {
            return;
        }

        ByteBuffer byteBuf = builder.getByteBuffer();

        int pos = byteBuf.position();
        byteBuf.position(0);

        int offset = 8;
        IntBuffer buffer = builder.getByteBuffer().asIntBuffer();

        byteBuf.position(pos);

        buffer.put(baseIndex + offset, x);
        buffer.put(baseIndex + offset + 1, y);
        buffer.put(baseIndex + offset + 2, z);
    }
}
