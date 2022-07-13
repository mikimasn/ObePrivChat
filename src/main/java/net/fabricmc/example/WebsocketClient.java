package net.fabricmc.example;

import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.*;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;

public class WebsocketClient extends WebSocketClient {
    private MinecraftClient mc;
    public String token="Token";

    public boolean IsLoggined=false;
    public boolean IsModarator;
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    private TimerTask AskingInterval = new TimerTask() {
        @Override
        public void run() {
            JSONObject outgoing = new JSONObject();
            outgoing.append("e",3);
            JSONObject outdata = new JSONObject();
            outdata.append("client","Mc Mod");
            outgoing.append("data",outdata);
            WebsocketClient.super.send(outgoing.toString());
            ExampleMod.LOGGER.info("Sended hearthbeat message");
        }
    };
    private Timer AskingIntervalTimer = new Timer();
    public boolean isInterval = false;
    public WebsocketClient(URI serverUri, MinecraftClient mc) {
        super(serverUri);
        this.mc = mc;
    };


    @Override
    public void onClose(int code, String reason, boolean remote) {
        ExampleMod.LOGGER.info("Websocket cloased with code: "+code);

        if(code==4403){
            mc.inGameHud.addChatMessage(MessageType.SYSTEM,new LiteralText("§cAre you sure that you have entered the correct token and that you are authorized to connect to the server?"),mc.player.getUuid());
            mc.inGameHud.addChatMessage(MessageType.SYSTEM,new LiteralText("§4Server Closed Connection"),mc.player.getUuid());
        }
        else
            mc.inGameHud.addChatMessage(MessageType.SYSTEM,new LiteralText("§4Server Closed Connection"),mc.player.getUuid());
        if(code==4100)
            mc.inGameHud.addChatMessage(MessageType.SYSTEM,new LiteralText("§aTrying to connect to the server"),mc.player.getUuid());
        this.IsLoggined=false;
        if(isInterval){
            AskingIntervalTimer.cancel();
            isInterval=false;
        }
    }
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        ExampleMod.LOGGER.info("Websocket opened");
        mc.inGameHud.addChatMessage(MessageType.SYSTEM,new LiteralText("§2Successfully connected to the server"),mc.player.getUuid());
    }
    @Override
    public void onMessage(String message) {
        ExampleMod.LOGGER.info("Recived message: "+message);
        JSONObject msg = new JSONObject(message);
        int event = msg.getInt("e");
        JSONObject data = msg.getJSONObject("data");

        if(event==0){
            AskingIntervalTimer.scheduleAtFixedRate(AskingInterval,0,data.getInt("interval")-20000);
            isInterval = true;
            JSONObject LogginMessage = new JSONObject();
            JSONObject LogginData = new JSONObject();
            LogginMessage.put("e",0);
            LogginData.put("token",token);
            LogginMessage.put("data",LogginData);
            this.send(LogginMessage.toString());
            ExampleMod.LOGGER.info("Sended Login message: "+LogginMessage.toString());
        } else if (event==1) {
            ExampleMod.LOGGER.info("Recived user joined message: "+data.getString("username"));
            if(this.IsLoggined)
                mc.inGameHud.addChatMessage(MessageType.SYSTEM,new LiteralText("§bUser §e"+data.getString("username")+" §bjoined the chat"),mc.player.getUuid());
        } else if (event==2) {
            ExampleMod.LOGGER.info("Recived Message: "+data.getString("message")+" Sended By: "+data.getString("message"));
            if(this.IsLoggined)
                mc.inGameHud.addChatMessage(MessageType.SYSTEM,new LiteralText("§b["+data.getString("username")+"] §f"+data.getString("message")),mc.player.getUuid());

        } else if (event==3) {
            ExampleMod.LOGGER.info("Recived user left message: "+data.getString("username"));
            if(this.IsLoggined)
                mc.inGameHud.addChatMessage(MessageType.SYSTEM,new LiteralText("§bUser §e"+data.getString("username")+" §bleft the chat"),mc.player.getUuid());
        } else if (event==4) {
            JSONObject userData = data.getJSONObject("user");
            mc.inGameHud.addChatMessage(MessageType.SYSTEM,new LiteralText("§bLoginned as §e"+userData.getString("Name")),mc.player.getUuid());
            JSONArray clients = data.getJSONArray("clients");
            String textToChat = "§bUsers connected to chat:";
            for(int i=0;i<clients.length();i++){
                textToChat+=" §e"+clients.getString(i);
                if(i+1!=clients.length())
                    textToChat+="§f,";
            }

            mc.inGameHud.addChatMessage(MessageType.SYSTEM,new LiteralText(textToChat),mc.player.getUuid());
            this.IsLoggined=true;
            this.IsModarator=userData.getBoolean("moderator");
        }

    }
    @Override
    public void onError(Exception ex) {

    }
    public void setToken(String token) throws NoSuchAlgorithmException {
        /*
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(
                token.getBytes(StandardCharsets.UTF_8));
        this.token=bytesToHex(encodedhash);
         */
        this.token=token;
    }
}
