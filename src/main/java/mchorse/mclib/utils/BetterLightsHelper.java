package mchorse.mclib.utils;

import java.lang.reflect.Field;

public class BetterLightsHelper
{
    private static ReflectionElement<Class> betterLightsClass = new ReflectionElement<>();
    private static ReflectionElement<Field> shadowPass = new ReflectionElement<>();

    /**
     * Checks whether BetterLights is currently rendering shadow map.
     */
    public static boolean isBetterLightsShadowPass()
    {
        /* only check once for isShadowPass Field to avoid too many reflection calls */
        if (!shadowPass.checked)
        {
            try
            {
                if (findBetterLightsClass())
                {
                    shadowPass.element = betterLightsClass.element.getDeclaredField("isDxShadowPass");
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

    public static boolean isBetterLightsLoaded()
    {
        return findBetterLightsClass();
    }

    private static boolean findBetterLightsClass()
    {
        /* only check once if Optifine is there - avoid too many reflection calls*/
        if (!betterLightsClass.checked)
        {
            try
            {
                betterLightsClass.element = Class.forName("dz.betterlights.BetterLightsMod");
            }
            catch (Exception e)
            { }

            betterLightsClass.checked = true;
        }

        return betterLightsClass.element != null;
    }

    /* avoid too many reflection calls by saving whether it was checked */
    private static class ReflectionElement<T>
    {
        private T element;
        private boolean checked;
    }
}
