package net.fmg793.cypressskinfix;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import ext.util.Ext1605Utils;
import net.minecraft.client.render.texture.HttpTexture;
import net.minecraft.client.render.texture.IHttpImageProcessor;

public class SkinManager {
	public static String playerName;
	public static IHttpImageProcessor imageProcessor;
	public static HttpTexture texture;
	
	public SkinManager(String url1, IHttpImageProcessor imageProcessor1, HttpTexture texture1) {
		playerName = url1;
		imageProcessor = imageProcessor1;
		texture = texture1;
	}
	
	public static void addSkin() {
		HttpURLConnection skinURLConnection = null;
		try {
			URL uuidURL = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
			JSONTokener uuidTokener = new JSONTokener(uuidURL.openStream());
			JSONObject uuidRoot = new JSONObject(uuidTokener);
			String uuid = uuidRoot.getString("id");
			URL profileURL = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
			JSONTokener profileTokener = new JSONTokener(profileURL.openStream());
			JSONObject profileRoot = new JSONObject(profileTokener);
			JSONArray propertiesArr = profileRoot.getJSONArray("properties");
			String texturesB64 = propertiesArr.getJSONObject(propertiesArr.length() - 1).getString("value");
			JSONObject decodedSkinURLJson = new JSONObject(new String(Base64.getDecoder().decode(texturesB64)));
			JSONObject skinArr = decodedSkinURLJson.getJSONObject("textures").getJSONObject("SKIN");
			URL skinURL = new URL(skinArr.getString("url"));
			
			boolean isSlim = false;
            if (skinArr.has("metadata")) {
            	JSONObject metadata = skinArr.getJSONObject("metadata");
                if ("slim".equals(metadata.optString("model", ""))) isSlim = true;
            }
			skinURLConnection = (HttpURLConnection)skinURL.openConnection();
			skinURLConnection.setDoInput(true);
			skinURLConnection.setDoOutput(false);
			skinURLConnection.connect();
            InputStream in = null;
            ByteArrayOutputStream out = null;
            try {
                in = skinURLConnection.getInputStream();
                BufferedImage image = ImageIO.read(in);
                int s = Math.max(1, image.getWidth() / 64);
                Graphics2D graphics = image.createGraphics();
                graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

                BufferedImage subimage;

                if (image.getHeight() > 32 * s) {
                    subimage = image.getSubimage(0, 32 * s, 56 * s, 16 * s);
                    graphics.drawImage(subimage, 0, 16 * s, null);
                }

                if (isSlim) {
                    subimage = image.getSubimage(45 * s, 16 * s, 9 * s, 16 * s);
                    graphics.drawImage(subimage, 46 * s, 16 * s, null);

                    subimage = image.getSubimage(49 * s, 16 * s, 2 * s, 4 * s);
                    graphics.drawImage(subimage, 50 * s, 16 * s, null);

                    subimage = image.getSubimage(53 * s, 20 * s, 2 * s, 12 * s);
                    graphics.drawImage(subimage, 54 * s, 20 * s, null);
                }

                graphics.dispose();

                out = new ByteArrayOutputStream();
                image = image.getSubimage(0, 0, 64 * s, 32 * s);
                ImageIO.write(image, "png", out);
            } finally {
                if (in != null) in.close();
            }
               
			if (imageProcessor == null) {
				texture.image = ImageIO.read(new ByteArrayInputStream(out.toByteArray()));
			} else {
				texture.image = imageProcessor.process(ImageIO.read(new ByteArrayInputStream(out.toByteArray())));
			}
		} catch (Exception exception11) {
			System.out.println("Failed to load web skin! Trying to Load local skin...");
			
			String string2 = Ext1605Utils.skinUrls.get(0);
			String string3 = string2 + playerName + ".png";
			try {
				texture.image = ImageIO.read(new File(string3.substring(7)));
			} catch (IOException iOException10) {
				System.out.println("Failed to load skin from " + string3);
			}
		} finally {
			if (skinURLConnection != null) {
				skinURLConnection.disconnect();
			}
		}

		if(texture.image == null) {
			System.out.println("Failed to load skin.");
		} else {
			texture.initialize();
		}
	}
}
