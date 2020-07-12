package mchorse.mclib.utils.keyframes;

public enum KeyframeEasing
{
    IN("in"), OUT("out"), INOUT("inout");

    public final String key;

    private KeyframeEasing(String key)
    {
        this.key = key;
    }

    public String getKey()
    {
        return "mclib.easing." + this.key;
    }
}
