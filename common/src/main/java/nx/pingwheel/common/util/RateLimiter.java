package nx.pingwheel.common.util;

import lombok.Getter;

import java.time.Duration;
import java.time.Instant;

public class RateLimiter {

	private static int onePointHeat;
	private static float oneTickCoolant;
	private static int heatMax;
	private float heatnow=0;
	@Getter
    private Instant lasttime=null;

	public static void setRates(int oph,float otc,int hm) {
		RateLimiter.onePointHeat=oph;
		RateLimiter.oneTickCoolant=otc;
		RateLimiter.heatMax=hm;
	}

	public RateLimiter() {}

	public boolean checkExceeded() {
		//是否是第一次标记
		if (lasttime==null) {
			this.lasttime=Instant.now();
			return false;
		}
		//计算与上次尝试标记之间的冷却
		Duration wtf = Duration.between(lasttime, Instant.now());
		float heatMinus1 = wtf.toMillis()/50F*oneTickCoolant;
		//过热时冷却减半
		float heatMinus = (heatnow > heatMax)?heatMinus1*0.5F:heatMinus1;
		//冷却应用于当前热度
		heatnow = Math.max(heatnow - heatMinus, 0);
		//通过当前的热度检测是否可以标记
		if (heatnow >= heatMax) {
			lasttime = Instant.now();
			return true;
		}
		//通过上述检查后，重置lasttime，返回false可标记，同时积攒一次热量
		lasttime = Instant.now();
		heatnow+=onePointHeat;
		return false;
	}

}
