package org.cccs.easql.domain;

/**
 * User: boycook
 * Date: 21/07/2011
 * Time: 13:45
 */
public class LinkTable {

    public final String name;
    public final String leftKey;
    public final String rightKey;

    public LinkTable(String name, String leftKey, String rightKey) {
        this.name = name;
        this.leftKey = leftKey;
        this.rightKey = rightKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LinkTable linkTable = (LinkTable) o;
        return leftKey.equals(linkTable.leftKey) && name.equals(linkTable.name) && rightKey.equals(linkTable.rightKey);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + leftKey.hashCode();
        result = 31 * result + rightKey.hashCode();
        return result;
    }
}
