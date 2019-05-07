package org.kteam.palm.model;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * @Package org.kteam.palm.model
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2016-03-06 16:07
 */
public class AccessTime  implements Serializable {

    public static final String COLUMN_CATEGORY_ID = "categoryId";

    @DatabaseField(generatedId = true)
    public int uid;

    @DatabaseField
    public String categoryId;

    @DatabaseField
    public String time;
}
