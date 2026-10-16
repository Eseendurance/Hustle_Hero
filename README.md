<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# 🎮 Hustle Hero (Naija Hustle & Business Simulation)

A Nigerian business & personal finance simulation game built with **Jetpack Compose**, **Kotlin**, and **Firebase**. Run your hustle, balance budgets, save in Ajo (Esusu), make decisions on Lagos streets, and conquer the markets!

---

## 🌟 Key Features
- **Dual Wallet Architecture:** Manage personal expenses vs. business cash flow (reinvest, take salary draws, or inject capital).
- **Ajo (Esusu) Savings Circles:** Traditional rotational savings pools with scheduled payout pots.
- **Dynamic Lagos Events:** NEPA power cuts, council levies, generator fuel crises, market flash sales, and customer credit books.
- **Everyday Life & Food Routines:** Fuel up with Nigerian street food (Suya, Jollof, Amala, Shawarma) while managing energy and health.
- **3D Market & Radio Stations:** Wazobia radio updates, street trends, and visual market navigation.
- **Leaderboards & Community:** Real-time Firebase-backed competitive leaderboards.

---

## 🚀 How to Run Locally

### Prerequisites
- **Android Studio** (Koala / Ladybug or newer recommended)
- **JDK 17+ or JDK 21+** (included in Android Studio under `/jbr`)
- Android SDK Platform 36 (Android 15 / 16 preview)

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/Eseendurance/Hustle_Hero.git
   cd Hustle_Hero
   ```
2. Set up environment variables:
   Copy `.env.example` to `.env` and configure your `GEMINI_API_KEY`:
   ```bash
   cp .env.example .env
   ```
3. Build and run via Gradle CLI or Android Studio:
   ```bash
   ./gradlew assembleDebug
   ```
   Or open the folder in Android Studio and click **Run 'app'** (Shift + F10) on an emulator or connected device.

---

## 📦 Best Places to Deploy for Free

Since **Hustle Hero** is an Android mobile game (APK/AAB), here are the top 100% free deployment options:

1. **GitHub Releases & Actions (Configured out of the box!):**
   - The included workflow `.github/workflows/build-apk.yml` builds your APK automatically on every commit.
   - You can download the APK artifact directly from your GitHub repository under the **Actions** tab or create a **GitHub Release** with the APK attached.
2. **Firebase App Distribution (100% Free):**
   - Because Hustle Hero already integrates Firebase, you can enable **App Distribution** in the Firebase Console.
   - Send instant APK test builds to players' emails or share an invite link. Testers receive automatic in-app updates!
3. **itch.io (100% Free for Indie Games):**
   - Create a free developer page on [itch.io](https://itch.io).
   - Upload your `app-debug.apk` with screenshots. itch.io gives you a custom public page where users can download and install your game directly.
4. **Amazon Appstore (Free Developer Account):**
   - Unlike Google Play's $25 registration fee, Amazon Appstore developer registration is completely free and reaches Android devices and Fire tablets.

