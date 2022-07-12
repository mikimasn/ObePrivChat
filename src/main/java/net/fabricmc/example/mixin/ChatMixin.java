package net.fabricmc.example.mixin;

import net.fabricmc.example.ExampleMod;
import net.fabricmc.example.SendMessageCallback;
import net.minecraft.client.gui.screen.Screen;

import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

@Mixin(Screen.class)
public class ChatMixin {
    @Inject(at = @At("HEAD"), method = "sendMessage",cancellable = true)
    private void addMessage(String message,CallbackInfo info) throws URISyntaxException, NoSuchAlgorithmException {
        ActionResult result = SendMessageCallback.EVENT.invoker().interact(message);

        if(result == ActionResult.FAIL) {
            info.cancel();
        }
    }
}
