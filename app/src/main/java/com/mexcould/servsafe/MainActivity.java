package com.mexcould.servsafe;

import android.app.Activity;
import android.os.Bundle;
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
    private ArrayList<Card> allCards = new ArrayList<>();
    private ArrayList<Card> deck = new ArrayList<>();
    private ArrayList<String> categories = new ArrayList<>();
    private int index = 0;
    private boolean answered = false;
    private int selectedAnswer = -1;
    private ArrayList<String> currentOptions = new ArrayList<>();
    private String selectedCategory = "All";
    private String query = "";
    private TextView progress, categoryChip, question, feedback, mastery, floridaNote;
    private Button[] optionButtons = new Button[4];
    private Button againBtn, knowBtn, prevBtn, nextBtn, shuffleBtn, resetBtn;
    private EditText search;
    private Spinner categorySpinner;
    private SharedPreferences prefs;
    private final int teal = Color.rgb(15,118,110), dark = Color.rgb(18,53,47), cream = Color.rgb(255,247,237), orange = Color.rgb(249,115,22);
    private final int green = Color.rgb(22,163,74), red = Color.rgb(220,38,38), gray = Color.rgb(95,95,95);

    static class Card { String category, question, answer; int originalIndex; }

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("study", MODE_PRIVATE);
        loadCards();
        buildUi();
        applyFilters(false);
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
                c.category=o.getString("category");
                c.question=o.getString("question");
                c.answer=o.getString("answer");
                c.originalIndex=i;
                allCards.add(c); set.add(c.category);
            }
            categories.addAll(set);
        } catch(Exception e) {
            Toast.makeText(this, "Could not load cards: "+e.getMessage(), Toast.LENGTH_LONG).show();
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
        b.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        b.setMinHeight(dp(48));
        b.setPadding(dp(14),dp(10),dp(14),dp(10));
        b.setBackground(rounded(bg, Color.TRANSPARENT, 0, 16));
        return b;
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
        TextView sub = tv("Multiple-choice study guide • manager certification practice • Florida notes", 13, Color.rgb(80,80,80), Typeface.NORMAL); root.addView(sub);
        floridaNote = tv("Florida manager focus: study for an approved Certified Food Protection Manager exam, keep proof of certification available for inspection, and remember Florida DOH guidance notes a 30-day window to regain compliance if a certified manager leaves. Always follow your current local regulator rules.", 12, Color.rgb(80,55,20), Typeface.NORMAL);
        floridaNote.setPadding(dp(12),dp(10),dp(12),dp(10)); floridaNote.setBackground(rounded(Color.rgb(255,237,213), orange, 1, 18)); LinearLayout.LayoutParams noteLp = new LinearLayout.LayoutParams(-1,-2); noteLp.setMargins(0,dp(10),0,dp(12)); root.addView(floridaNote,noteLp);
        categorySpinner = new Spinner(this); ArrayAdapter<String> ad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories); categorySpinner.setAdapter(ad); root.addView(categorySpinner, new LinearLayout.LayoutParams(-1,-2));
        search = new EditText(this); search.setHint("Search questions: temps, Norovirus, Florida, HACCP..."); search.setSingleLine(true); search.setTextSize(15); search.setPadding(dp(12),0,dp(12),0); LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(-1, dp(48)); slp.setMargins(0,dp(8),0,dp(8)); root.addView(search, slp);
        progress = tv("", 14, dark, Typeface.BOLD); root.addView(progress);
        mastery = tv("", 12, Color.rgb(70,70,70), Typeface.NORMAL); root.addView(mastery);

        LinearLayout card = new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setPadding(dp(18),dp(18),dp(18),dp(18)); card.setBackground(rounded(Color.WHITE, Color.rgb(220,220,220), 1, 22)); LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(-1,-2); clp.setMargins(0,dp(12),0,dp(12)); root.addView(card,clp);
        categoryChip = tv("", 12, teal, Typeface.BOLD); card.addView(categoryChip);
        question = tv("", 21, dark, Typeface.BOLD); LinearLayout.LayoutParams qlp = new LinearLayout.LayoutParams(-1,-2); qlp.setMargins(0,dp(10),0,dp(12)); card.addView(question, qlp);
        feedback = tv("Choose the best answer.", 15, gray, Typeface.NORMAL); feedback.setPadding(0,dp(10),0,0); card.addView(feedback);

        for (int i=0; i<optionButtons.length; i++) {
            final int choice = i;
            optionButtons[i] = btn("", Color.WHITE, dark);
            optionButtons[i].setBackground(rounded(Color.WHITE, Color.rgb(210,210,210), 1, 16));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2); lp.setMargins(0, dp(8), 0, 0);
            root.addView(optionButtons[i], lp);
            optionButtons[i].setOnClickListener(v -> choose(choice));
        }

        LinearLayout row1 = new LinearLayout(this); row1.setOrientation(LinearLayout.HORIZONTAL); row1.setGravity(Gravity.CENTER); LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(-1, dp(52)); rlp.setMargins(0,dp(10),0,0); root.addView(row1, rlp);
        againBtn = btn("Mark again", red, Color.WHITE); knowBtn = btn("Mark known", green, Color.WHITE);
        row1.addView(againBtn, new LinearLayout.LayoutParams(0,-1,1)); TextView sp = new TextView(this); row1.addView(sp,new LinearLayout.LayoutParams(dp(8),-1)); row1.addView(knowBtn, new LinearLayout.LayoutParams(0,-1,1));
        LinearLayout row2 = new LinearLayout(this); row2.setOrientation(LinearLayout.HORIZONTAL); LinearLayout.LayoutParams r2 = new LinearLayout.LayoutParams(-1, dp(48)); r2.setMargins(0,dp(8),0,0); root.addView(row2, r2);
        prevBtn = btn("← Prev", Color.WHITE, dark); nextBtn = btn("Next →", Color.WHITE, dark); shuffleBtn = btn("Shuffle", orange, Color.WHITE);
        row2.addView(prevBtn, new LinearLayout.LayoutParams(0,-1,1)); TextView sp2 = new TextView(this); row2.addView(sp2,new LinearLayout.LayoutParams(dp(8),-1)); row2.addView(nextBtn, new LinearLayout.LayoutParams(0,-1,1)); TextView sp3 = new TextView(this); row2.addView(sp3,new LinearLayout.LayoutParams(dp(8),-1)); row2.addView(shuffleBtn, new LinearLayout.LayoutParams(0,-1,1));
        resetBtn = btn("Clear saved progress", Color.rgb(75,85,99), Color.WHITE); LinearLayout.LayoutParams rlp3 = new LinearLayout.LayoutParams(-1, dp(48)); rlp3.setMargins(0,dp(8),0,0); root.addView(resetBtn, rlp3);
        TextView footer = tv("Study tip: answer first, then read the explanation. This is an original study aid, not an official ServSafe/NRA product. Verify current Florida/local certification rules before your exam.", 12, Color.rgb(85,85,85), Typeface.NORMAL); LinearLayout.LayoutParams flp = new LinearLayout.LayoutParams(-1,-2); flp.setMargins(0,dp(16),0,0); root.addView(footer, flp);
        setContentView(scroll);

        nextBtn.setOnClickListener(v -> move(1)); prevBtn.setOnClickListener(v -> move(-1)); shuffleBtn.setOnClickListener(v -> { Collections.shuffle(deck); index=0; resetQuestion(); render(); });
        againBtn.setOnClickListener(v -> mark(false, true)); knowBtn.setOnClickListener(v -> mark(true, true));
        resetBtn.setOnClickListener(v -> { prefs.edit().clear().apply(); Toast.makeText(this, "Progress cleared", Toast.LENGTH_SHORT).show(); render(); });
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){ public void onItemSelected(AdapterView<?> p, View v, int pos, long id){ selectedCategory=categories.get(pos); applyFilters(false);} public void onNothingSelected(AdapterView<?> p){} });
        search.addTextChangedListener(new TextWatcher(){ public void beforeTextChanged(CharSequence s,int st,int c,int a){} public void onTextChanged(CharSequence s,int st,int b,int c){ query=s.toString().toLowerCase(Locale.US); applyFilters(false);} public void afterTextChanged(Editable e){} });
    }

    private void applyFilters(boolean keepIndex) {
        int oldOrig = deck.size()>0 && index<deck.size()? deck.get(index).originalIndex : -1;
        deck.clear();
        for (Card c: allCards) {
            boolean cat = selectedCategory.equals("All") || c.category.equals(selectedCategory);
            String blob = (c.category+" "+c.question+" "+c.answer).toLowerCase(Locale.US);
            if (cat && (query.length()==0 || blob.contains(query))) deck.add(c);
        }
        if (keepIndex && oldOrig>=0) for (int i=0;i<deck.size();i++) if(deck.get(i).originalIndex==oldOrig) { index=i; break; }
        if (index>=deck.size()) index=0;
        resetQuestion();
        render();
    }

    private void resetQuestion() { answered=false; selectedAnswer=-1; currentOptions.clear(); }

    private void render() {
        if (deck.size()==0) {
            progress.setText("No questions match."); categoryChip.setText(""); question.setText("Try a different search or category."); feedback.setText(""); mastery.setText(masterText());
            for (Button b: optionButtons) { b.setText(""); b.setEnabled(false); }
            return;
        }
        Card c = deck.get(index);
        if (currentOptions.size()!=4) currentOptions = makeOptions(c);
        progress.setText("Question "+(index+1)+" of "+deck.size()+"  •  Total questions: "+allCards.size());
        categoryChip.setText(c.category);
        question.setText(c.question);
        feedback.setText(answered ? explanationText(c) : "Choose the best answer.");
        feedback.setTextColor(answered && selectedAnswer>=0 && currentOptions.get(selectedAnswer).equals(c.answer) ? green : (answered ? red : gray));
        mastery.setText(masterText());
        for (int i=0; i<optionButtons.length; i++) {
            String prefix = (char)('A'+i) + ". ";
            optionButtons[i].setText(prefix + currentOptions.get(i));
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
    }

    private String explanationText(Card c) {
        boolean correct = selectedAnswer>=0 && currentOptions.get(selectedAnswer).equals(c.answer);
        return (correct ? "Correct. " : "Not quite. Correct answer: ") + c.answer;
    }

    private ArrayList<String> makeOptions(Card card) {
        LinkedHashSet<String> opts = new LinkedHashSet<>();
        opts.add(card.answer);
        ArrayList<Card> pool = new ArrayList<>();
        for (Card c: allCards) if (c.originalIndex != card.originalIndex && c.category.equals(card.category)) pool.add(c);
        Collections.shuffle(pool, new Random(card.originalIndex * 1103515245L + 12345));
        for (Card c: pool) if (opts.size()<4) opts.add(c.answer);
        pool.clear();
        for (Card c: allCards) if (c.originalIndex != card.originalIndex && !opts.contains(c.answer)) pool.add(c);
        Collections.shuffle(pool, new Random(card.originalIndex * 2654435761L + 99));
        for (Card c: pool) if (opts.size()<4) opts.add(c.answer);
        while (opts.size()<4) opts.add("Ask a manager and verify with the current Florida/local food code rules.");
        ArrayList<String> out = new ArrayList<>(opts);
        Collections.shuffle(out, new Random(card.originalIndex * 7919L + 7));
        return out;
    }

    private void choose(int choice) {
        if (deck.size()==0 || answered) return;
        selectedAnswer = choice;
        answered = true;
        Card c = deck.get(index);
        boolean correct = currentOptions.get(choice).equals(c.answer);
        prefs.edit().putInt("c"+c.originalIndex, correct?1:-1).apply();
        Toast.makeText(this, correct ? "Correct" : "Review this one", Toast.LENGTH_SHORT).show();
        render();
    }

    private String masterText(){ int known=0, again=0; for(Card c: allCards){ int v=prefs.getInt("c"+c.originalIndex,0); if(v>0) known++; if(v<0) again++; } return "Progress: "+known+" correct/known • "+again+" review • "+(allCards.size()-known-again)+" new"; }
    private void move(int d){ if(deck.size()==0)return; index=(index+d+deck.size())%deck.size(); resetQuestion(); render(); }
    private void mark(boolean known, boolean advance){ if(deck.size()==0)return; Card c=deck.get(index); prefs.edit().putInt("c"+c.originalIndex, known?1:-1).apply(); Toast.makeText(this, known?"Marked known":"Marked for review", Toast.LENGTH_SHORT).show(); if(advance) move(1); else render(); }
}
