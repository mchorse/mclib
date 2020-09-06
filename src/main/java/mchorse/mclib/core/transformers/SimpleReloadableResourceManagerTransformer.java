package mchorse.mclib.core.transformers;

import mchorse.mclib.utils.coremod.ClassMethodTransformer;
import mchorse.mclib.utils.coremod.CoreClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class SimpleReloadableResourceManagerTransformer extends ClassMethodTransformer
{
    public SimpleReloadableResourceManagerTransformer()
    {
        this.setMcp("getResource", "(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraft/client/resources/IResource;");
        this.setNotch("a", "(Ljy;)Lbnh;");
    }

    @Override
    public void processMethod(String name, MethodNode method)
    {
        LabelNode label = this.getFirstLabel(method);

        if (label != null)
        {
            InsnList list = new InsnList();
            String desc = "(Lmchorse/mclib/utils/resources/MultiResourceLocation;)L" + CoreClassTransformer.get("bnh", "net/minecraft/client/resources/IResource") + ";";

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