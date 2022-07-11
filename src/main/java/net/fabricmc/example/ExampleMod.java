package net.fabricmc.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.network.C2SPacketTypeCallback;
import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.network.MessageType;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class ExampleMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.Ą
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("obeprivchat");
	private boolean BlockMessages = false;
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("Obe Private Chat sucesffuly run");
		SendMessageCallback.EVENT.register((message -> {
			LOGGER.info("Recived Message: "+message);
			if(message.equals(".s")){
				LOGGER.info("changed switch");
				BlockMessages=!BlockMessages;
			}

			if(BlockMessages)
				return ActionResult.FAIL;
			else
				return ActionResult.PASS;

		}));
	}
}
