package com.davidjo.remedialexercise.data.diagnosis;

public class Diagnosis {

    public static DiagnosticResult getResult(DiagnosticAnswer answer) {

        if (answer.gotSurgery && answer.monthsAfterSurgery < 1) {
            return new DiagnosticResult(false,
                    "수술한 지 1개월이 지나지 않은 경우 재활운동은 적합하지 않습니다. 의사와 상담해보세요.");
        }

        if (answer.painLevel >= 4) {
            return new DiagnosticResult(false,
                    "통증이 심한 경우 재활운동은 적합하지 않습니다. 의사와 상담해보세요.");
        }

        if (answer.multiplePain) {
            return new DiagnosticResult(false,
                    "여러 부위의 통증은 신경계 질환일 수 있습니다. 의사와 상담해보세요.");
        }

        return new DiagnosticResult(true, "");
    }

}
