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

import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicInteger;

//FIXME::Pick common methods
public final class CommandHelper {
    public static int executeCommand(MinecraftServer server, String command) {
        if (server.isCommandBlockEnabled() && !StringUtil.isNullOrEmpty(command)) {
            try {
                AtomicInteger successCount = new AtomicInteger();
                server.getCommands().performPrefixedCommand(server.createCommandSourceStack().withCallback((success, count) -> {
                    if (success) successCount.addAndGet(count);
                }), command);
                return successCount.get();
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Execute Command Action in Origins Mod");
                CrashReportCategory crashreportcategory = crashreport.addCategory("Command to be executed");
                crashreportcategory.setDetail("Command", command);
                throw new ReportedException(crashreport);
            }
        }
        return 0;
    }

    private static CommandSource getSource(Entity entity) {
        boolean validOutput = !(entity instanceof ServerPlayer) || ((ServerPlayer) entity).connection != null;
        return OriginsConfig.INSTANCE.general.showOutput.getValue() && validOutput ? entity : CommandSource.NULL;
    }

    public static OptionalInt executeAt(Entity entity, Vec3 position, String command) {
        MinecraftServer server = entity.level().getServer();
        if (server != null) {
            AtomicInteger successCount = new AtomicInteger();
            CommandSourceStack source = new CommandSourceStack(
                    getSource(entity),
                    position,
                    entity.getRotationVector(),
                    entity.level() instanceof ServerLevel sl ? sl : null,
                    OriginsConfig.INSTANCE.general.permissionLevel.getValue(),
                    entity.getName().getString(),
                    entity.getDisplayName(),
                    server,
                    entity).withCallback((success, count) -> {
                if (success) successCount.addAndGet(count);
            });
            server.getCommands().performPrefixedCommand(source, command);
            return OptionalInt.of(successCount.intValue());
        }
        return OptionalInt.empty();
    }
}
