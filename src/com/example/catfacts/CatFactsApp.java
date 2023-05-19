package com.example.catfacts;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class CatFactsApp extends JFrame {
    private DefaultListModel<String> listModel;
    private JList<String> factList;
    private JButton detailsButton;
    private JButton deleteButton;
    private JTextField searchField;
    private JButton searchButton;

    public CatFactsApp() {
        setTitle("Cat Facts App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        factList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(factList);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        detailsButton = new JButton("Details");
        deleteButton = new JButton("Delete");
        buttonPanel.add(detailsButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        JPanel searchPanel = new JPanel();
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        add(searchPanel, BorderLayout.NORTH);

        fetchCatFacts();

        detailsButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = factList.getSelectedIndex();
                if (selectedIndex != -1) {
                    String selectedFact = listModel.get(selectedIndex);
                    showFactDetails(selectedFact);
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = factList.getSelectedIndex();
                if (selectedIndex != -1) {
                    String selectedFact = listModel.get(selectedIndex);
                    deleteFact(selectedFact);
                    listModel.remove(selectedIndex);
                }
            }
        });

        searchButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                String searchKeyword = searchField.getText();
                if (!searchKeyword.isEmpty()) {
                    searchFacts(searchKeyword);
                } else {
                    fetchCatFacts();
                }
            }
        });
    }

    private void fetchCatFacts() {
        String apiUrl = "https://catfact.ninja/facts";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String response = reader.readLine();
                reader.close();

                JSONObject jsonResponse = new JSONObject(response);
                JSONArray factArray = jsonResponse.getJSONArray("data");

                listModel.clear();

                for (int i = 0; i < factArray.length(); i++) {
                    JSONObject factObject = factArray.getJSONObject(i);
                    String fact = factObject.getString("fact");
                    listModel.addElement(fact);
                }
            } else {
                System.out.println("Error: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showFactDetails(String fact) {
        JOptionPane.showMessageDialog(this, fact, "Fact Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteFact(String fact) {
    }

    private void searchFacts(String keyword) {
        ArrayList<String> searchResults = new ArrayList<>();

        for (int i = 0; i < listModel.getSize(); i++) {
            String fact = listModel.get(i);
            if (fact.toLowerCase().contains(keyword.toLowerCase())) {
                searchResults.add(fact);
            }
        }

        listModel.clear();
        for (String result : searchResults) {
            listModel.addElement(result);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CatFactsApp().setVisible(true);
            }
        });
    }
}
