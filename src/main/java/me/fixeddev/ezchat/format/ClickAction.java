package me.fixeddev.ezchat.format;

public enum ClickAction {
    EXECUTE_COMMAND(null),
    EXECUTE(EXECUTE_COMMAND),
    SUGGEST_COMMAND(null),
    SUGGEST(SUGGEST_COMMAND),
    OPEN_URL(null),
    OPEN(OPEN_URL),
    NONE(null);

    private ClickAction aliasOf;
    private ClickAction shortVersion;

    ClickAction(ClickAction aliasOf){
        if(aliasOf == null){
            this.aliasOf = this;
        } else {
            this.aliasOf = aliasOf;
            aliasOf.shortVersion = this;
            shortVersion = this;
        }
    }

    public ClickAction getAliasOf() {
        return aliasOf;
    }

    public ClickAction getShortVersion() {
        return shortVersion;
    }

}
