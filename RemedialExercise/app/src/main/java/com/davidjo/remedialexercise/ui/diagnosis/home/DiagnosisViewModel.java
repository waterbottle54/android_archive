package com.davidjo.remedialexercise.ui.diagnosis.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.davidjo.remedialexercise.data.BodyPart;
import com.davidjo.remedialexercise.data.diagnosis.Diagnosis;
import com.davidjo.remedialexercise.data.diagnosis.DiagnosticAnswer;
import com.davidjo.remedialexercise.data.diagnosis.DiagnosticResult;

public class DiagnosisViewModel extends ViewModel {

    private final MutableLiveData<DiagnosticAnswer> answer = new MutableLiveData<>(new DiagnosticAnswer());

    private final MutableLiveData<Event> event = new MutableLiveData<>(null);

    public LiveData<DiagnosticAnswer> getAnswer() {
        return answer;
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }


    public void onBodyPartSelected(BodyPart bodyPart) {
        DiagnosticAnswer newAnswer = answer.getValue() != null ? answer.getValue() : new DiagnosticAnswer();
        newAnswer.bodyPart = bodyPart;
        answer.setValue(newAnswer);
    }

    public void onPainLevelSelected(int painLevel) {
        DiagnosticAnswer newAnswer = answer.getValue() != null ? answer.getValue() : new DiagnosticAnswer();
        newAnswer.painLevel = painLevel;
        answer.setValue(newAnswer);
    }

    public void onMonthsAfterSurgerySelected(int months) {
        DiagnosticAnswer newAnswer = answer.getValue() != null ? answer.getValue() : new DiagnosticAnswer();
        newAnswer.monthsAfterSurgery = months;
        answer.setValue(newAnswer);
    }

    public void onGotSurgeryChecked(boolean checked) {
        DiagnosticAnswer newAnswer = answer.getValue() != null ? answer.getValue() : new DiagnosticAnswer();
        newAnswer.gotSurgery = checked;
        answer.setValue(newAnswer);

        event.setValue(new Event.ShowMonthsInputUI(newAnswer.gotSurgery));
    }

    public void onMultiplePainChecked(boolean checked) {
        DiagnosticAnswer newAnswer = answer.getValue() != null ? answer.getValue() : new DiagnosticAnswer();
        newAnswer.multiplePain = checked;
        answer.setValue(newAnswer);
    }

    public void onSubmitClicked() {

        DiagnosticAnswer answer = this.answer.getValue();
        if (answer == null) {
            return;
        }

        DiagnosticResult result = Diagnosis.getResult(answer);

        if (result.positive) {
            event.setValue(new Event.NavigateSuccessScreen(answer.bodyPart));
        } else {
            event.setValue(new Event.NavigateFailureScreen(result.message));
        }
    }


    public static class Event {

        public static class NavigateSuccessScreen extends Event {
            public final BodyPart bodyPart;

            public NavigateSuccessScreen(BodyPart bodyPart) {
                this.bodyPart = bodyPart;
            }
        }

        public static class NavigateFailureScreen extends Event {
            public final String message;

            public NavigateFailureScreen(String message) {
                this.message = message;
            }
        }

        public static class ShowMonthsInputUI extends Event {

            public final boolean show;

            public ShowMonthsInputUI(boolean show) {
                this.show = show;
            }
        }
    }

}
