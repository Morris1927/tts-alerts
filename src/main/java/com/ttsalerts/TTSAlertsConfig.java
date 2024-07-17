package com.ttsalerts;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("ttsalerts")
public interface TTSAlertsConfig extends Config
{

	@Range( max = 100 )
	@ConfigItem(
			keyName = "ttsVolume",
			name = "TTS Volume",
			description = "Volume of the TTS alerts",
			position = 1
	)
	default int ttsVolume()
	{
		return 50;
	}

	@Range( max = 99 )
	@ConfigItem(
			keyName = "hpThreshold",
			name = "HP Threshold",
			description = "Threshold which prevents alerts above desired HP",
			position = 2
	)
	default int hpThreshold()
	{
		return 99;
	}

	@ConfigItem(
			keyName = "bAlertHP",
			name = "Alert HP",
			description = "Alerts user on receiving a hitsplat bigger than 0",
			position = 3
	)
	default Boolean bAlertHP()
	{
		return false;
	}

	@ConfigItem(
			keyName = "bAlertSuperior",
			name = "Alert Superior",
			description = "Alerts user on superior spawning",
			position = 4
	)
	default Boolean bAlertSuperior()
	{
		return false;
	}
}
