package net.peacefulcraft.rtp;

// LuckPerms API (The Global Storage)
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;

// EssentialsX API (The Local Application)
import com.earth2me.essentials.Essentials;


// Bukkit Standard Imports
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class GlobalNick implements Listener {

    private final Essentials essentials;

    public GlobalNick() {
        this.essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
    }

    // 1. Sync on Join
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        LuckPerms lp = LuckPermsProvider.get();
        net.luckperms.api.model.user.User lpUser = lp.getUserManager().getUser(player.getUniqueId());

        if (lpUser != null) {
			String nick = lpUser.getCachedData().getMetaData().getMetaValue("essentials-nick");
			if (nick != null) {
				// Use the FULL PATH for Essentials User here:
				com.earth2me.essentials.User essUser = essentials.getUser(player);
				essUser.setNickname(nick);
				essUser.setDisplayNick();
			}
		}
    }
    

    // 2. Capture the command and save to LuckPerms
    @EventHandler
    public void onNickCommand(PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage().toLowerCase();
        if (msg.startsWith("/nick ") || msg.startsWith("/nickname ")) {
            
            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("PCNEssentials"), () -> {
                Player player = event.getPlayer();
                com.earth2me.essentials.User essUser = essentials.getUser(player);
                String newNick = essUser.getNickname();

                LuckPerms lp = LuckPermsProvider.get();
                net.luckperms.api.model.user.User lpUser = lp.getUserManager().getUser(player.getUniqueId());

                if (lpUser != null) {
                    // Clear old nick meta and add new one
                    lpUser.data().clear(NodeType.META.predicate(mn -> mn.getMetaKey().equals("essentials-nick")));
                    if (newNick != null) {
                        lpUser.data().add(MetaNode.builder("essentials-nick", newNick).build());
                    }
                    lp.getUserManager().saveUser(lpUser);
                }
            }, 5L); // 5 tick delay ensures Essentials finishes writing its data first
        }
    }
}
