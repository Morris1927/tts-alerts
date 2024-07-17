package com.ttsalerts;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
		name = "TTS Alerts"
)
public class TTSAlertsPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private TTSAlertsConfig config;

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied hitsplatApplied)
	{
		if (!config.bAlertHP()) return;
		String name = client.getLocalPlayer().getName();
		if (!hitsplatApplied.getActor().getName().equalsIgnoreCase(name)) return;

		int damage = hitsplatApplied.getHitsplat().getAmount();
		if (damage > 0) {
			int hp = client.getBoostedSkillLevel(Skill.HITPOINTS) - damage;
			if (hp <= config.hpThreshold()) {
				sendTTS(String.valueOf((hp)));
			}
		}


	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage) {
		if(!config.bAlertSuperior()) return;

		String msg = chatMessage.getMessage();
		if(msg.contains("A superior foe has appeared")) sendTTS("Superior spawned");
	}

	private void sendTTS(String message) {
		String[] command = {"powershell.exe", "Add-Type -AssemblyName System.Speech; $Synth = New-Object System.Speech.Synthesis.SpeechSynthesizer;" +
				"$Synth.SelectVoice('" + config.ttsVoice() + "');" +
				"$Synth.Volume = " + String.valueOf(config.ttsVolume()) + ";" +
				"$Synth.Speak('" + message +"');"};
		try {
			ProcessBuilder pb = new ProcessBuilder(command);
			Process process = pb.start();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	@Provides
	TTSAlertsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TTSAlertsConfig.class);
	}
}
