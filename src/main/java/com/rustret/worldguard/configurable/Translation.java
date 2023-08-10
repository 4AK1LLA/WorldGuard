package com.rustret.worldguard.configurable;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;

public enum Translation {
    COORDINATES ("coordinates"), NOT_YOUR_REGION ("not-your-region"), PVP_NOT_ALLOWED ("pvp-not-allowed"),
    INVENTORY_IS_FULL ("inventory-is-full"), COMMAND_FOR_PLAYERS ("command-for-players"),
    PLAYER_IS_OFFLINE ("player-is-offline"), FLAG_NOT_FOUND ("flag-not-found"), SEE_REGION_HELP ("see-region-help"),
    UNACCEPTABLE_NAME_LENGTH ("unacceptable-name-length"), UNACCEPTABLE_NAME_SYMBOLS ("unacceptable-name-symbols"),
    UNACCEPTABLE_REGION_SIZE ("unacceptable-region-size"), UNACCEPTABLE_SIDE_LENGTH ("unacceptable-side-length"),
    UNACCEPTABLE_REGIONS_COUNT ("unacceptable-regions-count"), UNACCEPTABLE_MEMBERS_COUNT ("unacceptable-members-count"),
    SELECTION_IS_MISSING ("selection-is-missing"), NAME_IS_TAKEN ("name-is-taken"), REGION_NOT_FOUND ("region-not-found"),
    NOT_OWNER ("not-owner"), SELECTION_INTERSECTS_REGION ("selection-intersects-region"),
    MEMBER_ALREADY_EXIST ("member-already-exist"), MEMBER_NOT_FOUND ("member-not-found"), LEVEL_DIFFERENCE ("level-difference"),
    OWN_NO_REGIONS ("own-no-regions"), WAND_SUCCESS ("wand-success"), FLAG_SUCCESS ("flag-success"),
    CLAIM_SUCCESS ("claim-success"), DELETE_SUCCESS ("delete-success"), ADDMEMBER_SUCCESS ("addmember-success"),
    REMOVEMEMBER_SUCCESS ("removemember-success"), LIST_SUCCESS ("list-success"), FIRST_POINT ("first-point"),
    SECOND_POINT ("second-point"), PARAMETERLESS_RG_COMMAND ("parameterless-rg-command"), INFO_SUCCESS ("info-success"),
    INFO_NO_MEMBERS ("info-no-members"), HELP_SUCCESS ("help-success"), WAND_DESCRIPTION ("wand-description"),
    FLAG_DESCRIPTION ("flag-description"), RG_DESCRIPTION ("rg-description"), FLAG_USAGE ("flag-usage");

    private final String translation;

    Translation(String key) {
        translation = PluginConfig.getTranslation(key);
    }

    public void sendMessage(CommandSender sender, Object... args) {
        sender.sendMessage(String.format(translation, args));
    }

    public void sendPopup(Player player) {
        player.sendPopup(translation);
    }

    public String get(Object... args) {
        return String.format(translation, args);
    }
}
