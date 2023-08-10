package com.rustret.worldguard;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;

public enum Messages {
    FOREIGN_RG ("rg-foreign"),
    NO_PVP ("rg-no-pvp"),
    COORDINATES ("coordinates"),
    WRONG_SYNTAX ("wrong-syntax-error"),
    FULL_INVENTORY ("full-inventory-error"),
    WAND ("wand-success"),
    NOT_PLAYER ("not-player-error"),
    PLAYER_OFFLINE ("player-offline-error"),
    FLAG_INVALID ("flag-invalid-error"),
    FLAG_SUCCESS ("flag-success"),
    FIRST_POS ("rg-first-point"),
    SECOND_POS ("rg-second-point"),
    RG ("rg"),
    RG_WRONG ("rg-parameter-error"),
    RG_NAME_LENGTH ("rg-name-length-error"),
    RG_NAME_REGEX ("rg-name-regex-error"),
    MISSIING_SELECTION ("rg-selection-missing-error"),
    RG_SIZE_LIMIT ("rg-size-error"),
    RG_SIDE_LIMIT ("rg-side-length-error"),
    RG_CLAIM ("rg-claim-success"),
    RG_EXIST ("rg-name-exist-error"),
    RG_NOT_EXIST ("rg-not-exist-error"),
    RG_NOT_OWNER ("rg-not-owner-error"),
    RG_DELETE ("rg-delete-success"),
    RG_INTERSECT ("rg-intersect-error"),
    RG_COUNT_LIMIT ("rg-count-error"),
    MEMBERS_LIMIT ("rg-member-count-error"),
    ADDMEMBER_SUCCESS ("rg-addmember-success"),
    MEMBER_EXIST ("rg-member-exist-error"),
    MEMBER_NOT_EXIST ("rg-member-not-exist-error"),
    REMOVEMEMBER_SUCCESS ("rg-removemember-success"),
    LEVEL_DIFFERENCE ("rg-level-error"),
    REGIONS_EMPTY ("rg-empty-error"),
    RG_LIST ("rg-list-success"),
    RG_INFO ("rg-info-success"),
    RG_HELP ("rg-help-success"),
    RG_NO_MEMBERS ("rg-no-members");

    private final String message;

    Messages(String configSection) {
        message = PluginConfig.getMessage(configSection);
    }

    public void send(CommandSender sender, Object... args) {
        sender.sendMessage(String.format(message, args));
    }

    public void sendPopup(Player player) {
        player.sendPopup(message);
    }

    public String get(Object... args) {
        return String.format(message, args);
    }
}
