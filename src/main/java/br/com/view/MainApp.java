package br.com.view;
import javax.swing.*;
import com.formdev.flatlaf.FlatLightLaf;
public class MainApp {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(PessoaList::new);
    }
}
