# the Alculator → Play Console — your step-by-step

Everything I built is ready in this folder. This is the part only you can do (it needs your
Google login). Work top to bottom; it takes ~30–45 min the first time.

---

## 0. Files you'll need (all in `/home/pod/projects/sesh-score/`)
- **App Bundle**: `app/build/outputs/bundle/release/Alculator-release.aab`
- **Icon**: `playstore/icon-512.png`
- **Feature graphic**: `playstore/feature-1024x500.png`
- **Listing text**: `playstore/listing.md`
- **Privacy HTML**: `playstore/privacy/index.html`
- **Screenshots**: you capture these (step 2)

⚠️ **KEYSTORE — DO NOT LOSE.** `alculator.keystore` is your upload key.
Password / key password: `Alc-a2cea84062fca44c`  · alias: `alculator`.
Save these in your password manager now. Back up the `.keystore` file off the server.
(With Play App Signing an upload key can be reset if lost, but keep it anyway.)

---

## 1. Host the privacy policy (GitHub Pages)
1. Create a new **public** GitHub repo, e.g. `alculator-privacy`.
2. Upload `playstore/privacy/index.html` to the repo root.
3. Repo **Settings → Pages → Build from branch → main / root → Save**.
4. After a minute your URL is: `https://<your-username>.github.io/alculator-privacy/`
5. Open it to confirm it loads. Keep the URL for step 4.

## 2. Screenshots (on your S23)
Capture 2–4 (min 2 required). Suggested:
- Empty state (serif wordmark)
- A ranked list with 3–4 drinks
- The Add Drink sheet open
- A multipack card (e.g. 440ml ×6)
Then drop them in `playstore/screenshots/` on the server and tell me — I'll validate/resize
to Play specs (PNG/JPG, 9:16, 1080×1920 is ideal) if needed. Phone screenshots already meet specs.

## 3. Create the app
Play Console → **Create app**:
- App name: **the Alculator**
- Default language: **English (United Kingdom)**
- App or game: **App**
- Free or paid: **Free** (you can't switch paid→free later, but free→ stays free)
- Accept the declarations → **Create app**.

## 4. Set up the listing & policies (left-hand menu)
**Store presence → Main store listing**
- App name: `the Alculator`
- Short description / Full description: copy from `playstore/listing.md`
- App icon: upload `icon-512.png`
- Feature graphic: upload `feature-1024x500.png`
- Phone screenshots: upload from step 2
- App category: **Lifestyle** · Contact email: `paul.edward.odea@gmail.com`
- Save.

**Policy → App content** (complete each card):
- **Privacy policy**: paste the GitHub Pages URL from step 1.
- **Ads**: No, contains no ads.
- **App access**: All functionality available without restrictions (no login).
- **Content rating**: start questionnaire → category Utility/Reference or Lifestyle →
  answer **YES** to "references to alcohol / promotes alcohol". Everything else No
  (no violence, no user content, no data sharing). → expect a **PEGI 18 / Mature** rating. ✓
- **Target audience**: select **18+ only**. Not appealing to children → No.
- **Data safety**: **No data collected**, **No data shared**. Submit.
- **Government apps**: No. **Financial features**: None. **Health**: No.

## 5. Upload the build (Internal testing)
**Testing → Internal testing → Create new release**
- When prompted about **Play App Signing**: **accept / continue** (Google manages the
  signing key; your keystore stays the upload key). 
- Upload `Alculator-release.aab`.
- Release name auto-fills `1 (1.0)`. Release notes: `First release.`
- **Next → Save → Review release → Start rollout to Internal testing**.

## 6. Add testers & install
- Internal testing → **Testers** tab → create an email list (add your own Gmail at least).
- Copy the **opt-in URL**, open it on the S23, accept, then install from the Play link.
- Confirm it runs (empty state → add drinks → rank → swipe/edit/share).

## 7. Later: go public
When you're happy, **Testing → Internal testing → Promote release → Production**, then
complete the Production rollout. First Production review for an alcohol-themed app can take
a few days.

---

### Quick reference — app facts for any form that asks
- Package name: `com.alculator`
- Version: `1.0` (versionCode 1)
- Min Android: 8.0 (API 26) · Target: Android 15 (API 35)
- Collects data: No · Internet permission: No · Ads: No · IAP: No
