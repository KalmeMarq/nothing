package me.kalmemarq;

public record Identifier(String namespace, String path) implements Comparable<Identifier> {
    public Identifier {
        // TODO: Validate
    }

    public Identifier(String identifier) {
        this(unpackIdentifier(identifier));
    }

    private Identifier(String[] identifier) {
        this(identifier[0], identifier[1]);
    }

    @Override
    public int compareTo(Identifier o) {
        int res = this.path.compareTo(o.path);
        if (res == 0) res = this.namespace.compareTo(o.namespace);
        return res;
    }

    @Override
    public String toString() {
        return this.namespace() + ":" + this.path();
    }

    private static String[] unpackIdentifier(String identifier) {
        String[] id = { "minicraft", identifier };
        int idxSep = identifier.indexOf(":");
        if (idxSep != -1) {
            id[1] = identifier.substring(idxSep + 1);
            if (idxSep > 0) {
                id[0] = identifier.substring(0, idxSep);
            }
        }
        return id;
    }
}
