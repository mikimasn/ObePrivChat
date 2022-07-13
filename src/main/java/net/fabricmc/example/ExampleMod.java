package net.fabricmc.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.example.config.ModConfigs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.world.WorldEvents;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

public class ExampleMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.Ą
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("obeprivchat");
	public static final String MOD_ID = "obeprivchat";
	private boolean BlockMessages = false;
	private MinecraftClient mc = MinecraftClient.getInstance();
	private String token = "";
	private String Websocketadress="wss://obechatgateway.herokuapp.com/";
	private WebsocketClient ws=new WebsocketClient(new URI(Websocketadress),mc);

	public ExampleMod() throws URISyntaxException {
	}

	@Override
	public void onInitialize() {
		ModConfigs.registerConfigs();
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		token = ModConfigs.TOKEN;
		LOGGER.info("Obe Private Chat sucesffuly run");
		CloseGameCallback.EVENT.register(()->{

			if(ws.isOpen()){
				LOGGER.info("Closed Websocket");
				ws.close(1000);
			}
			BlockMessages=false;
			return ActionResult.PASS;
		});
		SendMessageCallback.EVENT.register((message -> {
			LOGGER.info("Recived Message: "+message);
			if(message.startsWith("/"))
				return ActionResult.PASS;
			if(message.equals(".s")){
				LOGGER.info("changed switch");
				BlockMessages=!BlockMessages;
				if(BlockMessages)
					mc.inGameHud.addChatMessage(MessageType.SYSTEM,new LiteralText("§3Now your message are going to secret OBE server"),mc.player.getUuid());
				else
					mc.inGameHud.addChatMessage(MessageType.SYSTEM,new LiteralText("§4Now your message are going to bad admins of this server"),mc.player.getUuid());
				return ActionResult.FAIL;
			} else if (message.startsWith(".c")) {
				String parsedmessage[]=message.split(" ");
				if(ws.isOpen()){
					ws.close(4100);
				}
				else
					mc.inGameHud.addChatMessage(MessageType.SYSTEM,new LiteralText("§aTrying to connect to the server"),mc.player.getUuid());
				ws = new WebsocketClient(new URI(Websocketadress),mc);
				if(parsedmessage.length>1) {
					ws.setToken(parsedmessage[1]);
					token=parsedmessage[1];
				}else
					ws.setToken(token);

				ws.connect();
				return ActionResult.FAIL;
			} else if (message.startsWith(".b")) {
				if(ws.IsLoggined){
					if(ws.IsModarator){
						String parsedmessage[]=message.split(" ");
						if(parsedmessage.length>1){
						JSONObject payload = new JSONObject();
						JSONObject payloaddata = new JSONObject();
						payload.put("e",1);
						payloaddata.put("username",parsedmessage[1]);
						payload.put("data",payloaddata);
						LOGGER.info("Sended to server: "+payload.toString());
						ws.send(payload.toString());
						}
						else
							mc.inGameHud.addChatMessage(MessageType.SYSTEM,new LiteralText("§cNo username provided"),mc.player.getUuid());

					}

					else
						mc.inGameHud.addChatMessage(MessageType.SYSTEM,new LiteralText("§cYou are not moderator"),mc.player.getUuid());
				}
				else
					mc.inGameHud.addChatMessage(MessageType.SYSTEM,new LiteralText("§cYou are not connected to the server"),mc.player.getUuid());
				return ActionResult.FAIL;
			} else if (message.startsWith(".pick")) {
				if(ws.IsLoggined){
					message = message.replace('&','§');
					String parsedmessage[]=message.split(" ");
					if(parsedmessage.length>1){

						JSONObject payload = new JSONObject();
						JSONObject payloaddata = new JSONObject();
						payload.put("e",4);
						payloaddata.put("color",parsedmessage[1]);
						payload.put("data",payloaddata);
						LOGGER.info("Sended to server: "+payload.toString());
						ws.send(payload.toString());
					}
				}
				return ActionResult.FAIL;
			}
			/**
			else if (message.startsWith(".g")) {
				String parsedmessage[]=message.split(" ");
				if(parsedmessage.length>1){
					WebsocketClient testws = new WebsocketClient(new URI(Websocketadress),mc);
					testws.setToken(parsedmessage[1]);
					mc.inGameHud.addChatMessage(MessageType.SYSTEM,new LiteralText("§3Hash of your password: §f"+testws.token),mc.player.getUuid());
				}
				return ActionResult.FAIL;
			}
			 **/

			if(BlockMessages) {
				if(ws.IsLoggined){
					message = message.replace('&','§');
					JSONObject payload = new JSONObject();
					JSONObject payloaddata = new JSONObject();
					payload.put("e",2);
					payloaddata.put("message",message);
					payload.put("data",payloaddata);
					LOGGER.info("Sended to server: "+payload.toString());
					ws.send(payload.toString());
				}
				else
					mc.inGameHud.addChatMessage(MessageType.SYSTEM,new LiteralText("§cYou are not connected so no one will see your message"),mc.player.getUuid());
				return ActionResult.FAIL;
			}
			else
				return ActionResult.PASS;

		}));
	}
}
