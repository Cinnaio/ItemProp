package com.github.cinnaio.itemProp;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public class CapturedConsoleSender implements ConsoleCommandSender {

    private final ConsoleCommandSender realSender;
    private final StringBuilder output = new StringBuilder();

    public CapturedConsoleSender() {
        this.realSender = Bukkit.getConsoleSender();
    }

    public String getOutput() {
        return output.toString();
    }

    @Override
    public void sendMessage(@NotNull String message) {
        realSender.sendMessage(message);
        output.append(message).append("\n");
    }

    @Override
    public void sendMessage(@NotNull String... messages) {
        realSender.sendMessage(messages);
        for (String msg : messages) {
            output.append(msg).append("\n");
        }
    }
    
    @Override
    public void sendMessage(@Nullable UUID sender, @NotNull String message) {
        realSender.sendMessage(sender, message);
        output.append(message).append("\n");
    }

    @Override
    public void sendMessage(@Nullable UUID sender, @NotNull String... messages) {
        realSender.sendMessage(sender, messages);
        for (String msg : messages) {
            output.append(msg).append("\n");
        }
    }

    // Delegate methods
    @Override public @NotNull Server getServer() { return realSender.getServer(); }
    @Override public @NotNull String getName() { return realSender.getName(); }
    @Override public @NotNull Spigot spigot() { return realSender.spigot(); }
    @Override public boolean isPermissionSet(@NotNull String name) { return realSender.isPermissionSet(name); }
    @Override public boolean isPermissionSet(@NotNull Permission perm) { return realSender.isPermissionSet(perm); }
    @Override public boolean hasPermission(@NotNull String name) { return realSender.hasPermission(name); }
    @Override public boolean hasPermission(@NotNull Permission perm) { return realSender.hasPermission(perm); }
    @Override public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value) { return realSender.addAttachment(plugin, name, value); }
    @Override public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin) { return realSender.addAttachment(plugin); }
    @Override public @Nullable PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value, int ticks) { return realSender.addAttachment(plugin, name, value, ticks); }
    @Override public @Nullable PermissionAttachment addAttachment(@NotNull Plugin plugin, int ticks) { return realSender.addAttachment(plugin, ticks); }
    @Override public void removeAttachment(@NotNull PermissionAttachment attachment) { realSender.removeAttachment(attachment); }
    @Override public void recalculatePermissions() { realSender.recalculatePermissions(); }
    @Override public @NotNull Set<PermissionAttachmentInfo> getEffectivePermissions() { return realSender.getEffectivePermissions(); }
    @Override public boolean isOp() { return realSender.isOp(); }
    @Override public void setOp(boolean value) { realSender.setOp(value); }
    @Override public boolean isConversing() { return realSender.isConversing(); }
    @Override public void acceptConversationInput(@NotNull String input) { realSender.acceptConversationInput(input); }
    @Override public boolean beginConversation(@NotNull Conversation conversation) { return realSender.beginConversation(conversation); }
    @Override public void abandonConversation(@NotNull Conversation conversation) { realSender.abandonConversation(conversation); }
    @Override public void abandonConversation(@NotNull Conversation conversation, @NotNull ConversationAbandonedEvent details) { realSender.abandonConversation(conversation, details); }
    @Override public void sendRawMessage(@Nullable UUID sender, @NotNull String message) { realSender.sendRawMessage(sender, message); }
    @Override public void sendRawMessage(@NotNull String message) { realSender.sendRawMessage(message); }
}
