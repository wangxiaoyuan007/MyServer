package utils;

import java.util.UUID;

/**
 * @auther: wxy
 * @date: 2019/6/1 17:16
 */
public class UUIDUtil {
    public static String uuid(){
        return UUID.randomUUID().toString().replace("-","").toUpperCase();
    }

    public static void main(String[] args) {
        System.out.println( uuid());
    }
}
