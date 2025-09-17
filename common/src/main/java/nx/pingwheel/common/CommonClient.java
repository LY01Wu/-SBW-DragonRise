package nx.pingwheel.common;

import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import nx.pingwheel.common.config.ClientConfig;
import nx.pingwheel.common.core.GameContext;
import nx.pingwheel.common.core.PingController;
import nx.pingwheel.common.core.PingManager;
import nx.pingwheel.common.network.PingLocationS2CPacket;
import nx.pingwheel.common.network.UpdateChannelC2SPacket;
import nx.pingwheel.common.platform.IPlatformClientEventService;
import nx.pingwheel.common.platform.IPlatformContextService;
import nx.pingwheel.common.platform.IPlatformNetworkService;
import nx.pingwheel.common.render.OverlayRenderer;
import nx.pingwheel.common.render.WorldRenderContext;
import nx.pingwheel.common.screen.SettingsScreen;
import nx.pingwheel.common.util.InputUtils;

import java.lang.reflect.InvocationTargetException;

import static nx.pingwheel.common.util.InputUtils.*;

public class CommonClient {

	public static final CommonClient INSTANCE = new CommonClient();
	public static Minecraft Game = null;
	public static boolean DistantHorizonsLoaded = false;
	public static boolean SuperbWarfareLoaded  =  false;

	private CommonClient() {}

	@SneakyThrows
    public void onInit() {
		ClientConfig.HANDLER.load();

		IPlatformClientEventService.INSTANCE.registerTickStartEvent(this::onTickStart);
		IPlatformClientEventService.INSTANCE.registerJoinServerEvent(this::onJoinServer);
		IPlatformClientEventService.INSTANCE.registerLeaveServerEvent(this::onLeaveServer);
		IPlatformClientEventService.INSTANCE.registerRenderWorldEvent(this::onRenderWorld);
		IPlatformClientEventService.INSTANCE.registerRenderGUIEvent(this::onRenderGUI);

		IPlatformContextService.INSTANCE.registerKeyMapping(KEY_BINDING_PING);
		IPlatformContextService.INSTANCE.registerKeyMapping(KEY_BINDING_SETTINGS);
		IPlatformContextService.INSTANCE.registerKeyMapping(KEY_BINDING_NAME_LABELS);

		DistantHorizonsLoaded = IPlatformContextService.INSTANCE.isModLoaded("distanthorizons");
		SuperbWarfareLoaded  =  IPlatformContextService.INSTANCE.isModLoaded("superbwarfare");
	}

	public void onJoinServer() {
		IPlatformNetworkService.INSTANCE.sendToServer(new UpdateChannelC2SPacket(ClientConfig.HANDLER.getConfig().getChannel()));
	}

	public void onLeaveServer() {
		PingManager.clearPings();
	}

	public void onTickStart() {
		Game = Minecraft.getInstance();
		GameContext.updateDimension();

		if (InputUtils.consumePingHotkey()) {
			PingController.queuePingAction();
		}

		if (KEY_BINDING_SETTINGS.consumeClick()) {
			Game.setScreen(new SettingsScreen());
		}
	}

	public void onRenderWorld(WorldRenderContext ctx) {
		PingManager.updatePings(ctx);
		PingController.pollPingAction(ctx.tickDelta);
	}

	public void onRenderGUI(GuiGraphics guiGraphics, float tickDelta){
		OverlayRenderer.draw(guiGraphics, tickDelta);
	}

	public void onPingLocationPacket(PingLocationS2CPacket packet) {
		PingManager.acceptPingPacket(packet);
	}
}
