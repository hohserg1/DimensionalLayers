package hohserg.dimensional.layers.lens;

import gloomyfolken.hooklib.api.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.data.*;

@HookContainer
public class TextureAtlasSpriteLens {
    
    @FieldLens
    public static FieldAccessor<TextureAtlasSprite, AnimationMetadataSection>animationMetadata;
}
