package com.iafenvoy.origins.data.power;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.HudRender;
import com.iafenvoy.origins.data._common.helper.ResourceHelper;
import net.minecraft.util.Mth;

import java.util.Optional;

public interface HudRenderable extends ResourceHelper {
    Power getPowerForHudRender();

    Optional<HudRender> getHudRenderData();

    default float getRenderPercentage(OriginDataHolder holder) {
        return clampProgress(this.getValue(holder), this.getMinValue(), this.getMaxValue());
    }

    default boolean shouldRender(OriginDataHolder holder) {
        return this.getPowerForHudRender().isActive(holder);
    }

    static float clampProgress(float value, float min, float max) {
        return Mth.clamp((value - min) / (max - min), 0, 1);
    }
}
