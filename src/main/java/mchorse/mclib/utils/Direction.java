package mchorse.mclib.utils;

public enum Direction
{
    TOP, LEFT, BOTTOM, RIGHT;

    public boolean isHorizontal()
    {
        return this == LEFT || this == RIGHT;
    }

    public boolean isVertical()
    {
        return this == TOP || this == BOTTOM;
    }

    public Direction opposite()
    {
        if (this == TOP)
        {
            return BOTTOM;
        }
        else if (this == BOTTOM)
        {
            return TOP;
        }
        else if (this == LEFT)
        {
            return  RIGHT;
        }

        /* this == RIGHT */
        return LEFT;
    }
}
