// Hustle Hero Interactive Web Simulation & Showcase Engine

// Synthesized Web Audio Effects for Nigerian Street Hustle vibes
const soundFx = {
  ctx: null,
  init() {
    if (!this.ctx) {
      const AudioCtx = window.AudioContext || window.webkitAudioContext;
      if (AudioCtx) this.ctx = new AudioCtx();
    }
  },
  playCash() {
    this.init();
    if (!this.ctx) return;
    const osc = this.ctx.createOscillator();
    const gain = this.ctx.createGain();
    osc.type = 'triangle';
    osc.frequency.setValueAtTime(587.33, this.ctx.currentTime); // D5
    osc.frequency.exponentialRampToValueAtTime(880, this.ctx.currentTime + 0.15); // A5
    gain.gain.setValueAtTime(0.3, this.ctx.currentTime);
    gain.gain.exponentialRampToValueAtTime(0.01, this.ctx.currentTime + 0.3);
    osc.connect(gain);
    gain.connect(this.ctx.destination);
    osc.start();
    osc.stop(this.ctx.currentTime + 0.3);
  },
  playEvent() {
    this.init();
    if (!this.ctx) return;
    const osc = this.ctx.createOscillator();
    const gain = this.ctx.createGain();
    osc.type = 'sawtooth';
    osc.frequency.setValueAtTime(329.63, this.ctx.currentTime); // E4
    osc.frequency.linearRampToValueAtTime(440, this.ctx.currentTime + 0.2); // A4
    gain.gain.setValueAtTime(0.2, this.ctx.currentTime);
    gain.gain.exponentialRampToValueAtTime(0.01, this.ctx.currentTime + 0.35);
    osc.connect(gain);
    gain.connect(this.ctx.destination);
    osc.start();
    osc.stop(this.ctx.currentTime + 0.35);
  }
};

// Available Characters
const characters = [
  {
    id: "tunde",
    name: "Tunde",
    title: "Computer Village Tech Whiz",
    avatar: "👨🏾‍💻",
    perk: "+20% gadget demand & electronic sales",
    personalCash: 45000,
    businessCash: 120000,
    inventory: 50
  },
  {
    id: "mama_blessing",
    name: "Mama Blessing",
    title: "Balogun Market Wholesale Queen",
    avatar: "👩🏾‍🦱",
    perk: "12% cheaper wholesale restock costs",
    personalCash: 60000,
    businessCash: 180000,
    inventory: 90
  },
  {
    id: "chinedu",
    name: "Chinedu",
    title: "Alaba Commercial Apprentice",
    avatar: "🧔🏾‍♂️",
    perk: "Extra starting capital & high supplier trust",
    personalCash: 75000,
    businessCash: 220000,
    inventory: 120
  },
  {
    id: "iya_basira",
    name: "Iya Basira",
    title: "Mama Put Culinary Legend",
    avatar: "👩🏾‍🍳",
    perk: "+30% customer walk-in foot traffic",
    personalCash: 50000,
    businessCash: 110000,
    inventory: 70
  }
];

