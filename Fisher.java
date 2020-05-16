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


class Fisher{
    
    //how long to run in minutes
    static long timer = 0;

    public static void sleep(int miliseconds){
        try{
            Thread.sleep(miliseconds);
        }catch (Exception e) {
            System.out.println(e);
        }
    }

    public static BufferedImage getImage(Rectangle rectangle){
        try{
            Robot robot = new Robot();
            BufferedImage bobberImage = robot.createScreenCapture(rectangle);
            return bobberImage;
        }catch (Exception e) {
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
        }catch (Exception e) {
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
        }catch (Exception e) {
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
            sleep(70);
            robot.keyRelease(KeyEvent.VK_S);
            sleep(100);
            robot.keyPress(KeyEvent.VK_W);
            sleep(50);
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
        Rectangle rectangle = new Rectangle(640, 360, 2800, 1080);
        BufferedImage image = getImage(rectangle);
        try{
            ImageIO.write(image, "jpg", new File("Fullscreenshot.jpg"));
        }catch(IOException e){
            System.out.println(e);
        }
        int[] bobberLocation = {0, 0};
        for(int i = 0; i < 2800; i += 4){
            for(int j = 0; j < 1080; j+= 4){
                int pixel = image.getRGB(i, j);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;
                if(red >= 50 && green <= 30 && blue <= 30){
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
            Rectangle bobberRect = new Rectangle(bobber[0] + 10, bobber[1] - 5, 20, 20);
            BufferedImage bobberImage = getImage(bobberRect);
            int blue = 0;
            int red = 0;
            int green = 0;
            for(int i = 0; i < 20; i++){
                for(int j = 0; j < 20; j++){
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
            if(Math.abs(prevGreyValue - grey) > 16 && prevGreyValue != 0){
                moveCursor(((bobber[0]) * 10) / 15, ((bobber[1]) * 10) / 15);
                return;
            }
            System.out.println(grey + " : " + prevGreyValue + " : " + (prevGreyValue - grey));
            prevGreyValue = grey;
            sleep(100);
             countSec++;
             if(countSec % 10 == 0){
                System.out.println("Time Elapsed: " + (countSec / 10) + " seconds");
             }
             if(countSec / 10 > 20){
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

    static JLabel timerLabel;
    static JTextField timerTextField;
    static JButton timerSave;
    public static void main(String[] args){
        JFrame frame = new JFrame("Fishing Bot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450,300);
        frame.setLayout(new FlowLayout());
        button = new JButton("Start Fishing");
        button.setPreferredSize(new Dimension(150, 75));
        timerSave = new JButton("Save Time");
        timerLabel = new JLabel("Time to Fish in minutes");
        frame.getContentPane().add(timerLabel);
        timerTextField = new JTextField(10);
        frame.getContentPane().add(timerTextField);
        frame.getContentPane().add(timerSave);
        frame.getContentPane().add(button);
        frame.setVisible(true);
        button.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                startFishing();
                System.out.println("Done Fishing");
            }
        });
        timerSave.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                timer = Integer.valueOf(timerTextField.getText());
                System.out.println(timer);
            }
        });
    }
}



