package net.fabricmc.example.mixin;

import net.fabricmc.example.CloseGameCallback;
import net.fabricmc.example.ExampleMod;
import net.fabricmc.example.SendMessageCallback;
import net.minecraft.client.gui.hud.ChatHudListener;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

@Mixin(TitleScreen.class)
public class ExampleMixin {

	@Inject(at = @At("HEAD"), method = "init()V")
	private void init(CallbackInfo info) throws URISyntaxException, NoSuchAlgorithmException {
		ExampleMod.LOGGER.info("This line is printed by an example mod mixin!");
		ActionResult result = CloseGameCallback.EVENT.invoker().interact();
	}
}
