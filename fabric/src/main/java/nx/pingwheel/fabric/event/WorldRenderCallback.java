package nx.pingwheel.fabric.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import nx.pingwheel.common.render.WorldRenderContext;

public interface WorldRenderCallback {

	Event<WorldRenderCallback> START = EventFactory.createArrayBacked(WorldRenderCallback.class, (listeners) -> (worldRenderContext) -> {
		for (WorldRenderCallback event : listeners) {
			event.onRenderWorld(worldRenderContext);
		}
	});

	void onRenderWorld(WorldRenderContext worldRenderContext);
}
