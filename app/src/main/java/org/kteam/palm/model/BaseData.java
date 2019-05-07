package org.kteam.palm.model;

import android.text.TextUtils;

import com.j256.ormlite.field.DatabaseField;

import org.kteam.palm.common.utils.Constants;

import java.io.Serializable;

/**
 * @Package org.kteam.palm.model
 * @Project Palm
 *
 * @Description 基础数据
 *
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-13 22:07
 */
public class BaseData implements Serializable {
    public static final String COLUMN_TYPE = "type", COLUMN_UID = "uid", COLUMN_VALUE = "value";

    @DatabaseField(generatedId = true)
    public int uid;

    @DatabaseField
    public String id;

    @DatabaseField
    public String value;

    @DatabaseField
    public String label;

    @DatabaseField
    public String type;

    @DatabaseField
    public String description;

    @DatabaseField
    public int sort;

    public BaseData() {

    }

    public BaseData(String label, String value) {
        this.label = label;
        this.value = value;
    }

    //这个用来显示在PickerView上面的字符串,PickerView会通过反射获取getPickerViewText方法显示出来。
    public String getPickerViewText() {
        //这里还可以判断文字超长截断再提供显示
        if (Constants.PAY_MONTH_KEY.equals(type)) {
            if (!TextUtils.isEmpty(label)) {
                if (!label.contains("月")) {
                    label += "月";
                }
            }
        } else if (Constants.PAY_LEVEL_KEY.equals(type)) {
            if (!TextUtils.isEmpty(label)) {
                if (!label.contains("档")) {
                    label += "档";
                }
            }
        }
        return label;
    }
}
