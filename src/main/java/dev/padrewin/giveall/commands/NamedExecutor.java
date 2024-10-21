package dev.padrewin.giveall.commands;

import java.util.List;
import org.bukkit.permissions.Permissible;

public interface NamedExecutor {

    /**
     * @return the name of the exectuor
     */
    String getName();

    /**
     * @return the aliases of the exectuor
     */
    List<String> getAliases();

    /**
     * Checks if a Permissible has permission for this command
     *
     * @param permissible The Permissible
     * @return true if the command can be run, false otherwise
     */
    boolean hasPermission(Permissible permissible);

}
