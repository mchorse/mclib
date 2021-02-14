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

public class CPacketCustomPayloadTransformer extends ClassTransformer
{
    @Override
    public void process(String name, ClassNode node)
    {
        for (MethodNode method : node.methods)
        {
            this.replaceConstant(method, PayloadASM.MIN_SIZE);
        }
    }

    private void replaceConstant(MethodNode method, int constant)
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
            System.out.println("McLib: successfully patched " + method.name + "!");
        }
    }
}