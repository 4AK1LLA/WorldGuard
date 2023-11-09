package com.rustret.rg;

import cn.nukkit.utils.Config;

public class Messages {
    public final String SELECTION_POS1, SELECTION_POS2, FULL_INVENTORY, WAND_SUCCESS, HELP,
            CLAIM_NAME_LENGTH, CLAIM_NAME_REGEX, CLAIM_SELECTION_MISSING, CLAIM_LEVEL_MISMATCH,
            CLAIM_SIDE_LENGTH, CLAIM_SIZE, CLAIM_REGION_EXIST, CLAIM_COUNT_LIMIT, CLAIM_INTERSECT,
            CLAIM_SUCCESS, REGION_NOT_EXIST, NOT_OWNER, DELETE_SUCCESS, MEMBER_COUNT,
            PLAYER_NOT_FOUND, MEMBER_EXIST, ADDMEMBER_SUCCESS, MEMBER_NOT_FOUND, REMOVEMEMBER_SUCCESS,
            LIST_EMPTY, LIST_START, LIST_ENTRY, LIST_DELIMITER, LIST_NO_MEMBERS, FLAG_VALUE,
            FLAG_SUCCESS, FLAG_WRONG, CLAIM_LEVEL_WRONG;

    public Messages(Config cfg) {
        SELECTION_POS1 = cfg.getString("selection.pos1");
        SELECTION_POS2 = cfg.getString("selection.pos2");
        FULL_INVENTORY = cfg.getString("wand.full-inventory");
        WAND_SUCCESS = cfg.getString("wand.success");
        HELP = cfg.getString("help");
        CLAIM_NAME_LENGTH = cfg.getString("claim.name-length");
        CLAIM_NAME_REGEX = cfg.getString("claim.name-regex");
        CLAIM_SELECTION_MISSING = cfg.getString("claim.selection-missing");
        CLAIM_LEVEL_MISMATCH = cfg.getString("claim.level-mismatch");
        CLAIM_LEVEL_WRONG = cfg.getString("claim.level-wrong");
        CLAIM_SIDE_LENGTH = cfg.getString("claim.side-length");
        CLAIM_SIZE = cfg.getString("claim.size");
        CLAIM_REGION_EXIST = cfg.getString("claim.region-exist");
        CLAIM_COUNT_LIMIT = cfg.getString("claim.count-limit");
        CLAIM_INTERSECT = cfg.getString("claim.intersect");
        CLAIM_SUCCESS = cfg.getString("claim.success");
        REGION_NOT_EXIST = cfg.getString("region-not-exist");
        NOT_OWNER = cfg.getString("region-not-owner");
        DELETE_SUCCESS = cfg.getString("delete.success");
        MEMBER_COUNT = cfg.getString("addmember.member-count");
        PLAYER_NOT_FOUND = cfg.getString("addmember.player-not-found");
        MEMBER_EXIST = cfg.getString("addmember.member-exist");
        ADDMEMBER_SUCCESS = cfg.getString("addmember.success");
        MEMBER_NOT_FOUND = cfg.getString("removemember.member-not-found");
        REMOVEMEMBER_SUCCESS = cfg.getString("removemember.success");
        LIST_EMPTY = cfg.getString("list.empty");
        LIST_START = cfg.getString("list.start");
        LIST_ENTRY = cfg.getString("list.entry");
        LIST_DELIMITER = cfg.getString("list.delimiter-members");
        LIST_NO_MEMBERS = cfg.getString("list.no-members");
        FLAG_VALUE = cfg.getString("flag.value");
        FLAG_SUCCESS = cfg.getString("flag.success");
        FLAG_WRONG = cfg.getString("flag.wrong");
    }
}
