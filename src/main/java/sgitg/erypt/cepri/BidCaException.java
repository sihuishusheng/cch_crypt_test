package sgitg.erypt.cepri;

/**
 * Created by DELL on 2017/4/13.
 */
public class BidCaException extends Exception {

    private static final long serialVersionUID = 2713168687996206678L;

    private String errCode="100000";
    private String msg="-无-";

    public BidCaException(String errCode, String msg) {
        super(msg);
        super.fillInStackTrace();
        if(errCode!=null){
            this.errCode = errCode;
        }
        if(msg!=null){
            this.msg = msg;
        }
    }
    /**
     *
     * @param e
     * @param errCode 异常字典中定义的标准编号
     * @param msg 可以不是异常字典中定义的标准编号对应的信息
     */
    public BidCaException(Throwable e,String errCode, String msg) {
        super(msg,e);
        if(errCode!=null){
            this.errCode = errCode;
        }
        if(msg!=null){
            this.msg = msg;
        }
    }
    public BidCaException(Throwable e) {
        super(e);
        this.msg=e.getMessage();
    }
    /**
     * 属性 errCode 的get方法
     *
     * @return the errCode
     */
    public String getErrCode() {
        return errCode;
    }

    /**
     * 属性 errCode 的set方法
     *
     * @param errCode
     */
    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    /**
     * 属性 msg 的get方法
     *
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 属性 msg 的set方法
     *
     * @param msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }
}

