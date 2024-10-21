package hohserg.dimensional.layers.asm.dev.tweaks;

import com.Zeno410Utils.AccessFloat;
import com.Zeno410Utils.Accessor;
import gloomyfolken.hooklib.api.*;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

@HookContainer
public class GeographicraftAccessorFix {

    @FieldLens(targetField = "fieldName")
    public static FieldAccessor<Accessor, String> fieldNameAccessor;

    @FieldLens(targetField = "fieldName")
    public static FieldAccessor<AccessFloat, String> fieldNameAccessFloat;

    @Hook
    @OnBegin
    public static void setField(Accessor self, Class classObject) {
        deobfFieldName(self, classObject, fieldNameAccessor);
    }

    @Hook
    @OnBegin
    public static void setField(AccessFloat self, Class classObject) {
        deobfFieldName(self, classObject, fieldNameAccessFloat);
    }

    public static <A> void deobfFieldName(A self, Class classObject, FieldAccessor<A, String> accessor) {
        String internalClassName = FMLDeobfuscatingRemapper.INSTANCE.unmap(classObject.getName().replace('.', '/'));
        accessor.set(self, FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(internalClassName, accessor.get(self), null));
    }
}
