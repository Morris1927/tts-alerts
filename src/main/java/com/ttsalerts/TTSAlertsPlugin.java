package com.ttsalerts;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.CommandExecuted;
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

	private final String COMMAND_STRING = "alert";
	private final String COMMAND_HEALTH = "hp";
	private final String COMMAND_SUPERIOR = "sup";

	private boolean bAlertHealth = false;
	private boolean bAlertSuperior = false;

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied hitsplatApplied)
	{
		if (!bAlertHealth) return;
		String name = client.getLocalPlayer().getName();
		if (!hitsplatApplied.getActor().getName().equalsIgnoreCase(name)) return;

		int damage = hitsplatApplied.getHitsplat().getAmount();
		if (damage > 0) {
			int hp = client.getBoostedSkillLevel(Skill.HITPOINTS) - damage;
			sendTTS(String.valueOf((hp)));
		}


	}


	@Subscribe
	public void onChatMessage(ChatMessage chatMessage) {
		if(!bAlertSuperior) return;

		String msg = chatMessage.getMessage();
		if(msg.contains("A superior foe has appeared")) sendTTS("Superior spawned");
	}

	@Subscribe
	public void onCommandExecuted(CommandExecuted commandExecuted) {
		String command = commandExecuted.getCommand();
		String args[] = commandExecuted.getArguments();

		if (command.equalsIgnoreCase(COMMAND_STRING)) {
			switch (args[0]) {
				case COMMAND_HEALTH:
					bAlertHealth = !bAlertHealth;
					client.addChatMessage(ChatMessageType.CONSOLE, "", "Alert health: " + String.valueOf(bAlertHealth), "");
					break;

				case COMMAND_SUPERIOR:
					bAlertSuperior = !bAlertSuperior;
					client.addChatMessage(ChatMessageType.CONSOLE, "", "Alert superior: " + String.valueOf(bAlertSuperior), "");
					break;
			}
		}
	}


	private static void sendTTS(String message) {
		//	String[] command = {"powershell.exe", "Add-Type -AssemblyName System.Speech; (New-Object System.Speech.Synthesis.SpeechSynthesizer).Speak('" + message +"');"};
		String[] command = {"powershell.exe", "Add-Type -AssemblyName System.Speech; $Synth = New-Object System.Speech.Synthesis.SpeechSynthesizer;" +
				"$Synth.SelectVoice('Microsoft Zira Desktop');" +
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
