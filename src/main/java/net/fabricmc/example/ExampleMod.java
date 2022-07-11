package net.fabricmc.example;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.network.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class ExampleMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.Ą
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("obeprivchat");
	public static final ClientChatListener CHAT_LISTENER = new ChatMessage();
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("Obe Private Chat sucesffuly run");
	}
}
