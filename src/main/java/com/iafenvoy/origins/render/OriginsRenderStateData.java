package com.iafenvoy.origins.render;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data._common.ColorSettings;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;

public final class OriginsRenderStateData {
    public static final ContextKey<ColorSettings> MODEL_COLOR = key("model_color");
    public static final ContextKey<Boolean> HIDE_LAYERS = key("hide_layers");
    public static final ContextKey<Boolean> HIDE_OUTLINE = key("hide_outline");
    public static final ContextKey<Boolean> SHAKING = key("shaking");
    public static final ContextKey<Boolean> RENDER_ELYTRA = key("render_elytra");
    public static final ContextKey<Identifier> ELYTRA_TEXTURE = key("elytra_texture");

    private OriginsRenderStateData() {
    }

    private static <T> ContextKey<T> key(String path) {
        return new ContextKey<>(Identifier.fromNamespaceAndPath(Origins.MOD_ID, path));
    }
}
