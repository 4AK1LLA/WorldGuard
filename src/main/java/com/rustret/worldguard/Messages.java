package com.rustret.worldguard;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

public enum Messages {
    WRONG_SYNTAX (TextFormat.RED + "Неправильный синтаксис команды " + TextFormat.WHITE + "%s"),
    FULL_INVENTORY (TextFormat.RED + "У вас заполнен инвентарь"),
    WAND (TextFormat.GREEN + "Вы получили топорик для создания региона, теперь установите две точки и пропишите " + TextFormat.WHITE + "/rg claim [название региона]"),
    NOT_PLAYER (TextFormat.RED + "Эта комманда для игроков"),
    FIRST_POS (TextFormat.GREEN + "Координаты первой точки " + TextFormat.WHITE + "%s"),
    SECOND_POS (TextFormat.GREEN + "Координаты второй точки " + TextFormat.WHITE + "%s" + TextFormat.GREEN + "\nСоздайте регион с помоцью команды " + TextFormat.WHITE + "/rg claim [название региона]"),
    RG (TextFormat.GREEN + "Посмотреть все команды привата " + TextFormat.WHITE + "/rg help"),
    RG_WRONG (TextFormat.RED + "Неизвестный параметр, посмотреть все команды привата " + TextFormat.WHITE + "/rg help"),
    RG_NAME_LENGTH (TextFormat.RED + "Название региона должно иметь от " + TextFormat.WHITE + "3" + TextFormat.RED + " до " + TextFormat.WHITE + "12" + TextFormat.RED + " символов"),
    RG_NAME_REGEX (TextFormat.RED + "Название региона должно состоять только из латинских букв " + TextFormat.WHITE + "a-z" + TextFormat.RED + " и цифр " + TextFormat.WHITE + "0-9"),
    RG_HELP (TextFormat.WHITE + "/rg claim [название региона]" + TextFormat.GREEN + " - создать регион\n" +
            TextFormat.WHITE + "/rg addmember [название региона] [ник игрока]" + TextFormat.GREEN + " - добавить игрока в регион\n" +
            TextFormat.WHITE + "/rg removemember [название региона] [ник игрока]" + TextFormat.GREEN + " - удалить игрока из региона");

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
