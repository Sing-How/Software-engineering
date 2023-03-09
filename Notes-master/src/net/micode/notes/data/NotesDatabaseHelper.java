//read by 曾梦媛

package net.micode.notes.data;

import android.content.ContentValues;//用于保存一些数据（string boolean byte double float int long short ...）信息，这些信息可以被数据库操作时使用。
import android.content.Context;//加载和访问资源
import android.database.sqlite.SQLiteDatabase;//主要提供了对应于添加、删除、更新、查询的操作方法，配合content.values
import android.database.sqlite.SQLiteOpenHelper;//用来管理数据的创建和版本更新
import android.util.Log;

import net.micode.notes.data.Notes.DataColumns;
import net.micode.notes.data.Notes.DataConstants;
import net.micode.notes.data.Notes.NoteColumns;

//数据库操作，用SQLOpenhelper,对一些note和文件进行数据库的操作，比如删除文件后，将文件里的note也相应删除
public class NotesDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "note.db";//数据库名字

    private static final int DB_VERSION = 4;//数据库版本

    //接口，分为note和data，用于后面的程序
    public interface TABLE {
        public static final String NOTE = "note";

        public static final String DATA = "data";
    }

    //标签
    private static final String TAG = "NotesDatabaseHelper";

    private static NotesDatabaseHelper mInstance;
    
    //定义字符串CREATE_NOTE_TABLE_SQL的内容
    private static final String CREATE_NOTE_TABLE_SQL =
        "CREATE TABLE " + TABLE.NOTE + "(" +
            NoteColumns.ID + " INTEGER PRIMARY KEY," +
            NoteColumns.PARENT_ID + " INTEGER NOT NULL DEFAULT 0," +
            NoteColumns.ALERTED_DATE + " INTEGER NOT NULL DEFAULT 0," +
            NoteColumns.BG_COLOR_ID + " INTEGER NOT NULL DEFAULT 0," +
            NoteColumns.CREATED_DATE + " INTEGER NOT NULL DEFAULT (strftime('%s','now') * 1000)," +
            NoteColumns.HAS_ATTACHMENT + " INTEGER NOT NULL DEFAULT 0," +
            NoteColumns.MODIFIED_DATE + " INTEGER NOT NULL DEFAULT (strftime('%s','now') * 1000)," +
            NoteColumns.NOTES_COUNT + " INTEGER NOT NULL DEFAULT 0," +
            NoteColumns.SNIPPET + " TEXT NOT NULL DEFAULT ''," +
            NoteColumns.TYPE + " INTEGER NOT NULL DEFAULT 0," +
            NoteColumns.WIDGET_ID + " INTEGER NOT NULL DEFAULT 0," +
            NoteColumns.WIDGET_TYPE + " INTEGER NOT NULL DEFAULT -1," +
            NoteColumns.SYNC_ID + " INTEGER NOT NULL DEFAULT 0," +
            NoteColumns.LOCAL_MODIFIED + " INTEGER NOT NULL DEFAULT 0," +
            NoteColumns.ORIGIN_PARENT_ID + " INTEGER NOT NULL DEFAULT 0," +
            NoteColumns.GTASK_ID + " TEXT NOT NULL DEFAULT ''," +
            NoteColumns.VERSION + " INTEGER NOT NULL DEFAULT 0" +
        ")";//数据库中NOTE需要存储的项目的名称
    
    //定义字符串CREATE_DATA_TABLE_SQL的内容
    private static final String CREATE_DATA_TABLE_SQL =
        "CREATE TABLE " + TABLE.DATA + "(" +
            DataColumns.ID + " INTEGER PRIMARY KEY," +
            DataColumns.MIME_TYPE + " TEXT NOT NULL," +
            DataColumns.NOTE_ID + " INTEGER NOT NULL DEFAULT 0," +
            NoteColumns.CREATED_DATE + " INTEGER NOT NULL DEFAULT (strftime('%s','now') * 1000)," +
            NoteColumns.MODIFIED_DATE + " INTEGER NOT NULL DEFAULT (strftime('%s','now') * 1000)," +
            DataColumns.CONTENT + " TEXT NOT NULL DEFAULT ''," +
            DataColumns.DATA1 + " INTEGER," +
            DataColumns.DATA2 + " INTEGER," +
            DataColumns.DATA3 + " TEXT NOT NULL DEFAULT ''," +
            DataColumns.DATA4 + " TEXT NOT NULL DEFAULT ''," +
            DataColumns.DATA5 + " TEXT NOT NULL DEFAULT ''" +
        ")";//数据库中DATA需要存储的项目的名称

    //存储便签编号的一个数据表格
    private static final String CREATE_DATA_NOTE_ID_INDEX_SQL =
        "CREATE INDEX IF NOT EXISTS note_id_index ON " +
        TABLE.DATA + "(" + DataColumns.NOTE_ID + ");";

    /**
     * Increase folder's note count when move note to the folder
     */
    //当移入一个Note在文件夹中之后需要更改的数据的表格。
    private static final String NOTE_INCREASE_FOLDER_COUNT_ON_UPDATE_TRIGGER =
        "CREATE TRIGGER increase_folder_count_on_update "+
        " AFTER UPDATE OF " + NoteColumns.PARENT_ID + " ON " + TABLE.NOTE +
        " BEGIN " +
        "  UPDATE " + TABLE.NOTE +
        "   SET " + NoteColumns.NOTES_COUNT + "=" + NoteColumns.NOTES_COUNT + " + 1" +
        "  WHERE " + NoteColumns.ID + "=new." + NoteColumns.PARENT_ID + ";" +
        " END";

    /**
     * Decrease folder's note count when move note from folder
     */
    //当移出在文件夹中一个Note之后需要更改的数据的表格
    private static final String NOTE_DECREASE_FOLDER_COUNT_ON_UPDATE_TRIGGER =
        "CREATE TRIGGER decrease_folder_count_on_update " +
        " AFTER UPDATE OF " + NoteColumns.PARENT_ID + " ON " + TABLE.NOTE +
        " BEGIN " +
        "  UPDATE " + TABLE.NOTE +
        "   SET " + NoteColumns.NOTES_COUNT + "=" + NoteColumns.NOTES_COUNT + "-1" +
        "  WHERE " + NoteColumns.ID + "=old." + NoteColumns.PARENT_ID +
        "  AND " + NoteColumns.NOTES_COUNT + ">0" + ";" +
        " END";

    /**
     * Increase folder's note count when insert new note to the folder
     */
    //当插入一个Note在文件夹中之后需要更改的数据的表格
    private static final String NOTE_INCREASE_FOLDER_COUNT_ON_INSERT_TRIGGER =
        "CREATE TRIGGER increase_folder_count_on_insert " +
        " AFTER INSERT ON " + TABLE.NOTE +
        " BEGIN " +
        "  UPDATE " + TABLE.NOTE +
        "   SET " + NoteColumns.NOTES_COUNT + "=" + NoteColumns.NOTES_COUNT + " + 1" +
        "  WHERE " + NoteColumns.ID + "=new." + NoteColumns.PARENT_ID + ";" +
        " END";

    /**
     * Decrease folder's note count when delete note from the folder
     */
    //当删除在文件夹中一个Note之后需要更改的数据的表格
    private static final String NOTE_DECREASE_FOLDER_COUNT_ON_DELETE_TRIGGER =
        "CREATE TRIGGER decrease_folder_count_on_delete " +
        " AFTER DELETE ON " + TABLE.NOTE +
        " BEGIN " +
        "  UPDATE " + TABLE.NOTE +
        "   SET " + NoteColumns.NOTES_COUNT + "=" + NoteColumns.NOTES_COUNT + "-1" +
        "  WHERE " + NoteColumns.ID + "=old." + NoteColumns.PARENT_ID +
        "  AND " + NoteColumns.NOTES_COUNT + ">0;" +
        " END";

    /**
     * Update note's content when insert data with type {@link DataConstants#NOTE}
     */
    //当对文件夹中一个Note更新数据之后需要更改的数据的表格
    private static final String DATA_UPDATE_NOTE_CONTENT_ON_INSERT_TRIGGER =
        "CREATE TRIGGER update_note_content_on_insert " +
        " AFTER INSERT ON " + TABLE.DATA +
        " WHEN new." + DataColumns.MIME_TYPE + "='" + DataConstants.NOTE + "'" +
        " BEGIN" +
        "  UPDATE " + TABLE.NOTE +
        "   SET " + NoteColumns.SNIPPET + "=new." + DataColumns.CONTENT +
        "  WHERE " + NoteColumns.ID + "=new." + DataColumns.NOTE_ID + ";" +
        " END";

    /**
     * Update note's content when data with {@link DataConstants#NOTE} type has changed
     */
    //当Note数据被修改后需要更改的数据的表格。
    private static final String DATA_UPDATE_NOTE_CONTENT_ON_UPDATE_TRIGGER =
        "CREATE TRIGGER update_note_content_on_update " +
        " AFTER UPDATE ON " + TABLE.DATA +
        " WHEN old." + DataColumns.MIME_TYPE + "='" + DataConstants.NOTE + "'" +
        " BEGIN" +
        "  UPDATE " + TABLE.NOTE +
        "   SET " + NoteColumns.SNIPPET + "=new." + DataColumns.CONTENT +
        "  WHERE " + NoteColumns.ID + "=new." + DataColumns.NOTE_ID + ";" +
        " END";

    /**
     * Update note's content when data with {@link DataConstants#NOTE} type has deleted
     */
    //当Note数据被删除后需要更改的数据的表格。
    private static final String DATA_UPDATE_NOTE_CONTENT_ON_DELETE_TRIGGER =
        "CREATE TRIGGER update_note_content_on_delete " +
        " AFTER delete ON " + TABLE.DATA +
        " WHEN old." + DataColumns.MIME_TYPE + "='" + DataConstants.NOTE + "'" +
        " BEGIN" +
        "  UPDATE " + TABLE.NOTE +
        "   SET " + NoteColumns.SNIPPET + "=''" +
        "  WHERE " + NoteColumns.ID + "=old." + DataColumns.NOTE_ID + ";" +
        " END";

    /**
     * Delete datas belong to note which has been deleted
     */
    //当删除已删除的便签的数据后需要更改的数据的表格。
    private static final String NOTE_DELETE_DATA_ON_DELETE_TRIGGER =
        "CREATE TRIGGER delete_data_on_delete " +
        " AFTER DELETE ON " + TABLE.NOTE +
        " BEGIN" +
        "  DELETE FROM " + TABLE.DATA +
        "   WHERE " + DataColumns.NOTE_ID + "=old." + NoteColumns.ID + ";" +
        " END";

    /**
     * Delete notes belong to folder which has been deleted
     */
    //当删除已删除的文件夹的便签后需要更改的数据的表格。
    private static final String FOLDER_DELETE_NOTES_ON_DELETE_TRIGGER =
        "CREATE TRIGGER folder_delete_notes_on_delete " +
        " AFTER DELETE ON " + TABLE.NOTE +
        " BEGIN" +
        "  DELETE FROM " + TABLE.NOTE +
        "   WHERE " + NoteColumns.PARENT_ID + "=old." + NoteColumns.ID + ";" +
        " END";

    /**
     * Move notes belong to folder which has been moved to trash folder
     */
    //当还原垃圾桶中便签后需要更改的数据的表格。
    private static final String FOLDER_MOVE_NOTES_ON_TRASH_TRIGGER =
        "CREATE TRIGGER folder_move_notes_on_trash " +
        " AFTER UPDATE ON " + TABLE.NOTE +
        " WHEN new." + NoteColumns.PARENT_ID + "=" + Notes.ID_TRASH_FOLER +
        " BEGIN" +
        "  UPDATE " + TABLE.NOTE +
        "   SET " + NoteColumns.PARENT_ID + "=" + Notes.ID_TRASH_FOLER +
        "  WHERE " + NoteColumns.PARENT_ID + "=old." + NoteColumns.ID + ";" +
        " END";

    //构造函数，用于传入数据库的名称和版本
    public NotesDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //创建表格，用于存储标签属性
    public void createNoteTable(SQLiteDatabase db) {
        db.execSQL(CREATE_NOTE_TABLE_SQL);
        reCreateNoteTableTriggers(db);
        createSystemFolder(db);
        Log.d(TAG, "note table has been created");
    }

    //execSQL是数据库操作的API，主要是更改行为的SQL语句。
    //在这里主要是用来重新创建上述定义的表格用的，先删除原来有的数据库的触发器再重新创建新的数据库触发器
    private void reCreateNoteTableTriggers(SQLiteDatabase db) {
        db.execSQL("DROP TRIGGER IF EXISTS increase_folder_count_on_update");
        db.execSQL("DROP TRIGGER IF EXISTS decrease_folder_count_on_update");
        db.execSQL("DROP TRIGGER IF EXISTS decrease_folder_count_on_delete");
        db.execSQL("DROP TRIGGER IF EXISTS delete_data_on_delete");
        db.execSQL("DROP TRIGGER IF EXISTS increase_folder_count_on_insert");
        db.execSQL("DROP TRIGGER IF EXISTS folder_delete_notes_on_delete");
        db.execSQL("DROP TRIGGER IF EXISTS folder_move_notes_on_trash");

        db.execSQL(NOTE_INCREASE_FOLDER_COUNT_ON_UPDATE_TRIGGER);
        db.execSQL(NOTE_DECREASE_FOLDER_COUNT_ON_UPDATE_TRIGGER);
        db.execSQL(NOTE_DECREASE_FOLDER_COUNT_ON_DELETE_TRIGGER);
        db.execSQL(NOTE_DELETE_DATA_ON_DELETE_TRIGGER);
        db.execSQL(NOTE_INCREASE_FOLDER_COUNT_ON_INSERT_TRIGGER);
        db.execSQL(FOLDER_DELETE_NOTES_ON_DELETE_TRIGGER);
        db.execSQL(FOLDER_MOVE_NOTES_ON_TRASH_TRIGGER);
    }

    //创建系统文件夹
    private void createSystemFolder(SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        /**
         * call record foler for call notes
         */
        //通话记录文件夹
        values.put(NoteColumns.ID, Notes.ID_CALL_RECORD_FOLDER);
        values.put(NoteColumns.TYPE, Notes.TYPE_SYSTEM);
        db.insert(TABLE.NOTE, null, values);

        /**
         * root folder which is default folder
         */
        //根文件夹
        values.clear();
        values.put(NoteColumns.ID, Notes.ID_ROOT_FOLDER);
        values.put(NoteColumns.TYPE, Notes.TYPE_SYSTEM);
        db.insert(TABLE.NOTE, null, values);

        /**
         * temporary folder which is used for moving note
         */
        //临时文件夹,用于移动笔记
        values.clear();
        values.put(NoteColumns.ID, Notes.ID_TEMPARAY_FOLDER);
        values.put(NoteColumns.TYPE, Notes.TYPE_SYSTEM);
        db.insert(TABLE.NOTE, null, values);

        /**
         * create trash folder
         */
        //垃圾回收文件夹
        values.clear();
        values.put(NoteColumns.ID, Notes.ID_TRASH_FOLER);
        values.put(NoteColumns.TYPE, Notes.TYPE_SYSTEM);
        db.insert(TABLE.NOTE, null, values);
    }

    //创建表格，用来存储标签内容
    public void createDataTable(SQLiteDatabase db) {
        db.execSQL(CREATE_DATA_TABLE_SQL);
        reCreateDataTableTriggers(db);
        db.execSQL(CREATE_DATA_NOTE_ID_INDEX_SQL);
        Log.d(TAG, "data table has been created");
    }

    //同上文的execSQL
    private void reCreateDataTableTriggers(SQLiteDatabase db) {
        db.execSQL("DROP TRIGGER IF EXISTS update_note_content_on_insert");
        db.execSQL("DROP TRIGGER IF EXISTS update_note_content_on_update");
        db.execSQL("DROP TRIGGER IF EXISTS update_note_content_on_delete");

        db.execSQL(DATA_UPDATE_NOTE_CONTENT_ON_INSERT_TRIGGER);
        db.execSQL(DATA_UPDATE_NOTE_CONTENT_ON_UPDATE_TRIGGER);
        db.execSQL(DATA_UPDATE_NOTE_CONTENT_ON_DELETE_TRIGGER);
    }

    //实现两个表格（上面创建的两个表格）
    static synchronized NotesDatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NotesDatabaseHelper(context);
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createNoteTable(db);
        createDataTable(db);
    }

    //数据库版本的更新
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        boolean reCreateTriggers = false;
        boolean skipV2 = false;

        //如果旧版本号为V1
        if (oldVersion == 1) {
            upgradeToV2(db);//更新至版本V2
            skipV2 = true; //skipV2为真，则从V2更新为V3
            oldVersion++;
        }

        //如果旧版本号为V2且不跳过版本V2，更新至版本V3
        if (oldVersion == 2 && !skipV2) {
            upgradeToV3(db);
            reCreateTriggers = true;
            oldVersion++;
        }

        //如果旧版本号为V3，更新至版本V4
        if (oldVersion == 3) {
            upgradeToV4(db);
            oldVersion++;
        }

        //重新创建Note和Data数据库触发器
        if (reCreateTriggers) {
            reCreateNoteTableTriggers(db);
            reCreateDataTableTriggers(db);
        }
        //旧版本号不等于新版本号，抛出异常
        if (oldVersion != newVersion) {
            throw new IllegalStateException("Upgrade notes database to version " + newVersion
                    + "fails");
        }
    }

    //更新到V2版本
    private void upgradeToV2(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE.NOTE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE.DATA);
        createNoteTable(db);
        createDataTable(db);
    }

    //更新到V3版本
    private void upgradeToV3(SQLiteDatabase db) {
        //删除未使用的触发器
        db.execSQL("DROP TRIGGER IF EXISTS update_note_modified_date_on_insert");
        db.execSQL("DROP TRIGGER IF EXISTS update_note_modified_date_on_delete");
        db.execSQL("DROP TRIGGER IF EXISTS update_note_modified_date_on_update");
        //为gtask id添加一列
        db.execSQL("ALTER TABLE " + TABLE.NOTE + " ADD COLUMN " + NoteColumns.GTASK_ID
                + " TEXT NOT NULL DEFAULT ''");
        //添加一个垃圾回收系统文件夹
        ContentValues values = new ContentValues();
        values.put(NoteColumns.ID, Notes.ID_TRASH_FOLER);
        values.put(NoteColumns.TYPE, Notes.TYPE_SYSTEM);
        db.insert(TABLE.NOTE, null, values);
    }

    //更新到V4版本
    private void upgradeToV4(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + TABLE.NOTE + " ADD COLUMN " + NoteColumns.VERSION
                + " INTEGER NOT NULL DEFAULT 0");
    }
}
