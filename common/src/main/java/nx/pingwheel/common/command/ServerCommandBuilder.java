package nx.pingwheel.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import nx.pingwheel.common.config.ServerConfig;
import nx.pingwheel.common.config.ChannelMode;
import nx.pingwheel.common.resource.LanguageUtils;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ServerCommandBuilder {
	private ServerCommandBuilder() {}

	private static final ServerConfig Config = ServerConfig.HANDLER.getConfig();

	public static LiteralArgumentBuilder<CommandSourceStack> build(TriConsumer<CommandContext<CommandSourceStack>, Boolean, MutableComponent> responseHandler) {
		var langDefaultChannel = LanguageUtils.command("default_channel");
		var langPlayerTracking = LanguageUtils.command("player_tracking");
		var langRegenTime = LanguageUtils.command("regen_time");
		var langRateLimit = LanguageUtils.command("rate_limit");
		var validModes = List.of(ChannelMode.values());
		var validModeNames = validModes.stream().map(ChannelMode::toString).toList();

		var cmdDefaultChannel = LiteralArgumentBuilder.<CommandSourceStack>literal("default_channel")
			.executes((context) -> {
				var currentChannelMode = Config.getDefaultChannelMode();

				responseHandler.accept(context, true, langDefaultChannel.path("get.response")
					.get(langDefaultChannel.path("value").path(currentChannelMode.toString()).get().withStyle(ChatFormatting.YELLOW)));
				return 1;
			})
			.then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("mode_name", StringArgumentType.string())
				.suggests((context, builder) -> CompletableFuture.supplyAsync(() -> {
					for (var mode : validModeNames) {
						builder.suggest(mode);
					}
					return builder.build();
				}))
				.executes((context) -> {
					var newModeStr = context.getArgument("mode_name", String.class);
					var newMode = validModes.stream().filter(e -> e.name().equalsIgnoreCase(newModeStr)).findFirst().orElse(null);

					if (newMode == null) {
						responseHandler.accept(context, false, langDefaultChannel.path("set.reject")
							.get(String.join(" | ", validModeNames)));
						return 0;
					}

					Config.setDefaultChannelMode(newMode);
					ServerConfig.HANDLER.save();

					responseHandler.accept(context, true, langDefaultChannel.path("set.response")
						.get(langDefaultChannel.path("value").path(newMode.toString()).get().withStyle(ChatFormatting.YELLOW)));
					return 1;
				}));

		var cmdPlayerTracking = LiteralArgumentBuilder.<CommandSourceStack>literal("player_tracking")
			.executes((context) -> {
				var isPlayerTrackingEnabled = Config.isPlayerTrackingEnabled();

				responseHandler.accept(context, true, langPlayerTracking.path("get.response")
					.get(LanguageUtils.from(isPlayerTrackingEnabled).withStyle(ChatFormatting.YELLOW)));
				return 1;
			})
			.then(RequiredArgumentBuilder.<CommandSourceStack, Boolean>argument("state", BoolArgumentType.bool())
				.executes((context) -> {
					var enablePlayerTracking = context.getArgument("state", Boolean.class);

					Config.setPlayerTrackingEnabled(enablePlayerTracking);
					ServerConfig.HANDLER.save();

					responseHandler.accept(context, true, langPlayerTracking.path("set.response")
						.get(LanguageUtils.from(enablePlayerTracking).withStyle(ChatFormatting.YELLOW)));
					return 1;
				}));

		//这里暂时把之前的配置命令取消了，以后再加

//		var cmdRegenTime = LiteralArgumentBuilder.<CommandSourceStack>literal("regen_time")
//			.executes((context) -> {
//				var regenTime = Config.getMsToRegenerate();
//
//				responseHandler.accept(context, true, langRegenTime.path("get.response")
//					.get(LanguageUtils.from(regenTime).withStyle(ChatFormatting.YELLOW)));
//				return 1;
//			})
//			.then(RequiredArgumentBuilder.<CommandSourceStack, Integer>argument("time", IntegerArgumentType.integer(0))
//				.executes((context) -> {
//					var regenTime = context.getArgument("time", Integer.class);
//
//					Config.setMsToRegenerate(regenTime);
//					ServerConfig.HANDLER.save();
//
//					responseHandler.accept(context, true, langRegenTime.path("set.response")
//						.get(LanguageUtils.from(regenTime).withStyle(ChatFormatting.YELLOW)));
//					return 1;
//				}));
//
//		var cmdRateLimit = LiteralArgumentBuilder.<CommandSourceStack>literal("rate_limit")
//			.executes((context) -> {
//				var rateLimit = Config.getRateLimit();
//
//				responseHandler.accept(context, true, langRateLimit.path("get.response")
//					.get(LanguageUtils.from(rateLimit).withStyle(ChatFormatting.YELLOW)));
//				return 1;
//			})
//			.then(RequiredArgumentBuilder.<CommandSourceStack, Integer>argument("limit", IntegerArgumentType.integer(0))
//				.executes((context) -> {
//					var rateLimit = context.getArgument("limit", Integer.class);
//
//					Config.setRateLimit(rateLimit);
//					ServerConfig.HANDLER.save();
//
//					responseHandler.accept(context, true, langRateLimit.path("set.response")
//						.get(LanguageUtils.from(rateLimit).withStyle(ChatFormatting.YELLOW)));
//					return 1;
//				}));

		Command<CommandSourceStack> helpCallback = (context) -> {
			responseHandler.accept(context, true, LanguageUtils.join(
				Component.empty(),
				Component.literal("/pingwheel:server default_channel"),
				LanguageUtils.wrapped(langDefaultChannel.path("get.description").get()).withStyle(ChatFormatting.GRAY),
				Component.literal("/pingwheel:server default_channel <mode_name>"),
				LanguageUtils.wrapped(langDefaultChannel.path("set.description").get()).withStyle(ChatFormatting.GRAY),
				Component.literal("/pingwheel:server player_tracking"),
				LanguageUtils.wrapped(langPlayerTracking.path("get.description").get()).withStyle(ChatFormatting.GRAY),
				Component.literal("/pingwheel:server player_tracking true|false"),
				LanguageUtils.wrapped(langPlayerTracking.path("set.description").get()).withStyle(ChatFormatting.GRAY)
//				Component.literal("/pingwheel:server regen_time"),
//				LanguageUtils.wrapped(langRegenTime.path("get.description").get()).withStyle(ChatFormatting.GRAY),
//				Component.literal("/pingwheel:server regen_time <milliseconds>"),
//				LanguageUtils.wrapped(langRegenTime.path("set.description").get()).withStyle(ChatFormatting.GRAY),
//				Component.literal("/pingwheel:server rate_limit"),
//				LanguageUtils.wrapped(langRateLimit.path("get.description").get()).withStyle(ChatFormatting.GRAY),
//				Component.literal("/pingwheel:server rate_limit <limit>"),
//				LanguageUtils.wrapped(langRateLimit.path("set.description").get()).withStyle(ChatFormatting.GRAY)
			));
			return 1;
		};

		var cmdHelp = LiteralArgumentBuilder.<CommandSourceStack>literal("help")
			.executes(helpCallback);

		return LiteralArgumentBuilder.<CommandSourceStack>literal("pingwheel:server")
			.requires(source -> source.hasPermission(2))
			.executes(helpCallback)
			.then(cmdHelp)
			.then(cmdDefaultChannel)
			.then(cmdPlayerTracking);
//			.then(cmdRegenTime)
//			.then(cmdRateLimit);
	}
}