// Available Nigerian Events
const eventsPool = [
  {
    id: "fuel_scarcity",
    title: "Fuel Scarcity & PMS Queue",
    category: "Energy Shock",
    icon: "⛽",
    description: "Fuel queues stretch 2km down Ikorodu Road. Black market petrol hits ₦1,400/litre. Generator costs are about to bite hard this week.",
    choices: [
      {
        text: "Buy black market fuel (-₦25,000) to keep power on full-time",
        businessDelta: -25000,
        personalDelta: 0,
        reputationDelta: 5,
        energyDelta: 0,
        message: "Your shop stayed brightly lit while neighbors were in the dark! Customers trooped in."
      },
      {
        text: "Shut down generator at 4 PM to conserve fuel (-₦8,000 lost sales)",
        businessDelta: -8000,
        personalDelta: 0,
        reputationDelta: -4,
        energyDelta: -5,
        message: "You saved fuel cash, but several evening rush customers turned back disappointed."
      },
      {
        text: "Temporarily pass inflation to customers (+15% price hike)",
        businessDelta: 15000,
        personalDelta: 0,
        reputationDelta: -8,
        energyDelta: -5,
        message: "You maintained profit margins, but regular customers grumbled about greedy traders."
      }
    ]
  },
  {
    id: "balogun_rush",
    title: "Lagos Payday Flash Rush",
    category: "Market Opportunity",
    icon: "🎉",
    description: "Corporate salaries hit bank accounts today! Balogun and Ikeja markets are packed with eager buyers looking for fresh stock.",
    choices: [
      {
        text: "Run a 'Buy 2 Get 1 Free' promotional flash deal",
        businessDelta: 35000,
        personalDelta: 0,
        reputationDelta: 8,
        energyDelta: -10,
        message: "Huge clearance success! Stock flew off the shelves and buzz spread on WhatsApp."
      },
      {
        text: "Maintain premium pricing to maximize unit margin",
        businessDelta: 22000,
        personalDelta: 0,
        reputationDelta: 2,
        energyDelta: 0,
        message: "Healthy, steady profits without breaking your back or diluting your brand."
      },
      {
        text: "Hire 2 street promoters with flyers (-₦10,000)",
        businessDelta: 42000,
        personalDelta: 0,
        reputationDelta: 12,
        energyDelta: -5,
        message: "The street promoters pulled massive crowds right into your shop!"
      }
    ]
  },
  {
    id: "council_levy",
    title: "Local Council Revenue Taskforce",
    category: "Street Bureaucracy",
    icon: "📋",
    description: "Men in neon-yellow reflective jackets arrive at your storefront demanding 'Annual Trade & Signboard Permit' verification.",
    choices: [
      {
        text: "Pay the official receipt fee immediately (-₦15,000)",
        businessDelta: -15000,
        personalDelta: 0,
        reputationDelta: 4,
        energyDelta: -5,
        message: "They pasted the official yellow sticker on your door. Peace of mind restored."
      },
      {
        text: "Negotiate a 'soft settlement' on the spot (-₦7,000)",
        businessDelta: -7000,
        personalDelta: 0,
        reputationDelta: -2,
        energyDelta: 0,
        message: "Settled quickly with a cold drink and cash. They promised not to bother you this quarter."
      },
      {
        text: "Call your market union chairperson to intervene",
        businessDelta: -2000,
        personalDelta: 0,
        reputationDelta: 6,
        energyDelta: -12,
        message: "The market union backed you up firmly! You only paid minimal union administrative dues."
      }
    ]
  }
];

// Game State
const gameState = {
  week: 1,
  character: characters[0],
  personalCash: 45000,
  businessCash: 120000,
  inventory: 50,
  unitSellingPrice: 4500,
  unitWholesaleCost: 2400,
  salaryWithdrawal: 15000,
  ajoSaved: 20000,
  ajoWeeklyQuota: 10000,
  ajoPayoutWeek: 4,
  energy: 85,
  stress: 20,
  reputation: 60,
  activeEvent: null,
  history: []
};

// Currency Formatter
function formatNaira(amount) {
  return '₦' + Math.max(0, Math.round(amount)).toLocaleString('en-NG');
}

// UI Elements
const els = {
  weekNumber: document.getElementById('weekNumber'),
  personalCash: document.getElementById('personalCash'),
  businessCash: document.getElementById('businessCash'),
  totalNetWorth: document.getElementById('totalNetWorth'),
  ajoSaved: document.getElementById('ajoSaved'),
  reputationScore: document.getElementById('reputationScore'),
  energyLevel: document.getElementById('energyLevel'),
  stressLevel: document.getElementById('stressLevel'),
  salarySlider: document.getElementById('salarySlider'),
  salaryValue: document.getElementById('salaryValue'),
  characterSelector: document.getElementById('characterSelector'),
  playerAvatar: document.getElementById('playerAvatar'),
  playerName: document.getElementById('playerName'),
  playerTitle: document.getElementById('playerTitle'),
  eventBox: document.getElementById('eventBox'),
  btnNextWeek: document.getElementById('btnNextWeek'),
  themeToggle: document.getElementById('themeToggle')
};

