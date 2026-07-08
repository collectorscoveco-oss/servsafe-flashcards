# P012 — ServSafe Florida Flashcards

Status: Prototype v0.3.0 debug APK built for sideload testing.

Goal: Android randomized 50-question multiple-choice study guide for Florida manager ServSafe / Certified Food Protection Manager exam prep with Florida-specific certification reminders.

Current features:
- 434 original multiple-choice study questions across categories, including more ServSafe-style scenario wording.
- Randomized 50-question practice tests from the full matching bank.
- All-bank mode for running every matching question without limiting the question bank.
- Four answer choices per question with immediate correct/review feedback.
- Automatic 4.5-second advance after each answered question.
- Progress bar during the test and final grade/score screen at completion.
- Category filter and text search for focused practice sets.
- Florida manager certification notes and study-aid disclaimer.

Tech stack: Native Android Java, Gradle Android Plugin 8.7.3, minSdk 23, targetSdk 35.

Next steps:
- Test on Android phone.
- Add missed-question-only practice and stored score history if desired.
- Add custom icon polish/screenshots before any Play Store path.
