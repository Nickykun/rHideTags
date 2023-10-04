package ru.rey.rhidetags;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Core extends JavaPlugin implements Listener {

    private Scoreboard board;
    private Team team;

    private String message;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        message = getConfig().getString("message");

        boardSettings();

        Bukkit.getPluginManager().registerEvents(this, this);

        if (!Bukkit.getOnlinePlayers().isEmpty())
            Bukkit.getOnlinePlayers().forEach(p -> hideName(p));

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§6rHideTags §f| §aSuccessfully enabled");
        Bukkit.getConsoleSender().sendMessage("§6rHideTags §f| §aBy: §fvk.com/omashune");
        Bukkit.getConsoleSender().sendMessage("");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        hideName(e.getPlayer());
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent e) {
        if (!(e.getRightClicked() instanceof Player)) return;

        Player player = (Player) e.getRightClicked();
        String messageWithPlaceholders = PlaceholderAPI.setPlaceholders(player, message);
        String coloredMessage = ChatColor.translateAlternateColorCodes('&', messageWithPlaceholders);
        coloredMessage = translateHexColorCodes("&#", "", coloredMessage);

        e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,
                TextComponent.fromLegacyText(coloredMessage.replace("$name", player.getDisplayName())));
    }

    private String translateHexColorCodes(String startTag, String endTag, String message) {
        final Pattern hexPattern = Pattern.compile(startTag + "([A-Fa-f0-9]{6})" + endTag);
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, ChatColor.of('#' + group).toString());
        }
        return matcher.appendTail(buffer).toString();
    }

    private void hideName(Player p) {
        team.addEntry(p.getName());
        p.setScoreboard(board);
    }

    private void boardSettings() {
        board = Bukkit.getScoreboardManager().getNewScoreboard();
        board.registerNewTeam("rHideTags");

        team = board.getTeam("rHideTags");

        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        team.setCanSeeFriendlyInvisibles(false);
    }

}
