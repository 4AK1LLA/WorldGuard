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
            "§aСоздайте регион с помощью команды §f/rg claim [название региона]"),
    RG ("§a\u2714 Посмотреть все команды привата §f/rg help"),
    RG_WRONG ("§c\u2718 Неизвестный параметр, посмотреть все команды привата §f/rg help"),
    RG_NAME_LENGTH ("§c\u2718 Название региона может иметь от §f3§c до §f12§c символов"),
    RG_NAME_REGEX ("§c\u2718 Название региона может состоять только из маленьких латинских букв §fa-z§c и цифр §f0-9"),
    MISSIING_SELECTION ("§c\u2718 Вы не установили §fдве точки§c с помощью топора §f/wand"),
    RG_SIZE_LIMIT ("§c\u2718 Максимальный размер региона §f50.000§c блоков"),
    RG_SIDE_LIMIT ("§c\u2718 Максимальная длина, ширина и высота региона §f50§c блоков"),
    RG_CLAIM ("§a\u2714 Регион с названием §f%s§a успешно создан, его размер составляет §f%d§a блоков"),
    RG_EXIST ("§c\u2718 Регион с названием §f%s§c уже существует"),
    RG_NOT_EXIST ("§c\u2718 Региона с названием §f%s§c не существует"),
    RG_NOT_OWNER ("§c\u2718 Вы не являетесь владельцем этого региона"),
    RG_DELETE ("§a\u2714 Регион §f%s§a был успешно удален"),
    RG_INTERSECT ("§c\u2718 Ваш регион пересекается с другим регионом"),
    FOREIGN_RG ("§cЭто чужой регион"),
    NO_PVP ("§cВ этом регионе отключен пвп"),
    RG_COUNT_LIMIT ("§c\u2718 Вы достигли ограничения по количеству регионов"),
    FLAG_INVALID ("§c\u2718 Флаг §f%s§c не существует"),
    FLAG_SUCCESS ("§a\u2714 Значение флага §f%s§a для региона §f%s§a успешно изменено на §f%s"),
    PLAYER_OFFLINE ("§c\u2718 Этого игрока сейчас нет на сервере"),
    MEMBERS_LIMIT ("§c\u2718 В регионе максимальное количетсво участников"),
    MEMBER_EXIST ("§c\u2718 Этот игрок уже в регионе"),
    ADDMEMBER_SUCCESS ("§a\u2714 Вы успешно добавили игрока §f%s§a в регион §f%s"),
    RG_HELP ("§f/wand§a - получить топорик для создания региона\n" +
            "§f/rg claim [название региона]§a - создать регион\n" +
            "§f/rg delete [название региона]§a - удалить регион\n" +
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

    public void sendPopup(Player player) {
        player.sendPopup(message);
    }
}
