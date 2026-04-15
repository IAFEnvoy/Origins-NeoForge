package com.iafenvoy.origins.data.power;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import org.jetbrains.annotations.NotNull;

public interface Toggleable {
    //Server side call!!!
    void toggle(@NotNull OriginDataHolder holder, String key);
}
