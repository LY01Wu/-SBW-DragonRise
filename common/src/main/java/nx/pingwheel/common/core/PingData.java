package nx.pingwheel.common.core;

import lombok.Setter;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class PingData {

	@Setter
	protected Vec3 pos;
	public final @Nullable UUID entityId;
	public final @Nullable UUID authorId;
	public final int sequence;
	public final int dimension;

	protected PingData(Vec3 pos, @Nullable UUID entityId, @Nullable UUID authorId, int sequence, int dimension) {
		this.pos = pos;
		this.entityId = entityId;
		this.authorId = authorId;
		this.sequence = sequence;
		this.dimension = dimension;
	}
}
