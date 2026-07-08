package com.mexcould.servsafe;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.text.Editable;
import android.text.TextWatcher;
import android.graphics.drawable.GradientDrawable;
import org.json.*;
import java.io.*;
import java.util.*;

public class MainActivity extends Activity {
    private static final int QUESTIONS_PER_TEST = 50;
    private static final long AUTO_NEXT_DELAY_MS = 4500;

    private ArrayList<Card> allCards = new ArrayList<>();
    private ArrayList<Card> pool = new ArrayList<>();
    private ArrayList<Card> testDeck = new ArrayList<>();
    private ArrayList<String> categories = new ArrayList<>();
    private int index = 0;
    private int correctCount = 0;
    private int answeredCount = 0;
    private boolean answered = false;
    private boolean finished = false;
    private int selectedAnswer = -1;
    private ArrayList<String> currentOptions = new ArrayList<>();
    private String selectedCategory = "All";
    private String query = "";

    private TextView progress, categoryChip, question, feedback, mastery, floridaNote, timerText, gradeText;
    private ProgressBar progressBar;
    private Button[] optionButtons = new Button[4];
    private Button prevBtn, nextBtn, newTestBtn, allQuestionsBtn;
    private EditText search;
    private Spinner categorySpinner;
    private SharedPreferences prefs;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable pendingAdvance;

    private final int teal = Color.rgb(15,118,110), dark = Color.rgb(18,53,47), cream = Color.rgb(255,247,237), orange = Color.rgb(249,115,22);
    private final int green = Color.rgb(22,163,74), red = Color.rgb(220,38,38), gray = Color.rgb(95,95,95), blue = Color.rgb(37,99,235);

