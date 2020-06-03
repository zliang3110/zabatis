package me.zhangll.zabatis.exceptions;


@Deprecated
public class IbatisException extends RuntimeException {

    private static final long serialVersionUID = -9077686425644896407L;

    public IbatisException(){
        super();
    }

    public IbatisException(String message){
        super(message);
    }

    public IbatisException(String message, Throwable cause){
        super(message, cause);
    }

    public IbatisException(Throwable cause){
        super(cause);
    }
}
