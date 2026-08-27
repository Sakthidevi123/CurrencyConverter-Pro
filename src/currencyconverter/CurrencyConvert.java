package currencyconverter;

import currencyconverter.ui.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

public final class CurrencyConvert extends JFrame {

    public static final String VERSION = "1.0.0";
    private static final String APP_TITLE = "Currency Converter Pro";

    private final SettingsManager settings = new SettingsManager();
    private final ThemeManager themeManager = new ThemeManager();
    private final HistoryManager historyManager = new HistoryManager();
    private final FavoriteManager favoriteManager = new FavoriteManager();
    private final ConnectionManager connectionManager = new ConnectionManager();
    private final LanguageManager language = new LanguageManager();
    private final CurrencyAPI api = new CurrencyAPI();

    private final List<Currency> catalogue = Currency.catalogue();
    private final Map<String, Currency> byCode = new LinkedHashMap<>();
    private double lastRate;
    private double lastResult;
    private String lastResultText = "";
    private LocalDateTime lastUpdated;

    private GradientPanel header;
    private JLabel titleLabel, dateLabel, timeLabel, statusDotLabel, connectionLabel;
    private RoundedButton themeButton, settingsButton;

    private JPanel bodyPanel;
    private ShadowPanel converterCard, resultCard, historyCard;
    private JLabel amountLabel, fromLabel, toLabel, favoritesLabel, historyTitle, historyEmptyLabel;
    private RoundedTextField amountField, searchField;
    private RoundedComboBox<Currency> fromCombo, toCombo;
    private RoundedButton swapButton, convertButton, clearButton, refreshButton,
            copyButton, exportButton, favoriteButton;
    private LoadingSpinner spinner;
    private JLabel loadingLabel;

    private JLabel resultValueLabel, resultRateLabel, resultUpdatedLabel, resultFlagLabel, resultCaption;
    private JPanel favoritesStrip, historyList;
    private RoundedScrollPane historyScroll;

    private JPanel statusBar;
    private JLabel statusReadyLabel, statusApiLabel, statusVersionLabel;

    private float resultAlpha = 1f;
    private Timer resultFade;
    private Timer clockTimer;

    public CurrencyConvert() {
        for (Currency currency : catalogue) {
            byCode.put(currency.getCode(), currency);
        }
        language.setLanguage(settings.get(SettingsManager.KEY_LANGUAGE, "English"));

        initFrame();
        buildHeader();
        buildBody();
        buildStatusBar();
        registerShortcuts();
        applyTheme();
        restoreSession();
        startClock();
        probeConnection();
    }

    private void initFrame() {
        setTitle(APP_TITLE);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(950, 650));
        setSize(1180, 760);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setIconImage(buildAppIcon());

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { shutdown(); }
        });
    }

  
    private Image buildAppIcon() {
        int size = 64;
        java.awt.image.BufferedImage image =
                new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setPaint(new GradientPaint(0, 0, new Color(0x2563EB), size, size, new Color(0x3B82F6)));
        g2.fillRoundRect(0, 0, size, size, 18, 18);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 34));
        FontMetrics fm = g2.getFontMetrics();
        String glyph = "\u20B9";
        g2.drawString(glyph, (size - fm.stringWidth(glyph)) / 2, (size - fm.getHeight()) / 2 + fm.getAscent());
        g2.dispose();
        return image;
    }
    
