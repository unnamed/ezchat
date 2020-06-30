package me.fixeddev.ezchat.format;

public enum ClickAction {
    EXECUTE_COMMAND(null),
    SUGGEST_COMMAND(null),
    OPEN_URL(null),
    EXECUTE(EXECUTE_COMMAND),
    SUGGEST(SUGGEST_COMMAND),
    OPEN(OPEN_URL),
    NONE(null);

    private ClickAction aliasOf;
    private ClickAction shortVersion;

    ClickAction(ClickAction aliasOf) {
        if (aliasOf == null) {
            this.aliasOf = this;
        } else {
            this.aliasOf = aliasOf;

            aliasOf.setShortVersion(this);
        }

        setShortVersion(this);
    }

    public ClickAction getAliasOf() {
        return aliasOf;
    }

    public ClickAction getShortVersion() {
        return shortVersion;
    }

    void setShortVersion(ClickAction shortVer) {
        shortVersion = shortVer;
    }
}
