package cn.jinelei.rainbow.smart.model;

public abstract class BaseBean {
    protected abstract void parse(byte[] bytes, int offset) throws Exception;

    protected abstract void parse(byte[] bytes) throws Exception;

    protected abstract byte[] toBytes();

    protected abstract void putTo(byte[] dest);
}
