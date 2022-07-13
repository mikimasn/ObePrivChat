package net.fabricmc.example.mixin;

import net.fabricmc.example.CloseGameCallback;
import net.fabricmc.example.ExampleMod;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin {
    @Inject(at=@At("Head"),method = "init()V")
    private void init(CallbackInfo info) throws URISyntaxException, NoSuchAlgorithmException {
        ExampleMod.LOGGER.info("Multiplayer Screen Mixin");
        ActionResult result = CloseGameCallback.EVENT.invoker().interact();
    }
}
