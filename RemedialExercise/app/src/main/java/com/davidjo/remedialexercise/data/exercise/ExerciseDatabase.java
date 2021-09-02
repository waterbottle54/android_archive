package com.davidjo.remedialexercise.data.exercise;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.davidjo.remedialexercise.data.BodyPart;

import javax.inject.Inject;
import javax.inject.Provider;

@Database(entities = {Exercise.class}, version = 1, exportSchema = false)
public abstract class ExerciseDatabase extends RoomDatabase {

    public abstract ExerciseDao exerciseDao();

    public static class Callback extends RoomDatabase.Callback {

        private final Provider<ExerciseDatabase> database;

        @Inject
        public Callback(Provider<ExerciseDatabase> database) {
            this.database = database;
        }

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            ExerciseDao dao = database.get().exerciseDao();

            new Thread(() -> {
                dao.insert(new Exercise(
                        "아픈 목을 위한 목디스크 재활운동",
                        "목디스트 재활운동 영상입니다. 천천히 따라하실수 있을겁니다.\n" +
                                "서울대정병원 내부에서 만든 영상으로 촬영부터 출연 제작까지 모두 서울대정병원에서 했습니다.",
                        "서울대정병원",
                        "QT8ZFLgRJoc",
                        BodyPart.NECK
                ));
                dao.insert(new Exercise(
                        "손목 스트레칭",
                        "손목터널증후군이 의심된다면? 1분 자가진단법으로 확인해 보고 손목 스트레칭을 따라해 보세요.",
                        "자생한방병원",
                        "At2RJXtYJKk",
                        BodyPart.WRIST
                ));
                dao.insert(new Exercise(
                        "허리 통증을 없애주는 허리운동",
                        "잘못된 자세로 운동하는 것은 오히려 허리 통증을 악화시킬 수 있습니다.\n" +
                                "허리 통증이 있을 때는 무리한 운동보다는 허리 주변 근육을 풀어주고 쉬어주는 것이 좋습니다.",
                        "연세사랑병원",
                        "f-mgnsrDWHg",
                        BodyPart.BACK
                ));
                dao.insert(new Exercise(
                        "발목 손상 후 재활운동",
                        "발목 손상 후 재활운동 / Ankle Rehabilitation",
                        "인제대학교 백병원",
                        "je4fquoVp_I",
                        BodyPart.ANKLE
                ));
                dao.insert(new Exercise(
                        "무릎 재활운동",
                        "재활운동은 수술 후 재활만 해당되는 것은 아닙니다. 관절염 예방을 위헤 집에서도 쉽게 할수 있는 동작들입니다. " +
                                "만약 운동을 하다가 통증을 느낄 시 운동을 중단하시고 반드시 내원 후 전문의 진료를 받으시길 추천드립니다. ",
                        "날개병원",
                        "GQsYq9YaQ1c",
                        BodyPart.KNEE
                ));
                dao.insert(new Exercise(
                        "어깨관절 수술 후 재활운동법",
                        "1. 수술 후 4 ~ 6주: 24시간 보조기 착용\n" +
                                "2. 수술 후 6 ~ 8주: 준비운동과 도르래운동\n" +
                                "3. 수술 후 8 ~ 12주: 2번 운동 + 스트레칭 운동\n" +
                                "4. 수술 후 12주 이후: 3번 운동 + 근력 강화 운동",
                        "서울성모병원",
                        "kPvaN-Y21nw",
                        BodyPart.SHOULDER
                ));
            }).start();
        }
    }
}
