package com.davidjo.remedialexercise.data.diagnosis;

import com.davidjo.remedialexercise.data.BodyPart;

public class DiagnosticAnswer {

    public BodyPart bodyPart;
    public int painLevel;
    public boolean gotSurgery;
    public int monthsAfterSurgery;
    public boolean multiplePain;

    public DiagnosticAnswer() {
        bodyPart = BodyPart.NECK;
        painLevel = 1;
        gotSurgery = false;
        monthsAfterSurgery = 5;
        multiplePain = false;
    }
}
