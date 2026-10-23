import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Map;

public class TagExtractorFrame extends JFrame {
    private JButton btnLoadStopWords, btnExtractTags, btnSaveTags;
    private JLabel lblSourceFile;
    private JTextArea txtAreaOutput;
    private JFileChooser fileChooser;

    private File stopWordsFile;
    private File documentFile;
    private TagExtractorEngine engine;

    public TagExtractorFrame() {
        engine = new TagExtractorEngine();
        fileChooser = new JFileChooser();

        setTitle("Tag / Keyword Extractor");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        createLayout();
        initListeners();
    }

    private void initComponents() {
        btnLoadStopWords = new JButton("1. Load Stop Words");
        btnExtractTags = new JButton("2. Extract Tags");
        btnSaveTags = new JButton("3. Save Tags to File");

        // Save button starts disabled until there is data to save
        btnSaveTags.setEnabled(false);

        lblSourceFile = new JLabel("No document selected.");
        lblSourceFile.setFont(new Font("Arial", Font.ITALIC, 12));

        txtAreaOutput = new JTextArea();
        txtAreaOutput.setEditable(false);
        txtAreaOutput.setFont(new Font("Monospaced", Font.PLAIN, 13));
    }

    private void createLayout() {
        JPanel controlPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnLoadStopWords);
        buttonPanel.add(btnExtractTags);
        buttonPanel.add(btnSaveTags);

        JPanel statusPanel = new JPanel();
        statusPanel.add(new JLabel("Active File: "));
        statusPanel.add(lblSourceFile);

        controlPanel.add(buttonPanel);
        controlPanel.add(statusPanel);

        JScrollPane scrollPane = new JScrollPane(txtAreaOutput);

        add(controlPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initListeners() {
        // Action 1: Pick and load the stop words file
        btnLoadStopWords.addActionListener(e -> {
            fileChooser.setDialogTitle("Select Stop Words File");
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                stopWordsFile = fileChooser.getSelectedFile();
                try {
                    engine.loadStopWords(stopWordsFile);
                    JOptionPane.showMessageDialog(this, "Stop words successfully loaded!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error loading stop words: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Action 2: Pick text document and process tags
        btnExtractTags.addActionListener(e -> {
            if (!engine.hasStopWordsLoaded()) {
                JOptionPane.showMessageDialog(this, "Please load a stop words file first!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            fileChooser.setDialogTitle("Select Text Document to Scan");
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                documentFile = fileChooser.getSelectedFile();
                lblSourceFile.setText(documentFile.getName()); // Requirement: display active file name

                try {
                    engine.processDocument(documentFile);
                    displayTags();
                    btnSaveTags.setEnabled(true);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Action 3: Save results back out to a text file
        btnSaveTags.addActionListener(e -> {
            fileChooser.setDialogTitle("Save Extracted Tags As...");
            int returnVal = fileChooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File saveFile = fileChooser.getSelectedFile();
                try (PrintWriter writer = new PrintWriter(new FileWriter(saveFile))) {
                    for (Map.Entry<String, Integer> entry : engine.getTagMap().entrySet()) {
                        writer.println(entry.getKey() + " : " + entry.getValue());
                    }
                    JOptionPane.showMessageDialog(this, "Tags successfully saved!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void displayTags() {
        txtAreaOutput.setText(""); // Clear previous text
        txtAreaOutput.append(String.format("%-20s %s\n", "TAG / KEYWORD", "FREQUENCY"));
        txtAreaOutput.append("---------------------------------------\n");

        for (Map.Entry<String, Integer> entry : engine.getTagMap().entrySet()) {
            txtAreaOutput.append(String.format("%-20s %d\n", entry.getKey(), entry.getValue()));
        }
    }
}