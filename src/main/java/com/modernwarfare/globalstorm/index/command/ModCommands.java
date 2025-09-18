package com.modernwarfare.globalstorm.index.command;

import com.google.gson.JsonElement;
import com.modernwarfare.globalstorm.resource.JsonAssetsManager;
import com.modernwarfare.globalstorm.resource.point.PointGroupData;
import com.modernwarfare.globalstorm.resource.tools.JsonTool;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class ModCommands {

    private static final Path root = FMLPaths.GAMEDIR.get().resolve("CPoints");

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("MW").executes((context) -> {

            Map<String, List<PointGroupData>> test = JsonAssetsManager.getINSTANCE().getPointData().getAllData();
            //.getSource().sendSystemMessage(Component.literal(test.toString()));
            for (Map.Entry<String, List<PointGroupData>> entry : test.entrySet()) {
                for (PointGroupData group : entry.getValue()) {
                    context.getSource().sendSystemMessage(Component.literal(group.getName()));
                }

            }

            //context.getSource().sendSystemMessage(Component.literal(test.toString()));

            return SINGLE_SUCCESS;
        }).then(Commands.literal("test").executes(context -> {



            return 0;
        })

        ));
    };

}
