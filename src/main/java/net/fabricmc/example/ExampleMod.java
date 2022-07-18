package net.fabricmc.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.example.config.ModConfigs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.world.WorldEvents;
import org.json.JSONObject;
import org.lwjgl.system.CallbackI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.jna.platform.mac.SystemB;
import net.minecraft.network.PacketEncoder;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

public class ExampleMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.Ą
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("obeprivchat");
	public static final String MOD_ID = "obeprivchat";
	private boolean BlockMessages = false;
	private String PrivateChat[]=new String[0];
	private MinecraftClient mc = MinecraftClient.getInstance();
	TextParser textParser = new TextParser();
	private String token = "";
	private String Websocketadress="ws://obechatgateway.herokuapp.com/";
	private WebsocketClient ws=new WebsocketClient(new URI(Websocketadress),mc);
	private String prefix = ".";

	public ExampleMod() throws URISyntaxException {
	}

	@Override
	public void onInitialize() {
		ModConfigs.registerConfigs();
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		prefix = ModConfigs.PREFIX;
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
			if(message.equals(prefix+"s")){
				LOGGER.info("changed switch");
				BlockMessages=!BlockMessages;
				if(BlockMessages)
					mc.inGameHud.addChatMessage(MessageType.SYSTEM,new LiteralText("§3Now your message are going to secret OBE server"),mc.player.getUuid());
				else
					mc.inGameHud.addChatMessage(MessageType.SYSTEM,new LiteralText("§4Now your message are going to bad admins of this server"),mc.player.getUuid());
				return ActionResult.FAIL;
			} else if (message.startsWith(prefix+"c")) {
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
			} else if (message.startsWith(prefix+"banmember")) {
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
			} else if (message.startsWith(prefix+"pick")) {
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
			else if(message.startsWith(prefix+"priv")){
				if(ws.IsLoggined){
					String parsedmessage[]=message.split(" ");
					if(parsedmessage.length>1){
						BlockMessages=true;
						String[] users = Arrays.copyOfRange(parsedmessage,1,parsedmessage.length);
						PrivateChat = users;
						String usersString ="";
						for(int i=0;i<users.length;i++){
							usersString+=" "+users[i];
							if(i+1!=users.length)
								usersString+=",";
						}
						mc.inGameHud.addChatMessage(MessageType.SYSTEM,new LiteralText("§3Now your message are going to:"+usersString),mc.player.getUuid());
					}
					else{
						PrivateChat = new String[0];
						mc.inGameHud.addChatMessage(MessageType.SYSTEM,new LiteralText("§3Now your message are going to everyone"),mc.player.getUuid());
					}

				}


				return ActionResult.FAIL;
			}
			else if(message.startsWith(prefix+"r")){
				if(ws.IsLoggined){
					String parsedmessage[]=message.split(" ",2);
					if(parsedmessage.length>1){
						message = parsedmessage[1].replace('&','§');
						JSONObject payload = new JSONObject();
						JSONObject payloaddata = new JSONObject();
						int Eventnumber=5;
						payloaddata.put("users", ws.LastPrivate);
						payload.put("e",Eventnumber);
						payloaddata.put("message",message);
						payload.put("data",payloaddata);
						LOGGER.info("Sended to server: "+payload.toString());
						ws.send(payload.toString());
					}

				}


				return ActionResult.FAIL;
			}
			else if(message.startsWith(prefix+"prefix")){
				String parsedmessage[]=message.split(" ");

				if(parsedmessage.length>1){
					prefix = parsedmessage[1];
					mc.inGameHud.addChatMessage(MessageType.SYSTEM,new LiteralText("§aYour prefix is now: "+prefix),mc.player.getUuid());
				}
				return ActionResult.FAIL;
			}

			if(BlockMessages) {
				if(ws.IsLoggined){
					message = message.replace('&','§');
					JSONObject payload = new JSONObject();
					JSONObject payloaddata = new JSONObject();
					int Eventnumber=2;
					if(PrivateChat.length>0){
						Eventnumber=5;
						payloaddata.put("users", PrivateChat);
					}
					payload.put("e",Eventnumber);
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
