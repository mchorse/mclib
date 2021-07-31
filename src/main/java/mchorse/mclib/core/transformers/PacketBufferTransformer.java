package mchorse.mclib.core.transformers;

import mchorse.mclib.utils.PayloadASM;
import mchorse.mclib.utils.coremod.ClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PacketBufferTransformer extends ClassTransformer
{
    @Override
    public void process(String name, ClassNode node)
    {
        for (MethodNode method : node.methods)
        {
            replaceConstant(method, node.name, PayloadASM.MIN_SIZE);
        }
    }

    public static void replaceConstant(MethodNode method, String className, int constant)
    {
        List<AbstractInsnNode> targets = new ArrayList<AbstractInsnNode>();
        Iterator<AbstractInsnNode> it = method.instructions.iterator();

        while (it.hasNext())
        {
            AbstractInsnNode node = it.next();

            if (node.getOpcode() == Opcodes.SIPUSH && ((IntInsnNode) node).operand == constant)
            {
                targets.add(node);
            }
        }

        for (AbstractInsnNode target : targets)
        {
            method.instructions.insert(target, new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/mclib/utils/PayloadASM", "getPayloadSize", "()I", false));
            method.instructions.remove(target);
        }

        if (!targets.isEmpty())
        {
            System.out.println("McLib: successfully patched " + className + "." + method.name + "!");
        }
    }
}