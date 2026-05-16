package com.ixuea.courses.mymusic.component.feed.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ixuea.courses.mymusic.R;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds publish-screen media selection outside the Activity.
 */
public class FeedPublishViewModel extends ViewModel {
    private final MutableLiveData<List<Object>> mediaItems = new MutableLiveData<>();
    private final ArrayList<LocalMedia> selectedImages = new ArrayList<>();

    public LiveData<List<Object>> getMediaItems() {
        return mediaItems;
    }

    public void setSelectedImages(List<LocalMedia> data) {
        selectedImages.clear();
        if (data != null) {
            selectedImages.addAll(data);
        }
        publishMediaItems();
    }

    public void removeSelectedImage(int position) {
        if (position >= 0 && position < selectedImages.size()) {
            selectedImages.remove(position);
            publishMediaItems();
        }
    }

    public List<LocalMedia> getSelectedImages() {
        return new ArrayList<>(selectedImages);
    }

    private void publishMediaItems() {
        ArrayList<Object> items = new ArrayList<>(selectedImages);
        if (items.size() < 9) {
            items.add(R.drawable.add_fill);
        }
        mediaItems.setValue(items);
    }
}
