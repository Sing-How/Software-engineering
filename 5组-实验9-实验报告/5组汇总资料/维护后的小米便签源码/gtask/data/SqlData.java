//  Read By 孙明宇

package net.micode.notes.gtask.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import net.micode.notes.data.Notes;
import net.micode.notes.data.Notes.DataColumns;
import net.micode.notes.data.Notes.DataConstants;
import net.micode.notes.data.Notes.NoteColumns;
import net.micode.notes.data.NotesDatabaseHelper.TABLE;
import net.micode.notes.gtask.exception.ActionFailureException;

import org.json.JSONException;
import org.json.JSONObject;

public class SqlData {
    //  TAG存放获取的类的简称
    private static final String TAG = SqlData.class.getSimpleName();

    //  设置默认ID为-99999
    private static final int INVALID_ID = -99999;

    //  设立一个新对象
    public static final String[] PROJECTION_DATA = new String[] {
            DataColumns.ID, DataColumns.MIME_TYPE, DataColumns.CONTENT, DataColumns.DATA1,
            DataColumns.DATA3
    };

    //  设置5个列的不同属性，分别为不同值
    public static final int DATA_ID_COLUMN = 0;

    public static final int DATA_MIME_TYPE_COLUMN = 1;

    public static final int DATA_CONTENT_COLUMN = 2;

    public static final int DATA_CONTENT_DATA_1_COLUMN = 3;

    public static final int DATA_CONTENT_DATA_3_COLUMN = 4;

    //  下面几行都是在定义变量
    private ContentResolver mContentResolver;

    //  判断是否由默认创立的（是则返回true）
    private boolean mIsCreate;

    //  下面五行是几个变量的定义
    private long mDataId;

    private String mDataMimeType;

    private String mDataContent;

    private long mDataContentData1;

    private String mDataContentData3;

    private ContentValues mDiffDataValues;

    //  由默认构造的，构造一个新对象
    public SqlData(Context context) {
        mContentResolver = context.getContentResolver();
        mIsCreate = true;
        mDataId = INVALID_ID;
        mDataMimeType = DataConstants.NOTE;
        mDataContent = "";
        mDataContentData1 = 0;
        mDataContentData3 = "";
        mDiffDataValues = new ContentValues();
    }

    //  由复制构造的，复制c的内容，赋值给新对象
    public SqlData(Context context, Cursor c) {
        mContentResolver = context.getContentResolver();
        mIsCreate = false;
        loadFromCursor(c);
        mDiffDataValues = new ContentValues();
    }

    //  将光标c所指向的对象的内容，赋值给新对象
    private void loadFromCursor(Cursor c) {
        mDataId = c.getLong(DATA_ID_COLUMN);
        mDataMimeType = c.getString(DATA_MIME_TYPE_COLUMN);
        mDataContent = c.getString(DATA_CONTENT_COLUMN);
        mDataContentData1 = c.getLong(DATA_CONTENT_DATA_1_COLUMN);
        mDataContentData3 = c.getString(DATA_CONTENT_DATA_3_COLUMN);
    }

