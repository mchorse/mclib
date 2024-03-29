package mchorse.mclib.utils;

public class MathUtils
{
    public static int clamp(int x, int min, int max)
    {
        return x < min ? min : (x > max ? max : x);
    }

    public static<T extends Comparable> T clamp(T x, T min, T max)
    {
        return x.compareTo(min) < 0 ? min : (x.compareTo(max) > 0 ? max : x);
    }

    public static float clamp(float x, float min, float max)
    {
        return x < min ? min : (x > max ? max : x);
    }

    public static double clamp(double x, double min, double max)
    {
        return x < min ? min : (x > max ? max : x);
    }

    public static long clamp(long x, long min, long max)
    {
        return x < min ? min : (x > max ? max : x);
    }

    public static int cycler(int x, int min, int max)
    {
        return x < min ? max : (x > max ? min : x);
    }

    public static float cycler(float x, float min, float max)
    {
        return x < min ? max : (x > max ? min : x);
    }

    public static double cycler(double x, double min, double max)
    {
        return x < min ? max : (x > max ? min : x);
    }

    public static int gridIndex(int x, int y, int size, int width)
    {
        x = x / size;
        y = y / size;

        return x + y * width / size;
    }

    public static int gridRows(int count, int size, int width)
    {
        double x = count * size / (double) width;

        return count <= 0 ? 1 : (int) Math.ceil(x);
    }

    /**
     * Remove 360 degrees of flips between previous and current angle.
     * A difference greater or equal than 180 degrees will be treated as a flip.
     * @param prev previous angle in radians
     * @param current current angle in radians
     * @return cleaned current in radians
     */
    public static float filterFlips(float prev, float current)
    {
        final float sign = (prev > current) ? 1 : -1;
        final float add = sign * 2.0F * (float) Math.PI;

        while (Math.abs(prev - current) >= Math.PI)
        {
            current += add;
        }

        return current;
    }
}