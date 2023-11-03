package com.techmania.photoalbum.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_images")
class MyImages(
    val imageTitle : String,
    val imageDescription : String,
    val imageAsString : String
) {
    @PrimaryKey(autoGenerate = true)
    var imageId = 0

}