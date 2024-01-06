import json

data = {
    "input": {
        "interviewId": "45e6dbd5-b2ac-441f-95c8-01887a52dfb1",
        "overallFeedback": {
            "strength": "Very good wrestler",
            "areasOfImprovement": "Need improvements on coding skills"
        },
        "questions": []
    }
}

for i in range(10):
    question = {
        "startTime": i * 10 + 3,
        "endTime": i * 10 + 10,
        "question": "Question " + str(i + 1),
        "difficulty": "MEDIUM",
        "feedbacks": []
    }

    for j in range(3):
        feedback = {
            "categoryId": j,
            "rating": 7,
            "weightage": "80",
            "feedback": "Feedback serial number: " + str(j + 1)
        }
        question["feedbacks"].append(feedback)

    data["input"]["questions"].append(question)

print(json.dumps(data))
