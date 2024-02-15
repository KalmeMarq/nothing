package me.kalmemarq.common;

import java.util.Objects;

public class Identifier implements Comparable<Identifier> {
	private final String namespace;
	private final String path;

	public Identifier(String namespace, String path) {
		this.namespace = namespace;
		this.path = path;
	}

    public Identifier(String identifier) {
        this(unpackIdentifier(identifier));
    }

    private Identifier(String[] identifier) {
        this(identifier[0], identifier[1]);
    }
	
	public static Identifier of(String identifier) {
		return new Identifier(identifier);
	}

	public static Identifier of(String namespace, String path) {
		return new Identifier(namespace, path);
	}

	public String getNamespace() {
		return this.namespace;
	}

	public String getPath() {
		return this.path;
	}

	@Override
    public int compareTo(Identifier o) {
        int res = this.path.compareTo(o.path);
        if (res == 0) res = this.namespace.compareTo(o.namespace);
        return res;
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Identifier that = (Identifier) o;
		return Objects.equals(this.namespace, that.namespace) && Objects.equals(this.path, that.path);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.namespace, this.path);
	}

	@Override
	public String toString() {
		return this.namespace + ":" + this.path;
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
