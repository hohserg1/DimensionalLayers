package hohserg.dimensional.layers.asm.multitude.patcher

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, StandardOpenOption}
import scala.collection.mutable
import scala.io.Source
import scala.util.Using;

object PatchableClasses {

  class ClassSet(file: File) {
    private val set: mutable.Set[String] = mutable.Set.from(Using(Source.fromFile(file))(_.getLines().toSet.map(_.trim).filter(_.nonEmpty)).getOrElse(Set.empty))

    def contains(className: String): Boolean =
      set.contains(className)

    def add(className: String): Unit = {
      set.add(className)
      Files.write(file.toPath, ("\n" + className).getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND)
    }
  }

  val accepted = new ClassSet(new File("./config/dimensional_layers_multitude_patcher_accepted.txt"))
  val checked = new ClassSet(new File("./config/dimensional_layers_multitude_patcher_checked.txt"))

}
