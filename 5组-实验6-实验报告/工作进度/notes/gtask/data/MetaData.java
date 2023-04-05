//  Read By 孙明宇

package net.micode.notes.gtask.data;

import android.database.Cursor;
import android.util.Log;

import net.micode.notes.tool.GTaskStringUtils;

import org.json.JSONException;
import org.json.JSONObject;


public class MetaData extends Task {
    // 调用getSimpleName函数，将类的简写放入TAG中
    private final static String TAG = MetaData.class.getSimpleName();
    //  初始化相关联的Gid为NULL
    private String mRelatedGid = null;

    //  函数功能：设置数据，传入gid和meatInfo，将对应键值对放入meatInfo中
    public void setMeta(String gid, JSONObject metaInfo) {
        //  尝试利用put函数，放入
        try {
            metaInfo.put(GTaskStringUtils.META_HEAD_GTASK_ID, gid);
        }
        //  如果放入失败，则返回一个异常，输出错误信息
        catch (JSONException e) {
            Log.e(TAG, "failed to put related gid");
        }
        //  将其他信息也进行设置
        setNotes(metaInfo.toString());
        setName(GTaskStringUtils.META_NOTE_NAME);
    }

    //  获取相关联的Gid
    public String getRelatedGid() {
        return mRelatedGid;
    }

    //  函数功能：判断当前数据是否值得保存，即是否为空，若不为空，则返回真值，即值得保存
    @Override
    public boolean isWorthSaving() {
        return getNotes() != null;
    }

    //  函数功能：用JSON数据对象设置元数据内容
    @Override
    public void setContentByRemoteJSON(JSONObject js) {
        //  调用通过远程JSON对象设置内容函数
        super.setContentByRemoteJSON(js);
        //  数据不为空
        if (getNotes() != null) {
            try {
                //  建立JSON对象，并且获取相关联的gid
                JSONObject metaInfo = new JSONObject(getNotes().trim());
                mRelatedGid = metaInfo.getString(GTaskStringUtils.META_HEAD_GTASK_ID);
            } 
            //  出现异常，未获得相关联gid，输出异常
            catch (JSONException e) {
                Log.w(TAG, "failed to get related gid");
                //  复原相关联gid对象
                mRelatedGid = null;
            }
        }
    }

    //  函数功能：使用本地JSON对象设置内容（若用到，则抛出异常）
    @Override
    public void setContentByLocalJSON(JSONObject js) {
        // this function should not be called
        throw new IllegalAccessError("MetaData:setContentByLocalJSON should not be called");
    }

    //  函数功能：利用数据内容获得本地JSON对象（若用到，则抛出异常）
    @Override
    public JSONObject getLocalJSONFromContent() {
        throw new IllegalAccessError("MetaData:getLocalJSONFromContent should not be called");
    }

    //  函数功能：获取同步动作状态（若用到，则抛出异常）
    @Override
    public int getSyncAction(Cursor c) {
        throw new IllegalAccessError("MetaData:getSyncAction should not be called");
    }

}
