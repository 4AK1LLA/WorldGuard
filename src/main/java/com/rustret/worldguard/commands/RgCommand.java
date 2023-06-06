package com.rustret.worldguard.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;

public class RgCommand extends Command {

    public RgCommand() {
        super("rg", "Создание и редактирование регионов", "/rg");
        commandParameters.clear();

        commandParameters.put("claimCommand", new CommandParameter[] {
                CommandParameter.newEnum("claimParameter", new CommandEnum("claimOption", "claim")),
                CommandParameter.newType("regionName", CommandParamType.STRING)
        });

        commandParameters.put("addmemberCommand", new CommandParameter[] {
                CommandParameter.newEnum("addmemberParameter", new CommandEnum("addmemberOption", "addmember")),
                CommandParameter.newType("player", CommandParamType.TARGET)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        return false;
    }
}