    //  函数功能：共享js对象的数据，并提供异常抛出处理机制
    public void setContent(JSONObject js) throws JSONException {
        //  若js中有ID属性，则dataID设置为对应属性值，否则设置为默认值
        long dataId = js.has(DataColumns.ID) ? js.getLong(DataColumns.ID) : INVALID_ID;
        //  若是默认构造的或者是构造对象的ID和上一句赋值的ID不同，则放入mDiffDataValues中
        if (mIsCreate || mDataId != dataId) {
            mDiffDataValues.put(DataColumns.ID, dataId);
        }
        //  将两个类中对象的mDataId设为dataID
        mDataId = dataId;

        //  下面几行都是同上述，就不再赘述

        String dataMimeType = js.has(DataColumns.MIME_TYPE) ? js.getString(DataColumns.MIME_TYPE)
                : DataConstants.NOTE;
        if (mIsCreate || !mDataMimeType.equals(dataMimeType)) {
            mDiffDataValues.put(DataColumns.MIME_TYPE, dataMimeType);
        }
        mDataMimeType = dataMimeType;

        String dataContent = js.has(DataColumns.CONTENT) ? js.getString(DataColumns.CONTENT) : "";
        if (mIsCreate || !mDataContent.equals(dataContent)) {
            mDiffDataValues.put(DataColumns.CONTENT, dataContent);
        }
        mDataContent = dataContent;

        long dataContentData1 = js.has(DataColumns.DATA1) ? js.getLong(DataColumns.DATA1) : 0;
        if (mIsCreate || mDataContentData1 != dataContentData1) {
            mDiffDataValues.put(DataColumns.DATA1, dataContentData1);
        }
        mDataContentData1 = dataContentData1;

        String dataContentData3 = js.has(DataColumns.DATA3) ? js.getString(DataColumns.DATA3) : "";
        if (mIsCreate || !mDataContentData3.equals(dataContentData3)) {
            mDiffDataValues.put(DataColumns.DATA3, dataContentData3);
        }
        mDataContentData3 = dataContentData3;
    }

    //  函数功能：若是默认构造的对象，则告知用户错误信息；否则，则创建JSONObject对象，将类中对象的值赋给JSONObject对象
    public JSONObject getContent() throws JSONException {
        if (mIsCreate) {
            Log.e(TAG, "it seems that we haven't created this in database yet");
            return null;
        }
        JSONObject js = new JSONObject();
        js.put(DataColumns.ID, mDataId);
        js.put(DataColumns.MIME_TYPE, mDataMimeType);
        js.put(DataColumns.CONTENT, mDataContent);
        js.put(DataColumns.DATA1, mDataContentData1);
        js.put(DataColumns.DATA3, mDataContentData3);
        return js;
    }

    //  函数功能：将当前所做修改，保存到库中
    public void commit(long noteId, boolean validateVersion, long version) {
        //  若是默认构造的对象
        if (mIsCreate) {
            //  若未做更改，从DiffDataValues中删除对应内容
            if (mDataId == INVALID_ID && mDiffDataValues.containsKey(DataColumns.ID)) {
                mDiffDataValues.remove(DataColumns.ID);
            }

            //  放入对应noteId的内容
            mDiffDataValues.put(DataColumns.NOTE_ID, noteId);
            //  将其插入到mContentResovler，即解释器中
            Uri uri = mContentResolver.insert(Notes.CONTENT_DATA_URI, mDiffDataValues);
            //  返回对应插入的note 的 id，并提供异常抛出和处理机制
            try {
                mDataId = Long.valueOf(uri.getPathSegments().get(1));
            } catch (NumberFormatException e) {
                Log.e(TAG, "Get note id error :" + e.toString());
                throw new ActionFailureException("create note failed");
            }
        } else {    //  若是通过复制操作构造的对象
            //  若有要修改的内容
            if (mDiffDataValues.size() > 0) {
                int result = 0;
                // 根据validateVersion的值不同，进行不同选择的更新
                if (!validateVersion) {
                    result = mContentResolver.update(ContentUris.withAppendedId(
                            Notes.CONTENT_DATA_URI, mDataId), mDiffDataValues, null, null);
                } else {
                    result = mContentResolver.update(ContentUris.withAppendedId(
                            Notes.CONTENT_DATA_URI, mDataId), mDiffDataValues,
                            " ? in (SELECT " + NoteColumns.ID + " FROM " + TABLE.NOTE
                                    + " WHERE " + NoteColumns.VERSION + "=?)", new String[] {
                                    String.valueOf(noteId), String.valueOf(version)
                            });
                }
                //  若没有更新成功，则返回对应信息
                if (result == 0) {
                    Log.w(TAG, "there is no update. maybe user updates note when syncing");
                }
            }
        }
        //  清除修改的内容，并且修改创建方式
        mDiffDataValues.clear();
        mIsCreate = false;
    }

    //  获取当前的id
    public long getId() {
        return mDataId;
    }
}
