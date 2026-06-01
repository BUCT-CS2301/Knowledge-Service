package com.util;

/** Neo4j 文物 objectId 与 MySQL collect.user_collect_id 的稳定映射（与 String.hashCode 一致） */
public final class RelicKeyUtil {

    private RelicKeyUtil() {}

    public static int toRelicKey(String objectId) {
        if (objectId == null || objectId.isBlank()) {
            throw new IllegalArgumentException("objectId 不能为空");
        }
        return objectId.hashCode();
    }
}
