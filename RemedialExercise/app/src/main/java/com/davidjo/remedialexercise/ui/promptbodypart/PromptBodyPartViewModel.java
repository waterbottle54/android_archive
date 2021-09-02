package com.davidjo.remedialexercise.ui.promptbodypart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.davidjo.remedialexercise.data.BodyPart;

public class PromptBodyPartViewModel extends ViewModel {

    private final MutableLiveData<BodyPart> bodyPart = new MutableLiveData<>(BodyPart.NECK);
    private final MutableLiveData<Event> event = new MutableLiveData<>(null);

    public LiveData<BodyPart> getBodyPart() {
        return bodyPart;
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public void onBodyPartSelected(BodyPart part) {
        bodyPart.setValue(part);
    }

    public void onConfirmClicked() {
        event.setValue(new Event.NavigateToDestination());
    }

    public static class Event {
        public static class NavigateToDestination extends Event { }
    }

}
