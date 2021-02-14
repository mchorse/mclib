package mchorse.mclib.core.transformers;

import mchorse.mclib.utils.coremod.ClassMethodTransformer;
import mchorse.mclib.utils.coremod.CoreClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Iterator;

public class EntityRendererTransformer extends ClassMethodTransformer
{
    public EntityRendererTransformer()
    {
        this.mcp = "updateCameraAndRender";
        this.mcpSign = "(FJ)V";
        this.notch = "a";
        this.notchSign = "(FJ)V";
    }

    @Override
    public void processMethod(String name, MethodNode method)
    {
        Iterator<AbstractInsnNode> it = method.instructions.iterator();
        AbstractInsnNode pre = null;
        AbstractInsnNode post = null;

        int i = 0;
        boolean gui = false;

        while (it.hasNext())
        {
            AbstractInsnNode node = it.next();

            if (node instanceof MethodInsnNode)
            {
                MethodInsnNode methodNode = (MethodInsnNode) node;
                String desc = methodNode.owner + "/" + methodNode.name + methodNode.desc;
                String targetPost = CoreClassTransformer.obfuscated ? "rl/b()V" : "net/minecraft/profiler/Profiler/endSection()V";
                String targetPre = CoreClassTransformer.obfuscated ? "rl/c(Ljava/lang/String;)V" : "net/minecraft/profiler/Profiler/endStartSection(Ljava/lang/String;)V";

                if (desc.equals(targetPost))
                {
                    i++;

                    if (i == 2)
                    {
                        post = node.getPrevious().getPrevious().getPrevious();

                        break;
                    }
                }
                else if (gui && desc.equals(targetPre))
                {
                    pre = node;
                }
            }
            else if (node instanceof LdcInsnNode)
            {
                LdcInsnNode ldc = (LdcInsnNode) node;

                if (ldc.cst.equals("gui"))
                {
                    gui = true;
                }
            }
        }

        if (pre != null && post != null)
        {
            method.instructions.insert(pre, new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/mclib/client/InputRenderer", "preRenderOverlay", "()V", false));
            method.instructions.insertBefore(post, new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/mclib/client/InputRenderer", "postRenderOverlay", "()V", false));

            System.out.println("McLib: successfully patched updateCameraAndRender!");
        }
    }
}
