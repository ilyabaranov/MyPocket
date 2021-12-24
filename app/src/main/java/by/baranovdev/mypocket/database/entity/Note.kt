package by.baranovdev.mypocket.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName="note_table")
data class Note(
    @ColumnInfo(name="note_description")
    var description :String,
    @ColumnInfo(name="note_money")
    val money:Float,
    @ColumnInfo(name="note_category")
    val category:String,
    @ColumnInfo(name="note_category_color_red")
    val colorRed:Int,
    @ColumnInfo(name="note_category_color_green")
    val colorGreen:Int,
    @ColumnInfo(name="note_category_color_blue")
    val colorBlue:Int,
    @ColumnInfo(name="note_timestamp")
    val timestamp:Long
){
    @PrimaryKey(autoGenerate = true)
    var id:Int? = null
}
