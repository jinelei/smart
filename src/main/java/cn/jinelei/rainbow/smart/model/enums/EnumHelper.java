package cn.jinelei.rainbow.smart.model.enums;

public class EnumHelper {

    public static <E> E valueOf(E e, Object val) {
        if (null == val)
            return null;
        E[] all = (E[]) e.getClass().getEnumConstants();
        for (int i = 0; i < all.length; i++) {
            if (val instanceof String || val instanceof Object) {
                if (val.equals(all[i].toString()))
                    return all[i];
            }
            if (val instanceof Integer || val instanceof Long || val instanceof Short || val instanceof Byte
                    || val instanceof Float || val instanceof Double || val instanceof Boolean)
                if (val == e)
                    return all[i];
        }
        return null;
    }

    public static <E> boolean equals(E e, Object obj) {
        return e.toString().equals(obj.toString());
    }

}