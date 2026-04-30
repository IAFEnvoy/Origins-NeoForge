package com.iafenvoy.origins.data.power;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.KeySettings;
import org.jetbrains.annotations.NotNull;

public interface Toggleable {
    KeySettings getKey();

    //Server side call!!!
    void toggle(@NotNull OriginDataHolder holder, String key);
}
