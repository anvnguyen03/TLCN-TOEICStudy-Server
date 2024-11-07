package com.toeic.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DisplayTestItemDTO {

	private ETestItemType type; // PART, QUESTION, QUESTION_GROUP
    private float startTimestamp;
    private PartDTO part;
    private QuestionDTO question;
    private QuestionGroupDTO questionGroup;
    
}
