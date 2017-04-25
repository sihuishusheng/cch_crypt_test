package sgitg.erypt.test.entity;

/**
 * Created by DELL on 2017/4/18.
 */
public class Crypt_file {
    private  int id;
    private int bid_id;
    private String encrypt_path;
    private String encrypt_file_name;
    private String express_path;
    private String express_file_name;
    private String file_type;
    private String file_stuta;
    private  int encry_count;
    private String crypt_remark;
    private Long file_size;

    public int getId() {
        return id;
    }

    public Crypt_file setId(int id) {
        this.id = id;
        return this;
    }

    public int getBid_id() {
        return bid_id;
    }

    public Crypt_file setBid_id(int bid_id) {
        this.bid_id = bid_id;
        return this;
    }

    public String getEncrypt_path() {
        return encrypt_path;
    }

    public Crypt_file setEncrypt_path(String encrypt_path) {
        this.encrypt_path = encrypt_path;
        return this;
    }

    public String getEncrypt_file_name() {
        return encrypt_file_name;
    }

    public Crypt_file setEncrypt_file_name(String encrypt_file_name) {
        this.encrypt_file_name = encrypt_file_name;
        return this;
    }

    public String getExpress_path() {
        return express_path;
    }

    public Crypt_file setExpress_path(String express_path) {
        this.express_path = express_path;
        return this;
    }

    public String getExpress_file_name() {
        return express_file_name;
    }

    public Crypt_file setExpress_file_name(String express_file_name) {
        this.express_file_name = express_file_name;
        return this;
    }

    public String getFile_type() {
        return file_type;
    }

    public Crypt_file setFile_type(String file_type) {
        this.file_type = file_type;
        return this;
    }

    public String getFile_stuta() {
        return file_stuta;
    }

    public Crypt_file setFile_stuta(String file_stuta) {
        this.file_stuta = file_stuta;
        return this;
    }

    public int getEncry_count() {
        return encry_count;
    }

    public Crypt_file setEncry_count(int encry_count) {
        this.encry_count = encry_count;
        return this;
    }

    public String getCrypt_remark() {
        return crypt_remark;
    }

    public Crypt_file setCrypt_remark(String crypt_remark) {
        this.crypt_remark = crypt_remark;
        return this;
    }

    public long getFile_size() {
        return file_size;
    }

    public Crypt_file setFile_size(long file_size) {
        this.file_size = file_size;
        return this;
    }
}
