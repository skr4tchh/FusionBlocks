package pw.fusionmine.fusionblocks.lang;

import pw.fusionmine.fusionblocks.utils.FileChecker;

public class Lang extends FileChecker {

    public Lang(String filedir) {
        super(filedir);
    }

    public void needle() {
        addParam("prefix", "<gradient:#B72D19:#B2E938>[FusionBlocks</gradient><gradient:#B2E938:#B2E938>]</gradient> ");
        addParam("nofound", "#D93E28Block not found");
        addParam("nofoundplayer", "#D93E28No found player");
        addParam("nopermission", "<gradient:#FF0000:#FFA500>No permissions</gradient>");
    }

    public String prefix(String key) {
        return getString("prefix") + getString(key);
    }

}
