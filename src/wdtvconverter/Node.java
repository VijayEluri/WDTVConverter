package wdtvconverter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author camon
 */
public class Node {
    protected Node parent;
    protected String key;
    protected String value;
    protected Map<String, List<Node>> contents;

    public Node(String key, String value) {
        this.parent = null;
        this.key = key;
        this.value = value;
        this.contents = new HashMap<String, List<Node>>();
    }

    public void add(Node n) {
        n.setParent(this);
        List<Node> list = contents.get(n.key);
        if (list == null) list = new LinkedList<Node>();
        list.add(n);
        contents.put(n.key, list);
    }

    public List<Node> get(String s) {
        return contents.get(s);
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getParent() {
        return parent;
    }

    public String toFormatedString() {
        return toFormatedString(0);
    }

    private String toFormatedString(int level) {
        String res = "";
        for (int i = 0 ; i < level ; i++) res += "  ";
        String result = res + key + ": " + value ;
        if (contents.isEmpty()) return result;
        for (List<Node> list : contents.values()) {
            for (Node n : list) {
                result += "\n" + n.toFormatedString(level + 1);
            }
        }
        return result;
    }
}
