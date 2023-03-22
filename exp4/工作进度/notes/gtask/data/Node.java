//  Read By 孙明宇

package net.micode.notes.gtask.data;

import android.database.Cursor;

import org.json.JSONObject;

//  定义表示各种状态的常量
//  远端就是指远程云端
public abstract class Node {
    public static final int SYNC_ACTION_NONE = 0;   //没有需要同步的内容

    public static final int SYNC_ACTION_ADD_REMOTE = 1; //在远端需要增加内容

    public static final int SYNC_ACTION_ADD_LOCAL = 2;  //在本地需要更新内容

    public static final int SYNC_ACTION_DEL_REMOTE = 3; //在远端需要删除内容

    public static final int SYNC_ACTION_DEL_LOCAL = 4;  //在本地需要删除内容

    public static final int SYNC_ACTION_UPDATE_REMOTE = 5;  //在远端需要更新内容

    public static final int SYNC_ACTION_UPDATE_LOCAL = 6;   //在本地需要更新内容

    public static final int SYNC_ACTION_UPDATE_CONFLICT = 7; //更新出现冲突

    public static final int SYNC_ACTION_ERROR = 8; //同步出现错误

    private String mGid;        //gid

    private String mName;       //名字

    private long mLastModified; //最后一次被修改

    private boolean mDeleted;   //是否被删除

    //  初始化
    public Node() {
        mGid = null;
        mName = "";
        mLastModified = 0;
        mDeleted = false;
    }

    public abstract JSONObject getCreateAction(int actionId);

    public abstract JSONObject getUpdateAction(int actionId);

    public abstract void setContentByRemoteJSON(JSONObject js);

    public abstract void setContentByLocalJSON(JSONObject js);

    public abstract JSONObject getLocalJSONFromContent();

    public abstract int getSyncAction(Cursor c);

    //  设置gid
    public void setGid(String gid) {
        this.mGid = gid;
    }

    //  设置名字
    public void setName(String name) {
        this.mName = name;
    }

    //  设置最后一次修改时间
    public void setLastModified(long lastModified) {
        this.mLastModified = lastModified;
    }

    //  标记是否被删除
    public void setDeleted(boolean deleted) {
        this.mDeleted = deleted;
    }

    //  获取gid
    public String getGid() {
        return this.mGid;
    }

    //  获取名字
    public String getName() {
        return this.mName;
    }

    //  获取最后一次被修改时间
    public long getLastModified() {
        return this.mLastModified;
    }

    //  获取是否被删除这一状态
    public boolean getDeleted() {
        return this.mDeleted;
    }

}
