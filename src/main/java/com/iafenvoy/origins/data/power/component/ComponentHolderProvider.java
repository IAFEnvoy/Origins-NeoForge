package com.iafenvoy.origins.data.power.component;

import com.iafenvoy.origins.attachment.OriginDataHolder;

public interface ComponentHolderProvider<T> {
    T constructHolder(OriginDataHolder holder);
}