    static class Card { String category, question, answer; int originalIndex; }

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("study", MODE_PRIVATE);
        loadCards();
        buildUi();
        applyFiltersAndStartNewTest();
    }

    private void loadCards() {
        try {
            InputStream in = getAssets().open("cards.json");
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[4096]; int n;
            while ((n = in.read(buf)) > 0) out.write(buf,0,n);
            JSONArray arr = new JSONArray(out.toString("UTF-8"));
            TreeSet<String> set = new TreeSet<>(); set.add("All");
            for (int i=0;i<arr.length();i++) {
                JSONObject o = arr.getJSONObject(i);
                Card c = new Card();
                c.category=o.getString("category"); c.question=o.getString("question"); c.answer=o.getString("answer"); c.originalIndex=i;
                allCards.add(c); set.add(c.category);
            }
            categories.addAll(set);
        } catch(Exception e) {
            Toast.makeText(this, "Could not load questions: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private TextView tv(String text, int sp, int color, int style) {
        TextView v = new TextView(this);
        v.setText(text); v.setTextSize(sp); v.setTextColor(color); v.setTypeface(Typeface.DEFAULT, style); v.setLineSpacing(2,1.08f);
        return v;
    }
    private Button btn(String text, int bg, int fg) {
        Button b = new Button(this);
        b.setText(text); b.setTextColor(fg); b.setTextSize(14); b.setAllCaps(false); b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setGravity(Gravity.CENTER); b.setMinHeight(dp(48)); b.setPadding(dp(12),dp(8),dp(12),dp(8));
        b.setBackground(rounded(bg, Color.TRANSPARENT, 0, 16)); return b;
    }
    private GradientDrawable rounded(int color, int strokeColor, int stroke, int radius) {
        GradientDrawable g = new GradientDrawable(); g.setColor(color); g.setCornerRadius(dp(radius)); if (stroke>0) g.setStroke(dp(stroke), strokeColor); return g;
    }
    private int dp(int v) { return (int)(v*getResources().getDisplayMetrics().density + 0.5f); }

    private void buildUi() {
        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(16),dp(16),dp(16),dp(22)); root.setBackgroundColor(cream);
        scroll.addView(root);
        TextView title = tv("Florida ServSafe Manager", 25, dark, Typeface.BOLD); root.addView(title);
        TextView sub = tv("50-question randomized practice tests • full question bank • instant feedback", 13, Color.rgb(80,80,80), Typeface.NORMAL); root.addView(sub);
        floridaNote = tv("Florida manager focus: practice for an approved Certified Food Protection Manager exam. Questions are worded in ServSafe-style scenarios, but this is an original study aid — not an official ServSafe/NRA product. Verify current Florida/local rules before your exam.", 12, Color.rgb(80,55,20), Typeface.NORMAL);
        floridaNote.setPadding(dp(12),dp(10),dp(12),dp(10)); floridaNote.setBackground(rounded(Color.rgb(255,237,213), orange, 1, 18)); LinearLayout.LayoutParams noteLp = new LinearLayout.LayoutParams(-1,-2); noteLp.setMargins(0,dp(10),0,dp(12)); root.addView(floridaNote,noteLp);

        categorySpinner = new Spinner(this); ArrayAdapter<String> ad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories); categorySpinner.setAdapter(ad); root.addView(categorySpinner, new LinearLayout.LayoutParams(-1,-2));
        search = new EditText(this); search.setHint("Optional search/filter: temps, allergens, Florida..."); search.setSingleLine(true); search.setTextSize(15); search.setPadding(dp(12),0,dp(12),0); LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(-1, dp(48)); slp.setMargins(0,dp(8),0,dp(8)); root.addView(search, slp);

        LinearLayout actions = new LinearLayout(this); actions.setOrientation(LinearLayout.HORIZONTAL); LinearLayout.LayoutParams alp = new LinearLayout.LayoutParams(-1, dp(50)); alp.setMargins(0,0,0,dp(8)); root.addView(actions, alp);
        newTestBtn = btn("New 50", teal, Color.WHITE); allQuestionsBtn = btn("All-bank mode", blue, Color.WHITE);
        actions.addView(newTestBtn, new LinearLayout.LayoutParams(0,-1,1)); TextView asp = new TextView(this); actions.addView(asp, new LinearLayout.LayoutParams(dp(8),-1)); actions.addView(allQuestionsBtn, new LinearLayout.LayoutParams(0,-1,1));

        progress = tv("", 14, dark, Typeface.BOLD); root.addView(progress);
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal); progressBar.setMax(QUESTIONS_PER_TEST); LinearLayout.LayoutParams plp = new LinearLayout.LayoutParams(-1, dp(14)); plp.setMargins(0,dp(6),0,dp(6)); root.addView(progressBar, plp);
        mastery = tv("", 12, Color.rgb(70,70,70), Typeface.NORMAL); root.addView(mastery);
        timerText = tv("", 13, blue, Typeface.BOLD); LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(-1,-2); tlp.setMargins(0,dp(4),0,0); root.addView(timerText, tlp);

        LinearLayout card = new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setPadding(dp(18),dp(18),dp(18),dp(18)); card.setBackground(rounded(Color.WHITE, Color.rgb(220,220,220), 1, 22)); LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(-1,-2); clp.setMargins(0,dp(12),0,dp(12)); root.addView(card,clp);
        categoryChip = tv("", 12, teal, Typeface.BOLD); card.addView(categoryChip);
        question = tv("", 21, dark, Typeface.BOLD); LinearLayout.LayoutParams qlp = new LinearLayout.LayoutParams(-1,-2); qlp.setMargins(0,dp(10),0,dp(12)); card.addView(question, qlp);
        feedback = tv("Choose the best answer.", 15, gray, Typeface.NORMAL); feedback.setPadding(0,dp(10),0,0); card.addView(feedback);
        gradeText = tv("", 20, dark, Typeface.BOLD); gradeText.setPadding(0,dp(12),0,0); card.addView(gradeText);

        for (int i=0; i<optionButtons.length; i++) {
            final int choice = i;
            optionButtons[i] = btn("", Color.WHITE, dark);
            optionButtons[i].setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            optionButtons[i].setBackground(rounded(Color.WHITE, Color.rgb(210,210,210), 1, 16));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2); lp.setMargins(0, dp(8), 0, 0);
            root.addView(optionButtons[i], lp);
            optionButtons[i].setOnClickListener(v -> choose(choice));
        }

        LinearLayout row2 = new LinearLayout(this); row2.setOrientation(LinearLayout.HORIZONTAL); LinearLayout.LayoutParams r2 = new LinearLayout.LayoutParams(-1, dp(48)); r2.setMargins(0,dp(10),0,0); root.addView(row2, r2);
        prevBtn = btn("← Review", Color.WHITE, dark); nextBtn = btn("Skip / Next →", orange, Color.WHITE);
        row2.addView(prevBtn, new LinearLayout.LayoutParams(0,-1,1)); TextView sp2 = new TextView(this); row2.addView(sp2,new LinearLayout.LayoutParams(dp(8),-1)); row2.addView(nextBtn, new LinearLayout.LayoutParams(0,-1,1));

        TextView footer = tv("Each test pulls 50 randomized questions from the available bank/filter. Use New 50 for another randomized set. All-bank mode runs every matching question.", 12, Color.rgb(85,85,85), Typeface.NORMAL); LinearLayout.LayoutParams flp = new LinearLayout.LayoutParams(-1,-2); flp.setMargins(0,dp(16),0,0); root.addView(footer, flp);
        setContentView(scroll);

        newTestBtn.setOnClickListener(v -> startTest(false));
        allQuestionsBtn.setOnClickListener(v -> startTest(true));
        nextBtn.setOnClickListener(v -> { cancelPendingAdvance(); move(1, true); });
        prevBtn.setOnClickListener(v -> { cancelPendingAdvance(); move(-1, false); });
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){ public void onItemSelected(AdapterView<?> p, View v, int pos, long id){ selectedCategory=categories.get(pos); applyFiltersAndStartNewTest();} public void onNothingSelected(AdapterView<?> p){} });
        search.addTextChangedListener(new TextWatcher(){ public void beforeTextChanged(CharSequence s,int st,int c,int a){} public void onTextChanged(CharSequence s,int st,int b,int c){ query=s.toString().toLowerCase(Locale.US); applyFiltersAndStartNewTest();} public void afterTextChanged(Editable e){} });
    }

    private void applyFiltersAndStartNewTest() {
        pool.clear();
        for (Card c: allCards) {
            boolean cat = selectedCategory.equals("All") || c.category.equals(selectedCategory);
            String blob = (c.category+" "+c.question+" "+c.answer).toLowerCase(Locale.US);
            if (cat && (query.length()==0 || blob.contains(query))) pool.add(c);
        }
        startTest(false);
    }

    private void startTest(boolean allBank) {
        cancelPendingAdvance();
        nextBtn.setOnClickListener(v -> { cancelPendingAdvance(); move(1, true); });
        testDeck.clear();
        ArrayList<Card> shuffled = new ArrayList<>(pool);
        Collections.shuffle(shuffled, new Random(System.nanoTime()));
        int limit = allBank ? shuffled.size() : Math.min(QUESTIONS_PER_TEST, shuffled.size());
        for (int i=0; i<limit; i++) testDeck.add(shuffled.get(i));
        index=0; correctCount=0; answeredCount=0; finished=false; resetQuestion(); render();
    }

    private void resetQuestion() { answered=false; selectedAnswer=-1; currentOptions.clear(); timerText.setText(""); }

    private void render() {
        if (testDeck.size()==0) {
            progress.setText("No questions match this filter."); progressBar.setMax(1); progressBar.setProgress(0); categoryChip.setText(""); question.setText("Try a different search or category."); feedback.setText(""); gradeText.setText(""); mastery.setText(bankText());
            for (Button b: optionButtons) { b.setText(""); b.setEnabled(false); }
            return;
        }
        if (finished || index >= testDeck.size()) { showGrade(); return; }
        Card c = testDeck.get(index);
        if (currentOptions.size()!=4) currentOptions = makeOptions(c);
        gradeText.setText("");
        progressBar.setMax(testDeck.size()); progressBar.setProgress(answeredCount);
        progress.setText("Question "+(index+1)+" of "+testDeck.size()+"  •  Answered: "+answeredCount+"  •  Correct: "+correctCount+"  •  Bank: "+pool.size());
        categoryChip.setText(c.category);
        question.setText(c.question);
        feedback.setText(answered ? explanationText(c) : "Choose the best answer.");
        feedback.setTextColor(answered && selectedAnswer>=0 && currentOptions.get(selectedAnswer).equals(c.answer) ? green : (answered ? red : gray));
        mastery.setText(bankText());
        for (int i=0; i<optionButtons.length; i++) {
            String prefix = (char)('A'+i) + ". ";
            optionButtons[i].setText(prefix + currentOptions.get(i));
            optionButtons[i].setVisibility(View.VISIBLE);
            optionButtons[i].setEnabled(!answered);
            int bg = Color.WHITE, stroke = Color.rgb(210,210,210), fg = dark;
            if (answered) {
                boolean isCorrect = currentOptions.get(i).equals(c.answer);
                boolean isChosen = i == selectedAnswer;
                if (isCorrect) { bg = Color.rgb(220,252,231); stroke = green; fg = Color.rgb(20,83,45); }
                else if (isChosen) { bg = Color.rgb(254,226,226); stroke = red; fg = Color.rgb(127,29,29); }
            }
            optionButtons[i].setTextColor(fg);
            optionButtons[i].setBackground(rounded(bg, stroke, 1, 16));
        }
        nextBtn.setText(answered ? "Next now →" : "Skip / Next →");
        prevBtn.setEnabled(index > 0);
    }

    private String explanationText(Card c) {
        boolean correct = selectedAnswer>=0 && currentOptions.get(selectedAnswer).equals(c.answer);
        return (correct ? "Correct. " : "Not quite. Correct answer: ") + c.answer;
    }

    private ArrayList<String> makeOptions(Card card) {
        LinkedHashSet<String> opts = new LinkedHashSet<>(); opts.add(card.answer);
        ArrayList<Card> same = new ArrayList<>();
        for (Card c: allCards) if (c.originalIndex != card.originalIndex && c.category.equals(card.category) && !c.answer.equals(card.answer)) same.add(c);
        Collections.shuffle(same, new Random(card.originalIndex * 1103515245L + System.nanoTime()));
        for (Card c: same) if (opts.size()<4) opts.add(c.answer);
        ArrayList<Card> any = new ArrayList<>();
        for (Card c: allCards) if (c.originalIndex != card.originalIndex && !opts.contains(c.answer)) any.add(c);
        Collections.shuffle(any, new Random(card.originalIndex * 2654435761L + System.nanoTime()));
        for (Card c: any) if (opts.size()<4) opts.add(c.answer);
        while (opts.size()<4) opts.add("Verify the current Florida/local food code rule before acting.");
        ArrayList<String> out = new ArrayList<>(opts);
        Collections.shuffle(out, new Random(card.originalIndex * 7919L + System.nanoTime()));
        return out;
    }

    private void choose(int choice) {
        if (finished || testDeck.size()==0 || answered) return;
        selectedAnswer = choice; answered = true; answeredCount++;
        Card c = testDeck.get(index);
        boolean correct = currentOptions.get(choice).equals(c.answer);
        if (correct) correctCount++;
        prefs.edit().putInt("c"+c.originalIndex, correct?1:-1).apply();
        timerText.setText("Next question in 4.5 seconds...");
        scheduleAutoAdvance();
        render();
    }

    private void scheduleAutoAdvance() {
        cancelPendingAdvance();
        pendingAdvance = () -> move(1, true);
        handler.postDelayed(pendingAdvance, AUTO_NEXT_DELAY_MS);
    }
    private void cancelPendingAdvance() { if (pendingAdvance != null) { handler.removeCallbacks(pendingAdvance); pendingAdvance = null; } }

    private void move(int d, boolean countSkip) {
        if (testDeck.size()==0) return;
        cancelPendingAdvance();
        if (d > 0 && !answered && countSkip) answeredCount++;
        index += d;
        if (index < 0) index = 0;
        if (index >= testDeck.size()) { finished=true; showGrade(); return; }
        resetQuestion(); render();
    }

    private void showGrade() {
        cancelPendingAdvance(); finished = true; index = testDeck.size();
        progressBar.setMax(testDeck.size()); progressBar.setProgress(testDeck.size());
        int total = Math.max(1, testDeck.size());
        int pct = Math.round((correctCount * 100f) / total);
        String letter = pct >= 90 ? "A" : pct >= 80 ? "B" : pct >= 70 ? "C / passing range" : pct >= 60 ? "D" : "Needs review";
        progress.setText("Test complete • "+correctCount+" correct out of "+testDeck.size());
        categoryChip.setText("Grade");
        question.setText("Your grade: "+pct+"%  —  "+letter);
        feedback.setText("ServSafe Manager commonly requires 70% or higher. Review missed topics, then tap New 50 for another randomized 50-question set from the full bank.");
        feedback.setTextColor(pct >= 70 ? green : red);
        gradeText.setText(correctCount+" / "+testDeck.size()+" correct");
        mastery.setText(bankText()); timerText.setText("");
        for (Button b: optionButtons) { b.setVisibility(View.GONE); }
        nextBtn.setText("New 50 →"); nextBtn.setOnClickListener(v -> startTest(false)); prevBtn.setEnabled(false);
    }

    private String bankText(){ int known=0, again=0; for(Card c: allCards){ int v=prefs.getInt("c"+c.originalIndex,0); if(v>0) known++; if(v<0) again++; } return "Saved progress across full bank: "+known+" correct/known • "+again+" review • "+(allCards.size()-known-again)+" new"; }
}
