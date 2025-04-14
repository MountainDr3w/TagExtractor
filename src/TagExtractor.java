import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class TagExtractor extends JFrame {
    Map<String, Integer> tagFrequency;
    Set<String> stopWords;
    File textFile;
    File stopWordsFile;

    JTextArea extractedDisplay;
    JFileChooser fileChooser;

    /*
    Will have the open file button
    and the open stopWords file button
     */
    //Buttons panel
    JPanel buttonsPanel;
    JButton openFileButton, openStopWordsButton;

    /*
    This panel will just include the JTextArea with the words and frequencies
    Along with a process files button
     */
    //Display Panel
    JPanel displayPanel;
    JTextArea extractedTagFrequencies;
    JButton processFilesButton;

    /*
    This panel will allow the user to save the extracted tags to its own new file
     */
    //Save file Panel
    JPanel saveFilePanel;
    JButton saveButton;


    public TagExtractor() {
        setTitle("Tag Extractor");

        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;

        // center frame in screen
        setSize(screenWidth / 2, screenHeight / 2);
        setLocation(screenWidth / 4, screenHeight / 4);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        fileChooser = new JFileChooser();
        stopWords = new TreeSet<>();
        tagFrequency = new TreeMap<>();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        createButtonPanel();
        createDisplayPanel();
        createSavePanel();

        mainPanel.add(buttonsPanel, BorderLayout.NORTH);
        mainPanel.add(displayPanel, BorderLayout.CENTER);
        mainPanel.add(saveFilePanel, BorderLayout.SOUTH);


        add(mainPanel);
    }

    public void createButtonPanel(){
        buttonsPanel = new JPanel();

        openFileButton = new JButton("Open Text File");
        openFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int ret = fileChooser.showOpenDialog(null);
                if(ret == JFileChooser.APPROVE_OPTION){
                    textFile = fileChooser.getSelectedFile();
                }
            }
        });

        openStopWordsButton = new JButton("Open Stop Words File");
        openStopWordsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int ret = fileChooser.showOpenDialog(null);
                if (ret == JFileChooser.APPROVE_OPTION) {
                     stopWordsFile = fileChooser.getSelectedFile();
                }
            }
        });

        buttonsPanel.add(openFileButton);
        buttonsPanel.add(openStopWordsButton);
    }

    public void createDisplayPanel(){
        displayPanel = new JPanel();
        displayPanel.setLayout(new BorderLayout(10,10));

        extractedTagFrequencies = new JTextArea(10, 35);
        extractedTagFrequencies.setEditable(false);
        extractedTagFrequencies.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane extractedTagFrequencesScrollPane = new JScrollPane(extractedTagFrequencies);

        processFilesButton = new JButton("Process Files");
        processFilesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!((stopWordsFile == null) || (textFile == null))) {
                    loadStopWords(stopWordsFile);
                    processFile(textFile);
                    extractedTagFrequencies.setText("");
                    for (Map.Entry<String, Integer> entry : tagFrequency.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).collect(Collectors.toList()))
                    //Found some weird way to sort it by frequency... thanks internet
                    {
                        extractedTagFrequencies.append(entry.getKey() + ": " + entry.getValue() + "\n");
                    }
                }
            }
        });

        displayPanel.add(extractedTagFrequencesScrollPane, BorderLayout.CENTER);
        displayPanel.add(processFilesButton, BorderLayout.SOUTH);

    }
//
    public void createSavePanel(){
        saveFilePanel = new JPanel(new BorderLayout(10,10));
        saveButton = new JButton("Save Tags");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int ret = fileChooser.showSaveDialog(null);
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    saveTags(file);
                }
            }
        });

        saveFilePanel.add(saveButton);
    }
//
    private void loadStopWords(File file){
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String word;
            while ((word = reader.readLine()) != null) {
                stopWords.add(word.trim().toLowerCase()); //Each line is a word so it should check each word in the stopWords file and add it to a list
            }
        }catch (IOException e){
            e.printStackTrace();

        }

    }
//
    private void processFile(File file){
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while((line = reader.readLine()) != null) {
                String[] wordsList = line.split("\\W+"); // splits the line into words seperated by \\W+ which means any non word character

                //Should go through the list of words and count them
                for (String word : wordsList) {
                    word = word.trim().toLowerCase();
                    word = word.replaceAll("[^a-z]","");
                    if(!stopWords.contains(word) && !word.isEmpty()) {
                        tagFrequency.put(word, tagFrequency.getOrDefault(word, 0) + 1); //Either adds 1 to count of creates a new key
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private void saveTags(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Map.Entry<String, Integer> entry : tagFrequency.entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
