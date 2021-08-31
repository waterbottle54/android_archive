package com.davidjo.greenworld.ui.add.photo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.davidjo.greenworld.data.photo.PhotoRepository;
import com.davidjo.greenworld.data.photo.PixabayPhoto;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PhotoViewModel extends ViewModel {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private PhotoRepository repository;

    private final MutableLiveData<String> currentQuery = new MutableLiveData<>("");

    private final LiveData<List<PixabayPhoto>> photos = Transformations.switchMap(
            currentQuery, queryString -> repository.getSearchResults(queryString));

    private final Observer<List<PixabayPhoto>> photosObserver;


    @Inject
    public PhotoViewModel(PhotoRepository repository) {

        this.repository = repository;

        // 사진 검색 결과를 관찰하고 이벤트 전송

        assert currentQuery.getValue() != null;
        photosObserver = pixabayPhotos -> {
            if (pixabayPhotos == null) {
                event.setValue(new Event.ShowSearchFailureMessage("이미지 검색에 실패했습니다"));
            } else if (pixabayPhotos.isEmpty() && !currentQuery.getValue().isEmpty()) {
                event.setValue(new Event.ShowNoResultMessage("검색 결과가 없습니다"));
            }
        };
        photos.observeForever(photosObserver);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        photos.removeObserver(photosObserver);
    }

    public LiveData<Event> getEvent() {
        return event;
    }

    public LiveData<List<PixabayPhoto>> getPhotos() {
        return photos;
    }

    // UI 이벤트 처리

    public void onSearchClick(String query) {
        currentQuery.setValue(query);
    }

    public void onPhotoClick(PixabayPhoto photo) {
        event.setValue(new Event.NavigateBackWithResult(photo.getPreviewURL()));
    }

    // 프래그먼트에 전송되는 이벤트

    public static class Event {

        public static class ShowSearchFailureMessage extends Event {
            public final String message;
            public ShowSearchFailureMessage(String message) {
                this.message = message;
            }
        }

        public static class ShowNoResultMessage extends Event {
            public final String message;
            public ShowNoResultMessage(String message) {
                this.message = message;
            }
        }

        public static class NavigateBackWithResult extends Event {
            public final String photoUrl;
            public NavigateBackWithResult(String photoUrl) {
                this.photoUrl = photoUrl;
            }
        }
    }

}
