package hohserg.dimensional.layers.asm.multitude.patcher

import net.minecraft.launchwrapper.IClassTransformer
import org.objectweb.asm.tree.{AbstractInsnNode, ClassNode, InsnList, LdcInsnNode}
import org.objectweb.asm.{ClassReader, ClassWriter}

import java.lang.Long as JLong
import scala.collection.JavaConverters.asScalaBufferConverter

class MultitudeTransformer extends IClassTransformer {

  override def transform(name: String, transformedName: String, basicClass: Array[Byte]): Array[Byte] = {
    if (basicClass == null)
      return null

    if (PatchableClasses.accepted.contains(transformedName)) {
      patch(basicClass).getOrElse(basicClass)

    } else if (PatchableClasses.checked.contains(transformedName)) {
      basicClass

    } else {
      PatchableClasses.checked.add(transformedName)
      patch(basicClass) match {
        case Some(patchedClass) =>
          PatchableClasses.accepted.add(transformedName)
          patchedClass
        case None =>
          basicClass
      }
    }
  }

  def patch(bytes: Array[Byte]): Option[Array[Byte]] = {
    val cr = new ClassReader(bytes)
    val cn = new ClassNode
    cr.accept(cn, 0)
    if (process(cn)) {
      val cw = new ClassWriter(ClassWriter.COMPUTE_MAXS)
      cn.accept(cw)
      Some(cw.toByteArray)
    } else
      None
  }

  def process(cn: ClassNode): Boolean = {
    var changedSomething = false
    for (mn <- cn.methods.asScala) {
      val replacements: Seq[(AbstractInsnNode, InsnList)] =
        mn.instructions.toArray.toList.collect {
          case node: LdcInsnNode =>
            node.cst match {
              case i: Integer if i.intValue() == 255 => node -> make_255_Int_Replacement
              case i: Integer if i.intValue() == 256 => node -> make_256_Int_Replacement
              case l: JLong if l.longValue() == 255 => node -> make_255_Long_Replacement
              case l: JLong if l.longValue() == 256 => node -> make_256_Long_Replacement
            }
        }
      if (replacements.nonEmpty) {
        changedSomething = true
        for ((from, to) <- replacements) {
          mn.instructions.insert(from, to)
          mn.instructions.remove(from)
        }
      }
    }
    changedSomething
  }

  //use world if present, or overworld otherwise
  def make_255_Int_Replacement: InsnList = ???

  def make_256_Int_Replacement: InsnList = ???

  def make_255_Long_Replacement: InsnList = ???

  def make_256_Long_Replacement: InsnList = ???

}
