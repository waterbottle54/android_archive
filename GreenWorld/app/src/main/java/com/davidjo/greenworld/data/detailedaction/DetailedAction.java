package com.davidjo.greenworld.data.detailedaction;

import com.davidjo.greenworld.data.action.Action;
import com.davidjo.greenworld.data.category.Category;

import java.util.Objects;

public class DetailedAction extends Action {

    private final int icon;     // 작은 아이콘
    private final int score;    // 1회 시행당 점수

    public DetailedAction(Action action, Category category) {
        super(action.getId(), action.getUserId(), action.getActionName(), action.getRepetitions(), action.getCreated());
        this.icon = category.icon;
        this.score = category.score;
    }

    public int getIcon() {
        return icon;
    }

    public int getScore() {
        return score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetailedAction that = (DetailedAction) o;
        return icon == that.icon && score == that.score;
    }

    @Override
    public int hashCode() {
        return Objects.hash(icon, score);
    }

}
