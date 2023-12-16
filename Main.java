import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/**
 *
 *    |    +----------------------+
 *      |----|     Main Flow         |
 *      |    +----------------------+
 *      |
 *      |
 *      |   +-------------------------+
 *      |---|     Health Journal             |
 *      |   +-------------------------+
 *      |   |                       |
 *      |   | - Add New Entry
 *      |   | - View Past Entries
 *      |   |------------------------
 *      |
 *      |
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */









public class Main extends JFrame{
    private JButton addEntryButton;
    private JButton viewEntriesButton;

    private JTextField dateText;
    private JTextField moodText;
    private JTextField stressText;
    private JButton saveButton;
    private JPanel MainPanel;
    private EntryManager entryManager;
    // pulic HealthJournal main constructor
    public Main(){

        setTitle("Health Journal");
        setDefaultCloseOperation (EXIT_ON_CLOSE);
        setSize (800,800);
        setLocationRelativeTo (null);

        // initlizae MainPanel and set a layout

        MainPanel = new JPanel();
        MainPanel.setLayout(new BoxLayout(MainPanel, BoxLayout.Y_AXIS));


        //  Initialize and add action listeners to buttons

        addEntryButton = new JButton("Add New Entry");
        viewEntriesButton = new JButton("View Past Entries");


        // call the method in open add entryscreen, when you click the "Add New Entry button is clicked, this action listers
        // triggers the openAddEntryScreen() method, which opens the dialog for adding a new journal
        addEntryButton.addActionListener(e -> openAddEntryScreen());

        viewEntriesButton.addActionListener(e -> openViewEntriesScreen());

        // add buttons to MainPanel
        MainPanel.add(addEntryButton);
        MainPanel.add(viewEntriesButton);
        setContentPane (MainPanel);

        entryManager = new EntryManager();
        setVisible (true);
    }

    private void openViewEntriesScreen() {
        JDialog viewEntriesDialog = new JDialog(this, "View Past Entries");
        viewEntriesDialog.setLayout(new BorderLayout());
        viewEntriesDialog.setSize(400, 300);

        // Call getPastEntries() that returns a JPanel with past entries
        JPanel entriesPanel = getPastEntries();
        viewEntriesDialog.add(entriesPanel, BorderLayout.CENTER);

        viewEntriesDialog.setVisible(true);
    }

