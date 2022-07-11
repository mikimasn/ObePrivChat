package net.fabricmc.example;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public interface SendMessageCallback {
    Event<SendMessageCallback> EVENT = EventFactory.createArrayBacked(SendMessageCallback.class,(listiners)->(message)->{
        for(SendMessageCallback listener:listiners){
            ActionResult result = listener.interact(message);

            if(result != ActionResult.PASS) {
                return result;
            }

        }
        return ActionResult.PASS;
    });
    ActionResult interact(String message);
}
