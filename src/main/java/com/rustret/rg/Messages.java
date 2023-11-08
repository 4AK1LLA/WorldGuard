package com.rustret.rg;

import cn.nukkit.utils.Config;

public class Messages {
    public final String SELECTION_POS1, SELECTION_POS2, FULL_INVENTORY, WAND_SUCCESS, RG_HELP,
            CLAIM_NAME_LENGTH, CLAIM_NAME_REGEX, CLAIM_SELECTION_MISSING, CLAIM_LEVEL_MISMATCH,
            CLAIM_SIDE_LENGTH, CLAIM_SIZE;

    public Messages(Config cfg) {
        SELECTION_POS1 = cfg.getString("selection.pos1");
        SELECTION_POS2 = cfg.getString("selection.pos2");
        FULL_INVENTORY = cfg.getString("wand.full-inventory");
        WAND_SUCCESS = cfg.getString("wand.success");
        RG_HELP = cfg.getString("rg.no-parameters");
        CLAIM_NAME_LENGTH = cfg.getString("rg.claim.name-length");
        CLAIM_NAME_REGEX = cfg.getString("rg.claim.name-regex");
        CLAIM_SELECTION_MISSING = cfg.getString("rg.claim.selection-missing");
        CLAIM_LEVEL_MISMATCH = cfg.getString("rg.claim.level-mismatch");
        CLAIM_SIDE_LENGTH = cfg.getString("rg.claim.side-length");
        CLAIM_SIZE = cfg.getString("rg.claim.size");
    }
}
