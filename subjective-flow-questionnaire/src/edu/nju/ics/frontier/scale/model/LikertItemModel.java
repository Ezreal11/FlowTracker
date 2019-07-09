package edu.nju.ics.frontier.scale.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LikertItemModel {
    private String icon;
    private String title;
    private String question;
    private String[] optionLabels;
    private int[] optionScores;
    private int answer;

    private List<AnswerChangeListener> listeners = new ArrayList<>();

    public interface AnswerChangeListener {
        void answerChanged(AnswerModel model);
    }

    public LikertItemModel(String icon, String title, String question, String[] optionLabels) {
        this(icon, title, question, optionLabels, null, -1);
    }

    public void addAnswerChangeListener(AnswerChangeListener l) {
        if (l != null) {
            listeners.add(l);
        }
    }

    public void removeAnswerChangeListener(AnswerChangeListener l) {
        if (l != null) {
            listeners.remove(l);
        }
    }

    public LikertItemModel(String icon, String title, String question,
                           String[] optionLabels, int[] optionScores, int answer) {
        this.icon = icon;
        this.title = title;
        this.question = question;
        this.optionLabels = optionLabels;
        this.optionScores = optionScores;
        this.answer = answer;

        if (this.optionLabels == null && this.optionScores == null) {
            throw new IllegalArgumentException("No option labels nor scores in the Likert item!");
        } else if (this.optionScores == null && this.optionLabels != null) {
            this.optionScores = new int[this.optionLabels.length];
            for (int i = 0; i < this.optionScores.length; i++) {
                this.optionScores[i] = i + 1;
            }
        } else if (this.optionLabels == null && this.optionScores != null) {
            this.optionLabels = new String[this.optionScores.length];
            for (int i = 0; i < this.optionLabels.length; i++) {
                this.optionLabels[i] = String.valueOf(this.optionScores[i]);
            }
        }

        if (this.optionLabels.length > this.optionScores.length) {
            String[] temp = new String[this.optionScores.length];
            for (int i = 0; i < temp.length; i++) {
                temp[i] = this.optionLabels[i];
            }
            this.optionLabels = temp;
        } else if (this.optionScores.length > this.optionLabels.length) {
            int[] temp = new int[this.optionLabels.length];
            for (int i = 0; i < temp.length; i++) {
                temp[i] = this.optionScores[i];
            }
            this.optionScores = temp;
        }

        if (this.optionLabels.length == 0 || this.optionScores.length == 0) {
            throw new IllegalArgumentException("Empty option labels and scores in the Likert item!");
        }
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnsweredOptionLabel() {
        if (answer >= 0 && answer < optionLabels.length) {
            return optionLabels[answer];
        } else {
            return null;
        }
    }

    public String[] getOptionLabels() {
        return optionLabels;
    }

    public void setOptionLabels(String[] optionLabels) {
        this.optionLabels = optionLabels;
    }

    public int getAnsweredOptionScore() {
        if (answer >= 0 && answer < optionScores.length) {
            return optionScores[answer];
        } else {
            return -1;
        }
    }

    public int[] getOptionScores() {
        return optionScores;
    }

    public void setOptionScores(int[] optionScores) {
        this.optionScores = optionScores;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        if (answer == this.answer || answer < 0 || answer >= optionLabels.length) {
            return;
        }
        this.answer = answer;

        if (!(listeners == null || listeners.isEmpty())) {
            AnswerModel model = new AnswerModel(
                    this.title,
                    this.answer,
                    this.optionScores[this.answer]);
            for (AnswerChangeListener l : listeners) {
                l.answerChanged(model);
            }
        }
    }

    @Override
    public String toString() {
        return String.format("{icon:\"%s\",title:\"%s\",question:\"%s\"," +
                        "optionLabels:%s,optionScores:%s,answer:%d}",
                icon, title, question,
                Arrays.toString(optionLabels), Arrays.toString(optionScores), answer);
    }
}
