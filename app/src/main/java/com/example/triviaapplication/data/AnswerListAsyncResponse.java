package com.example.triviaapplication.data;

import com.example.triviaapplication.model.Question;

import java.util.ArrayList;

/*
    An interface will create an entity that isn't related to anything.
    It is an abstract concept.
    It can be extended more than once unlike inheritance that can only be assigned once.
 */

public interface AnswerListAsyncResponse {
    void processFinished(ArrayList<Question> questionArrayList);
}
