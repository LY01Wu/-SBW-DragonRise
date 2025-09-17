package nx.pingwheel.common.core;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import nx.pingwheel.common.config.ClientConfig;
import nx.pingwheel.common.math.MathUtils;
import nx.pingwheel.common.math.ScreenPos;
import nx.pingwheel.common.network.PingLocationS2CPacket;
import nx.pingwheel.common.platform.IPlatformForgeService;
import nx.pingwheel.common.render.WorldRenderContext;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static nx.pingwheel.common.CommonClient.Game;
import static nx.pingwheel.common.CommonClient.SuperbWarfareLoaded;
import static nx.pingwheel.common.config.ClientConfig.*;
import static nx.pingwheel.common.resource.ResourceConstants.*;

public class PingView extends PingData {

	private static final ClientConfig CLIENT_CONFIG = ClientConfig.HANDLER.getConfig();

	private int spawnTime;
	private int age;
	@Getter
	private @Nullable PlayerInfo playerInfo;
	@Getter
	private @Nullable ItemStack itemStack;
	@Getter
	private ScreenPos screenPos;
	@Getter
	private double distance;
	@Getter
	private float scale;
	@Getter
	private Player getRenderPlayer;
	@Getter
	@Setter
	private boolean drawItemIcon=false;
	@Getter
	private int entType;
	@Getter
	@Setter
	private boolean drawVehicleIcon=false;

	private PingView(Vec3 pos, @Nullable UUID entityId, @Nullable UUID authorId, int sequence, int dimension) {
		super(pos, entityId, authorId, sequence, dimension);
	}

	public static PingView of(Vec3 pos, @Nullable UUID entityId, @Nullable UUID authorId, int sequence, int dimension) {
		return new PingView(
			pos,
			entityId,
			authorId,
			sequence,
			dimension
		);
	}

	public static PingView from(PingLocationS2CPacket packet) {
		return PingView.of(
			packet.pos(),
			packet.entity(),
			packet.author(),
			packet.sequence(),
			packet.dimension()
		);
	}

	public void update(WorldRenderContext ctx, int gameTime) {
		if (this.spawnTime == 0) {
			this.spawnTime = gameTime;
		}

		this.age = gameTime - spawnTime;

		final var connection = Game.getConnection();

		if (connection != null && this.authorId != null) {
			this.playerInfo = connection.getPlayerInfo(this.authorId);
		}

		if (this.entityId != null) {
			final var ent = GameContext.getEntity(this.entityId);

			if (ent != null) {
				if (ent.getType() == EntityType.ITEM && CLIENT_CONFIG.isItemIconVisible()) {
					this.itemStack = ((ItemEntity)ent).getItem().copy();
				}

				this.pos = ent.getPosition(ctx.tickDelta).add(0, ent.getBoundingBox().getYsize(), 0);
			}
		}

		this.screenPos = MathUtils.worldToScreen(pos, ctx.modelViewMatrix, ctx.projectionMatrix);
		this.distance = ctx.camera.getPosition().distanceTo(pos);
		this.getRenderPlayer = (Player) ctx.camera.getEntity();
		this.calculateScale();
	}

	public SoundEvent getTeamSound(LocalPlayer player,SoundEvent sound1, SoundEvent sound2, SoundEvent sound1other,SoundEvent sound2other) {
		if (player!=null && player.getTeam() != null) {
			if (player.getTeam().getName().equals("EA")) {
                    return (player.getUUID().equals(authorId)&&SuperbWarfareLoaded)?sound1other:sound1;
            }else if (player.getTeam().getName().equals("WE")) {
                    return (player.getUUID().equals(authorId)&&SuperbWarfareLoaded)?sound2:sound2other;
            }else {return PING_SOUND_EVENT;}
		}return PING_SOUND_EVENT;
	}

	public void playSound(SoundQueueManager manager) {
		if (this.dimension != GameContext.getDimension()){return;}
		LocalPlayer player = Minecraft.getInstance().player;
		final var ent = GameContext.getEntity(this.entityId);
		if (player == null || player.getTeam() == null) return;
        SoundEvent soundEvent = getTeamSound(player,
				CN_NORMAL_ID, US_NORMAL_ID,
				CN_NORMAL_RADIO_ID, US_NORMAL_RADIO_ID);
		if (this.entityId != null) {
			if (ent != null) {
				if(IPlatformForgeService.INSTANCE.isModLoaded("superbwarfare") ) {
					if((entType=IPlatformForgeService.INSTANCE.getVehicleType(ent))>=0){
						drawVehicleIcon=true;
					}
				}else{return;}
                soundEvent = switch (entType) {
                    case 0 -> getTeamSound(player,
                            CN_HELI_ID, US_HELI_ID,
                            CN_HELI_RADIO_ID, US_HELI_RADIO_ID);
                    case 1 -> getTeamSound(player,
                            CN_VEHICLE_ID, US_VEHICLE_ID,
                            CN_VEHICLE_RADIO_ID, US_VEHICLE_RADIO_ID);
                    case 2 -> getTeamSound(player,
                            CN_JET_ID, US_JET_ID,
                            CN_JET_RADIO_ID, US_JET_RADIO_ID);
                    default -> soundEvent;
                };
			}
		}PFSoundInstance pfs = new PFSoundInstance(soundEvent, player, CLIENT_CONFIG.getPingVolume() / 100f, 1f);
		manager.addSoundInstance(pfs);
//		Game.getSoundManager().play(new PFSoundInstance(soundEvent, player, CLIENT_CONFIG.getPingVolume() / 100f, 1f));
		//考虑之后做一个音频排队播放
	}

	public boolean isExpired() {
		return this.age > CLIENT_CONFIG.getPingDuration() * TPS && CLIENT_CONFIG.getPingDuration() < MAX_PING_DURATION;
	}

	public boolean isRemovable() {
		return (CLIENT_CONFIG.getCorrectionPeriod() >= MAX_CORRECTION_PERIOD || this.age > CLIENT_CONFIG.getCorrectionPeriod() * TPS) && this.distanceToCenter() < CLIENT_CONFIG.getRemoveRadius();
	}

	public float distanceToCenter() {
		if (this.screenPos == null) {
			return 0f;
		}

		final var wnd = Game.getWindow();
		final var center = new Vec2(wnd.getGuiScaledWidth() * 0.5f, wnd.getGuiScaledHeight() * 0.5f);

		return this.screenPos.distanceTo(center);
	}

	public boolean isCloserToCenter(@Nullable PingView b) {
		if (b == null) {
			return true;
		}

		return this.distanceToCenter() < b.distanceToCenter();
	}

	private void calculateScale() {
		final var scale = 2.0 / Math.pow(distance, 0.3);
		final var pingSize = CLIENT_CONFIG.getPingSize() / 100f;

		this.scale = (float)Math.max(1.0, scale) * 0.5f * pingSize;
	}
}
