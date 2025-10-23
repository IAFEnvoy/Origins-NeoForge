package com.iafenvoy.origins.data.action.builtin.block;

import com.iafenvoy.origins.data.action.BlockAction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record ExecuteCommandAction(String command) implements BlockAction {
    public static final MapCodec<ExecuteCommandAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.fieldOf("command").forGetter(ExecuteCommandAction::command)
    ).apply(i, ExecuteCommandAction::new));

    @Override
    public @NotNull MapCodec<? extends BlockAction> codec() {
        return CODEC;
    }

    @Override
    public void accept(@NotNull Level level, @NotNull BlockPos pos, @NotNull Direction direction) {
        if (level instanceof ServerLevel serverLevel) this.performCommand(serverLevel, pos, direction);
    }

    public void performCommand(ServerLevel level, BlockPos pos, Direction direction) {
        MinecraftServer minecraftserver = level.getServer();
        if (minecraftserver.isCommandBlockEnabled() && !StringUtil.isNullOrEmpty(this.command)) {
            try {
                CommandSourceStack commandsourcestack = createCommandSourceStack(level, pos, direction, Component.empty());
                minecraftserver.getCommands().performPrefixedCommand(commandsourcestack, this.command);
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Executing command");
                CrashReportCategory crashreportcategory = crashreport.addCategory("Command to be executed");
                crashreportcategory.setDetail("Command", this::command);
                crashreportcategory.setDetail("From", "Execute Command Action in Origins Mod");
                throw new ReportedException(crashreport);
            }
        }
    }

    public static CommandSourceStack createCommandSourceStack(ServerLevel level, BlockPos pos, Direction direction, Component name) {
        return new CommandSourceStack(CommandSource.NULL, Vec3.atCenterOf(pos), new Vec2(0.0F, direction.toYRot()), level, 2, name.getString(), name, level.getServer(), null);
    }
}
