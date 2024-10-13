package me.fortibrine.boss.api.message;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class MessageManager {

    private @NotNull ConfigurationSection section;

    public MessageManager(@NotNull ConfigurationSection section) {
        this.section = section;
    }

    public void reload(@NotNull ConfigurationSection section) {
        this.section = section;
    }

    public void sendMessage(@NotNull CommandSender sender, @NotNull Message message) {
        String path = message.getPath();
        String messageString = section.getString(path);

        if (messageString != null) {
            sender.sendMessage(
                    MiniMessage.miniMessage().deserialize(
                            messageString
                    ).asComponent()
            );
        }
    }

}
