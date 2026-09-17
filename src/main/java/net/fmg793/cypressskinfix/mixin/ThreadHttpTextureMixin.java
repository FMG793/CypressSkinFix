package net.fmg793.cypressskinfix.mixin;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import net.fmg793.cypressskinfix.SkinManager;
import net.minecraft.client.render.texture.HttpTexture;
import net.minecraft.client.render.texture.IHttpImageProcessor;
import net.minecraft.client.render.texture.ThreadHttpTexture;

import java.awt.Color;

@Mixin(ThreadHttpTexture.class)
public class ThreadHttpTextureMixin {
	@Shadow
	String url;
	@Shadow
	IHttpImageProcessor imageProcessor;
	@Shadow
	HttpTexture texture;
	
	@Overwrite
	public void run() {
		while(this.url.charAt(0) == 1 || this.url.charAt(0) == 2) {
			this.url = this.url.substring(1);
		}

		if(this.url == "@@") {
			this.texture.image = new BufferedImage(32, 16, 2);
			Graphics2D graphics2D13 = this.texture.image.createGraphics();
			graphics2D13.setColor(new Color(0, 0, 0));
			graphics2D13.fillRect(0, 0, 32, 16);
			graphics2D13.dispose();
		} else {
			new SkinManager(url, imageProcessor, texture);
			SkinManager.addSkin();
		}
	}
}
