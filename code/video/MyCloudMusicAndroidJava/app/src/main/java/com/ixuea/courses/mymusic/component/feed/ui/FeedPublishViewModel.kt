package com.ixuea.courses.mymusic.component.feed.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ixuea.courses.mymusic.R
import com.luck.picture.lib.entity.LocalMedia

/**
 * Holds publish-screen media selection outside the Activity.
 */
class FeedPublishViewModel : ViewModel() {
    private val _mediaItems = MutableLiveData<List<Any>>()
    private val selectedImages = mutableListOf<LocalMedia>()

    val mediaItems: LiveData<List<Any>> = _mediaItems

    fun setSelectedImages(data: List<LocalMedia>?) {
        selectedImages.clear()
        data?.let(selectedImages::addAll)
        publishMediaItems()
    }

    fun removeSelectedImage(position: Int) {
        if (position in selectedImages.indices) {
            selectedImages.removeAt(position)
            publishMediaItems()
        }
    }

    fun getSelectedImages(): List<LocalMedia> {
        return selectedImages.toList()
    }

    private fun publishMediaItems() {
        val items = selectedImages.toMutableList<Any>()
        if (items.size < 9) {
            items += R.drawable.add_fill
        }
        _mediaItems.value = items
    }
}
