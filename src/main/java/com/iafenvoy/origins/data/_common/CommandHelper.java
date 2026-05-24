package com.iafenvoy.origins.data._common;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

public interface CommandHelper {
    default void executeCommand(Level level, Consumer<CommandSourceStack> consumer, String command) {
        if (level instanceof ServerLevel serverLevel) this.executeCommand(serverLevel.getServer(), consumer, command);
    }

    default void executeCommand(MinecraftServer server, Consumer<CommandSourceStack> consumer, String command) {
        CommandSourceStack stack = server.createCommandSourceStack();
        consumer.accept(stack);
        if (server.isCommandBlockEnabled()) this.executeCommand(stack, server, command);
    }

    default void executeCommand(Entity entity, String command) {
        this.executeCommand(entity, entity.position(), command);
    }

    default void executeCommand(Entity entity, Vec3 pos, String command) {
        if (entity.level() instanceof ServerLevel level)
            this.executeCommand(entity.createCommandSourceStack().withPosition(pos), level.getServer(), command);
    }

    default void executeCommand(CommandSourceStack stack, MinecraftServer server, String command) {
        if (!StringUtil.isNullOrEmpty(command))
            try {
                server.getCommands().performPrefixedCommand(stack, command);
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Execute Command in Origins Mod");
                CrashReportCategory crashreportcategory = crashreport.addCategory("Command to be executed");
                crashreportcategory.setDetail("Command", command);
                throw new ReportedException(crashreport);
            }
    }
}
