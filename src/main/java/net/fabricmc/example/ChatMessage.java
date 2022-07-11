package net.fabricmc.example;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.client.gui.hud.ChatHudListener;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;

import java.util.UUID;

public class ChatMessage implements ClientChatListener {
    @Override
    public void onChatMessage(MessageType messageType,
                                   Text message,
                                   UUID senderUuid){
        ExampleMod.LOGGER.info("getted a message");
    }

}
