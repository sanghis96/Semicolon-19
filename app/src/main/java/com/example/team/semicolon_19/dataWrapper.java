package com.example.team.semicolon_19;

public class dataWrapper {
    int questionNumber;
    String answer;

    dataWrapper(int questionNumber, String answer) {
        this.questionNumber = questionNumber;
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "dataWrapper{" +
                "questionNumber=" + questionNumber +
                ", answer='" + answer + '\'' +
                '}';
    }
}