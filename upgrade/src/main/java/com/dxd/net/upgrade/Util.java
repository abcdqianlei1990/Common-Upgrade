package com.dxd.net.upgrade;

import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Created by channey on 2017/3/1.
 */

public class Util {
    public static String[] listToArray(@NonNull ArrayList<String> list) {
        String[] arr = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }
}
