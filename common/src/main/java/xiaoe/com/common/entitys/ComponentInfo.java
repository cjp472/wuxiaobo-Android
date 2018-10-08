package xiaoe.com.common.entitys;

import java.util.List;

/**
 * 组件数据实体类
 */
public class ComponentInfo {

    // 店铺组件类型
    private String type;
    // 组件的子类型，eg: 信息流分为图文、音频、视频
    private String subType;
    // 标题
    private String title;
    // 内容
    private String desc;
    // 价格
    private String price;
    // 参与人数描述，eg: **人在听
    private String joinedDesc;
    // 定制的图片链接
    private String imgUrl;
    // 是否购买
    private boolean hasBuy;
    // 子列表集合
    private List<RecentUpdateListItem> subList;
    // 轮播图集合
    private List<String> shufflingList;

    public ComponentInfo() {

    }

    public void setShufflingList(List<String> shufflingList) {
        this.shufflingList = shufflingList;
    }

    public List<String> getShufflingList() {

        return shufflingList;
    }

    public void setSubList(List<RecentUpdateListItem> subList) {
        this.subList = subList;
    }

    public List<RecentUpdateListItem> getSubList() {

        return subList;
    }

    public void setHasBuy(boolean hasBuy) {
        this.hasBuy = hasBuy;
    }

    public boolean isHasBuy() {

        return hasBuy;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getSubType() {

        return subType;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setJoinedDesc(String joinedDesc) {
        this.joinedDesc = joinedDesc;
    }

    public String getJoinedDesc() {
        return joinedDesc;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice() {
        return price;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getType() {
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
