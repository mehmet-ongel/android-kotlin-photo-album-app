package com.techmania.photoalbum.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

class ConvertImage {

    companion object{

        fun convertToString(bitmap: Bitmap) : String?{

            val stream = ByteArrayOutputStream()
            val resultCompress = bitmap.compress(Bitmap.CompressFormat.PNG,100,stream)

            if (resultCompress){

                val byteArray = stream.toByteArray()

                val imageAsString = if (byteArray.size > 2000000){
                    resizeImage(bitmap,0.1)
                }else if (byteArray.size in 1000000..2000000){
                    resizeImage(bitmap,0.5)
                }else{
                    Base64.encodeToString(byteArray,Base64.DEFAULT)
                }

                return imageAsString

            }else{
                return null
            }

        }

        fun resizeImage(bitmap: Bitmap,coefficient:Double) : String?{

            val resizedBitmap = Bitmap.createScaledBitmap(bitmap,(bitmap.width * coefficient).toInt(),(bitmap.height * coefficient).toInt(),true)
            val newStream = ByteArrayOutputStream()
            val resultCompress = resizedBitmap.compress(Bitmap.CompressFormat.PNG,100,newStream)
            if (resultCompress){
                val newByteArray = newStream.toByteArray()
                return Base64.encodeToString(newByteArray,Base64.DEFAULT)
            }else{
                return null
            }

        }

        fun convertToBitmap(imageAsString: String) : Bitmap{

            val byteArrayAsDecodedString = Base64.decode(imageAsString,Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(byteArrayAsDecodedString,0,byteArrayAsDecodedString.size)
            return bitmap

        }

    }

}