package sgitg.erypt.test.entity;

/**
 * Created by DELL on 2017/4/18.
 */
public class Bid {

    private  int id;
    private int package_id;
    private double tol_price;
    private String ency_tol_price;
    private String signature;
    private String encry_stuta;
    private  int encry_count;
    private String encry_remark;

    public int getId() {
        return id;
    }

    public Bid setId(int id) {
        this.id = id;
        return this;
    }

    public int getPackage_id() {
        return package_id;
    }

    public Bid setPackage_id(int package_id) {
        this.package_id = package_id;
        return this;
    }

    public double getTol_price() {
        return tol_price;
    }

    public Bid setTol_price(double tol_price) {
        this.tol_price = tol_price;
        return this;
    }

    public String getEncy_tol_price() {
        return ency_tol_price;
    }

    public Bid setEncy_tol_price(String ency_tol_price) {
        this.ency_tol_price = ency_tol_price;
        return this;
    }

    public String getSignature() {
        return signature;
    }

    public Bid setSignature(String signature) {
        this.signature = signature;
        return this;
    }

    public String getEncry_stuta() {
        return encry_stuta;
    }

    public Bid setEncry_stuta(String encry_stuta) {
        this.encry_stuta = encry_stuta;
        return this;
    }

    public int getEncry_count() {
        return encry_count;
    }

    public Bid setEncry_count(int encry_count) {
        this.encry_count = encry_count;
        return this;
    }

    public String getEncry_remark() {
        return encry_remark;
    }

    public Bid setEncry_remark(String encry_remark) {
        this.encry_remark = encry_remark;
        return this;
    }
}
