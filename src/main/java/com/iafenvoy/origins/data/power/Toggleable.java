package com.iafenvoy.origins.data.power;

import com.iafenvoy.origins.attachment.OriginDataHolder;

public interface Toggleable extends Power {
    //Server side call!!!
    void toggle(OriginDataHolder holder, int index);
}
