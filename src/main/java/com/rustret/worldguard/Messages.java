package com.rustret.worldguard;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

public enum Messages {
    WRONG_SYNTAX (TextFormat.RED + "Неправильный синтаксис команды " + TextFormat.WHITE + "%s"),
    FULL_INVENTORY (TextFormat.RED + "У вас заполнен инвентарь"),
    WAND_SUCCESS (TextFormat.GREEN + "Вы получили топорик для создания региона, теперь установите две точки и пропишите " + TextFormat.WHITE + "/rg claim [название региона]"),
    NOT_PLAYER (TextFormat.RED + "Эта комманда для игроков"),
    FIRST_POS (TextFormat.GREEN + "Координаты первой точки " + TextFormat.WHITE + "%s"),
    SECOND_POS (TextFormat.GREEN + "Координаты второй точки " + TextFormat.WHITE + "%s" + TextFormat.GREEN + "\nСоздайте регион с помоцью команды " + TextFormat.WHITE + "/rg claim [название региона]");

    private final String message;

    Messages(String msg) {
        message = msg + TextFormat.RESET;
    }

    public void send(CommandSender sender, Object... args) {
        sender.sendMessage(String.format(message, args));
    }

    public void send(Player player, Object... args) {
        player.sendMessage(String.format(message, args));
    }
}
