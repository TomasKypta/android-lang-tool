package cz.tomaskypta.tools.langtool.importing.splitting;

import cz.tomaskypta.tools.langtool.CommandlineArguments;
import cz.tomaskypta.tools.langtool.importing.ImportConfig;

/**
 * Created by Tomáš Kypta on 02.10.14.
 */
public class SplittingConfig extends ImportConfig {

    public String splittingConfigFile;


    public SplittingConfig() {
    }

    public SplittingConfig(SplittingConfig other) {
        super(other);
        this.splittingConfigFile = other.splittingConfigFile;
    }

    public SplittingConfig(CommandlineArguments args) {
        super(args);
        this.splittingConfigFile = args.getSplittingConfigFile();
    }
}
