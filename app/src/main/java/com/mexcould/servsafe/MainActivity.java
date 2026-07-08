
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
    private boolean showingAnswer = false;
    private String selectedCategory = "All";
    private String query = "";
    private TextView progress, categoryChip, question, answer, mastery, floridaNote;
    private Button flipBtn, againBtn, knowBtn, prevBtn, nextBtn, shuffleBtn;
    private EditText search;
    private Spinner categorySpinner;
    private SharedPreferences prefs;
    private final int teal = Color.rgb(15,118,110), dark = Color.rgb(18,53,47), cream = Color.rgb(255,247,237), orange = Color.rgb(249,115,22);

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
                Card c = new Card(); c.category=o.getString("category"); c.question=o.getString("question"); c.answer=o.getString("answer"); c.originalIndex=i;
                allCards.add(c); set.add(c.category);
            }
            categories.addAll(set);
        } catch(Exception e) {
            Toast.makeText(this, "Could not load cards: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private TextView tv(String text, int sp, int color, int style) {
        TextView v = new TextView(this); v.setText(text); v.setTextSize(sp); v.setTextColor(color); v.setTypeface(Typeface.DEFAULT, style); v.setLineSpacing(2,1.08f); return v;
    }
    private Button btn(String text, int bg, int fg) {
        Button b = new Button(this); b.setText(text); b.setTextColor(fg); b.setTextSize(14); b.setAllCaps(false); b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        GradientDrawable gd = new GradientDrawable(); gd.setColor(bg); gd.setCornerRadius(dp(16)); b.setBackground(gd); b.setPadding(dp(8),dp(8),dp(8),dp(8)); return b;
    }
    private GradientDrawable rounded(int color, int strokeColor, int stroke) { GradientDrawable g = new GradientDrawable(); g.setColor(color); g.setCornerRadius(dp(22)); if (stroke>0) g.setStroke(dp(stroke), strokeColor); return g; }
    private int dp(int v) { return (int)(v*getResources().getDisplayMetrics().density + 0.5f); }

    private void buildUi() {
        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(16),dp(16),dp(16),dp(22)); root.setBackgroundColor(cream);
        scroll.addView(root);
        TextView title = tv("ServSafe Manager Florida", 25, dark, Typeface.BOLD); root.addView(title);
        TextView sub = tv("Flashcard study guide • original practice cards • Florida manager notes", 13, Color.rgb(80,80,80), Typeface.NORMAL); root.addView(sub);
        floridaNote = tv("Florida note: Keep proof of an approved food manager certificate available for inspection. If a certified manager leaves and the operation becomes out of compliance, Florida DOH guidance notes a 30-day window to regain compliance. Verify current local regulator rules before testing.", 12, Color.rgb(80,55,20), Typeface.NORMAL);
        floridaNote.setPadding(dp(12),dp(10),dp(12),dp(10)); floridaNote.setBackground(rounded(Color.rgb(255,237,213), orange, 1)); LinearLayout.LayoutParams noteLp = new LinearLayout.LayoutParams(-1,-2); noteLp.setMargins(0,dp(10),0,dp(12)); root.addView(floridaNote,noteLp);
        categorySpinner = new Spinner(this); ArrayAdapter<String> ad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories); categorySpinner.setAdapter(ad); root.addView(categorySpinner, new LinearLayout.LayoutParams(-1,-2));
        search = new EditText(this); search.setHint("Search cards (temps, Norovirus, Florida, HACCP...)"); search.setSingleLine(true); search.setTextSize(15); search.setPadding(dp(12),0,dp(12),0); LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(-1, dp(48)); slp.setMargins(0,dp(8),0,dp(8)); root.addView(search, slp);
        progress = tv("", 14, dark, Typeface.BOLD); root.addView(progress);
        mastery = tv("", 12, Color.rgb(70,70,70), Typeface.NORMAL); root.addView(mastery);
        LinearLayout card = new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setPadding(dp(18),dp(18),dp(18),dp(18)); card.setBackground(rounded(Color.WHITE, Color.rgb(220,220,220), 1)); LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(-1,-2); clp.setMargins(0,dp(12),0,dp(12)); root.addView(card,clp);
        categoryChip = tv("", 12, teal, Typeface.BOLD); card.addView(categoryChip);
        question = tv("", 22, dark, Typeface.BOLD); LinearLayout.LayoutParams qlp = new LinearLayout.LayoutParams(-1,-2); qlp.setMargins(0,dp(10),0,dp(10)); card.addView(question, qlp);
        answer = tv("", 18, Color.rgb(40,40,40), Typeface.NORMAL); answer.setPadding(0,dp(10),0,0); card.addView(answer);
        card.setOnClickListener(v -> flip());
        flipBtn = btn("Show answer", teal, Color.WHITE); root.addView(flipBtn, new LinearLayout.LayoutParams(-1, dp(52)));
        LinearLayout row1 = new LinearLayout(this); row1.setOrientation(LinearLayout.HORIZONTAL); row1.setGravity(Gravity.CENTER); LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(-1, dp(52)); rlp.setMargins(0,dp(8),0,0); root.addView(row1, rlp);
        againBtn = btn("Again", Color.rgb(220,38,38), Color.WHITE); knowBtn = btn("I know it", Color.rgb(22,163,74), Color.WHITE);
        row1.addView(againBtn, new LinearLayout.LayoutParams(0,-1,1)); LinearLayout.LayoutParams spacer = new LinearLayout.LayoutParams(dp(8),-1); TextView sp = new TextView(this); row1.addView(sp,spacer); row1.addView(knowBtn, new LinearLayout.LayoutParams(0,-1,1));
        LinearLayout row2 = new LinearLayout(this); row2.setOrientation(LinearLayout.HORIZONTAL); LinearLayout.LayoutParams r2 = new LinearLayout.LayoutParams(-1, dp(48)); r2.setMargins(0,dp(8),0,0); root.addView(row2, r2);
        prevBtn = btn("← Prev", Color.WHITE, dark); nextBtn = btn("Next →", Color.WHITE, dark); shuffleBtn = btn("Shuffle", orange, Color.WHITE);
        row2.addView(prevBtn, new LinearLayout.LayoutParams(0,-1,1)); TextView sp2 = new TextView(this); row2.addView(sp2,new LinearLayout.LayoutParams(dp(8),-1)); row2.addView(nextBtn, new LinearLayout.LayoutParams(0,-1,1)); TextView sp3 = new TextView(this); row2.addView(sp3,new LinearLayout.LayoutParams(dp(8),-1)); row2.addView(shuffleBtn, new LinearLayout.LayoutParams(0,-1,1));
        TextView footer = tv("Study tip: run the Temperature drills until instant, then scenarios. This app is a study aid, not an official ServSafe/NRA product.", 12, Color.rgb(85,85,85), Typeface.NORMAL); LinearLayout.LayoutParams flp = new LinearLayout.LayoutParams(-1,-2); flp.setMargins(0,dp(16),0,0); root.addView(footer, flp);
        setContentView(scroll);
        flipBtn.setOnClickListener(v -> flip());
        nextBtn.setOnClickListener(v -> move(1)); prevBtn.setOnClickListener(v -> move(-1)); shuffleBtn.setOnClickListener(v -> { Collections.shuffle(deck); index=0; showingAnswer=false; render(); });
        againBtn.setOnClickListener(v -> mark(false)); knowBtn.setOnClickListener(v -> mark(true));
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
        if (index>=deck.size()) index=0; showingAnswer=false; render();
    }
    private void render() {
        if (deck.size()==0) { progress.setText("No cards match."); categoryChip.setText(""); question.setText("Try a different search or category."); answer.setText(""); mastery.setText(masterText()); return; }
        Card c = deck.get(index); progress.setText("Card "+(index+1)+" of "+deck.size()+"  •  Total cards: "+allCards.size()); categoryChip.setText(c.category); question.setText(c.question); answer.setText(showingAnswer ? c.answer : "Tap the card or Show answer when ready."); flipBtn.setText(showingAnswer ? "Hide answer" : "Show answer"); mastery.setText(masterText());
    }
    private String masterText(){ int known=0, again=0; for(Card c: allCards){ int v=prefs.getInt("c"+c.originalIndex,0); if(v>0) known++; if(v<0) again++; } return "Progress: "+known+" known • "+again+" marked again • "+(allCards.size()-known-again)+" new"; }
    private void flip(){ showingAnswer=!showingAnswer; render(); }
    private void move(int d){ if(deck.size()==0)return; index=(index+d+deck.size())%deck.size(); showingAnswer=false; render(); }
    private void mark(boolean known){ if(deck.size()==0)return; Card c=deck.get(index); prefs.edit().putInt("c"+c.originalIndex, known?1:-1).apply(); Toast.makeText(this, known?"Marked known":"Marked for review", Toast.LENGTH_SHORT).show(); move(1); }
}
