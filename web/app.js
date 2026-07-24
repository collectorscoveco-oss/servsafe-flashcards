const cards = [
  { c: 'Basics', q: 'What is the main goal of food safety?', a: 'To prevent foodborne illness.' },
  { c: 'Basics', q: 'What does TCS stand for?', a: 'Time/Temperature Control for Safety.' },
  { c: 'Basics', q: 'What are TCS foods?', a: 'Foods that need time and temperature control to stay safe.' },
  { c: 'Basics', q: 'Give examples of TCS foods.', a: 'Meat, poultry, fish, eggs, dairy, cooked rice, cooked beans, cut melon, cut tomatoes, and leafy greens.' },
  { c: 'Basics', q: 'What is the temperature danger zone?', a: '41°F to 135°F.' },
  { c: 'Basics', q: 'Why is the danger zone important?', a: 'Bacteria grow quickly in this range.' },
  { c: 'Basics', q: 'What is cross-contamination?', a: 'When harmful germs transfer from one food or surface to another.' },
  { c: 'Basics', q: 'What is the best way to prevent cross-contamination?', a: 'Separate raw and ready-to-eat foods, use clean equipment, and wash hands.' },

  { c: 'Illness', q: 'What are the Big 6 pathogens?', a: 'Norovirus, Salmonella Typhi, Shigella, E. coli, Hepatitis A, and non-typhoidal Salmonella.' },
  { c: 'Illness', q: 'Which illness is commonly linked to poor handwashing?', a: 'Norovirus and Hepatitis A.' },
  { c: 'Illness', q: 'Which pathogen is often linked to undercooked poultry and eggs?', a: 'Salmonella.' },
  { c: 'Illness', q: 'Which pathogen is often linked to undercooked ground beef?', a: 'E. coli.' },
  { c: 'Illness', q: 'Which pathogen grows well in refrigerated ready-to-eat foods?', a: 'Listeria.' },
  { c: 'Illness', q: 'What should a manager do if a worker has vomiting or diarrhea?', a: 'Exclude them from working with food.' },

  { c: 'Hygiene', q: 'When should food handlers wash hands?', a: 'Before food work, after restroom use, after raw food, coughing/sneezing, trash, and touching face/hair/body.' },
  { c: 'Hygiene', q: 'How long should hands be scrubbed?', a: 'At least 10–15 seconds.' },
  { c: 'Hygiene', q: 'Can hand sanitizer replace handwashing?', a: 'No.' },
  { c: 'Hygiene', q: 'When should gloves be changed?', a: 'When dirty/torn, before a new task, after raw food, or after 4 hours of continuous use.' },
  { c: 'Hygiene', q: 'Can bare hands touch ready-to-eat food?', a: 'No. Use gloves, tongs, deli paper, or utensils.' },
  { c: 'Hygiene', q: 'What jewelry is usually allowed while preparing food?', a: 'A plain wedding band only.' },

  { c: 'Temperatures', q: 'Poultry must be cooked to what temperature?', a: '165°F for 15 seconds.' },
  { c: 'Temperatures', q: 'Stuffed meat, stuffed pasta, and stuffing must reach what temperature?', a: '165°F for 15 seconds.' },
  { c: 'Temperatures', q: 'Food reheated for hot holding must reach what temperature?', a: '165°F within 2 hours.' },
  { c: 'Temperatures', q: 'Ground beef, ground pork, and injected meats must reach what temperature?', a: '155°F for 15 seconds.' },
  { c: 'Temperatures', q: 'Seafood, steaks, pork chops, and eggs for immediate service must reach what temperature?', a: '145°F for 15 seconds.' },
  { c: 'Temperatures', q: 'Fruits, vegetables, grains, and legumes for hot holding must reach what temperature?', a: '135°F.' },
  { c: 'Temperatures', q: 'Hot food must be held at what temperature?', a: '135°F or higher.' },
  { c: 'Temperatures', q: 'Cold food must be held at what temperature?', a: '41°F or lower.' },

  { c: 'Cooling', q: 'How fast must hot TCS food be cooled?', a: '135°F to 70°F within 2 hours, then 70°F to 41°F within 4 more hours.' },
  { c: 'Cooling', q: 'Total cooling time cannot exceed how many hours?', a: '6 hours.' },
  { c: 'Cooling', q: 'What are good ways to cool food quickly?', a: 'Shallow pans, ice baths, smaller portions, blast chillers, and ice paddles.' },
  { c: 'Cooling', q: 'Should hot food be cooled in deep containers?', a: 'No. It cools too slowly.' },

  { c: 'Receiving', q: 'Cold TCS food should be received at what temperature?', a: '41°F or lower.' },
  { c: 'Receiving', q: 'Hot TCS food should be received at what temperature?', a: '135°F or higher.' },
  { c: 'Receiving', q: 'Frozen food should be rejected if it has what signs?', a: 'Ice crystals, water stains, thawing, or refreezing.' },
  { c: 'Receiving', q: 'What should you do with food from an unapproved supplier?', a: 'Reject it.' },
  { c: 'Receiving', q: 'When should dented cans be rejected?', a: 'If deeply dented, on the seam, swollen, leaking, or rusty.' },

  { c: 'Storage', q: 'What does FIFO mean?', a: 'First In, First Out.' },
  { c: 'Storage', q: 'How far off the floor should food be stored?', a: 'At least 6 inches.' },
  { c: 'Storage', q: 'What is the correct top-to-bottom storage order in a cooler?', a: 'Ready-to-eat food, seafood, whole cuts, ground meat, poultry.' },
  { c: 'Storage', q: 'Why is poultry stored on the bottom?', a: 'It has the highest cooking temperature and can drip onto other foods.' },
  { c: 'Storage', q: 'How should chemicals be stored?', a: 'Away from food, utensils, and food-contact surfaces.' },
  { c: 'Storage', q: 'Ready-to-eat TCS food kept longer than 24 hours needs what?', a: 'Date marking.' },
  { c: 'Storage', q: 'How long can ready-to-eat TCS food usually be kept at 41°F or lower?', a: 'Maximum 7 days, with prep/open date as day 1.' },

  { c: 'Thawing', q: 'What are safe ways to thaw food?', a: 'In a cooler, under running water, in a microwave if cooked immediately, or as part of cooking.' },
  { c: 'Thawing', q: 'Can food be thawed on the counter?', a: 'No.' },
  { c: 'Thawing', q: 'If food is thawed in the microwave, what must happen next?', a: 'Cook it immediately.' },

  { c: 'Cleaning', q: 'What is the difference between cleaning and sanitizing?', a: 'Cleaning removes dirt/food. Sanitizing reduces germs.' },
  { c: 'Cleaning', q: 'What are the steps for cleaning and sanitizing?', a: 'Scrape, wash, rinse, sanitize, air-dry.' },
  { c: 'Cleaning', q: 'Food-contact surfaces must be cleaned and sanitized when?', a: 'After use, before switching foods, after contamination, and at least every 4 hours during continuous use.' },
  { c: 'Cleaning', q: 'Should dishes be towel-dried after sanitizing?', a: 'No. They should air-dry.' },
  { c: 'Cleaning', q: 'What is sanitizer concentration measured with?', a: 'Test strips.' },

  { c: 'Manager Duties', q: 'What is active managerial control?', a: 'A manager actively controls risk factors that cause foodborne illness.' },
  { c: 'Manager Duties', q: 'What are examples of active managerial control?', a: 'Training staff, checking temperatures, enforcing handwashing, monitoring cleaning, and correcting unsafe practices.' },
  { c: 'Manager Duties', q: 'What is HACCP?', a: 'A food safety system that identifies and controls hazards.' },
  { c: 'Manager Duties', q: 'What does CCP stand for?', a: 'Critical Control Point.' },
  { c: 'Manager Duties', q: 'Who is responsible for food safety in the operation?', a: 'The person in charge/manager.' },

  { c: 'Florida', q: 'Does Florida require certified food managers?', a: 'Yes, many food service operations must have certified food managers.' },
  { c: 'Florida', q: 'How soon must a Florida food manager usually be certified after employment?', a: 'Within 30 days.' },
  { c: 'Florida', q: 'How long is the certification commonly valid?', a: 'Usually 5 years, depending on the approved provider.' }
];

