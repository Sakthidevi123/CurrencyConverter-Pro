# Currency Converter Pro

A premium-looking Windows 11 style desktop currency converter built with **pure Java Swing**
(no JavaFX, no external UI libraries, no database). Every surface — buttons, combo boxes,
text fields, cards, toasts, dialogs, spinner, splash — is a custom `Graphics2D` component.

![Java](https://img.shields.io/badge/Java-21%2B-blue)
![Swing](https://img.shields.io/badge/UI-Java%20Swing-2563EB)
![License](https://img.shields.io/badge/license-MIT-green)

---

## Features

| Area | What it does |
|---|---|
| Conversion | Live rates for 35 currencies, `SwingWorker` powered, UI never freezes |
| Offline mode | Last successful response is cached to `rates.cache` and reused when offline |
| Search | Type `ind`, `aus`, `uni` to filter both currency lists by code, name or country |
| Swap | Swaps currencies and promotes the last result into the amount field |
| Favourites | One-click pairs (`USD → INR`, …); add with ☆, remove with right click |
| History | Last 20 conversions, persisted in `history.txt`, click an entry to reuse it |
| Export | CSV export with `Date, Time, Amount, From, To, Rate, Result` columns |
| Themes | Light, Dark, Blue, Emerald, Purple — persisted in `theme.properties` |
| Settings | Theme, font size, language, default currencies, auto-save, animations, sound |
| Extras | Splash screen, statistics, mini calculator, toasts, tooltips, live clock |

### Keyboard shortcuts

| Shortcut | Action |
|---|---|
| `Ctrl + Enter` | Convert |
| `Ctrl + D` | Toggle dark mode |
| `Ctrl + H` | Focus history |
| `Ctrl + S` | Export CSV |
| `Ctrl + C` | Copy result |
| `Ctrl + R` | Refresh rates |

---

## Project structure

```text
CurrencyConverterPro/
├── build.xml                 NetBeans (Ant) build script
├── manifest.mf
├── nbproject/
│   ├── project.xml
│   └── project.properties
├── lib/
│   └── json-20240303.jar     org.json
├── run.bat / run.sh          compile + run without NetBeans
└── src/currencyconverter/
    ├── CurrencyConvert.java      main window (view + controller wiring)
    ├── CurrencyAPI.java          all networking + offline cache
    ├── Currency.java             currency value object + catalogue
    ├── ConversionRecord.java     history entry model
    ├── ThemeManager.java         palettes + theme.properties
    ├── SettingsManager.java      settings.properties
    ├── HistoryManager.java       history.txt
    ├── FavoriteManager.java      favorites.txt
    ├── CSVExporter.java          history.csv
    ├── ClipboardManager.java
    ├── ConnectionManager.java
    ├── InputValidator.java
    ├── LanguageManager.java      EN / TA / HI / FR / DE / JA
    ├── MiniCalculator.java
    ├── Dialogs.java
    ├── SplashScreenWindow.java
    ├── resources/               icon placeholders
    └── ui/
        ├── RoundedButton.java    hover fade + ripple + variants
        ├── RoundedPanel.java
        ├── RoundedTextField.java placeholder + focus ring
        ├── RoundedComboBox.java  flat popup, custom chevron
        ├── RoundedScrollPane.java
        ├── RoundedDialog.java    undecorated, draggable, ESC to close
        ├── GradientPanel.java
        ├── ShadowPanel.java      soft layered drop shadow
        ├── GlassPanel.java       glassmorphism surface
        ├── LoadingSpinner.java
        └── Toast.java            fading toast notifications
```

---

## Opening in Apache NetBeans

1. `File → Open Project…` and select the `CurrencyConverterPro` folder.
2. NetBeans regenerates `nbproject/build-impl.xml` automatically on first build.
3. If the JSON library is not listed: `Project → Properties → Libraries → Add JAR`
   and pick `lib/json-20240303.jar`.
4. Press **F6** to run. Main class: `currencyconverter.CurrencyConvert`.

### Without NetBeans

```bash
./run.sh          # macOS / Linux
run.bat           # Windows
```

---

## API key

All networking lives in `CurrencyAPI.java`:

```java
private static final String API_KEY = "";
```

Paste your [exchangerate-api.com](https://www.exchangerate-api.com/) key between the quotes.
While the key is empty the app automatically uses the free, key-less
`open.er-api.com` endpoint so it still runs out of the box.

---

## Generated data files

Created automatically in the working directory on first run:
`history.txt`, `favorites.txt`, `theme.properties`, `settings.properties`,
`rates.cache`, and `history.csv` on export.

---

## Architecture notes

- **MVC inspired** — `CurrencyConvert` is the view/controller; the manager classes hold
  all state and persistence; `CurrencyAPI` owns every network call. No UI class touches
  HTTP, and no manager touches Swing.
- **SOLID** — one responsibility per manager, custom components depend on palettes passed
  in rather than reading global state, new themes are added by one map entry.
- **No stack traces reach the user** — every failure surfaces as a styled dialog or toast.

## License

MIT
