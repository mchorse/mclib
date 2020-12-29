package mchorse.mclib.core.transformers;

import mchorse.mclib.utils.coremod.ClassMethodTransformer;
import mchorse.mclib.utils.coremod.CoreClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
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
		AbstractInsnNode location = null;

		int i = 0;

		while (it.hasNext())
		{
			AbstractInsnNode node = it.next();

			if (node instanceof MethodInsnNode)
			{
				MethodInsnNode methodNode = (MethodInsnNode) node;
				String desc = methodNode.owner + "/" + methodNode.name + methodNode.desc;
				String target = CoreClassTransformer.obfuscated ? "rl/b()V" : "net/minecraft/profiler/Profiler/endSection()V";

				if (desc.equals(target))
				{
					i++;

					if (i == 2)
					{
						location = node.getPrevious().getPrevious().getPrevious();

						break;
					}
				}
			}
		}

		if (location != null)
		{
			method.instructions.insertBefore(location, new MethodInsnNode(Opcodes.INVOKESTATIC, "mchorse/mclib/client/InputRenderer", "preRenderOverlay", "()V", false));

			System.out.println("McLib: successfully patched updateCameraAndRender!");
		}
	}
}
