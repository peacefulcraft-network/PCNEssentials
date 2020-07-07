package net.peacefulcraft.rtp;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.bukkit.scheduler.BukkitRunnable;

import net.peacefulcraft.rtp.events.UpdateNotifier;

public class UpdateCheck extends BukkitRunnable {

  public void run() {
    URL url;
    try {
      url = new URL("https://api.github.com/repos/peacefulcraft-network/PCNEssentials/releases/latest");
      HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
      con.setRequestMethod("GET");
      con.setConnectTimeout(5000);
      con.setReadTimeout(5000);
      con.setInstanceFollowRedirects(false);
      con.connect();

      if (con.getResponseCode() != 200) {
        throw new IOException("Error contacting release API");
      }

      String resp = "";
      Scanner sc = new Scanner(url.openStream());
      while(sc.hasNextLine()) {
        resp += sc.nextLine();
      }
      sc.close();
      con.disconnect();

      JsonParser parser = new JsonParser();
      JsonObject jresp = (JsonObject) parser.parse(resp);
      String latestRelease = jresp.get("tag_name").getAsString();

      if (latestRelease.equalsIgnoreCase(PCNEssentials.release)) {
        PCNEssentials.getPluginInstance().logNotice("Update check found that you are running the latest version of PCNEssentials.");
      } else {
        String downloadUrl = jresp.get("html_url").getAsString();
        PCNEssentials.getPluginInstance().logNotice(PCNEssentials.messagePrefix + "An update for PCNEssentials is available. Download it from " + downloadUrl);

        if (UpdateNotifier.getInstance() == null) {
          UpdateNotifier playerJoinNotifier = new UpdateNotifier(downloadUrl);
          PCNEssentials.getPluginInstance().getServer().getPluginManager().registerEvents(playerJoinNotifier, PCNEssentials.getPluginInstance());
        } else {
          UpdateNotifier.setUpdateUrl(downloadUrl);
        }
      }

    } catch (IOException e) {
      PCNEssentials.getPluginInstance().logNotice("An error occured while checking for updates");
      e.printStackTrace();
    }
  }
}