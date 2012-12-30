package net.vatov.math.plot;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import net.vatov.ampl.solver.io.UserIO;
import net.vatov.math.utils.VehicleRoute;

public class GuiIO implements UserIO {

    private final PlotVrp gui;
    
    public GuiIO (PlotVrp gui) {
        this.gui = gui;
    }
    
    public Integer getChoice(List<String> options, Integer defaultOption, String question) {
        return null;
    }

    public Integer getInt(Integer defaultValue, String question) {
        return null;
    }

    public Boolean getYesNo(Boolean defaultValue, String question) {
        return null;
    }

    public void refreshData(Object data) {
        gui.refreshRoutes((List<VehicleRoute>) data);
    }

    public void pause(String msg) {
        JOptionPane.showMessageDialog(gui.getFrame(), msg, "title", JOptionPane.PLAIN_MESSAGE);
    }
    
    public void screenshot(String root) {
        try {
            Robot robot = new Robot();
            BufferedImage image = robot.createScreenCapture(gui.getFrame().getBounds());
            ImageIO.write(image, "png", new File(root + File.separator + "cw-" + System.currentTimeMillis() + ".png"));
        } catch (Exception e) {
            throw new PlotException(e);
        }
    }
}
