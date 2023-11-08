package com.rustret.rg;

import cn.nukkit.utils.Config;

public class Messages {
    public final String SELECTION_POS1, SELECTION_POS2, FULL_INVENTORY, WAND_SUCCESS, HELP,
            CLAIM_NAME_LENGTH, CLAIM_NAME_REGEX, CLAIM_SELECTION_MISSING, CLAIM_LEVEL_MISMATCH,
            CLAIM_SIDE_LENGTH, CLAIM_SIZE, CLAIM_REGION_EXIST, CLAIM_COUNT_LIMIT, CLAIM_INTERSECT,
            CLAIM_SUCCESS;

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
        CLAIM_SIDE_LENGTH = cfg.getString("claim.side-length");
        CLAIM_SIZE = cfg.getString("claim.size");
        CLAIM_REGION_EXIST = cfg.getString("claim.region-exist");
        CLAIM_COUNT_LIMIT = cfg.getString("claim.count-limit");
        CLAIM_INTERSECT = cfg.getString("claim.intersect");
        CLAIM_SUCCESS = cfg.getString("claim.success");
    }
}
