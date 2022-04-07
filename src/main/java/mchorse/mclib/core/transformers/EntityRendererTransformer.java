package mchorse.mclib.core.transformers;

import mchorse.mclib.utils.coremod.ClassMethodTransformer;
import mchorse.mclib.utils.coremod.ClassTransformer;
import mchorse.mclib.utils.coremod.CoreClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Iterator;
import java.util.ListIterator;

public class EntityRendererTransformer extends ClassTransformer
{
    @Override
    public void process(String name, ClassNode node)
    {
        for (MethodNode methodNode : node.methods)
        {
            if (methodNode.name.equals(this.checkName(methodNode, "a", "(FJ)V", "updateCameraAndRender", "(FJ)V")))
            {
                this.processUpdateCameraAndRender(methodNode);
            }
            else if(methodNode.name.equals(this.checkName(methodNode, "a", "(IFJ)V", "renderWorldPass", "(IFJ)V")))
            {
                this.processRenderWorldPass(methodNode);
            }
        }
    }

    public void processRenderWorldPass(MethodNode method)
    {
        ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();

        while (iterator.hasNext())
        {
            AbstractInsnNode currentNode = iterator.next();

            if (currentNode instanceof MethodInsnNode)
            {
                /* credit goes to MiaoNLI for discovering this possibility and implementing it in Minema */

                MethodInsnNode mnode = (MethodInsnNode) currentNode;
                if ((mnode.name.equals("a") || mnode.name.equals("setupCameraTransform")) && mnode.desc.equals("(FI)V"))
                {
                    iterator.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/mclib/utils/MatrixUtils", "ASMAfterCamera", "()V", false));

                    System.out.println("McLib: successfully patched renderWorldPass!");

                    break;
                }
            }
        }
    }

    public void processUpdateCameraAndRender(MethodNode method)
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
