package nx.pingwheel.common.core;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;

import static nx.pingwheel.common.CommonClient.Game;

public class SoundQueueManager {
    private static final LinkedList<PFSoundInstance> soundQueue = new LinkedList<>();
    private static PFSoundInstance currentlyPlaying = null;
    private static Instant playbackStartTime = null;
    public SoundQueueManager() {
    }

    public void addSoundInstance(PFSoundInstance soundInstance) {
        if (currentlyPlaying == null) {
            // 立即播放
            startPlaying(soundInstance);
        } else {
            soundQueue.add(soundInstance);
        }
    }
    private static void startPlaying(PFSoundInstance sound) {
        currentlyPlaying = sound;
        playbackStartTime = Instant.now();
        Game.getSoundManager().play(sound);
    }
    private static void playNext() {
        if (soundQueue.isEmpty()) {
            return;
        }
        PFSoundInstance nextSound = soundQueue.poll();
        startPlaying(nextSound);
    }

    public static void clear() {
        soundQueue.clear();
        currentlyPlaying = null;
        playbackStartTime = null;
    }
    public void update() {
        if (currentlyPlaying != null) {
            // 计算已经过去的时间（毫秒）
            long elapsed = Duration.between(playbackStartTime, Instant.now()).toMillis();
            if (elapsed >= currentlyPlaying.getTime()) {
                // 当前声音播放完成
                currentlyPlaying = null;
                playbackStartTime = null;
                // 尝试播放下一个
                playNext();
            }
        }
    }
}
