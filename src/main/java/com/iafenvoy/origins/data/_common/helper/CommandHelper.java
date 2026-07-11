package com.iafenvoy.origins.data._common.helper;

import com.iafenvoy.origins.config.OriginsConfig;
import it.unimi.dsi.fastutil.booleans.BooleanIntPair;
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

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public interface CommandHelper {
    BooleanIntPair NO_OP = BooleanIntPair.of(false, 0);

    //Server execute
    default BooleanIntPair executeCommand(Level level, Consumer<CommandSourceStack> consumer, String command) {
        return level instanceof ServerLevel serverLevel ? this.executeCommand(serverLevel.getServer(), consumer, command) : NO_OP;
    }

    default BooleanIntPair executeCommand(MinecraftServer server, Consumer<CommandSourceStack> consumer, String command) {
        CommandSourceStack stack = server.createCommandSourceStack();
        consumer.accept(stack);
        return server.isCommandBlockEnabled() ? this.executeCommand(stack, server, command) : NO_OP;
    }

    //Entity execute
    default BooleanIntPair executeCommand(Entity entity, String command) {
        return this.executeCommand(entity, entity.position(), command);
    }

    default BooleanIntPair executeCommand(Entity entity, Vec3 pos, String command) {
        if (entity.level() instanceof ServerLevel level)
            this.executeCommand(entity.createCommandSourceStack().withPosition(pos).withPermission(OriginsConfig.INSTANCE.general.permissionLevel.getValue()).withSuppressedOutput(), level.getServer(), command);
        return NO_OP;
    }

    //Common
    default BooleanIntPair executeCommand(CommandSourceStack stack, MinecraftServer server, String command) {
        if (!StringUtil.isNullOrEmpty(command))
            try {
                AtomicReference<BooleanIntPair> reference = new AtomicReference<>(NO_OP);
                server.getCommands().performPrefixedCommand(stack.withCallback((success, result) -> reference.set(BooleanIntPair.of(success, result))), command);
                return reference.get();
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Execute Command in Origins Mod");
                CrashReportCategory crashreportcategory = crashreport.addCategory("Command to be executed");
                crashreportcategory.setDetail("Command", command);
                throw new ReportedException(crashreport);
            }
        return NO_OP;
    }
}
