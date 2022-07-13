package net.fabricmc.example;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

public interface CloseGameCallback {
    Event<CloseGameCallback> EVENT = EventFactory.createArrayBacked(CloseGameCallback.class,(listiners)->()->{
        for(CloseGameCallback listener:listiners){
            ActionResult result = listener.interact();

        }
        return ActionResult.PASS;
    });
    ActionResult interact() throws URISyntaxException, NoSuchAlgorithmException;
}