const temps = [
  ['Cold holding', '41°F or lower'], ['Hot holding', '135°F or higher'], ['Danger zone', '41°F–135°F'],
  ['Poultry', '165°F'], ['Reheating', '165°F within 2 hours'], ['Ground meat', '155°F'],
  ['Seafood/steaks/pork', '145°F'], ['Veg/grains hot holding', '135°F'], ['Cooling', '135→70°F in 2 hr, 70→41°F in 4 hr']
];

const guide = {
  'Start Here': [
    'The manager’s job is to prevent foodborne illness before it happens.',
    'Most exam questions are about time, temperature, contamination, hygiene, cleaning, and manager control.',
    'If a question sounds unsafe, the manager should stop the unsafe action, correct it, and retrain if needed.'
  ],
  'TCS Food Basics': [
    'TCS means Time/Temperature Control for Safety.',
    'TCS foods support fast bacteria growth if they sit in the danger zone too long.',
    'Common TCS foods: meat, poultry, seafood, eggs, dairy, cooked rice, cooked beans, cut melon, cut tomatoes, and cut leafy greens.',
    'Danger zone: 41°F to 135°F. Keep cold food cold and hot food hot.'
  ],
  'Temperature Rules': [
    'Cold holding: 41°F or lower.',
    'Hot holding: 135°F or higher.',
    'Poultry, stuffed foods, and reheated food for hot holding: 165°F.',
    'Ground meat and injected meats: 155°F.',
    'Seafood, steaks, pork chops, and eggs for immediate service: 145°F.',
    'Fruits, vegetables, grains, and legumes for hot holding: 135°F.'
  ],
  'Cooling and Reheating': [
    'Cool hot TCS food from 135°F to 70°F within 2 hours.',
    'Then cool from 70°F to 41°F within 4 more hours.',
    'Total cooling time can be no more than 6 hours.',
    'Use shallow pans, ice baths, smaller portions, blast chillers, or ice paddles.',
    'Reheat food for hot holding to 165°F within 2 hours.'
  ],
  'Personal Hygiene': [
    'Wash hands before food work and after restroom use, touching raw food, trash, face, hair, phone, or body.',
    'Scrub hands for at least 10–15 seconds.',
    'Hand sanitizer does not replace handwashing.',
    'Change gloves when dirty or torn, after raw food, before a new task, and after long continuous use.',
    'Do not touch ready-to-eat food with bare hands.'
  ],
  'Cross-Contamination': [
    'Keep raw food away from ready-to-eat food.',
    'Use separate cutting boards, utensils, and prep areas when possible.',
    'Clean and sanitize equipment between foods.',
    'Store cooler food from top to bottom: ready-to-eat, seafood, whole cuts, ground meat, poultry.',
    'Poultry goes on the bottom because it needs the highest cooking temperature.'
  ],
  'Receiving and Storage': [
    'Buy food only from approved suppliers.',
    'Receive cold TCS food at 41°F or lower and hot TCS food at 135°F or higher.',
    'Reject cans that are swollen, leaking, rusty, deeply dented, or dented on the seam.',
    'Store food at least 6 inches off the floor.',
    'Use FIFO: First In, First Out.',
    'Ready-to-eat TCS food held over 24 hours needs date marking and is usually kept no more than 7 days.'
  ],
  'Cleaning and Sanitizing': [
    'Cleaning removes food and dirt. Sanitizing lowers germs to safe levels.',
    'Correct order: scrape, wash, rinse, sanitize, air-dry.',
    'Food-contact surfaces need cleaning and sanitizing after use, before switching foods, after contamination, and at least every 4 hours during continuous use.',
    'Use test strips to check sanitizer strength.',
    'Do not towel-dry sanitized dishes; let them air-dry.'
  ],
  'Big 6 Illnesses': [
    'Know these names: Norovirus, Salmonella Typhi, Shigella, E. coli, Hepatitis A, and non-typhoidal Salmonella.',
    'Vomiting or diarrhea means the worker should be excluded from food work.',
    'Norovirus and Hepatitis A are strongly linked to poor handwashing.',
    'E. coli is often linked to undercooked ground beef.',
    'Salmonella is often linked to poultry and eggs.'
  ],
  'Manager Duties': [
    'Active managerial control means the manager is watching for food safety risks and correcting them early.',
    'Train employees, check temperatures, monitor cleaning, watch handwashing, and stop unsafe habits.',
    'HACCP is a system for identifying and controlling hazards.',
    'A CCP, or Critical Control Point, is a step where a hazard can be prevented, eliminated, or reduced.',
    'The person in charge is responsible for keeping the operation safe.'
  ],
  'Florida Notes': [
    'Florida food service operations commonly need certified food managers.',
    'A manager usually must be certified within 30 days of employment.',
    'ServSafe Manager is commonly used for Certified Food Protection Manager training/exams.',
    'Certification is commonly valid for 5 years, depending on the approved provider.'
  ],
  'How To Study': [
    'First memorize temperatures and cooling rules.',
    'Then study handwashing, glove use, and ready-to-eat food protection.',
    'Use flashcards for facts and the quiz for recall.',
    'If you miss a topic twice, go back to that category and study only those cards.'
  ]
};

