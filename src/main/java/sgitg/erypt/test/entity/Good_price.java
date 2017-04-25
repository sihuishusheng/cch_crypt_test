package sgitg.erypt.test.entity;

/**
 * Created by DELL on 2017/4/18.
 */
public class Good_price {
    private  int bid_id;
    private int package_id;
    private int good_id;

    private double good_price;
    private String ency_price;
    private String signature;
    private String encry_stuta;
    private  int encry_count;
    private String encry_remark;

    public int getBid_id() {
        return bid_id;
    }

    public Good_price setBid_id(int bid_id) {
        this.bid_id = bid_id;
        return this;
    }

    public int getPackage_id() {
        return package_id;
    }

    public Good_price setPackage_id(int package_id) {
        this.package_id = package_id;
        return this;
    }

    public int getGood_id() {
        return good_id;
    }

    public Good_price setGood_id(int good_id) {
        this.good_id = good_id;
        return this;
    }

    public double getGood_price() {
        return good_price;
    }

    public Good_price setGood_price(double good_price) {
        this.good_price = good_price;
        return this;
    }

    public String getEncy_price() {
        return ency_price;
    }

    public Good_price setEncy_price(String ency_price) {
        this.ency_price = ency_price;
        return this;
    }

    public String getSignature() {
        return signature;
    }

    public Good_price setSignature(String signature) {
        this.signature = signature;
        return this;
    }

    public String getEncry_stuta() {
        return encry_stuta;
    }

    public Good_price setEncry_stuta(String encry_stuta) {
        this.encry_stuta = encry_stuta;
        return this;
    }

    public int getEncry_count() {
        return encry_count;
    }

    public Good_price setEncry_count(int encry_count) {
        this.encry_count = encry_count;
        return this;
    }

    public String getEncry_remark() {
        return encry_remark;
    }

    public Good_price setEncry_remark(String encry_remark) {
        this.encry_remark = encry_remark;
        return this;
    }
}
