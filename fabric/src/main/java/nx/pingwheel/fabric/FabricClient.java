package nx.pingwheel.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import nx.pingwheel.common.CommonClient;
import nx.pingwheel.common.command.ClientCommandBuilder;
import nx.pingwheel.common.network.PingLocationS2CPacket;
import nx.pingwheel.common.resource.LanguageUtils;
import nx.pingwheel.common.resource.ResourceReloadListener;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static nx.pingwheel.common.Global.MOD_ID;
import static nx.pingwheel.common.resource.ResourceConstants.PING_SOUND_EVENT;
import static nx.pingwheel.common.resource.ResourceConstants.PING_SOUND_ID;

@Environment(EnvType.CLIENT)
public class FabricClient implements ClientModInitializer {

	public static final ResourceLocation RELOAD_LISTENER_ID = new ResourceLocation(MOD_ID, "reload-listener");

	@Override
	public void onInitializeClient() {
		CommonClient.INSTANCE.onInit();

		Registry.register(BuiltInRegistries.SOUND_EVENT, PING_SOUND_ID, PING_SOUND_EVENT);

		// packets
		ClientPlayNetworking.registerGlobalReceiver(
			PingLocationS2CPacket.PACKET_ID,
			(a, b, packet, c)
				-> CommonClient.INSTANCE.onPingLocationPacket(PingLocationS2CPacket.readSafe(packet))
		);

		// resource reload
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES)
			.registerReloadListener(new IdentifiableResourceReloadListener() {
				@Override
				public ResourceLocation getFabricId() {
					return RELOAD_LISTENER_ID;
				}

				@Override
				public CompletableFuture<Void> reload(PreparationBarrier helper, ResourceManager resourceManager, ProfilerFiller loadProfiler, ProfilerFiller applyProfiler, Executor loadExecutor, Executor applyExecutor) {
					return ResourceReloadListener.reloadTextures(helper, resourceManager, loadExecutor, applyExecutor);
				}
			});

		// commands
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(ClientCommandBuilder.build((context, success, response) -> {
				if (success) {
					context.getSource().sendFeedback(LanguageUtils.withModPrefix(response));
				} else {
					context.getSource().sendError(LanguageUtils.withModPrefix(response));
				}
			}));
		});
	}
}