//header
    
    private void buildHeader() {
        header = new GradientPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(16, 26, 16, 22));
        header.setPreferredSize(new Dimension(0, 86));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        left.setOpaque(false);

        JLabel logo = new JLabel(new ImageIcon(buildAppIcon().getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        left.add(logo);

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));

        titleLabel = new JLabel(APP_TITLE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Live exchange rates for 35 world currencies");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(255, 255, 255, 200));

        titles.add(titleLabel);
        titles.add(Box.createVerticalStrut(2));
        titles.add(subtitle);
        left.add(titles);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        JPanel clock = new JPanel();
        clock.setOpaque(false);
        clock.setLayout(new BoxLayout(clock, BoxLayout.Y_AXIS));
        dateLabel = new JLabel("", SwingConstants.RIGHT);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(255, 255, 255, 210));
        dateLabel.setAlignmentX(RIGHT_ALIGNMENT);
        timeLabel = new JLabel("", SwingConstants.RIGHT);
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setAlignmentX(RIGHT_ALIGNMENT);
        clock.add(dateLabel);
        clock.add(timeLabel);
        right.add(clock);
        right.add(Box.createHorizontalStrut(6));

        GlassPanel connection = new GlassPanel(new FlowLayout(FlowLayout.CENTER, 7, 6));
        connection.setPreferredSize(new Dimension(112, 36));
        statusDotLabel = new JLabel("\u25CF");
        statusDotLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        statusDotLabel.setForeground(new Color(0xF59E0B));
        connectionLabel = new JLabel("Checking");
        connectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        connectionLabel.setForeground(Color.WHITE);
        connection.add(statusDotLabel);
        connection.add(connectionLabel);
        right.add(connection);

        themeButton = headerButton(" Dark", "Toggle dark mode (Ctrl + D)");
        themeButton.addActionListener(e -> toggleTheme());
        right.add(themeButton);

        settingsButton = headerButton("Settings", "Open settings");
        settingsButton.setPreferredSize(new Dimension(80, 36));
        settingsButton.addActionListener(e -> openSettings());
        right.add(settingsButton);

        RoundedButton helpButton = headerButton("?", "Help and keyboard shortcuts");
        helpButton.setPreferredSize(new Dimension(44, 36));
        helpButton.addActionListener(e -> openHelp());
        right.add(helpButton);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
    }

    private RoundedButton headerButton(String text, String tooltip) {
    RoundedButton button = new RoundedButton(text, RoundedButton.Variant.GHOST);
    button.setToolTipText(tooltip);
    button.setFont(new Font("Segoe UI", Font.BOLD, 13));

    // Header buttons: keep white text and use blue hover background
    button.applyPalette(
            new Color(0x2563EB),   // primary / hover tint source
            new Color(0x60A5FA),   // accent
            new Color(0x1D4ED8),   // surface
            Color.WHITE,            // text
            null                    // no border
    );

    button.setPreferredSize(new Dimension(96, 36));
    return button;
}

   //body
    
    private void buildBody() {
        bodyPanel = new JPanel(new BorderLayout(18, 0));
        bodyPanel.setBorder(BorderFactory.createEmptyBorder(18, 20, 8, 20));

        JPanel leftColumn = new JPanel(new BorderLayout(0, 16));
        leftColumn.setOpaque(false);
        leftColumn.add(buildConverterCard(), BorderLayout.CENTER);
        leftColumn.add(buildResultCard(), BorderLayout.SOUTH);

        bodyPanel.add(leftColumn, BorderLayout.CENTER);
        bodyPanel.add(buildHistoryCard(), BorderLayout.EAST);
        add(bodyPanel, BorderLayout.CENTER);
    }

    private JComponent buildConverterCard() {
        converterCard = new ShadowPanel(new BorderLayout());
        converterCard.setArc(24);

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBorder(BorderFactory.createEmptyBorder(22, 26, 22, 26));

       //search
        JPanel searchRow = new JPanel(new BorderLayout(10, 0));
        searchRow.setOpaque(false);
        searchRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        searchField = new RoundedTextField("Search currency, code or country \u2014 try \"ind\", \"aus\", \"uni\"");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setToolTipText("Filters both currency lists as you type");
        searchField.getDocument().addDocumentListener(new SimpleDocumentListener(this::applySearchFilter));
        searchRow.add(searchField, BorderLayout.CENTER);
        inner.add(searchRow);
        inner.add(Box.createVerticalStrut(18));

        //amount
        amountLabel = fieldLabel("Amount");
        inner.add(row(amountLabel));
        inner.add(Box.createVerticalStrut(7));
        amountField = new RoundedTextField("Enter Amount");
        amountField.setFont(new Font("Segoe UI", Font.BOLD, 20));
        amountField.setToolTipText("Digits only, greater than zero");
        amountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        amountField.setPreferredSize(new Dimension(100, 52));
        amountField.addActionListener(e -> convert());
        inner.add(amountField);
        inner.add(Box.createVerticalStrut(18));

        //currency selector
        JPanel selectors = new JPanel(new GridBagLayout());
        selectors.setOpaque(false);
        selectors.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 0, 6, 0);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.gridy = 0; c.weightx = 1;
        fromLabel = fieldLabel("From Currency");
        selectors.add(fromLabel, c);

        c.gridx = 1; c.weightx = 0; c.insets = new Insets(0, 14, 6, 14);
        selectors.add(new JLabel(" "), c);

        c.gridx = 2; c.weightx = 1; c.insets = new Insets(0, 0, 6, 0);
        toLabel = fieldLabel("To Currency");
        selectors.add(toLabel, c);

        c.gridy = 1; c.gridx = 0; c.weightx = 1;
        fromCombo = new RoundedComboBox<>(catalogue.toArray(new Currency[0]));
        fromCombo.setPreferredSize(new Dimension(100, 48));
        fromCombo.setToolTipText("Currency you are converting from");
        selectors.add(fromCombo, c);

        c.gridx = 1; c.weightx = 0; c.insets = new Insets(0, 14, 6, 14);
        swapButton = new RoundedButton("Swap", RoundedButton.Variant.SECONDARY);
        swapButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
        swapButton.setPreferredSize(new Dimension(80, 48));
        swapButton.setArc(24);
        swapButton.setToolTipText("Swap currencies");
        swapButton.addActionListener(e -> swapCurrencies());
        selectors.add(swapButton, c);

        c.gridx = 2; c.weightx = 1; c.insets = new Insets(0, 0, 6, 0);
        toCombo = new RoundedComboBox<>(catalogue.toArray(new Currency[0]));
        toCombo.setPreferredSize(new Dimension(100, 48));
        toCombo.setToolTipText("Currency you are converting to");
        selectors.add(toCombo, c);

        inner.add(selectors);
        inner.add(Box.createVerticalStrut(20));

      //actions
        JPanel actions = new JPanel(new GridBagLayout());
        actions.setOpaque(false);
        actions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        GridBagConstraints a = new GridBagConstraints();
        a.fill = GridBagConstraints.BOTH;
        a.gridy = 0; a.gridx = 0; a.weightx = 1; a.insets = new Insets(0, 0, 0, 10);

        convertButton = new RoundedButton("Convert", RoundedButton.Variant.PRIMARY);
        convertButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        convertButton.setPreferredSize(new Dimension(100, 50));
        convertButton.setArc(18);
        convertButton.setToolTipText("Convert (Ctrl + Enter)");
        convertButton.addActionListener(e -> convert());
        actions.add(convertButton, a);

        a.gridx = 1; a.weightx = 0;
        clearButton = new RoundedButton("Clear", RoundedButton.Variant.SECONDARY);
        clearButton.setPreferredSize(new Dimension(104, 50));
        clearButton.setArc(18);
        clearButton.setToolTipText("Clear the form");
        clearButton.addActionListener(e -> clearAll());
        actions.add(clearButton, a);

        a.gridx = 2;
        refreshButton = new RoundedButton("Refresh", RoundedButton.Variant.SECONDARY);
        refreshButton.setPreferredSize(new Dimension(128, 50));
        refreshButton.setArc(18);
        refreshButton.setToolTipText("Fetch the latest rates (Ctrl + R)");
        refreshButton.addActionListener(e -> refreshRates());
        actions.add(refreshButton, a);

        a.gridx = 3; a.insets = new Insets(0, 0, 0, 0);
        favoriteButton = new RoundedButton("Add", RoundedButton.Variant.SECONDARY);

        favoriteButton.setPreferredSize(new Dimension(100, 50));
        favoriteButton.setArc(18);
        favoriteButton.setToolTipText("Add or remove this pair from favourites");
        favoriteButton.addActionListener(e -> toggleFavorite());
        fromCombo.addActionListener(e -> updateFavoriteButton());
        toCombo.addActionListener(e -> updateFavoriteButton());
        actions.add(favoriteButton, a);

        inner.add(actions);
        inner.add(Box.createVerticalStrut(12));

        /* --- loading row --- */
        JPanel loadingRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        loadingRow.setOpaque(false);
        loadingRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        spinner = new LoadingSpinner();
        loadingLabel = new JLabel("Fetching latest exchange rates\u2026");
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loadingLabel.setVisible(false);
        loadingRow.add(spinner);
        loadingRow.add(loadingLabel);
        inner.add(loadingRow);
        inner.add(Box.createVerticalStrut(10));

        /* --- favourites --- */
        favoritesLabel = fieldLabel("Favourites");
        inner.add(row(favoritesLabel));
        inner.add(Box.createVerticalStrut(8));
        favoritesStrip = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        favoritesStrip.setOpaque(false);
        favoritesStrip.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));
        inner.add(favoritesStrip);

        converterCard.add(inner, BorderLayout.CENTER);
        return converterCard;
    }

    private JComponent buildResultCard() {
        resultCard = new ShadowPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, resultAlpha));
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        resultCard.setArc(24);
        resultCard.setPreferredSize(new Dimension(100, 176));

        JPanel inner = new JPanel(new BorderLayout(18, 0));
        inner.setOpaque(false);
        inner.setBorder(BorderFactory.createEmptyBorder(20, 26, 20, 26));

        resultFlagLabel = new JLabel("\uD83C\uDDEE\uD83C\uDDF3", SwingConstants.CENTER);
        resultFlagLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        resultFlagLabel.setPreferredSize(new Dimension(72, 72));
        JPanel flagWrap = new JPanel(new BorderLayout());
        flagWrap.setOpaque(false);
        flagWrap.add(resultFlagLabel, BorderLayout.NORTH);

        JPanel texts = new JPanel();
        texts.setOpaque(false);
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));

        resultCaption = new JLabel("Converted Amount");
        resultCaption.setFont(new Font("Segoe UI", Font.BOLD, 12));

        resultValueLabel = new JLabel("\u2014");
        resultValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 34));

        resultRateLabel = new JLabel("Enter an amount and press Convert to see the live rate");
        resultRateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        resultUpdatedLabel = new JLabel("Last updated \u2014");
        resultUpdatedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        texts.add(resultCaption);
        texts.add(Box.createVerticalStrut(4));
        texts.add(resultValueLabel);
        texts.add(Box.createVerticalStrut(6));
        texts.add(resultRateLabel);
        texts.add(Box.createVerticalStrut(3));
        texts.add(resultUpdatedLabel);

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));

        copyButton = new RoundedButton("Copy", RoundedButton.Variant.SECONDARY);
        copyButton.setPreferredSize(new Dimension(132, 42));
        copyButton.setMaximumSize(new Dimension(132, 42));
        copyButton.setToolTipText("Copy the result (Ctrl + C)");
        copyButton.addActionListener(e -> copyResult());

        exportButton = new RoundedButton("Export CSV", RoundedButton.Variant.SECONDARY);
        exportButton.setPreferredSize(new Dimension(132, 42));
        exportButton.setMaximumSize(new Dimension(132, 42));
        exportButton.setToolTipText("Export history to CSV (Ctrl + S)");
        exportButton.addActionListener(e -> exportCsv());

        buttons.add(copyButton);
        buttons.add(Box.createVerticalStrut(10));
        buttons.add(exportButton);

        inner.add(flagWrap, BorderLayout.WEST);
        inner.add(texts, BorderLayout.CENTER);
        inner.add(buttons, BorderLayout.EAST);
        resultCard.add(inner, BorderLayout.CENTER);
        return resultCard;
    }

    private JComponent buildHistoryCard() {
        historyCard = new ShadowPanel(new BorderLayout());
        historyCard.setArc(24);
        historyCard.setPreferredSize(new Dimension(352, 100));

        JPanel inner = new JPanel(new BorderLayout(0, 12));
        inner.setOpaque(false);
        inner.setBorder(BorderFactory.createEmptyBorder(20, 20, 18, 16));

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        historyTitle = new JLabel("Recent Conversions");
        historyTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));

        RoundedButton clearHistory = new RoundedButton("Clear", RoundedButton.Variant.SECONDARY);
        clearHistory.setFont(new Font("Segoe UI", Font.BOLD, 12));
        clearHistory.setPreferredSize(new Dimension(62, 28));
        clearHistory.setToolTipText("Remove all stored conversions");
        clearHistory.addActionListener(e -> {
            historyManager.clear();
            refreshHistory();
            Toast.show(this, "History cleared", Toast.Type.INFO);
        });

        head.add(historyTitle, BorderLayout.WEST);
        head.add(clearHistory, BorderLayout.EAST);

        historyList = new JPanel();
        historyList.setOpaque(false);
        historyList.setLayout(new BoxLayout(historyList, BoxLayout.Y_AXIS));

        historyScroll = new RoundedScrollPane(historyList);

        JPanel footer = new JPanel(new GridLayout(1, 2, 8, 0));
        footer.setOpaque(false);
        RoundedButton statsButton = new RoundedButton("Statistics", RoundedButton.Variant.SECONDARY);
        statsButton.setPreferredSize(new Dimension(10, 38));
        statsButton.setToolTipText("Usage statistics and most used currencies");
        statsButton.addActionListener(e -> openStatistics());
        RoundedButton calcButton = new RoundedButton("Calculator", RoundedButton.Variant.SECONDARY);
        calcButton.setPreferredSize(new Dimension(10, 38));
        calcButton.setToolTipText("Open the mini calculator");
        calcButton.addActionListener(e -> openCalculator());
        footer.add(statsButton);
        footer.add(calcButton);

        inner.add(head, BorderLayout.NORTH);
        inner.add(historyScroll, BorderLayout.CENTER);
        inner.add(footer, BorderLayout.SOUTH);
        historyCard.add(inner, BorderLayout.CENTER);
        return historyCard;
    }

    private void buildStatusBar() {
        statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEmptyBorder(9, 26, 9, 26));

        statusReadyLabel = new JLabel("Ready");
        statusReadyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 0));
        right.setOpaque(false);
        statusApiLabel = new JLabel("API: checking\u2026");
        statusApiLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusVersionLabel = new JLabel("Version " + VERSION);
        statusVersionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JLabel about = new JLabel("About");
        about.setFont(new Font("Segoe UI", Font.BOLD, 12));
        about.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        about.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { openAbout(); }
        });
        right.add(statusApiLabel);
        right.add(statusVersionLabel);
        right.add(about);

        statusBar.add(statusReadyLabel, BorderLayout.WEST);
        statusBar.add(right, BorderLayout.EAST);
        add(statusBar, BorderLayout.SOUTH);
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return label;
    }

    private JPanel row(JComponent component) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        panel.add(component);
        return panel;
    }

   
    private void applyTheme() {
        ThemeManager.Palette p = themeManager.getPalette();
        int fontDelta = switch (settings.get(SettingsManager.KEY_FONT_SIZE, "Medium")) {
            case "Small" -> -1;
            case "Large" -> 2;
            default -> 0;
        };

        getContentPane().setBackground(p.background);
        bodyPanel.setBackground(p.background);
        statusBar.setBackground(p.card);
        header.setColors(p.headerStart, p.headerEnd);

        for (ShadowPanel card : new ShadowPanel[]{converterCard, resultCard, historyCard}) {
            card.setFill(p.card);
            card.setShadowOpacity(p.dark ? 0.24f : 0.11f);
        }

        for (JLabel label : new JLabel[]{amountLabel, fromLabel, toLabel, favoritesLabel,
                historyTitle, resultValueLabel}) {
            label.setForeground(p.text);
        }
        resultCaption.setForeground(p.primary);
        resultRateLabel.setForeground(p.subText);
        resultUpdatedLabel.setForeground(p.subText);
        loadingLabel.setForeground(p.subText);
        statusReadyLabel.setForeground(p.subText);
        statusApiLabel.setForeground(p.subText);
        statusVersionLabel.setForeground(p.subText);

        amountField.applyPalette(p.dark ? p.background : new Color(0xF8FAFC), p.border, p.primary, p.text, p.subText);
        searchField.applyPalette(p.dark ? p.background : new Color(0xF8FAFC), p.border, p.primary, p.text, p.subText);
        fromCombo.applyPalette(p.dark ? p.background : new Color(0xF8FAFC), p.border, p.text, p.primary);
        toCombo.applyPalette(p.dark ? p.background : new Color(0xF8FAFC), p.border, p.text, p.primary);
        historyScroll.applyPalette(p.scrollThumb);
        spinner.setColor(p.primary);

        boolean animations = settings.getBoolean(SettingsManager.KEY_ANIMATIONS);
        for (RoundedButton button : new RoundedButton[]{convertButton, clearButton, refreshButton,
                swapButton, copyButton, exportButton, favoriteButton}) {
            button.applyPalette(p.primary, p.accent, p.dark ? p.background : new Color(0xF1F5F9), p.text, p.border);
            button.setAnimationsEnabled(animations);
        }

        applyFontScale(this, fontDelta);
        themeButton.setText(p.dark ? "Light" : "Dark");
        refreshFavorites();
        refreshHistory();
        repaint();
    }

    /** Recursively nudges every font by the configured delta. */
    private void applyFontScale(Container container, int delta) {
        if (delta == 0) return;
        for (Component component : container.getComponents()) {
            Font font = component.getFont();
            if (font != null) {
                component.setFont(font.deriveFont((float) Math.max(9, font.getSize() + delta)));
            }
            if (component instanceof Container child) {
                applyFontScale(child, delta);
            }
        }
    }

    private void toggleTheme() {
        themeManager.toggleDarkMode();
        settings.set(SettingsManager.KEY_THEME, themeManager.getThemeName());
        settings.save();
        applyTheme();
        Toast.show(this, themeManager.isDark() ? "Dark theme enabled" : "Light theme enabled", Toast.Type.INFO);
    }

   //behaviour
    
    private void registerShortcuts() {
        JRootPane root = getRootPane();
        bind(root, "ctrl ENTER", this::convert);
        bind(root, "ctrl D", this::toggleTheme);
        bind(root, "ctrl H", () -> historyScroll.requestFocusInWindow());
        bind(root, "ctrl S", this::exportCsv);
        bind(root, "ctrl C", this::copyResult);
        bind(root, "ctrl R", this::refreshRates);
    }

    private void bind(JRootPane root, String stroke, Runnable action) {
        String key = "action-" + stroke;
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(stroke), key);
        root.getActionMap().put(key, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { action.run(); }
        });
    }

    private void startClock() {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm:ss a");
        clockTimer = new Timer(1000, e -> {
            LocalDateTime now = LocalDateTime.now();
            dateLabel.setText(now.format(dateFormat));
            timeLabel.setText(now.format(timeFormat));
        });
        clockTimer.start();
    }

    private void restoreSession() {
        selectCurrency(fromCombo, settings.get(SettingsManager.KEY_DEFAULT_FROM, "USD"));
        selectCurrency(toCombo, settings.get(SettingsManager.KEY_DEFAULT_TO, "INR"));
        String lastAmount = settings.get(SettingsManager.KEY_LAST_AMOUNT, "");
        if (!lastAmount.isBlank()) amountField.setText(lastAmount);
        updateFavoriteButton();
    }

    private void selectCurrency(RoundedComboBox<Currency> combo, String code) {
        Currency currency = byCode.get(code);
        if (currency != null) combo.setSelectedItem(currency);
    }

    private Currency selected(RoundedComboBox<Currency> combo) {
        Object value = combo.getSelectedItem();
        return value instanceof Currency currency ? currency : null;
    }

    private void applySearchFilter() {
        String term = searchField.getText();
        List<Currency> filtered = new ArrayList<>();
        for (Currency currency : catalogue) {
            if (currency.matches(term)) filtered.add(currency);
        }
        if (filtered.isEmpty()) return;

        Currency from = selected(fromCombo);
        Currency to = selected(toCombo);
        Currency[] items = filtered.toArray(new Currency[0]);

        fromCombo.setModel(new DefaultComboBoxModel<>(items));
        toCombo.setModel(new DefaultComboBoxModel<>(items));

        fromCombo.setSelectedItem(filtered.contains(from) ? from : filtered.get(0));
        toCombo.setSelectedItem(filtered.contains(to) ? to : filtered.get(Math.min(1, filtered.size() - 1)));
        updateFavoriteButton();
    }

    private void swapCurrencies() {
        Currency from = selected(fromCombo);
        Currency to = selected(toCombo);
        if (from == null || to == null) return;
        fromCombo.setSelectedItem(to);
        toCombo.setSelectedItem(from);

        /* the previous result becomes the new amount */
        if (lastResult > 0) {
            amountField.setText(new DecimalFormat("0.##").format(lastResult));
        }
        updateFavoriteButton();
        animateResult();
        Toast.show(this, "Currencies swapped", Toast.Type.INFO);
    }

    private void clearAll() {
        amountField.setText("");
        searchField.setText("");
        resultValueLabel.setText("\u2014");
        resultRateLabel.setText("Enter an amount and press Convert to see the live rate");
        resultUpdatedLabel.setText("Last updated \u2014");
        resultFlagLabel.setText("");
        lastResult = 0;
        lastRate = 0;
        lastResultText = "";
        selectCurrency(fromCombo, settings.get(SettingsManager.KEY_DEFAULT_FROM, "USD"));
        selectCurrency(toCombo, settings.get(SettingsManager.KEY_DEFAULT_TO, "INR"));
        updateFavoriteButton();
        setStatus("Form cleared");
    }

    private void convert() {
        Currency from = selected(fromCombo);
        Currency to = selected(toCombo);
        InputValidator.Result validation = InputValidator.validate(amountField.getText(),
                from == null ? null : from.getCode(), to == null ? null : to.getCode());

        if (!validation.isValid()) {
            Dialogs.message(this, themeManager.getPalette(), Dialogs.Kind.WARNING,
                    "Check your input", validation.getMessage());
            return;
        }
        performConversion(validation.getAmount(), from, to);
    }

    private void performConversion(double amount, Currency from, Currency to) {
        setBusy(true);
        setStatus("Fetching latest exchange rates\u2026");

        new SwingWorker<CurrencyAPI.RateResult, Void>() {
            private String failure;

            @Override
            protected CurrencyAPI.RateResult doInBackground() {
                try {
                    return api.fetchRates(from.getCode());
                } catch (CurrencyAPI.RateUnavailableException ex) {
                    failure = ex.getMessage();
                    return null;
                }
            }

            @Override
            protected void done() {
                setBusy(false);
                CurrencyAPI.RateResult result;
                try {
                    result = get();
                } catch (Exception ex) {
                    result = null;
                    failure = "The conversion could not be completed.";
                }

                if (result == null) {
                    connectionManager.setOnline(false);
                    updateConnectionBadge();
                    setStatus("Offline \u2014 no cached rates for " + from.getCode());
                    Dialogs.message(CurrencyConvert.this, themeManager.getPalette(), Dialogs.Kind.ERROR,
                            "Rates unavailable", failure);
                    return;
                }

                double rate = result.rateFor(to.getCode());
                if (rate <= 0) {
                    Dialogs.message(CurrencyConvert.this, themeManager.getPalette(), Dialogs.Kind.ERROR,
                            "Unsupported pair",
                            "No exchange rate is published for " + from.getCode() + " to " + to.getCode() + ".");
                    return;
                }

                connectionManager.setOnline(!result.isOffline());
                updateConnectionBadge();
                showResult(amount, from, to, rate, result.getUpdated(), result.isOffline());
            }
        }.execute();
    }

    private void showResult(double amount, Currency from, Currency to, double rate,
                            LocalDateTime updated, boolean offline) {
        DecimalFormat money = new DecimalFormat("#,##0.00");
        DecimalFormat rateFormat = new DecimalFormat("#,##0.####");

        lastRate = rate;
        lastResult = amount * rate;
        lastUpdated = updated;
        lastResultText = to.getSymbol() + " " + money.format(lastResult);

        resultFlagLabel.setText(to.getFlag());
        resultValueLabel.setText(lastResultText);
        resultRateLabel.setText("1 " + from.getCode() + " = " + rateFormat.format(rate) + " " + to.getCode()
                + "   \u00B7   " + money.format(amount) + " " + from.getCode());
        resultUpdatedLabel.setText("Last updated  "
                + updated.format(DateTimeFormatter.ofPattern("dd MMM yyyy  hh:mm a"))
                + (offline ? "   \u00B7   Offline Mode" : ""));
        animateResult();

        ConversionRecord record = new ConversionRecord(LocalDateTime.now(), amount,
                from.getCode(), to.getCode(), rate, lastResult);
        historyManager.add(record, settings.getBoolean(SettingsManager.KEY_AUTOSAVE));
        refreshHistory();
        countConversion();

        settings.set(SettingsManager.KEY_LAST_AMOUNT, new DecimalFormat("0.##").format(amount));
        settings.save();

        setStatus(offline ? "Offline Mode \u2014 showing last saved rate" : "Conversion successful");
        Toast.show(this, offline ? "Offline Mode \u2014 last saved rate used" : "Conversion successful",
                offline ? Toast.Type.WARNING : Toast.Type.SUCCESS);
        playBeep();
    }

    private void animateResult() {
        if (!settings.getBoolean(SettingsManager.KEY_ANIMATIONS)) {
            resultAlpha = 1f;
            resultCard.repaint();
            return;
        }
        if (resultFade != null && resultFade.isRunning()) resultFade.stop();
        resultAlpha = 0.15f;
        resultFade = new Timer(14, null);
        resultFade.addActionListener(e -> {
            resultAlpha = Math.min(1f, resultAlpha + 0.07f);
            resultCard.repaint();
            if (resultAlpha >= 1f) resultFade.stop();
        });
        resultFade.start();
    }

    private void refreshRates() {
        Currency from = selected(fromCombo);
        Currency to = selected(toCombo);
        if (from == null || to == null) return;
        String raw = amountField.getText().trim();
        double amount = 1;
        InputValidator.Result validation = InputValidator.validate(raw, from.getCode(), to.getCode());
        if (validation.isValid()) amount = validation.getAmount();
        performConversion(amount, from, to);
    }

    private void copyResult() {
        if (lastResultText.isBlank()) {
            Toast.show(this, "Nothing to copy yet", Toast.Type.WARNING);
            return;
        }
        if (ClipboardManager.copy(lastResultText)) {
            Toast.show(this, "Copied Successfully", Toast.Type.SUCCESS);
            setStatus("Result copied to clipboard");
        } else {
            Toast.show(this, "Clipboard unavailable", Toast.Type.ERROR);
        }
    }

    private void exportCsv() {
        if (historyManager.isEmpty()) {
            Dialogs.message(this, themeManager.getPalette(), Dialogs.Kind.INFO,
                    "Nothing to export", "Convert at least one amount before exporting the history.");
            return;
        }
        JFileChooser chooser = new JFileChooser(new File("."));
        chooser.setSelectedFile(new File("history.csv"));
        chooser.setDialogTitle("Export conversion history");
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try {
            File file = CSVExporter.export(historyManager.getRecords(), chooser.getSelectedFile());
            Toast.show(this, "Export Successful", Toast.Type.SUCCESS);
            setStatus("Exported to " + file.getName());
        } catch (Exception ex) {
            Dialogs.message(this, themeManager.getPalette(), Dialogs.Kind.ERROR,
                    "Export failed", "The CSV file could not be written. Check the folder permissions.");
        }
    }

    private void toggleFavorite() {
        Currency from = selected(fromCombo);
        Currency to = selected(toCombo);
        if (from == null || to == null || from.getCode().equals(to.getCode())) {
            Toast.show(this, "Select two different currencies first", Toast.Type.WARNING);
            return;
        }
        boolean added = favoriteManager.toggle(from.getCode(), to.getCode());
        updateFavoriteButton();
        refreshFavorites();
        Toast.show(this, added ? "Added to favourites" : "Removed from favourites",
                added ? Toast.Type.SUCCESS : Toast.Type.INFO);
    }

    private void updateFavoriteButton() {
    Currency from = (Currency) fromCombo.getSelectedItem();
    Currency to = (Currency) toCombo.getSelectedItem();

    if (from == null || to == null) {
        favoriteButton.setText("Fav");
        return;
    }

    boolean favourite = favoriteManager.contains(from.getCode(), to.getCode());

    favoriteButton.setText(favourite ? "Remove" : "Add");
}

    private void refreshFavorites() {
        ThemeManager.Palette p = themeManager.getPalette();
        favoritesStrip.removeAll();
        for (String pair : favoriteManager.getPairs()) {
            String[] parts = pair.split(">");
            if (parts.length != 2) continue;
            RoundedButton chip = new RoundedButton("" + parts[0] + "->" + parts[1],
                    RoundedButton.Variant.SECONDARY);
            chip.setFont(new Font("Segoe UI", Font.BOLD, 12));
            chip.setPreferredSize(new Dimension(140, 36));
            chip.setArc(18);
            chip.applyPalette(p.primary, p.accent, p.dark ? p.background : new Color(0xF1F5F9), p.text, p.border);
            chip.setToolTipText("Convert " + parts[0] + " to " + parts[1] + " right click to remove");
            chip.addActionListener(e -> {
                searchField.setText("");
                selectCurrency(fromCombo, parts[0]);
                selectCurrency(toCombo, parts[1]);
                updateFavoriteButton();
                if (amountField.getText().isBlank()) amountField.setText("1");
                convert();
            });
            chip.addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        favoriteManager.remove(parts[0], parts[1]);
                        refreshFavorites();
                        updateFavoriteButton();
                        Toast.show(CurrencyConvert.this, "Removed from favourites", Toast.Type.INFO);
                    }
                }
            });
            favoritesStrip.add(chip);
        }
        favoritesStrip.revalidate();
        favoritesStrip.repaint();
    }

    private void refreshHistory() {
        ThemeManager.Palette p = themeManager.getPalette();
        historyList.removeAll();

        if (historyManager.isEmpty()) {
            JPanel empty = new JPanel();
            empty.setOpaque(false);
            empty.setLayout(new BoxLayout(empty, BoxLayout.Y_AXIS));
            empty.setBorder(BorderFactory.createEmptyBorder(48, 10, 10, 10));

            JLabel icon = new JLabel("\uD83D\uDD52", SwingConstants.CENTER);
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 34));
            icon.setAlignmentX(CENTER_ALIGNMENT);
            historyEmptyLabel = new JLabel("No conversions yet");
            historyEmptyLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            historyEmptyLabel.setForeground(p.subText);
            historyEmptyLabel.setAlignmentX(CENTER_ALIGNMENT);
            JLabel hint = new JLabel("Your last 20 conversions appear here");
            hint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            hint.setForeground(p.subText);
            hint.setAlignmentX(CENTER_ALIGNMENT);

            empty.add(icon);
            empty.add(Box.createVerticalStrut(8));
            empty.add(historyEmptyLabel);
            empty.add(Box.createVerticalStrut(4));
            empty.add(hint);
            historyList.add(empty);
        } else {
            DecimalFormat money = new DecimalFormat("#,##0.00");
            for (ConversionRecord record : historyManager.getRecords()) {
                RoundedPanel item = new RoundedPanel(new BorderLayout(10, 0), 14);
                item.setFill(p.dark ? p.background : new Color(0xF8FAFC));
                item.setBorderColor(p.border);
                item.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
                item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

                JPanel left = new JPanel();
                left.setOpaque(false);
                left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
                JLabel pair = new JLabel(money.format(record.getAmount()) + " " + record.getFrom()
                        + "  \u2192  " + record.getTo());
                pair.setFont(new Font("Segoe UI", Font.BOLD, 13));
                pair.setForeground(p.text);
                JLabel time = new JLabel(record.getFormattedTime() + "  \u00B7  " + record.getFormattedDate());
                time.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                time.setForeground(p.subText);
                left.add(pair);
                left.add(Box.createVerticalStrut(2));
                left.add(time);

                JLabel value = new JLabel(money.format(record.getResult()), SwingConstants.RIGHT);
                value.setFont(new Font("Segoe UI", Font.BOLD, 14));
                value.setForeground(p.primary);

                item.add(left, BorderLayout.CENTER);
                item.add(value, BorderLayout.EAST);
                item.setToolTipText("Click to reuse this conversion");
                item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                item.addMouseListener(new MouseAdapter() {
                    @Override public void mouseClicked(MouseEvent e) {
                        searchField.setText("");
                        amountField.setText(new DecimalFormat("0.##").format(record.getAmount()));
                        selectCurrency(fromCombo, record.getFrom());
                        selectCurrency(toCombo, record.getTo());
                        updateFavoriteButton();
                    }
                });

                historyList.add(item);
                historyList.add(Box.createVerticalStrut(8));
            }
        }
        historyList.revalidate();
        historyList.repaint();
    }

    private void setBusy(boolean busy) {
        convertButton.setEnabled(!busy);
        refreshButton.setEnabled(!busy);
        loadingLabel.setVisible(busy);
        if (busy) spinner.start(); else spinner.stop();
    }

    private void setStatus(String message) {
        statusReadyLabel.setText(message);
    }

    private void updateConnectionBadge() {
        boolean online = connectionManager.isOnline();
        statusDotLabel.setForeground(online ? new Color(0x22C55E) : new Color(0xEF4444));
        connectionLabel.setText(online ? "Online" : "Offline");
        statusApiLabel.setText(online
                ? (api.hasApiKey() ? "API: exchangerate-api (key)" : "API: open.er-api (free)")
                : "API: offline cache");
    }

    private void probeConnection() {
        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() { return connectionManager.checkConnection(); }
            @Override protected void done() {
                updateConnectionBadge();
                if (!connectionManager.isOnline()) {
                    Toast.show(CurrencyConvert.this, "Internet Disconnected \u2014 offline mode", Toast.Type.WARNING);
                    setStatus("Offline Mode");
                } else {
                    setStatus("Ready");
                }
            }
        }.execute();
    }

    private void countConversion() {
        String today = LocalDate.now().toString();
        if (!today.equals(settings.get(SettingsManager.KEY_DAILY_DATE))) {
            settings.set(SettingsManager.KEY_DAILY_DATE, today);
            settings.set(SettingsManager.KEY_DAILY_COUNT, "0");
        }
        settings.set(SettingsManager.KEY_DAILY_COUNT,
                String.valueOf(settings.getInt(SettingsManager.KEY_DAILY_COUNT, 0) + 1));
        settings.set(SettingsManager.KEY_TOTAL_COUNT,
                String.valueOf(settings.getInt(SettingsManager.KEY_TOTAL_COUNT, 0) + 1));
        settings.save();
    }

    private void playBeep() {
        if (settings.getBoolean(SettingsManager.KEY_SOUND)) {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    private void openSettings() {
        ThemeManager.Palette p = themeManager.getPalette();
        RoundedDialog dialog = new RoundedDialog(this, "Settings");
        dialog.setSurface(p.card);
        RoundedPanel content = dialog.getRoundedContent();
        content.setBorder(BorderFactory.createEmptyBorder(24, 28, 22, 28));

        JPanel form = new JPanel(new GridLayout(0, 2, 14, 14));
        form.setOpaque(false);

        JComboBox<String> theme = new JComboBox<>(ThemeManager.themeNames());
        theme.setSelectedItem(themeManager.getThemeName());
        JComboBox<String> fontSize = new JComboBox<>(new String[]{"Small", "Medium", "Large"});
        fontSize.setSelectedItem(settings.get(SettingsManager.KEY_FONT_SIZE, "Medium"));
        JComboBox<String> languageBox = new JComboBox<>(LanguageManager.LANGUAGES);
        languageBox.setSelectedItem(language.getLanguage());

        String[] codes = byCode.keySet().toArray(new String[0]);
        JComboBox<String> defaultFrom = new JComboBox<>(codes);
        defaultFrom.setSelectedItem(settings.get(SettingsManager.KEY_DEFAULT_FROM, "USD"));
        JComboBox<String> defaultTo = new JComboBox<>(codes);
        defaultTo.setSelectedItem(settings.get(SettingsManager.KEY_DEFAULT_TO, "INR"));

        JCheckBox autoSave = new JCheckBox("Auto save history", settings.getBoolean(SettingsManager.KEY_AUTOSAVE));
        JCheckBox animations = new JCheckBox("Animations", settings.getBoolean(SettingsManager.KEY_ANIMATIONS));
        JCheckBox sound = new JCheckBox("Sound effects", settings.getBoolean(SettingsManager.KEY_SOUND));
        for (JCheckBox box : new JCheckBox[]{autoSave, animations, sound}) {
            box.setOpaque(false);
            box.setForeground(p.text);
            box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }

        form.add(settingLabel("Theme", p));       form.add(theme);
        form.add(settingLabel("Font size", p));   form.add(fontSize);
        form.add(settingLabel("Language", p));    form.add(languageBox);
        form.add(settingLabel("Default from", p));form.add(defaultFrom);
        form.add(settingLabel("Default to", p));  form.add(defaultTo);
        form.add(autoSave);                       form.add(animations);
        form.add(sound);                          form.add(new JLabel(""));

        JLabel heading = new JLabel("Settings");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        heading.setForeground(p.text);
        heading.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        RoundedButton save = new RoundedButton("Save changes", RoundedButton.Variant.PRIMARY);
        save.applyPalette(p.primary, p.accent, p.card, p.text, p.border);
        save.addActionListener(e -> {
            settings.set(SettingsManager.KEY_THEME, (String) theme.getSelectedItem());
            settings.set(SettingsManager.KEY_FONT_SIZE, (String) fontSize.getSelectedItem());
            settings.set(SettingsManager.KEY_LANGUAGE, (String) languageBox.getSelectedItem());
            settings.set(SettingsManager.KEY_DEFAULT_FROM, (String) defaultFrom.getSelectedItem());
            settings.set(SettingsManager.KEY_DEFAULT_TO, (String) defaultTo.getSelectedItem());
            settings.set(SettingsManager.KEY_AUTOSAVE, autoSave.isSelected());
            settings.set(SettingsManager.KEY_ANIMATIONS, animations.isSelected());
            settings.set(SettingsManager.KEY_SOUND, sound.isSelected());
            settings.save();
            themeManager.applyTheme((String) theme.getSelectedItem());
            language.setLanguage((String) languageBox.getSelectedItem());
            applyTheme();
            dialog.dispose();
            Toast.show(this, "Settings saved", Toast.Type.SUCCESS);
        });

        RoundedButton cancel = new RoundedButton("Cancel", RoundedButton.Variant.SECONDARY);
        cancel.applyPalette(p.primary, p.accent, p.dark ? p.background : new Color(0xF1F5F9), p.text, p.border);
        cancel.addActionListener(e -> dialog.dispose());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        actions.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        actions.add(cancel);
        actions.add(save);

        content.add(heading, BorderLayout.NORTH);
        content.add(form, BorderLayout.CENTER);
        content.add(actions, BorderLayout.SOUTH);
        dialog.setPreferredSize(new Dimension(520, 420));
        dialog.showCentered();
    }

    private JLabel settingLabel(String text, ThemeManager.Palette p) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(p.text);
        return label;
    }

    private void openAbout() {
        Dialogs.message(this, themeManager.getPalette(), Dialogs.Kind.INFO, "About",
                "Currency Converter Pro " + VERSION + "\n\n"
                + "A desktop currency conversion suite built with Java Swing and Graphics2D. "
                + "Live rates, offline caching, favourites, history and CSV export. "
                + "No external UI libraries, no database.");
    }

    private void openHelp() {
        Dialogs.message(this, themeManager.getPalette(), Dialogs.Kind.INFO, "Help",
                "Keyboard shortcuts:  Ctrl+Enter convert, Ctrl+D dark mode, Ctrl+H history, "
                + "Ctrl+S export CSV, Ctrl+C copy result, Ctrl+R refresh rates. "
                + "Type in the search box to filter currencies, click a favourite chip for a "
                + "one click conversion, and right click a chip to remove it.");
    }

    private void openStatistics() {
        ThemeManager.Palette p = themeManager.getPalette();
        Map<String, Integer> usage = historyManager.usageByCurrency();
        StringBuilder builder = new StringBuilder();
        builder.append("Conversions today: ")
               .append(settings.getInt(SettingsManager.KEY_DAILY_COUNT, 0))
               .append("     Total: ")
               .append(settings.getInt(SettingsManager.KEY_TOTAL_COUNT, 0))
               .append("\n\nMost used currencies:\n");
        if (usage.isEmpty()) {
            builder.append("No data yet.");
        } else {
            int shown = 0;
            for (Map.Entry<String, Integer> entry : usage.entrySet()) {
                builder.append("  ").append(entry.getKey()).append("  \u00B7  ")
                       .append(entry.getValue()).append(" use(s)\n");
                if (++shown == 6) break;
            }
        }
        Dialogs.message(this, p, Dialogs.Kind.INFO, "Usage statistics", builder.toString());
    }

    private void openCalculator() {
        ThemeManager.Palette p = themeManager.getPalette();
        RoundedDialog dialog = new RoundedDialog(this, "Mini calculator");
        dialog.setSurface(p.card);
        RoundedPanel content = dialog.getRoundedContent();
        content.setBorder(BorderFactory.createEmptyBorder(22, 24, 20, 24));

        RoundedTextField display = new RoundedTextField("0");
        display.setFont(new Font("Segoe UI", Font.BOLD, 22));
        display.setPreferredSize(new Dimension(280, 52));
        display.applyPalette(p.dark ? p.background : new Color(0xF8FAFC), p.border, p.primary, p.text, p.subText);

        JPanel pad = new JPanel(new GridLayout(4, 4, 8, 8));
        pad.setOpaque(false);
        pad.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 0));
        String[] keys = {"7", "8", "9", "/", "4", "5", "6", "*", "1", "2", "3", "-", "0", ".", "=", "+"};
        for (String key : keys) {
            RoundedButton button = new RoundedButton(key,
                    "=".equals(key) ? RoundedButton.Variant.PRIMARY : RoundedButton.Variant.SECONDARY);
            button.applyPalette(p.primary, p.accent, p.dark ? p.background : new Color(0xF1F5F9), p.text, p.border);
            button.setPreferredSize(new Dimension(60, 46));
            button.addActionListener(e -> {
                if ("=".equals(key)) {
                    display.setText(MiniCalculator.evaluate(display.getText()));
                } else {
                    display.setText(display.getText() + key);
                }
            });
            pad.add(button);
        }

        RoundedButton useValue = new RoundedButton("Use as amount", RoundedButton.Variant.PRIMARY);
        useValue.applyPalette(p.primary, p.accent, p.card, p.text, p.border);
        useValue.addActionListener(e -> {
            String value = MiniCalculator.evaluate(display.getText());
            if (value.matches("\\d+(\\.\\d+)?")) {
                amountField.setText(value);
                dialog.dispose();
                Toast.show(this, "Amount updated", Toast.Type.SUCCESS);
            } else {
                Toast.show(this, "That expression is not a valid amount", Toast.Type.WARNING);
            }
        });

        content.add(display, BorderLayout.NORTH);
        content.add(pad, BorderLayout.CENTER);
        content.add(useValue, BorderLayout.SOUTH);
        dialog.showCentered();
    }

    /* =====================================================================
     * Lifecycle
     * ===================================================================== */

    private void shutdown() {
        if (clockTimer != null) clockTimer.stop();
        settings.set(SettingsManager.KEY_LAST_AMOUNT, amountField.getText().trim());
        Currency from = selected(fromCombo);
        Currency to = selected(toCombo);
        if (from != null) settings.set(SettingsManager.KEY_DEFAULT_FROM, from.getCode());
        if (to != null) settings.set(SettingsManager.KEY_DEFAULT_TO, to.getCode());
        settings.save();
        dispose();
        System.exit(0);
    }

    private static final class SimpleDocumentListener implements javax.swing.event.DocumentListener {
        private final Runnable action;
        SimpleDocumentListener(Runnable action) { this.action = action; }
        @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { action.run(); }
        @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { action.run(); }
        @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { action.run(); }
    }


    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            
        }

        SwingUtilities.invokeLater(() -> {
            SplashScreenWindow splash = new SplashScreenWindow();
            splash.showFor(1800, () -> new CurrencyConvert().setVisible(true));
        });
    }
}