// Initialize Game
function initGame() {
  updateUI();
  setupEventListeners();
  triggerRandomEvent();
}

function updateUI() {
  if (els.weekNumber) els.weekNumber.textContent = gameState.week;
  if (els.personalCash) els.personalCash.textContent = formatNaira(gameState.personalCash);
  if (els.businessCash) els.businessCash.textContent = formatNaira(gameState.businessCash);
  
  const netWorth = gameState.personalCash + gameState.businessCash + (gameState.inventory * gameState.unitWholesaleCost) + gameState.ajoSaved;
  if (els.totalNetWorth) els.totalNetWorth.textContent = formatNaira(netWorth);
  
  if (els.ajoSaved) els.ajoSaved.textContent = formatNaira(gameState.ajoSaved);
  if (els.reputationScore) els.reputationScore.textContent = gameState.reputation + ' / 100';
  if (els.energyLevel) els.energyLevel.textContent = gameState.energy + '%';
  if (els.stressLevel) els.stressLevel.textContent = gameState.stress + '%';
  
  if (els.playerAvatar) els.playerAvatar.textContent = gameState.character.avatar;
  if (els.playerName) els.playerName.textContent = gameState.character.name;
  if (els.playerTitle) els.playerTitle.textContent = gameState.character.title;
  
  const invEl = document.getElementById('invUnits');
  if (invEl) invEl.textContent = gameState.inventory + ' Units';
  
  const weeklyEstEl = document.getElementById('weeklyEstRevenue');
  if (weeklyEstEl) {
    const estimatedSales = Math.min(gameState.inventory, Math.floor(gameState.reputation * 0.8 + 15));
    const estimatedRev = estimatedSales * gameState.unitSellingPrice;
    weeklyEstEl.textContent = formatNaira(estimatedRev);
  }
}

function setupEventListeners() {
  // Tabs
  document.querySelectorAll('.tab-btn').forEach(btn => {
    btn.addEventListener('click', (e) => {
      document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
      document.querySelectorAll('.tab-pane').forEach(p => p.classList.remove('active'));
      btn.classList.add('active');
      const target = btn.getAttribute('data-tab');
      const pane = document.getElementById(target);
      if (pane) pane.classList.add('active');
    });
  });

  // Salary Slider
  if (els.salarySlider) {
    els.salarySlider.addEventListener('input', (e) => {
      gameState.salaryWithdrawal = parseInt(e.target.value, 10);
      if (els.salaryValue) els.salaryValue.textContent = formatNaira(gameState.salaryWithdrawal);
    });
  }

  // Next Week Turn Simulation
  if (els.btnNextWeek) {
    els.btnNextWeek.addEventListener('click', () => {
      simulateWeek();
    });
  }

  // Theme Toggle
  if (els.themeToggle) {
    els.themeToggle.addEventListener('click', () => {
      const current = document.documentElement.getAttribute('data-theme') || 'dark';
      const next = current === 'dark' ? 'light' : 'dark';
      document.documentElement.setAttribute('data-theme', next);
      els.themeToggle.textContent = next === 'dark' ? '🌙' : '☀️';
      localStorage.setItem('hustle_theme', next);
    });

    const savedTheme = localStorage.getItem('hustle_theme');
    if (savedTheme) {
      document.documentElement.setAttribute('data-theme', savedTheme);
      els.themeToggle.textContent = savedTheme === 'dark' ? '🌙' : '☀️';
    }
  }

  // Character Switcher
  const switchBtns = document.querySelectorAll('.char-card-btn');
  switchBtns.forEach((btn, index) => {
    btn.addEventListener('click', () => {
      const char = characters[index];
      gameState.character = char;
      gameState.personalCash = char.personalCash;
      gameState.businessCash = char.businessCash;
      gameState.inventory = char.inventory;
      soundFx.playCash();
      updateUI();
      alert(`Switched character to ${char.name} (${char.title})!`);
    });
  });
}

