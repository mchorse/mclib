package mchorse.mclib.client.gui.framework.elements.keyframes;

import mchorse.mclib.utils.keyframes.Keyframe;

public enum Selection
{
    NOT_SELECTED
    {
        @Override
        public double getX(Keyframe keyframe)
        {
            throw new IllegalStateException("Not supported by NOT_SELECTED!");
        }

        @Override
        public double getY(Keyframe keyframe)
        {
            throw new IllegalStateException("Not supported by NOT_SELECTED!");
        }

        @Override
        public void setX(Keyframe keyframe, double x)
        {
            throw new IllegalStateException("Not supported by NOT_SELECTED!");
        }

        @Override
        public void setY(Keyframe keyframe, double y)
        {
            throw new IllegalStateException("Not supported by NOT_SELECTED!");
        }
    },
    KEYFRAME
    {
        @Override
        public double getX(Keyframe keyframe)
        {
            return keyframe.tick;
        }

        @Override
        public double getY(Keyframe keyframe)
        {
            return keyframe.value;
        }

        @Override
        public void setX(Keyframe keyframe, double x)
        {
            keyframe.setTick((long) x);
        }

        @Override
        public void setY(Keyframe keyframe, double y)
        {
            keyframe.setValue(y);
        }
    },
    LEFT_HANDLE
    {
        @Override
        public double getX(Keyframe keyframe)
        {
            return keyframe.lx;
        }

        @Override
        public double getY(Keyframe keyframe)
        {
            return keyframe.ly;
        }

        @Override
        public void setX(Keyframe keyframe, double x)
        {
            keyframe.lx = (float) x;
        }

        @Override
        public void setY(Keyframe keyframe, double y)
        {
            keyframe.ly = (float) y;
        }
    },
    RIGHT_HANDLE
    {
        @Override
        public double getX(Keyframe keyframe)
        {
            return keyframe.rx;
        }

        @Override
        public double getY(Keyframe keyframe)
        {
            return keyframe.ry;
        }

        @Override
        public void setX(Keyframe keyframe, double x)
        {
            keyframe.rx = (float) x;
        }

        @Override
        public void setY(Keyframe keyframe, double y)
        {
            keyframe.ry = (float) y;
        }
    };

    public abstract double getX(Keyframe keyframe);

    public abstract double getY(Keyframe keyframe);

    public abstract void setX(Keyframe keyframe, double x);

    public abstract void setY(Keyframe keyframe, double y);
}