package xmu.wrxlab.abuilder.utils;

/** 自己写个简单的json */
public class SimpleJson {
    private StringBuilder ans;

    public SimpleJson() {
        ans = new StringBuilder();
    }

    public SimpleJson object() {
        ans.append("{");
        return this;
    }

    public SimpleJson endObject() {
        ans.append("}");
        return this;
    }

    public SimpleJson array() {
        ans.append("[");
        return this;
    }

    public SimpleJson endArray() {
        ans.append("]");
        return this;
    }

    public SimpleJson key(String key) {
        ans.append("\"").append(key).append("\":");
        return this;
    }

    public SimpleJson value(String value) {
        ans.append("\"").append(value).append("\"");
        return this;
    }

    public SimpleJson value(int value) {
        ans.append(value);
        return this;
    }

    public SimpleJson value(boolean value) {
        ans.append(value);
        return this;
    }

    public SimpleJson comma() {
        ans.append(",");
        return this;
    }

    public String toString() {
        return ans.toString();
    }
}
