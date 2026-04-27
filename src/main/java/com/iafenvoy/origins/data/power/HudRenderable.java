package com.iafenvoy.origins.data.power;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.HudRender;
import net.minecraft.util.Mth;

import java.util.Optional;

public interface HudRenderable {
    Power getPowerForHudRender();

    Optional<HudRender> getHudRenderData();

    float getRenderPercentage(OriginDataHolder holder);

    static float clampProgress(float value, float min, float max) {
        return Mth.clamp((value - min) / (max - min), 0, 1);
    }
}