    private JPanel getPastEntries() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        List<String> entries = readEntriesFile();
        for (String entry : entries) {
            panel.add(new JLabel(entry));
        }
        return panel;
    }


    private List<String> readEntriesFile(){
        List<String> entries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("journalEntries.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                entries.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Optionally, handle the exception more gracefully here
        }
        return entries;



    }




    public class EntryManager {
        private List<JournalEntry> entries;
        private String filePath = "journalEntries.txt"; // Path to the file

        public EntryManager() {
            entries = new ArrayList<>();
            loadEntries();
        }

        private void loadEntries() {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(","); // Assuming the format is "date,mood,stressLevel"
                    if (parts.length == 3) {
                        try {
                            int stressLevel = Integer.parseInt(parts[2].trim());
                            entries.add(new JournalEntry(parts[0].trim(), parts[1].trim(), stressLevel));
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid entry in file: " + line);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
            }
        }

        public void addEntry(JournalEntry entry) {
            entries.add(entry);
            saveEntryToFile(entry);
        }

        private void saveEntryToFile(JournalEntry entry) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
                writer.write(entry.toString());
                writer.newLine();
            } catch (IOException e) {
                System.out.println("Error writing to file: " + e.getMessage());
            }
        }

        public List<JournalEntry> getEntries() {
            return entries;
        }
    }


    public class EntryDialog extends JDialog {
        private JTextField dateText;
        private JTextField moodText;
        private JTextField stressText;
        private JButton saveButton;
        private EntryManager entryManager;  // Reference to manage entries

        public EntryDialog(JFrame parent, EntryManager entryManager) {
            super(parent, "Add New Entry");
            this.entryManager = entryManager;
            initializeUI();
            setupListeners();
        }

        private void initializeUI() {
            setLayout(new FlowLayout());
            setSize(300, 200);

            dateText = new JTextField(10);
            moodText = new JTextField(10);
            stressText = new JTextField(10);
            saveButton = new JButton("Save Entry");

            add(new JLabel("Date:"));
            add(dateText);
            add(new JLabel("Mood:"));
            add(moodText);
            add(new JLabel("Stress Level:"));
            add(stressText);
            add(saveButton);
        }

        private void setupListeners() {
            saveButton.addActionListener(e -> {
                String date = dateText.getText();
                String mood = moodText.getText();
                String stressLevel = stressText.getText();
                if (Main.isValidInput(date, mood, stressLevel)) {
                    try {
                        int stress = Integer.parseInt(stressLevel);
                        JournalEntry entry = new JournalEntry(date, mood, stress);
                        entryManager.addEntry(entry);
                        JOptionPane.showMessageDialog(this, "Entry saved successfully.");
                        this.dispose(); // Close the dialog after saving
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Error: Stress level must be a number.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid input. Please check your data.");
                }
            });
        }
    }



    private void openAddEntryScreen() {
        EntryDialog addEntryDialog = new EntryDialog(this, entryManager);
       // JDialog addEntryDialog = new JDialog(this, "Add New Entry");
        addEntryDialog.setLayout(new FlowLayout());
        addEntryDialog.setSize(300, 200);

        // Use instance variables directly
        dateText = new JTextField(10);
        stressText = new JTextField(10);
        moodText = new JTextField(10);
        saveButton = new JButton("Save Entry");


                saveButton.addActionListener (new ActionListener () {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String date = dateText.getText();
                    String mood = moodText.getText();
                    String stressLevel = stressText.getText();


                    // Validate and save the data
                    if (isValidInput(date, mood, stressLevel)) {
                        // call the method of saveEntry
                        saveEntry(date, mood, stressLevel);
                        JOptionPane.showMessageDialog(Main.this
                                , "Entry saved successfully.");
                    } else {
                        JOptionPane.showMessageDialog(Main.this, "Invalid input. Please check your data.");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(Main.this, "Error saving the entry: " + ex.getMessage());
                }




            }
        });





        addEntryDialog.add(new JLabel("Date:"));
        addEntryDialog.add(dateText);
        addEntryDialog.add(new JLabel("Stress level:"));
        addEntryDialog.add(stressText);
        addEntryDialog.add(new JLabel("Mood:"));
        addEntryDialog.add(moodText);
        addEntryDialog.add(saveButton);

        addEntryDialog.setVisible(true);


    }

    private static boolean isValidInput(String date, String mood, String stressLevel) {
        // Check if any field is empty
        if (date.trim().isEmpty() || mood.trim().isEmpty() || stressLevel.trim().isEmpty()) {
            return false;
        }

        // Validate stress level is a number within 1-10
        try {
            int stress = Integer.parseInt(stressLevel);
            if (stress < 1 || stress > 10) {
                return false; // Stress level is out of range
            }
        } catch (NumberFormatException e) {
            return false; // Stress level is not a number
        }

        return true; // All inputs are valid
    }


    private void saveEntry(String date, String mood, String stressLevel) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("journalEntries.txt", true))) {
            writer.write(date + "," + mood + "," + stressLevel);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public class JournalEntry {
        private String date;
        private String mood;
        private int stressLevel;

        public JournalEntry(String date, String mood, int stressLevel) {
            this.date = date;
            this.mood = mood;
            this.stressLevel = stressLevel;
        }

        @Override
        public String toString() {
            return date + "," + mood + "," + stressLevel;
        }

        // Getters and setters if needed
    }




    public static void main(String[] args) {
        new Main ();


    }

}