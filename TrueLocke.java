import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;

public class TrueLocke {
    public static void main(String[] args) {
        JFrame frame = new JFrame("TrueLocke - Personal Message Vault");
        frame.setSize(450, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextArea messageArea = new JTextArea("Write your message here...");
        JTextField conditionField = new JTextField("Enter unlock condition...");
        JButton lockButton = new JButton("Lock Message");
        JButton unlockButton = new JButton("Unlock Message");

        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Condition (for unlocking):"));
        panel.add(conditionField);
        panel.add(new JLabel("Your Message:"));
        panel.add(new JScrollPane(messageArea));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(lockButton);
        buttonPanel.add(unlockButton);
        panel.add(buttonPanel);

        frame.add(panel);
        frame.setVisible(true);

        // Action to Lock
        lockButton.addActionListener(e -> {
            String message = messageArea.getText().trim();
            String condition = conditionField.getText().trim();

            if (message.isEmpty() || condition.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter both message and condition.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(frame,
                    "This message will be locked and cannot be edited again.\nConfirm lock?",
                    "Confirm Lock", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                String timestamp = LocalDateTime.now().toString().replace(":", "-");
                File dir = new File("messages");
                dir.mkdirs();

                File file = new File(dir, timestamp + ".truelocke");

                try (FileWriter writer = new FileWriter(file)) {
                    writer.write("Timestamp: " + timestamp + "\n");
                    writer.write("Condition: " + condition + "\n");
                    writer.write("Message:\n" + message);
                    JOptionPane.showMessageDialog(frame, "Message locked and saved.");
                    messageArea.setText("");
                    conditionField.setText("");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error saving message.");
                }
            }
        });

        // Action to Unlock
        unlockButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser("messages");
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();

                try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
                    String line;
                    String condition = null, message = "";
                    boolean readingMessage = false;

                    while ((line = br.readLine()) != null) {
                        if (line.startsWith("Condition: ")) {
                            condition = line.substring(11).trim();
                        } else if (line.equals("Message:")) {
                            readingMessage = true;
                        } else if (readingMessage) {
                            message += line + "\n";
                        }
                    }

                    String userInput = JOptionPane.showInputDialog(frame, "Enter unlock condition:");
                    if (userInput != null && userInput.equals(condition)) {
                        JTextArea unlockedArea = new JTextArea(message);
                        unlockedArea.setEditable(false);
                        unlockedArea.setLineWrap(true);
                        unlockedArea.setWrapStyleWord(true);
                        JOptionPane.showMessageDialog(frame, new JScrollPane(unlockedArea), "Unlocked Message", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Incorrect condition. Access denied.");
                    }

                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error reading file.");
                }
            }
        });
    }
}
