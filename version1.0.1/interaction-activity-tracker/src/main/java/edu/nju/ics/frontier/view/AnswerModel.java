package edu.nju.ics.frontier.view;

public class AnswerModel {
    private String title;
    private int answer;
    private int optionScore;

    public AnswerModel(String title, int answer, int optionScore) {
        this.title = title;
        this.answer = answer;
        this.optionScore = optionScore;
    }

    public String getTitle() {
        return title;
    }

    public int getAnswer() {
        return answer;
    }

    public int getOptionScore() {
        return optionScore;
    }

    @Override
    public String toString() {
        return String.format("{title:\"%s\",answer:%d,optionScore:%d}",
                title, answer, optionScore);
    }
}
