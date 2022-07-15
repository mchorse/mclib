package mchorse.mclib.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.SingularMatrixException;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import java.nio.FloatBuffer;

public class RenderingUtils
{
    private static final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    /**
     * This method inverts the scale and rotation of the modelview matrix and
     * multiplies it on the OpenGL stack.
     */
    public static void glRevertRotationScale()
    {
        matrixBuffer.clear();

        MatrixUtils.matrixToFloatBuffer(matrixBuffer, getRevertRotationScale());

        GL11.glMultMatrix(matrixBuffer);
    }

    public static Matrix4f getRevertRotationScale()
    {
        Matrix4d[] transformation = MatrixUtils.getTransformation();

        Matrix4d invertRotScale = new Matrix4d();

        invertRotScale.setIdentity();

        invertRotScale.m00 = ((transformation[2].m00 != 0) ? 1 / transformation[2].m00 : 0);
        invertRotScale.m11 = ((transformation[2].m11 != 0) ? 1 / transformation[2].m11 : 0);
        invertRotScale.m22 = ((transformation[2].m22 != 0) ? 1 / transformation[2].m22 : 0);

        try
        {
            transformation[1].invert();
        }
        catch (SingularMatrixException e)
        { }

        invertRotScale.mul(transformation[1], invertRotScale);

        return new Matrix4f(invertRotScale);
    }


    /**
     * This method inverts the scale and rotation using the provided parameters
     * and multiplies the OpenGL matrix stack with it.
     * @param scale
     * @param rotation angles in radians
     * @param rotationOrder the order of the rotation
     */
    public static void glRevertRotationScale(Vector3d rotation, Vector3d scale, MatrixUtils.RotationOrder rotationOrder)
    {
        double invSx = (scale.x != 0) ? 1 / scale.x : 0;
        double invSy = (scale.y != 0) ? 1 / scale.y : 0;
        double invSz = (scale.z != 0) ? 1 / scale.z : 0;

        GlStateManager.scale(invSx, invSy, invSz);

        float rotx = (float) -Math.toDegrees(rotation.x);
        float roty = (float) -Math.toDegrees(rotation.y);
        float rotz = (float) -Math.toDegrees(rotation.z);

        switch (rotationOrder)
        {
            case ZYX:
                GlStateManager.rotate(rotz, 0, 0, 1);
                GlStateManager.rotate(roty, 0, 1, 0);
                GlStateManager.rotate(rotx, 1, 0, 0);

                break;
            case XYZ:
                GlStateManager.rotate(rotx, 1, 0, 0);
                GlStateManager.rotate(roty, 0, 1, 0);
                GlStateManager.rotate(rotz, 0, 0, 1);

                break;
            case XZY:
                GlStateManager.rotate(rotx, 1, 0, 0);
                GlStateManager.rotate(rotz, 0, 0, 1);
                GlStateManager.rotate(roty, 0, 1, 0);

                break;
            case YZX:
                GlStateManager.rotate(roty, 0, 1, 0);
                GlStateManager.rotate(rotz, 0, 0, 1);
                GlStateManager.rotate(rotx, 1, 0, 0);

                break;
            case YXZ:
                GlStateManager.rotate(roty, 0, 1, 0);
                GlStateManager.rotate(rotx, 1, 0, 0);
                GlStateManager.rotate(rotz, 0, 0, 1);

                break;
            case ZXY:
                GlStateManager.rotate(rotz, 0, 0, 1);
                GlStateManager.rotate(rotx, 1, 0, 0);
                GlStateManager.rotate(roty, 0, 1, 0);

                break;
        }

    }

    public static Matrix4f getFacingRotation(Facing facing, Vector3f position)
    {
        Entity camera = Minecraft.getMinecraft().getRenderViewEntity();
        float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
        Matrix4f transform = new Matrix4f();
        Matrix4f rotation = new Matrix4f();

        transform.setIdentity();

        float cYaw = - Interpolations.lerp(camera.prevRotationYaw, camera.rotationYaw, partialTicks);
        float cPitch = Interpolations.lerp(camera.prevRotationPitch, camera.rotationPitch, partialTicks);
        double cX = Interpolations.lerp(camera.prevPosX, camera.posX, partialTicks);
        double cY = Interpolations.lerp(camera.prevPosY, camera.posY, partialTicks) + camera.getEyeHeight();
        double cZ = Interpolations.lerp(camera.prevPosZ, camera.posZ, partialTicks);

        boolean lookAt = facing == RenderingUtils.Facing.LOOKAT_XYZ || facing == RenderingUtils.Facing.LOOKAT_Y;

        if (lookAt)
        {
            double dX = cX - position.x;
            double dY = cY - position.y;
            double dZ = cZ - position.z;
            double horizontalDistance = MathHelper.sqrt(dX * dX + dZ * dZ);

            cYaw = 180 - (float) (Math.toDegrees(MathHelper.atan2(dZ, dX)) - 90.0F);
            cPitch = (float) (Math.toDegrees(MathHelper.atan2(dY, horizontalDistance)));
        }

        if (facing == RenderingUtils.Facing.LOOKAT_XYZ || facing == RenderingUtils.Facing.ROTATE_XYZ)
        {
            rotation.rotX((float) Math.toRadians(cPitch));
            transform.mul(rotation);
            rotation.rotY((float) Math.toRadians(180 - cYaw));
            transform.mul(rotation);

        }
        else if (facing == RenderingUtils.Facing.ROTATE_Y || facing == RenderingUtils.Facing.LOOKAT_Y)
        {
            rotation.rotY((float) Math.toRadians(180 - cYaw));
            transform.mul(rotation);
        }

        return transform;
    }

    /**
     * This method multiples the openGL matrix stack with a rotation matrix
     * according to the facing parameter
     */
    public static void glFacingRotation(Facing facing, Vector3f position)
    {
        matrixBuffer.clear();
        MatrixUtils.matrixToFloatBuffer(matrixBuffer, getFacingRotation(facing, position));

        GL11.glMultMatrix(matrixBuffer);
    }

    public enum Facing
    {
        ROTATE_XYZ("rotate_xyz"),
        ROTATE_Y("rotate_y"),
        LOOKAT_XYZ("lookat_xyz"),
        LOOKAT_Y("lookat_y");

        public final String id;

        public static Facing fromString(String string)
        {
            for (Facing facing : values())
            {
                if (facing.id.equals(string))
                {
                    return facing;
                }
            }

            return null;
        }

        Facing(String id)
        {
            this.id = id;
        }
    }
}