let filtered = [...cards];
let cardIndex = 0;
let flipped = false;
let quiz = [];
let quizIndex = 0;
let quizScore = 0;
let answered = false;
const known = new Set(JSON.parse(localStorage.getItem('servsafeKnownCards') || '[]'));

const $ = (id) => document.getElementById(id);

function saveKnown() {
  localStorage.setItem('servsafeKnownCards', JSON.stringify([...known]));
  $('knownCount').textContent = known.size;
  $('totalCount').textContent = cards.length;
}

function initTemps() {
  $('tempGrid').innerHTML = temps.map(([name, value]) => `<div class="temp-item"><strong>${value}</strong><span>${name}</span></div>`).join('');
}

function initCategories() {
  const categories = ['All', ...new Set(cards.map(card => card.c))];
  $('categoryFilter').innerHTML = categories.map(c => `<option>${c}</option>`).join('');
}

function applyCategory() {
  const cat = $('categoryFilter').value;
  filtered = cat === 'All' ? [...cards] : cards.filter(card => card.c === cat);
  cardIndex = 0;
  flipped = false;
  renderCard();
}

function renderCard() {
  if (!filtered.length) return;
  const card = filtered[cardIndex];
  const globalIndex = cards.indexOf(card);
  $('cardMeta').textContent = `${card.c} • Card ${cardIndex + 1} of ${filtered.length}`;
  $('cardQuestion').textContent = card.q;
  $('cardAnswer').textContent = card.a;
  $('cardAnswer').classList.toggle('hidden', !flipped);
  $('markKnown').textContent = known.has(globalIndex) ? 'Known ✓' : 'Mark Known';
}

