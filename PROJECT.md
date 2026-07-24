# P012 — ServSafe Florida Flashcards

Status: Prototype v0.4.0 with Android debug APK plus simple web study app.

Goal: Simple study tools for Florida manager ServSafe / Certified Food Protection Manager exam prep with Florida-specific certification reminders.

Current Android features:
- 434 original multiple-choice study questions across categories, including more ServSafe-style scenario wording.
- Randomized 50-question practice tests from the full matching bank.
- All-bank mode for running every matching question without limiting the question bank.
- Four answer choices per question with immediate correct/review feedback.
- Automatic 4.5-second advance after each answered question.
- Progress bar during the test and final grade/score screen at completion.
- Category filter and text search for focused practice sets.
- Florida manager certification notes and study-aid disclaimer.

Current web features:
- Simple static app in `web/` for browser-based group study.
- Tap-to-flip flashcards by category.
- Known-card progress saved in the browser.
- 10-question randomized practice quiz with immediate feedback.
- Quick temperature cheat sheet and short study guide.

Tech stack: Native Android Java, Gradle Android Plugin 8.7.3, minSdk 23, targetSdk 35; static HTML/CSS/JS web app.

Next steps:
- Test Android APK on phone.
- Test the web app with learners and add/adjust cards based on missed topics.
- Add missed-question-only practice and stored score history if desired.
- Add custom icon polish/screenshots before any Play Store path.
