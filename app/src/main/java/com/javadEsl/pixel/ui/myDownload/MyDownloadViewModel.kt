package com.javadEsl.pixel.ui.myDownload

import android.os.Environment
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MyDownloadViewModel @Inject constructor(
    private val folderName: String
) : ViewModel() {


    fun getDownloadPictures(): List<File> {
        val root = Environment.getExternalStorageDirectory()
        val myDir = File("${root}/${folderName}")
        if (myDir.exists()) {
            val files = myDir.listFiles()
            val images = buildList {
                files?.forEach {
                    if (it?.isFile == true) add(it)
                    else it?.listFiles()?.forEach { subFile ->
                        if (subFile.isFile) add(subFile)
                    }
                }
            }

            return images
        }
        return emptyList()
    }
}