function flipCard() {
  flipped = !flipped;
  renderCard();
}

function moveCard(step) {
  cardIndex = (cardIndex + step + filtered.length) % filtered.length;
  flipped = false;
  renderCard();
}

function shuffle(array) {
  return array.map(value => ({ value, sort: Math.random() })).sort((a, b) => a.sort - b.sort).map(({ value }) => value);
}

function buildQuiz() {
  quiz = shuffle(cards).slice(0, 10);
  quizIndex = 0;
  quizScore = 0;
  answered = false;
  renderQuiz();
}

function renderQuiz() {
  if (quizIndex >= quiz.length) {
    $('quizBox').innerHTML = `<div class="quiz-question"><h3>Finished!</h3><p class="answer">Score: ${quizScore}/${quiz.length}</p><p>${quizScore >= 8 ? 'Great job. Review the missed cards and keep practicing.' : 'Keep studying the temperature and hygiene cards, then try again.'}</p></div>`;
    return;
  }
  const current = quiz[quizIndex];
  const wrongAnswers = shuffle(cards.filter(card => card.a !== current.a)).slice(0, 3).map(card => card.a);
  const options = shuffle([current.a, ...wrongAnswers]);
  $('quizBox').innerHTML = `
    <div class="quiz-question">
      <p class="card-meta">Question ${quizIndex + 1} of ${quiz.length} • ${current.c}</p>
      <h3>${current.q}</h3>
      <div class="options">${options.map(option => `<button class="option" data-answer="${escapeHtml(option)}">${option}</button>`).join('')}</div>
      <p id="feedback" class="feedback"></p>
      <div class="quiz-actions"><button id="nextQuiz" class="secondary" disabled>Next</button></div>
    </div>`;
  document.querySelectorAll('.option').forEach(btn => btn.addEventListener('click', () => answerQuiz(btn, current.a)));
  $('nextQuiz').addEventListener('click', () => { quizIndex++; answered = false; renderQuiz(); });
}

