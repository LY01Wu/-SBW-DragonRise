package nx.pingwheel.fabric.platform;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.gui.GuiGraphics;
import nx.pingwheel.common.platform.IPlatformClientEventService;
import nx.pingwheel.common.render.WorldRenderContext;
import nx.pingwheel.fabric.event.GuiRenderCallback;
import nx.pingwheel.fabric.event.WorldRenderCallback;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PlatformClientEventServiceImpl implements IPlatformClientEventService {

	@Override
	public void registerTickStartEvent(Runnable callback) {
		ClientTickEvents.START_CLIENT_TICK.register(client -> callback.run());
	}

	@Override
	public void registerJoinServerEvent(Runnable callback) {
		ClientPlayConnectionEvents.JOIN.register((a, b, c) -> callback.run());
	}

	@Override
	public void registerLeaveServerEvent(Runnable callback) {
		ClientPlayConnectionEvents.DISCONNECT.register((a, b) -> callback.run());
	}

	@Override
	public void registerRenderWorldEvent(Consumer<WorldRenderContext> callback) {
		WorldRenderCallback.START.register(callback::accept);
	}

	@Override
	public void registerRenderGUIEvent(BiConsumer<GuiGraphics, Float> callback) {
		GuiRenderCallback.START.register(callback::accept);
	}

	@Override
	public void updateSoundQueue() {

	}
}
