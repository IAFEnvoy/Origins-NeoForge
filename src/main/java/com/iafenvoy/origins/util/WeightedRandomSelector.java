package com.iafenvoy.origins.util;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class WeightedRandomSelector {
    private static final Random RANDOM = new Random();

    @Nullable
    public static <T extends WeightGetter> T selectRandomByWeight(List<T> holders) {
        if (holders == null || holders.isEmpty()) return null;
        int totalWeight = 0;
        for (T holder : holders)
            if (holder.weight() > 0)
                totalWeight += holder.weight();
        if (totalWeight <= 0) return holders.get(RANDOM.nextInt(holders.size()));
        int randomValue = RANDOM.nextInt(totalWeight);
        int currentWeight = 0;
        for (T holder : holders) {
            int weight = holder.weight();
            if (weight <= 0) continue;
            currentWeight += weight;
            if (randomValue < currentWeight) return holder;
        }
        return holders.getLast();
    }

    public interface WeightGetter {
        int weight();
    }
}
