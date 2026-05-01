package com.iafenvoy.origins.util;

import com.iafenvoy.origins.config.OriginsConfig;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.atomic.AtomicInteger;

public final class CommandHelper {
    public static void executeCommand(MinecraftServer server, String command) {
        if (server.isCommandBlockEnabled() && !StringUtil.isNullOrEmpty(command))
            try {
                server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), command);
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Execute Command Action in Origins Mod");
                CrashReportCategory crashreportcategory = crashreport.addCategory("Command to be executed");
                crashreportcategory.setDetail("Command", command);
                throw new ReportedException(crashreport);
            }
    }

    public static void executeAt(Entity entity, Vec3 position, String command) {
        MinecraftServer server = entity.level().getServer();
        if (server != null && entity.level() instanceof ServerLevel level&& !StringUtil.isNullOrEmpty(command))
            server.getCommands().performPrefixedCommand(new CommandSourceStack(CommandSource.NULL, position, entity.getRotationVector(), level, OriginsConfig.INSTANCE.general.permissionLevel.getValue(), entity.getName().getString(), entity.getDisplayName(), server, entity), command);
    }
}
