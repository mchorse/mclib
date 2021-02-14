package mchorse.mclib.utils.coremod;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public abstract class CoreClassTransformer implements IClassTransformer
{
    public static boolean obfuscated = false;

    public static boolean checkName(String name, String notch, String mcp)
    {
        if (name.equals(mcp) || name.equals(notch))
        {
            obfuscated = name.equals(notch);

            return true;
        }

        return false;
    }

    public static String get(String notch, String mcp)
    {
        return obfuscated ? notch : mcp;
    }

    public static String stringify(AbstractInsnNode node)
    {
        String output = node.toString();

        if (node instanceof LdcInsnNode)
        {
            output = "LDC " + ((LdcInsnNode) node).cst.toString();
        }
        else if (node instanceof MethodInsnNode)
        {
            MethodInsnNode method = (MethodInsnNode) node;

            output = "Method " + method.owner + "." + method.name + method.desc;
        }
        else if (node instanceof FieldInsnNode)
        {
            FieldInsnNode field = (FieldInsnNode) node;

            output = "Field " + field.owner + "." + field.name + field.desc;
        }
        else if (node instanceof VarInsnNode)
        {
            VarInsnNode var = (VarInsnNode) node;

            output = "Field " + var.getOpcode() + " " + var.var;
        }

        return output;
    }
}