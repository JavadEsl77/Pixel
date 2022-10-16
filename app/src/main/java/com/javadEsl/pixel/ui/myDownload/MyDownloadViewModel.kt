package com.javadEsl.pixel.ui.myDownload

import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MyDownloadViewModel @Inject constructor(
    private val folderName: String
) : ViewModel() {

    init {
        checkEmptyFolder()
    }

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

    fun deletePhotoAndDirectory(photo: File, onDeleted: (isDeleted: Boolean) -> Unit = {}) {
        if (photo.exists()) {
            photo.delete()
            val folder = File(photo.path.substringBeforeLast("/"))
            if (folder.isDirectory
                && folder.exists()
                && folder.listFiles().isNullOrEmpty()
            ) {
                folder.delete()
            }
            onDeleted(true)
            return
        }
        onDeleted(false)
    }

    private fun checkEmptyFolder() {
        val root = Environment.getExternalStorageDirectory()
        val myDir = File("${root}/${folderName}")
        if (myDir.exists()) {
            myDir.listFiles()?.map {
                if (it.isDirectory
                    && it.exists()
                    && it.listFiles().isNullOrEmpty()
                ) {
                    it.delete()
                }
            }
        }
    }
}