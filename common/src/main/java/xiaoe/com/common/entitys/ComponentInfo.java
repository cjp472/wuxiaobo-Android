package xiaoe.com.common.entitys;

public class ComponentInfo {

    private int type;
    private String title;
    private String desc;
    private String price;
    private String joined;

    public ComponentInfo(int type, String title, String desc, String price, String joined) {
        this.type = type;
        this.title = title;
        this.desc = desc;
        this.price = price;
        this.joined = joined;
    }

    public ComponentInfo(int type, String title, String desc, String price) {
        this.type = type;
        this.title = title;
        this.desc = desc;
        this.price = price;
    }

    public ComponentInfo(int type, String title, String desc) {
        this.type = type;
        this.title = title;
        this.desc = desc;
    }

    public ComponentInfo() {

    }

    public void setJoined(String joined) {
        this.joined = joined;
    }

    public String getJoined() {

        return joined;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice() {

        return price;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getType() {

        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return "type --- " + getType() + " title --- " + getTitle() + " desc --- " + getDesc() + " price --- " + getPrice();
    }
}
