package nx.pingwheel.common.resource;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import static nx.pingwheel.common.Global.MOD_ID;

public class ResourceConstants {
	private ResourceConstants() {}

	private static SoundEvent soundEvent(String name){
		return SoundEvent.createVariableRangeEvent(new ResourceLocation(MOD_ID, name));
	}
	private static SoundEvent[] soundEvents(String[] names) {
		SoundEvent[] soundEvents = new SoundEvent[names.length];
		for (int i=0; i<names.length; i++) {
			SoundEvent soundEvent = soundEvent(names[i]);
			soundEvents[i] = soundEvent;
		}
		return soundEvents;
	}

	public static final ResourceLocation PING_SOUND_ID = new ResourceLocation(MOD_ID, "ping");
	public static final SoundEvent PING_SOUND_EVENT = SoundEvent.createVariableRangeEvent(PING_SOUND_ID);
	public static final ResourceLocation PING_TEXTURE_ID = new ResourceLocation(MOD_ID, "textures/ping.png");
	public static final ResourceLocation ARROW_TEXTURE_ID = new ResourceLocation(MOD_ID, "textures/arrow.png");
	public static final ResourceLocation HELI_TEXTURE_ID = new ResourceLocation(MOD_ID, "textures/icon/heli.png");
	public static final ResourceLocation APC_TEXTURE_ID = new ResourceLocation(MOD_ID, "textures/icon/apc.png");
	public static final ResourceLocation NONE_TEXTURE_ID = new ResourceLocation(MOD_ID, "textures/icon/none.png");
	public static final ResourceLocation JET_TEXTURE_ID = new ResourceLocation(MOD_ID, "textures/icon/jet.png");

	//今后可能会把无线电语音变成原音再处理

	public static final SoundEvent CN_NORMAL_ID = soundEvent("cnnormal");
	public static final SoundEvent CN_VEHICLE_ID = soundEvent("cnvehicle");
	public static final SoundEvent CN_TANK_ID = soundEvent("cntank");
	public static final SoundEvent CN_ENEMY_ID = soundEvent("cnenemy");
	public static final SoundEvent CN_APC_ID = soundEvent("cnapc");
	public static final SoundEvent CN_JET_ID = soundEvent("cnjet");
	public static final SoundEvent CN_HELI_ID = soundEvent("cnheli");
	public static final SoundEvent CN_SAM_ID = soundEvent("cnsam");
	public static final SoundEvent CN_NORMAL_RADIO_ID = soundEvent("cnradionormal");
	public static final SoundEvent CN_VEHICLE_RADIO_ID = soundEvent("cnradiovehicle");
	public static final SoundEvent CN_TANK_RADIO_ID = soundEvent("cnradiotank");
	public static final SoundEvent CN_ENEMY_RADIO_ID = soundEvent("cnradioenemy");
	public static final SoundEvent CN_APC_RADIO_ID = soundEvent("cnradioapc");
	public static final SoundEvent CN_JET_RADIO_ID = soundEvent("cnradiojet");
	public static final SoundEvent CN_HELI_RADIO_ID = soundEvent("cnradioheli");
	public static final SoundEvent CN_SAM_RADIO_ID = soundEvent("cnradiosam");

	public static final SoundEvent US_NORMAL_ID = soundEvent("usnormal");
	public static final SoundEvent US_VEHICLE_ID = soundEvent("usvehicle");
	public static final SoundEvent US_TANK_ID = soundEvent("ustank");
	public static final SoundEvent US_ENEMY_ID = soundEvent("usenemy");
	public static final SoundEvent US_APC_ID = soundEvent("usapc");
	public static final SoundEvent US_JET_ID = soundEvent("usjet");
	public static final SoundEvent US_HELI_ID = soundEvent("usheli");
	public static final SoundEvent US_SAM_ID = soundEvent("ussam");
	public static final SoundEvent US_NORMAL_RADIO_ID = soundEvent("usradionormal");
	public static final SoundEvent US_VEHICLE_RADIO_ID = soundEvent("usradiovehicle");
	public static final SoundEvent US_TANK_RADIO_ID = soundEvent("usradiotank");
	public static final SoundEvent US_ENEMY_RADIO_ID = soundEvent("usradioenemy");
	public static final SoundEvent US_APC_RADIO_ID = soundEvent("usradioapc");
	public static final SoundEvent US_JET_RADIO_ID = soundEvent("usradiojet");
	public static final SoundEvent US_HELI_RADIO_ID = soundEvent("usradioheli");
	public static final SoundEvent US_SAM_RADIO_ID = soundEvent("usradiosam");


}
