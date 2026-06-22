package com.iafenvoy.origins.data._common.helper;

import com.iafenvoy.origins.attachment.OriginDataHolder;

public interface ResourceHelper {
    int getMinValue();

    int getMaxValue();

    int getValue(OriginDataHolder holder);

    void setValue(OriginDataHolder holder, int value);
}
