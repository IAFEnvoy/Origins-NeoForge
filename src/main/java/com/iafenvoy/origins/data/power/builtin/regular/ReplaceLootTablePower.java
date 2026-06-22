package com.iafenvoy.origins.data.power.builtin.regular;

import com.iafenvoy.origins.Origins;
import com.iafenvoy.origins.data.condition.BiEntityCondition;
import com.iafenvoy.origins.data.condition.BlockCondition;
import com.iafenvoy.origins.data.condition.ItemCondition;
import com.iafenvoy.origins.data.power.Power;
import com.iafenvoy.origins.data.power.Prioritized;
import com.iafenvoy.origins.util.codec.MiscCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.IdentifierException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReplaceLootTablePower extends Power implements Prioritized {
    public static final MapCodec<ReplaceLootTablePower> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            BaseSettings.CODEC.forGetter(Power::getSettings),
            Codec.unboundedMap(MiscCodecs.PATTERN, Codec.STRING).fieldOf("replace").forGetter(ReplaceLootTablePower::getReplace),
            BiEntityCondition.optionalCodec("bi_entity_condition").forGetter(ReplaceLootTablePower::getBiEntityCondition),
            BlockCondition.optionalCodec("block_condition").forGetter(ReplaceLootTablePower::getBlockCondition),
            ItemCondition.optionalCodec("item_condition").forGetter(ReplaceLootTablePower::getItemCondition),
            Codec.INT.optionalFieldOf("priority", 0).forGetter(ReplaceLootTablePower::getPriority)
    ).apply(i, ReplaceLootTablePower::new));
    private final Map<Pattern, String> replace;
    private final BiEntityCondition biEntityCondition;
    private final BlockCondition blockCondition;
    private final ItemCondition itemCondition;
    private final int priority;

    protected ReplaceLootTablePower(BaseSettings settings, Map<Pattern, String> replace, BiEntityCondition biEntityCondition, BlockCondition blockCondition, ItemCondition itemCondition, int priority) {
        super(settings);
        this.replace = replace;
        this.biEntityCondition = biEntityCondition;
        this.blockCondition = blockCondition;
        this.itemCondition = itemCondition;
        this.priority = priority;
    }

    public Map<Pattern, String> getReplace() {
        return this.replace;
    }

    public BiEntityCondition getBiEntityCondition() {
        return this.biEntityCondition;
    }

    public BlockCondition getBlockCondition() {
        return this.blockCondition;
    }

    public ItemCondition getItemCondition() {
        return this.itemCondition;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public @NotNull MapCodec<? extends Power> codec() {
        return CODEC;
    }


    public static final ResourceKey<LootTable> REPLACED_TABLE_KEY = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(Origins.MOD_ID, "replaced_loot_table"));
    private static final Stack<LootTable> REPLACEMENT_STACK = new Stack<>();
    private static final Stack<LootTable> BACKTRACK_STACK = new Stack<>();

    public boolean hasReplacement(ResourceKey<LootTable> lootTableKey) {
        Identifier id = lootTableKey.identifier();
        String idString = id.toString();
        for (Pattern replacement : this.replace.keySet())
            if (replacement.matcher(idString).matches())
                return true;
        return false;
    }

    public boolean doesApply(Entity owner, LootContext context) {
        Entity contextEntity = context.getParameter(LootContextParams.THIS_ENTITY);
        ItemInstance tool = context.hasParameter(LootContextParams.TOOL) ? context.getParameter(LootContextParams.TOOL) : ItemStack.EMPTY;
        ItemStack toolStack = tool instanceof ItemStack stack ? stack : ItemStack.EMPTY;
        return this.doesApply(owner, contextEntity, toolStack, BlockPos.containing(context.getParameter(LootContextParams.ORIGIN)));
    }

    public boolean doesApply(Entity owner, Entity contextEntity, ItemStack toolStack, BlockPos pos) {
        return this.itemCondition.test(owner.level(), toolStack) && this.blockCondition.test(owner.level(), pos) && this.biEntityCondition.test(owner, contextEntity);
    }

    public Optional<ResourceKey<LootTable>> getReplacement(ResourceKey<LootTable> key) {
        String id = key.identifier().toString();
        for (Map.Entry<Pattern, String> entry : this.replace.entrySet()) {
            Pattern regex = entry.getKey();
            String replacement = entry.getValue();
            Matcher matcher = regex.matcher(id);
            if (matcher.matches()) try {
                String replaced = matcher.replaceAll(replacement);
                ResourceKey<LootTable> replacedKey = ResourceKey.create(Registries.LOOT_TABLE, Identifier.parse(replaced));
                return Optional.of(replacedKey);
            } catch (IdentifierException e) {
                Origins.LOGGER.warn("Error trying to parse replacement string \"{}\": {}", replacement, e.getMessage());
            }
        }
        return Optional.empty();
    }

    //修复::Move to standalone class
    public static void clear() {
        REPLACEMENT_STACK.clear();
        BACKTRACK_STACK.clear();
    }

    public static void push(LootTable lootTable) {
        REPLACEMENT_STACK.add(lootTable);
    }

    public static LootTable pop() {
        if (REPLACEMENT_STACK.isEmpty()) return LootTable.EMPTY;
        LootTable table = REPLACEMENT_STACK.pop();
        BACKTRACK_STACK.push(table);
        return table;
    }

    public static LootTable restore() {
        if (BACKTRACK_STACK.isEmpty()) return LootTable.EMPTY;
        LootTable table = BACKTRACK_STACK.pop();
        REPLACEMENT_STACK.push(table);
        return table;
    }

    public static LootTable peek() {
        if (REPLACEMENT_STACK.isEmpty()) return LootTable.EMPTY;
        else return REPLACEMENT_STACK.peek();
    }
}