// Restock Action
window.restockInventory = function(units) {
  const cost = units * gameState.unitWholesaleCost;
  if (gameState.businessCash < cost) {
    alert("Oga, insufficient business cash! Transfer salary back or wait for customer sales.");
    return;
  }
  gameState.businessCash -= cost;
  gameState.inventory += units;
  soundFx.playCash();
  updateUI();
  showToast(`Restocked +${units} units for ${formatNaira(cost)}!`);
};

// Buy Meal Action
window.buyMeal = function(name, cost, energyBoost, stressRelief) {
  if (gameState.personalCash < cost) {
    alert("Insufficient personal wallet cash! Draw salary from business account first.");
    return;
  }
  gameState.personalCash -= cost;
  gameState.energy = Math.min(100, gameState.energy + energyBoost);
  gameState.stress = Math.max(0, gameState.stress - stressRelief);
  soundFx.playCash();
  updateUI();
  showToast(`Enjoyed ${name}! Energy +${energyBoost}%, Stress -${stressRelief}%.`);
};

// Contribute to Ajo
window.contributeAjo = function() {
  const amount = gameState.ajoWeeklyQuota;
  if (gameState.personalCash < amount) {
    alert("You do not have enough cash in your personal wallet to contribute ₦10,000 this week!");
    return;
  }
  gameState.personalCash -= amount;
  gameState.ajoSaved += amount;
  gameState.reputation = Math.min(100, gameState.reputation + 2);
  soundFx.playCash();
  updateUI();
  showToast(`Contributed ${formatNaira(amount)} to Balogun Unity Ajo circle!`);
};

// Trigger Dynamic Nigerian Event
function triggerRandomEvent() {
  const randomEvent = eventsPool[Math.floor(Math.random() * eventsPool.length)];
  gameState.activeEvent = randomEvent;
  
  if (!els.eventBox) return;
  
  els.eventBox.innerHTML = `
    <div class="event-header">
      <div class="event-icon">${randomEvent.icon}</div>
      <div class="event-meta">
        <span class="event-category">${randomEvent.category}</span>
        <h3>${randomEvent.title}</h3>
      </div>
    </div>
    <p class="event-description">${randomEvent.description}</p>
    <div class="event-choices-list">
      ${randomEvent.choices.map((c, i) => `
        <div class="choice-card-btn" onclick="handleChoiceSelection(${i})">
          <div class="choice-text">Option ${i+1}: ${c.text}</div>
          <div class="choice-impacts">
            ${c.businessDelta !== 0 ? `<span class="impact-badge ${c.businessDelta > 0 ? 'positive' : 'negative'}">Biz: ${c.businessDelta > 0 ? '+' : ''}${formatNaira(c.businessDelta)}</span>` : ''}
            ${c.reputationDelta !== 0 ? `<span class="impact-badge ${c.reputationDelta > 0 ? 'positive' : 'negative'}">Rep: ${c.reputationDelta > 0 ? '+' : ''}${c.reputationDelta}</span>` : ''}
          </div>
        </div>
      `).join('')}
    </div>
  `;
}

