# Currency Converter Pro

A premium-looking Windows 11 style desktop currency converter built with **pure Java Swing**
(no JavaFX, no external UI libraries, no database). Every surface — buttons, combo boxes,
text fields, cards, toasts, dialogs, spinner, splash — is a custom `Graphics2D` component.


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
