import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.imageio.ImageIO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import javax.swing.*;
import javafx.scene.control.TextField;
import java.awt.Dimension;
import javax.swing.SpringLayout;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;


class Fisher{
    
    //how long to run in minutes
    static String directory = System.getProperty("user.home");
    static String fileName = "properties.txt";
    static String absolutePath = directory + File.separator + fileName;

    static long timer = 0;
    static int resX = 1920;
    static int resY = 1080;
    static int scale = 100;
    

    public static void readSettings(){
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(absolutePath))) {
            String line = bufferedReader.readLine();
            while(line != null) {
                String[] setting = line.split(":");
                if(setting[0].equals("timer")){
                    timer = Integer.valueOf(setting[1]);
                }else if(setting[0].equals("resolution X")){
                    resX = Integer.valueOf(setting[1]);
                }else if(setting[0].equals("resolution Y")){
                    resY = Integer.valueOf(setting[1]);
                }else if(setting[0].equals("Scaling%")){
                    scale = Integer.valueOf(setting[1]);
                }
                line = bufferedReader.readLine();
            }
        }catch(FileNotFoundException e) {
            // Exception handling
        }catch(IOException e) {
            // Exception handling
        }
    }

    public static void writeSettings(){
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(absolutePath))) {
            bufferedWriter.write(String.valueOf("timer:" + timer + '\n'));
            bufferedWriter.write(String.valueOf("resolution X:" + resX + '\n'));
            bufferedWriter.write(String.valueOf("resolution Y:" + resY + '\n'));
            bufferedWriter.write(String.valueOf("Scaling%:" + scale + '\n'));
            bufferedWriter.close();
        }catch(IOException e) {
            // Exception handling
        }
    }

    public static void sleep(int miliseconds){
        try{
            Thread.sleep(miliseconds);
        }catch(Exception e) {
            System.out.println(e);
        }
    }

    public static BufferedImage getImage(Rectangle rectangle){
        try{
            Robot robot = new Robot();
            BufferedImage bobberImage = robot.createScreenCapture(rectangle);
            return bobberImage;
        }catch(Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public static void tabOut(){
        try{
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_ALT);
            sleep(100);
            robot.keyPress(KeyEvent.VK_TAB);
            sleep(100);
            robot.keyRelease(KeyEvent.VK_TAB);
            robot.keyRelease(KeyEvent.VK_ALT);
        }catch(Exception e) {
            System.out.println(e);
        }
    }

    public static void leftClick(){
        try{
            Robot robot = new Robot();
            sleep(100);
            robot.mousePress(InputEvent.BUTTON1_MASK);
            sleep(100);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            sleep(100);
        }catch(Exception e) {
            System.out.println(e);
        }
    }

    public static void rightClick(){
        try{
            Robot robot = new Robot();
            sleep(100);
            robot.mousePress(InputEvent.BUTTON3_MASK);
            sleep(100);
            robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
            sleep(100);
        }catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void castLine(){
        try{
            Robot robot = new Robot();
            moveCursor(1000, 1000);
            leftClick();
            sleep(100);
            robot.mousePress(InputEvent.BUTTON3_MASK);
            sleep(50);
            robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
            sleep(50);
            robot.mousePress(InputEvent.BUTTON3_MASK);
            sleep(50);
            robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
            sleep(100);
            tabOut();
            sleep(100);
        }catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void resetBobber(){
        try{
            Robot robot = new Robot();
            sleep(100);
            moveCursor(1000, 1000);
            leftClick();
            robot.keyPress(KeyEvent.VK_S);
            sleep(90);
            robot.keyRelease(KeyEvent.VK_S);
            sleep(100);
            robot.keyPress(KeyEvent.VK_W);
            sleep(60);
            robot.keyRelease(KeyEvent.VK_W);
            castLine();
        }catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void moveCursor(int x, int y){
        try{
            Robot robot = new Robot();
            sleep(100);
            robot.mouseMove(x, y);
            sleep(100);
        }catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public static int[] getBobberLocation(){
        sleep(500);
        Rectangle rectangle = new Rectangle(640, 360, 2800, 1080);
        BufferedImage image = getImage(rectangle);
        try{
            ImageIO.write(image, "jpg", new File("Fullscreenshot.jpg"));
        }catch(IOException e){
            System.out.println(e);
        }
        int[] bobberLocation = {0, 0};
        for(int i = 0; i < 2800; i += 1){
            for(int j = 0; j < 1080; j+= 1){
                int pixel = image.getRGB(i, j);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;
                if(
                    red >= 70 && green <= 80 && blue <= 40 ||
                    red >= 40 && red <= 55 && green <= 40 && blue <= 30
                ){
                    bobberLocation[0] = i + 640;
                    bobberLocation[1] = j + 360;
                    return bobberLocation;
                }
            }
        }
        if(bobberLocation[0] == 0 && bobberLocation[1] == 0){
            resetBobber();
            sleep(3000);
            return getBobberLocation();
        }
        return bobberLocation;
    }

    public static void catchFish(int[] bobber){
        int countSec = 0;
        int prevGreyValue = 0;
        while(true){
            Rectangle bobberRect = new Rectangle(bobber[0] + 20, bobber[1] - 5, 30, 30);
            BufferedImage bobberImage = getImage(bobberRect);
            int blue = 0;
            int red = 0;
            int green = 0;
            for(int i = 0; i < 30; i++){
                for(int j = 0; j < 30; j++){
                    int pixel = bobberImage.getRGB(i, j);
                    red += (pixel >> 16) & 0xff;
                    green += (pixel >> 8) & 0xff;
                    blue += (pixel) & 0xff;
                }
            }
            blue /= 400;
            red /= 400;
            green /= 400;
            int grey = (red + blue + green) / 3;
            if(Math.abs(prevGreyValue - grey) > 4 && prevGreyValue != 0){
                moveCursor(((bobber[0]) * 10) / 15, ((bobber[1]) * 10) / 15);
                return;
            }
            System.out.println(grey + " : " + prevGreyValue + " : " + (prevGreyValue - grey));
            prevGreyValue = grey;
            sleep(10);
            countSec++;
             if(countSec % 100 == 0){
                System.out.println("Time Elapsed: " + (countSec / 100) + " seconds");
             }
             if(countSec / 100 > 20){
                 return;
             }
        }
    }

    public static void startFishing(){
        long start = System.currentTimeMillis();
        timer *= 60000;
        while(true){
            sleep(2000);
            castLine();
            sleep(1000);
            int[] bobberLocation = new int[2];
            bobberLocation = getBobberLocation();
            System.out.print("Found Lure at " + bobberLocation[0] + ", " + bobberLocation[1]);
            catchFish(bobberLocation);
            sleep(1000);
            leftClick();
            rightClick();
            sleep(2000);
            if(System.currentTimeMillis() - start > timer){
                break;
            }
        }
    }


    static JButton button;

    static JLabel timerLabelTimer;
    static JTextField timerTextFieldTimer;
    static JLabel timerLabelResX;
    static JTextField timerTextFieldResX;
    static JLabel timerLabelResY;
    static JTextField timerTextFieldResY;
    static JLabel timerLabelScale;
    static JTextField timerTextFieldScale;

    public static void main(String[] args){
        readSettings();
        SpringLayout layout = new SpringLayout();
        JFrame frame = new JFrame("Fishing Bot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(255,255);
        frame.setLayout(layout);
        button = new JButton("Start Fishing");
        button.setPreferredSize(new Dimension(150, 75));
        timerLabelTimer = new JLabel("Time to Fish in minutes");
        timerTextFieldTimer = new JTextField(String.valueOf(timer), 5);
        timerLabelResX = new JLabel("X Resolution");
        timerTextFieldResX = new JTextField(String.valueOf(resX), 5);
        timerLabelResY = new JLabel("Y Resolution");
        timerTextFieldResY = new JTextField(String.valueOf(resY), 5);
        timerLabelScale = new JLabel("Scale in %");
        timerTextFieldScale = new JTextField(String.valueOf(scale), 5);
        layout.putConstraint(SpringLayout.WEST, button, 45, SpringLayout.WEST, frame);
        layout.putConstraint(SpringLayout.NORTH, button, 120, SpringLayout.NORTH, frame);
        layout.putConstraint(SpringLayout.WEST, timerLabelTimer, 10, SpringLayout.WEST, frame);
        layout.putConstraint(SpringLayout.NORTH, timerLabelTimer, 20, SpringLayout.NORTH, frame);
        layout.putConstraint(SpringLayout.WEST, timerTextFieldTimer, 150, SpringLayout.WEST, frame);
        layout.putConstraint(SpringLayout.NORTH, timerTextFieldTimer, 20, SpringLayout.NORTH, frame);
        layout.putConstraint(SpringLayout.WEST, timerLabelResX, 68, SpringLayout.WEST, frame);
        layout.putConstraint(SpringLayout.NORTH, timerLabelResX, 40, SpringLayout.NORTH, frame);
        layout.putConstraint(SpringLayout.WEST, timerTextFieldResX, 150, SpringLayout.WEST, frame);
        layout.putConstraint(SpringLayout.NORTH, timerTextFieldResX, 40, SpringLayout.NORTH, frame);
        layout.putConstraint(SpringLayout.WEST, timerLabelResY, 68, SpringLayout.WEST, frame);
        layout.putConstraint(SpringLayout.NORTH, timerLabelResY, 60, SpringLayout.NORTH, frame);
        layout.putConstraint(SpringLayout.WEST, timerTextFieldResY, 150, SpringLayout.WEST, frame);
        layout.putConstraint(SpringLayout.NORTH, timerTextFieldResY, 60, SpringLayout.NORTH, frame);
        layout.putConstraint(SpringLayout.WEST, timerLabelScale, 82, SpringLayout.WEST, frame);
        layout.putConstraint(SpringLayout.NORTH, timerLabelScale, 80, SpringLayout.NORTH, frame);
        layout.putConstraint(SpringLayout.WEST, timerTextFieldScale, 150, SpringLayout.WEST, frame);
        layout.putConstraint(SpringLayout.NORTH, timerTextFieldScale, 80, SpringLayout.NORTH, frame);
        frame.getContentPane().add(timerLabelTimer);
        frame.getContentPane().add(timerTextFieldTimer);
        frame.getContentPane().add(timerLabelResX);
        frame.getContentPane().add(timerTextFieldResX);
        frame.getContentPane().add(timerLabelResY);
        frame.getContentPane().add(timerTextFieldResY);
        frame.getContentPane().add(timerLabelScale);
        frame.getContentPane().add(timerTextFieldScale);
        frame.getContentPane().add(button);
        frame.setVisible(true);
        button.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                timer = Integer.valueOf(timerTextFieldTimer.getText());
                resX = Integer.valueOf(timerTextFieldResX.getText());
                resY = Integer.valueOf(timerTextFieldResY.getText());
                scale = Integer.valueOf(timerTextFieldScale.getText());
                writeSettings();
                //startFishing();
                System.out.println("Done Fishing");
            }
        });
    }
}



