package com.iafenvoy.origins.util;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtil;

import java.util.concurrent.atomic.AtomicInteger;

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
    }
}