window.handleChoiceSelection = function(choiceIndex) {
  const choice = gameState.activeEvent.choices[choiceIndex];
  if (!choice) return;

  gameState.businessCash += choice.businessDelta;
  gameState.personalCash += choice.personalDelta;
  gameState.reputation = Math.max(0, Math.min(100, gameState.reputation + choice.reputationDelta));
  gameState.energy = Math.max(0, Math.min(100, gameState.energy + choice.energyDelta));
  
  soundFx.playEvent();
  updateUI();
  
  els.eventBox.innerHTML = `
    <div style="text-align: center; padding: 20px 0;">
      <div style="font-size: 2.5rem; margin-bottom: 12px;">✅</div>
      <h3 style="color: var(--gold-light); margin-bottom: 8px;">Decision Executed!</h3>
      <p style="font-size: 1rem; color: var(--text-primary); max-width: 500px; margin: 0 auto 16px;">${choice.message}</p>
      <button class="btn-buy-sm" style="padding: 10px 20px; font-size: 0.9rem;" onclick="triggerRandomEvent()">Next Street Event ⚡</button>
    </div>
  `;
};

// Weekly Simulation Engine
function simulateWeek() {
  soundFx.playCash();
  
  // Sales volume based on inventory, energy, and reputation
  const baseDemand = Math.floor(gameState.reputation * 0.7 + (gameState.energy * 0.3));
  const unitsSold = Math.min(gameState.inventory, Math.max(5, baseDemand));
  const revenue = unitsSold * gameState.unitSellingPrice;
  const cogs = unitsSold * gameState.unitWholesaleCost;
  const grossProfit = revenue - cogs;
  
  // Weekly shop overhead (rent, generator fuel, waste)
  const overhead = 18000;
  
  // Salary withdrawal transfer
  let salaryPaid = 0;
  if (gameState.businessCash >= gameState.salaryWithdrawal) {
    salaryPaid = gameState.salaryWithdrawal;
    gameState.businessCash -= salaryPaid;
    gameState.personalCash += salaryPaid;
  }
  
  // Update inventory and business cash
  gameState.inventory -= unitsSold;
  gameState.businessCash += (revenue - overhead);
  
  // Ajo payout condition (e.g. week 4)
  let ajoPayoutMsg = '';
  if (gameState.week === gameState.ajoPayoutWeek) {
    const payoutPot = 50000; // 5 members x ₦10,000
    gameState.personalCash += payoutPot;
    ajoPayoutMsg = `\n🎉 AJO JACKPOT: It was your turn to collect the Esusu pot! Received ${formatNaira(payoutPot)} cash in your personal wallet!`;
  }
  
  // Fatigue and stress accumulation
  gameState.energy = Math.max(15, gameState.energy - 12);
  gameState.stress = Math.min(95, gameState.stress + 10);
  
  gameState.week += 1;
  updateUI();
  triggerRandomEvent();
  
  alert(`📊 Week ${gameState.week - 1} Business Report:\n` +
        `• Units Sold: ${unitsSold} units\n` +
        `• Gross Revenue: ${formatNaira(revenue)}\n` +
        `• Overheads & Fuel: -${formatNaira(overhead)}\n` +
        `• Salary Withdrawn: ${formatNaira(salaryPaid)}\n` +
        `• Remaining Stock: ${gameState.inventory} units` +
        ajoPayoutMsg);
}

// Toast notification helper
function showToast(msg) {
  const toast = document.createElement('div');
  toast.style.position = 'fixed';
  toast.style.bottom = '24px';
  toast.style.right = '24px';
  toast.style.background = 'rgba(0, 135, 81, 0.95)';
  toast.style.color = '#fff';
  toast.style.padding = '12px 20px';
  toast.style.borderRadius = '10px';
  toast.style.fontWeight = '700';
  toast.style.boxShadow = '0 6px 20px rgba(0,0,0,0.4)';
  toast.style.zIndex = '9999';
  toast.style.transition = 'all 0.3s ease';
  toast.textContent = msg;
  document.body.appendChild(toast);
  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateY(10px)';
    setTimeout(() => toast.remove(), 300);
  }, 2500);
}

// Run on load
document.addEventListener('DOMContentLoaded', initGame);
