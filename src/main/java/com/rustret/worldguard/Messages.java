package com.rustret.worldguard;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

public enum Messages {
    WRONG_SYNTAX ("§c\u2718 Неправильный синтаксис команды §f%s"),
    FULL_INVENTORY ("§c\u2718 У вас заполнен инвентарь"),
    WAND ("§a\u2714 Вы получили топорик для создания региона, теперь установите две точки и пропишите §f/rg claim [название региона]"),
    NOT_PLAYER ("§c\u2718 Эта комманда для игроков"),
    FIRST_POS ("§a\u2714 Координаты первой точки §fX %d §8|§f Y %d §8|§f Z %d"),
    SECOND_POS ("§a\u2714 Координаты второй точки §fX %d §8|§f Y %d §8|§f Z %d\n" +
            "§aСоздайте регион с помоцью команды §f/rg claim [название региона]"),
    RG ("§a\u2714 Посмотреть все команды привата §f/rg help"),
    RG_WRONG ("§c\u2718 Неизвестный параметр, посмотреть все команды привата §f/rg help"),
    RG_NAME_LENGTH ("§c\u2718 Название региона должно иметь от §f3§c до §f12§c символов"),
    RG_NAME_REGEX ("§c\u2718 Название региона должно состоять только из латинских букв §fa-z§c и цифр §f0-9"),
    MISSIING_SELECTION ("§c\u2718 Для создания региона нужно выделить §f2 точки§c с помощью топора §f/wand"),
    RG_HELP ("§f/wand§a - получить топорик для создания региона\n" +
            "§f/rg claim [название региона]§a - создать регион\n" +
            "§f/rg addmember [название региона] [ник игрока]§a - добавить игрока в регион\n" +
            "§f/rg removemember [название региона] [ник игрока]§a - удалить игрока из региона");

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