function answerQuiz(button, correct) {
  if (answered) return;
  answered = true;
  const picked = button.getAttribute('data-answer');
  const good = picked === correct;
  if (good) quizScore++;
  document.querySelectorAll('.option').forEach(btn => {
    if (btn.getAttribute('data-answer') === correct) btn.classList.add('correct');
  });
  if (!good) button.classList.add('wrong');
  $('feedback').textContent = good ? 'Correct!' : `Not quite. Correct answer: ${correct}`;
  $('feedback').className = `feedback ${good ? 'good' : 'bad'}`;
  $('nextQuiz').disabled = false;
}

function escapeHtml(text) {
  return text.replace(/[&<>"']/g, char => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' }[char]));
}

function initGuide() {
  $('guideGrid').innerHTML = Object.entries(guide).map(([title, items]) => `
    <article class="guide-card"><h3>${title}</h3><ul>${items.map(item => `<li>${item}</li>`).join('')}</ul></article>
  `).join('');
}

function initTabs() {
  document.querySelectorAll('.tab').forEach(tab => {
    tab.addEventListener('click', () => {
      document.querySelectorAll('.tab, .view').forEach(el => el.classList.remove('active'));
      tab.classList.add('active');
      $(tab.dataset.view).classList.add('active');
    });
  });
}

$('flashcard').addEventListener('click', flipCard);
$('flashcard').addEventListener('keydown', (event) => { if (event.key === 'Enter' || event.key === ' ') flipCard(); });
$('prevCard').addEventListener('click', () => moveCard(-1));
$('nextCard').addEventListener('click', () => moveCard(1));
$('markKnown').addEventListener('click', () => {
  const globalIndex = cards.indexOf(filtered[cardIndex]);
  known.has(globalIndex) ? known.delete(globalIndex) : known.add(globalIndex);
  saveKnown();
  renderCard();
});
$('shuffleCards').addEventListener('click', () => { filtered = shuffle(filtered); cardIndex = 0; flipped = false; renderCard(); });
$('categoryFilter').addEventListener('change', applyCategory);
$('newQuiz').addEventListener('click', buildQuiz);
$('resetProgress').addEventListener('click', () => { known.clear(); saveKnown(); renderCard(); });

initTemps();
initCategories();
initGuide();
initTabs();
saveKnown();
renderCard();
buildQuiz();
