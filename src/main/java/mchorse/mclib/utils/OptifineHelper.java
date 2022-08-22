package mchorse.mclib.utils;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * A class that uses reflection to access functionalities of the Optifine mod
 */
public class OptifineHelper
{
    private static ReflectionElement<Field> shadowPass = new ReflectionElement<>();
    private static ReflectionElement<Class> shadersClass = new ReflectionElement<>();
    private static ReflectionElement<Method> nextEntity = new ReflectionElement<>();
    private static ReflectionElement<Method> nextBlockEntity = new ReflectionElement<>();

    /**
     * Checks whether Optifine is currently rendering shadow map. Thanks to
     * MiaoNLI for suggesting how to do it!
     */
    public static boolean isOptifineShadowPass()
    {
        /* only check once for isShadowPass Field to avoid too many reflection calls */
        if (!shadowPass.checked)
        {
            try
            {
                if (findShadersClass())
                {
                    shadowPass.element = shadersClass.element.getDeclaredField("isShadowPass");
                }
            }
            catch (Exception e)
            {}

            shadowPass.checked = true;
        }

        if (shadowPass.element != null)
        {
            try
            {
                return (boolean) shadowPass.element.get(null);
            }
            catch (Exception e)
            {}
        }

        return false;
    }

    /**
     * Invokes net.optifine.shaders.Shaders.nextEntity(Entity) when Optifine is present
     * @param entity
     */
    public static void nextEntity(Entity entity)
    {
        if (!nextEntity.checked)
        {
            try
            {
                if (findShadersClass())
                {
                    nextEntity.element = shadersClass.element.getMethod("nextEntity", Entity.class);
                }
            }
            catch (Exception var1)
            { }

            nextEntity.checked = true;
        }

        if (nextEntity.element != null)
        {
            try
            {
                nextEntity.element.invoke(null, entity);
            }
            catch (Exception e)
            { }
        }
    }

    /**
     * Invokes net.optifine.shaders.Shaders.nextBlockEntity(TileEntity) when Optifine is present
     * @param tileEntity
     */
    public static void nextBlockEntity(TileEntity tileEntity)
    {
        if (!nextBlockEntity.checked)
        {
            try
            {
                if (findShadersClass())
                {
                    nextBlockEntity.element = shadersClass.element.getMethod("nextBlockEntity", TileEntity.class);
                }
            }
            catch (Exception e)
            { }

            nextBlockEntity.checked = true;
        }

        if (nextBlockEntity.element != null)
        {
            try
            {
                nextBlockEntity.element.invoke(null, tileEntity);
            }
            catch (Exception e)
            { }
        }
    }

    /**
     * @return true if the Shaders Class was found i.e. not equal to null
     */
    private static boolean findShadersClass()
    {
        /* only check once if Optifine is there - avoid too many reflection calls*/
        if (!shadersClass.checked)
        {
            try
            {
                shadersClass.element = Class.forName("net.optifine.shaders.Shaders");
            }
            catch (Exception e)
            { }

            shadersClass.checked = true;
        }

        return shadersClass.element != null;
    }

    /* avoid too many reflection calls by saving whether it was checked */
    private static class ReflectionElement<T>
    {
        private T element;
        private boolean checked;
    }
}
