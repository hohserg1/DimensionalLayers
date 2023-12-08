package hohserg.dimensional.layers.asm

import net.minecraft.launchwrapper.IClassTransformer
import org.objectweb.asm.Opcodes._
import org.objectweb.asm.tree._
import org.objectweb.asm.{ClassReader, ClassWriter, Label}

import scala.collection.JavaConverters._

class BaseWorldServerTransformer extends IClassTransformer {
  override def transform(name: String, transformedName: String, basicClass: Array[Byte]): Array[Byte] = {
    if (basicClass == null)
      return null

    if (name == "hohserg.dimensional.layers.worldgen.proxy.BaseWorldServer") {
      processClass(basicClass, transformBaseWorldServer)
    } else if (name == "net.minecraft.world.WorldServer") {
      processClass(basicClass, transformWorldServer)
    } else
      basicClass
  }

  def processClass(bytes: Array[Byte], processor: ClassNode => Unit): Array[Byte] = {
    val cr = new ClassReader(bytes)
    val cn = new ClassNode
    cr.accept(cn, 0)
    processor(cn)
    val cw = new ClassWriter(ClassWriter.COMPUTE_MAXS)
    cn.accept(cw)
    cw.toByteArray
  }

  def transformBaseWorldServer(cn: ClassNode): Unit = {
    cn.superName = "net/minecraft/world/WorldServer"
    cn.methods.asScala.filter(mn => mn.name == "<init>").foreach { mn =>
      mn.instructions.iterator().asScala
        .collect {
          case i: MethodInsnNode if i.owner == "net/minecraft/world/World" && i.name == "<init>" => i
        }
        .foreach(mn => mn.owner = "net/minecraft/world/WorldServer")
    }
  }

  def transformWorldServer(cn: ClassNode): Unit = {
    val mn = new MethodNode(ACC_PUBLIC, "<init>", "(Lnet/minecraft/world/storage/ISaveHandler;Lnet/minecraft/world/storage/WorldInfo;Lnet/minecraft/world/WorldProvider;Lnet/minecraft/profiler/Profiler;Z)V", null, null)

    val label0 = new Label()
    mn.visitLabel(label0);
    mn.visitVarInsn(ALOAD, 0)
    mn.visitVarInsn(ALOAD, 1)
    mn.visitVarInsn(ALOAD, 2)
    mn.visitVarInsn(ALOAD, 3)
    mn.visitVarInsn(ALOAD, 4)
    mn.visitVarInsn(ILOAD, 5)
    mn.visitMethodInsn(INVOKESPECIAL, "net/minecraft/world/World", "<init>", "(Lnet/minecraft/world/storage/ISaveHandler;Lnet/minecraft/world/storage/WorldInfo;Lnet/minecraft/world/WorldProvider;Lnet/minecraft/profiler/Profiler;Z)V", false)
    mn.visitInsn(RETURN)
    val label2 = new Label()
    mn.visitLabel(label2);

    mn.visitLocalVariable("this", "Lnet/minecraft/world/WorldServer;", null, label0, label2, 0)
    mn.visitLocalVariable("saveHandlerIn", "Lnet/minecraft/world/storage/ISaveHandler;", null, label0, label2, 1)
    mn.visitLocalVariable("info", "Lnet/minecraft/world/storage/WorldInfo;", null, label0, label2, 2)
    mn.visitLocalVariable("providerIn", "Lnet/minecraft/world/WorldProvider;", null, label0, label2, 3)
    mn.visitLocalVariable("profilerIn", "Lnet/minecraft/profiler/Profiler;", null, label0, label2, 4)
    mn.visitLocalVariable("client", "Z", null, label0, label2, 5)

    cn.methods.add(mn)
  }
}
