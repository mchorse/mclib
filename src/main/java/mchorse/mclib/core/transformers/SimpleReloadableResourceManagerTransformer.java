package mchorse.mclib.core.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import mchorse.mclib.utils.coremod.ClassTransformer;
import mchorse.mclib.utils.coremod.CoreClassTransformer;

public class SimpleReloadableResourceManagerTransformer extends ClassTransformer
{
    @Override
    public void process(String name, ClassNode node)
    {
        for (MethodNode method : node.methods)
        {
            String methodName = this.checkName(method, "a", "(Lkq;)Lbzx;", "getResource", "(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraft/client/resources/IResource;");

            if (methodName != null)
            {
                this.processGetResource(method);
            }
        }
    }

    private void processGetResource(MethodNode method)
    {
        LabelNode label = this.getFirstLabel(method);

        if (label != null)
        {
            InsnList list = new InsnList();
            String desc = "(Lmchorse/mclib/utils/resources/MultiResourceLocation;)L" + CoreClassTransformer.get("bzx", "net/minecraft/client/resources/IResource") + ";";

            list.add(new VarInsnNode(Opcodes.ALOAD, 1));
            list.add(new TypeInsnNode(Opcodes.INSTANCEOF, "mchorse/mclib/utils/resources/MultiResourceLocation"));
            list.add(new JumpInsnNode(Opcodes.IFEQ, label));
            list.add(new VarInsnNode(Opcodes.ALOAD, 1));
            list.add(new TypeInsnNode(Opcodes.CHECKCAST, "mchorse/mclib/utils/resources/MultiResourceLocation"));
            list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/mclib/utils/resources/RLUtils", "getStreamForMultiskin", desc, false));
            list.add(new InsnNode(Opcodes.ARETURN));

            method.instructions.insert(list);

            System.out.println("McLib: successfully patched getResource!");
        }
    }
}