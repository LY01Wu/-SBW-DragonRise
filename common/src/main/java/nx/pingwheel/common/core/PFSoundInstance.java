package nx.pingwheel.common.core;

import lombok.Getter;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

public class PFSoundInstance extends AbstractTickableSoundInstance {
    private final Entity entityToFollow;
    private final float volume;
    private final float pitch;
    @Getter
    private float time;
    protected PFSoundInstance(SoundEvent sound, Entity entityToFollow, float volume, float pitch) {
        super(sound, SoundSource.AMBIENT, entityToFollow.getCommandSenderWorld().getRandom());
        this.entityToFollow = entityToFollow;
        this.volume = volume;
        this.pitch = pitch;
        this.time = 100f;
    }

    @Override
    public void tick() {
    }

    @Override
    public float getVolume() {
        return volume;
    }

    @Override
    public float getPitch() {
        return pitch;
    }

    @Override
    public double getX() {
        return entityToFollow != null ? entityToFollow.getX() : 0;
    }

    @Override
    public double getY() {
        return entityToFollow != null ? entityToFollow.getY() : 0;
    }

    @Override
    public double getZ() {
        return entityToFollow != null ? entityToFollow.getZ() : 0;
    }

    @Override
    public boolean canStartSilent() {
        return false;
    }

    @Override
    public boolean isRelative() {
        return false; // 使用绝对坐标
    }
}
