package com.iafenvoy.origins.util;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtil;

public class CommandHelper {
    public static void executeCommand(MinecraftServer server, String command) {
        if (server.isCommandBlockEnabled() && !StringUtil.isNullOrEmpty(command)) {
            try {
                server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), command);
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Execute Command Action in Origins Mod");
                CrashReportCategory crashreportcategory = crashreport.addCategory("Command to be executed");
                crashreportcategory.setDetail("Command", command);
                throw new ReportedException(crashreport);
            }
        }
    }
}
