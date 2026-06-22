package com.iafenvoy.origins.data.power;

import com.iafenvoy.origins.attachment.OriginDataHolder;
import com.iafenvoy.origins.data._common.KeySettings;
import org.jetbrains.annotations.NotNull;

public interface Toggleable {
    KeySettings getKey();

    //服务端调用！！！
    void toggle(@NotNull OriginDataHolder holder, String key);
}
