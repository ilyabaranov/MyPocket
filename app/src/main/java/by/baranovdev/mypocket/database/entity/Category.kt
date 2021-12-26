package by.baranovdev.mypocket.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_table")
data class Category(
    @ColumnInfo(name = "category_name")
    val name:String,
    @ColumnInfo(name = "category_moneySum")
    var moneySum:Float
)
{
    @PrimaryKey(autoGenerate = true)
    var id :Int? = null
}

